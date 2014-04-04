package org.nhindirect.monitor.processor.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.dao.NotificationDuplicationDAO;
import org.nhindirect.monitor.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/notificationStore.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class TimeoutDupStateManager_addNotificationForOriginalRecipsTest 
{
	@Autowired
	private NotificationDuplicationDAO notifDao;
	
	@Before
	public void setUp() throws Exception
	{
		final Calendar qualTime = Calendar.getInstance(Locale.getDefault());
		qualTime.add(Calendar.YEAR, 10);
		
		notifDao.purgeNotifications(qualTime);
	}
	
	@Test
	public void testAddNotificationForOriginalRecips_nullDAO_assertException() throws Exception
	{
		final TimeoutDupStateManager mgr = new TimeoutDupStateManager();
		boolean execptionOccured = false;
		
		try
		{
			mgr.addNotificationForOriginalRecips(new ArrayList<Tx>());
		}
		catch (IllegalArgumentException e)
		{
			execptionOccured = true;
		}
		
		assertTrue(execptionOccured);
	}	
	
	@Test
	public void testAddNotificationForOriginalRecips_nullCollection_assertException() throws Exception
	{
		final TimeoutDupStateManager mgr = new TimeoutDupStateManager();
		boolean execptionOccured = false;
		
		mgr.setDao(notifDao);
		
		try
		{
			mgr.addNotificationForOriginalRecips(null);
		}
		catch (IllegalArgumentException e)
		{
			execptionOccured = true;
		}
		
		assertTrue(execptionOccured);
	}
	
	@Test
	public void testAddNotificationForOriginalRecips_noRecips_assertNotificationNotAdded() throws Exception
	{
		TimeoutDupStateManager mgr = new TimeoutDupStateManager();

		NotificationDuplicationDAO dao = mock(NotificationDuplicationDAO.class);
		
		mgr.setDao(dao);
		
		final Tx tx = TestUtils.makeMessage(TxMessageType.IMF, "1234", "", "", "", "");
				
		mgr.addNotificationForOriginalRecips(Arrays.asList(tx));
		
		verify(dao, never()).addNotification((String)any(), (String)any());
	}	
	
	@Test
	public void testAddNotificationForOriginalRecips_noOrigMsg_assertNotificationNotAdded() throws Exception
	{
		TimeoutDupStateManager mgr = new TimeoutDupStateManager();

		NotificationDuplicationDAO dao = mock(NotificationDuplicationDAO.class);
		
		mgr.setDao(dao);
		
		final Tx tx = TestUtils.makeMessage(TxMessageType.MDN, "1234", "", "test@test.com", "me@you.com", "test@test.com");
				
		mgr.addNotificationForOriginalRecips(Arrays.asList(tx));
		
		verify(dao, never()).addNotification((String)any(), (String)any());
	}	
	
	@Test
	public void testAddNotificationForOriginalRecips_addSingleRecip_nonReliable_assertNotificationsNotAdded() throws Exception
	{
		TimeoutDupStateManager mgr = new TimeoutDupStateManager();
		
		NotificationDuplicationDAO dao = mock(NotificationDuplicationDAO.class);
		
		mgr.setDao(dao);
		
		final Tx tx = TestUtils.makeMessage(TxMessageType.IMF, "1234", "", "test@test.com", "me@you.com", "test@test.com");
				
		mgr.addNotificationForOriginalRecips(Arrays.asList(tx));

		verify(dao, never()).addNotification((String)any(), (String)any());
	}	
	
	@Test
	public void testAddNotificationForOriginalRecips_addSingleRecip_assertNotificationsAdded() throws Exception
	{
		TimeoutDupStateManager mgr = new TimeoutDupStateManager();
		
		mgr.setDao(notifDao);
		
		final Tx tx = TestUtils.makeReliableMessage(TxMessageType.IMF, "1234", "", "test@test.com", "me@you.com", "test@test.com", "", "");
				
		mgr.addNotificationForOriginalRecips(Arrays.asList(tx));

		
		Set<String> recAddresses = notifDao.getReceivedAddresses("1234", Arrays.asList("me@you.com"));
		assertTrue(recAddresses.contains("me@you.com"));
	}	
	
	@Test
	public void testAddNotificationForOriginalRecips_addMultipleRecips_assertNotificationsAdded() throws Exception
	{
		TimeoutDupStateManager mgr = new TimeoutDupStateManager();

		mgr.setDao(notifDao);
		
		final Tx tx = TestUtils.makeReliableMessage(TxMessageType.IMF, "1234", "", "test@test.com", "me@you.com,you@you.com", "test@test.com", "", "");
				
		mgr.addNotificationForOriginalRecips(Arrays.asList(tx));
		
		Set<String> recAddresses = notifDao.getReceivedAddresses("1234", Arrays.asList("me@you.com", "you@you.com"));
		assertTrue(recAddresses.contains("me@you.com"));
		assertTrue(recAddresses.contains("you@you.com"));
	}	
	
	@Test
	public void testAddNotificationForOriginalRecips_addDupRecip_assertNotificationsAdded() throws Exception
	{
		TimeoutDupStateManager mgr = new TimeoutDupStateManager();

		mgr.setDao(notifDao);
		
		final Tx tx = TestUtils.makeReliableMessage(TxMessageType.IMF, "1234", "", "test@test.com", "me@you.com,you@you.com", "test@test.com", "", "");
				
		mgr.addNotificationForOriginalRecips(Arrays.asList(tx));
		
		Set<String> recAddresses = notifDao.getReceivedAddresses("1234", Arrays.asList("me@you.com", "you@you.com"));
		assertTrue(recAddresses.contains("me@you.com"));
		assertTrue(recAddresses.contains("you@you.com"));

		// add it again
		mgr.addNotificationForOriginalRecips(Arrays.asList(tx));	
		
		recAddresses = notifDao.getReceivedAddresses("1234", Arrays.asList("me@you.com", "you@you.com"));
		assertEquals(2, recAddresses.size());
	}		
}
