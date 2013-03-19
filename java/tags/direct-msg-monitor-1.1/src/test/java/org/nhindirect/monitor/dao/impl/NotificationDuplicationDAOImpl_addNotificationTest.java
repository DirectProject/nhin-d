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
public class NotificationDuplicationDAOImpl_addNotificationTest 
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
	public void testAddNotification_addNotification_assertCertAdded() throws Exception
	{
		final String messageId = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId, "gm2552@cerner.com");
		
		final Collection<String> addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());
	}
	
	@Test
	public void testAddNotification_addDuplicateNotification_assertCertAdded_noException() throws Exception
	{
		final String messageId = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId, "gm2552@cerner.com");
		
		Collection<String> addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());
		
		notifDao.addNotification(messageId, "gm2552@cerner.com");
		
		addresses = notifDao.getReceivedAddresses(messageId, Arrays.asList("gm2552@cerner.com"));
		
		assertEquals(1, addresses.size());
	}
	
}
