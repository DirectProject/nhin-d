package org.nhindirect.monitor.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class NotificationDuplicationDAOImpl_purgeNotificationsTest 
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
	public void testPurgeNotification_notificationPurged_assertPurged() throws Exception
	{
		final String messageId = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId, "gm2552@cerner.com");
		
		Collection<String> addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());
		
		Calendar qualTime = Calendar.getInstance(Locale.getDefault());
		qualTime.add(Calendar.YEAR, 10);
		
		notifDao.purgeNotifications(qualTime);
		
		addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(0, addresses.size());
		
		
	}
	
	@Test
	public void testPurgeNotification_notificationNotYetPurgable_assertNotPurged() throws Exception
	{
		final String messageId = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId, "gm2552@cerner.com");
		
		Collection<String> addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());
		
		Calendar qualTime = Calendar.getInstance(Locale.getDefault());
		qualTime.add(Calendar.HOUR, -1);
		
		notifDao.purgeNotifications(qualTime);
		
		addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());
		
		
	}
}
