package org.nhindirect.monitor.processor.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.monitor.dao.NotificationDAOException;
import org.nhindirect.monitor.dao.NotificationDuplicationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/notificationStore.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class DefaultDuplicateNotificationStateManager_purgeTest 
{
	@Autowired
	private NotificationDuplicationDAO notifDao;
	
	@Before
	public void setUp() throws Exception
	{
		Calendar qualTime = Calendar.getInstance(Locale.getDefault());
		qualTime.add(Calendar.YEAR, 10);
		
		notifDao.purgeNotifications(qualTime);
	}
	
	@Test
	public void testPurge_nullDAO_assertException() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		boolean execptionOccured = false;
		
		try
		{
			mgr.purge();
		}
		catch (IllegalArgumentException e)
		{
			execptionOccured = true;
		}
		
		assertTrue(execptionOccured);
	}
	
	@Test
	public void testPurgeNotification_notificationNotYetPurgable_assertNotPurged() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		// set one day ago.... messages are all new, so should not be purgable
		mgr.setMessageRetention(1);
		
		final String messageId = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId, "gm2552@cerner.com");
		
		Collection<String> addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());
		
		mgr.purge();
		
		addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());	
	}
	
	@Test
	public void testPurgeNotification_notificationnotificationPurged_assertNotPurged() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		// set the purge time to tomorrow so every thing today will get purged
		mgr.setMessageRetention(-1);
		
		final String messageId = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId, "gm2552@cerner.com");
		
		Collection<String> addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());
		
		mgr.purge();
		
		addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(0, addresses.size());	
	}	
	
	@Test
	public void testPurgeNotification_daoError_assertException() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		
		NotificationDuplicationDAO spyDao = mock(NotificationDuplicationDAO.class);
		doThrow(new NotificationDAOException("")).when(spyDao).purgeNotifications((Calendar)any());
		mgr.setDao(spyDao);
		
		mgr.purge();
		
		
	}	
}
