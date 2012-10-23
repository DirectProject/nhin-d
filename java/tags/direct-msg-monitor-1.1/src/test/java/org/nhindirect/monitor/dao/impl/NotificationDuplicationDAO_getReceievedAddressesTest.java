package org.nhindirect.monitor.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import java.util.Locale;

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
public class NotificationDuplicationDAO_getReceievedAddressesTest 
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
	public void testGetReceievedAddressesTest_emptyAddressCollection_returnEmpty() throws Exception
	{
		final Set<String> addresses = notifDao.getReceivedAddresses("test", new ArrayList<String>());
		assertEquals(0, addresses.size());
	}
	
	@Test
	public void testGetReceievedAddressesTest_nullAddressCollection_returnEmpty() throws Exception
	{
		final Set<String> addresses = notifDao.getReceivedAddresses("test", null);
		assertEquals(0, addresses.size());
	}
	
	@Test
	public void testGetReceievedAddressesTest_emptyMessageId_returnEmpty() throws Exception
	{
		Collection<String> addresses = new ArrayList<String>();
		addresses.add("gm2552@cerner.com");
		final Set<String> retAddresses = notifDao.getReceivedAddresses("", addresses);
		assertEquals(0, retAddresses.size());
	}
	
	@Test
	public void testGetReceievedAddressesTest_nullMessageId_returnEmpty() throws Exception
	{
		Collection<String> addresses = new ArrayList<String>();
		addresses.add("gm2552@cerner.com");
		final Set<String> retAddresses = notifDao.getReceivedAddresses(null, addresses);
		assertEquals(0, retAddresses.size());
	}
	
	@Test
	public void testGetReceievedAddressesTest_messageIdNotFound_returnEmpty() throws Exception
	{
		final String messageId1 = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId1, "gm2552@cerner.com");
		
		Collection<String> addresses = new ArrayList<String>();
		addresses.add("gm2552@cerner.com");
		final Set<String> retAddresses = notifDao.getReceivedAddresses(UUID.randomUUID().toString(), addresses);
		
		assertEquals(0, retAddresses.size());
	}
	
	@Test
	public void testGetReceievedAddressesTest_addressNotFound_returnEmpty() throws Exception
	{
		final String messageId1 = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId1, "gm2552@cerner.com");
		
		Collection<String> addresses = new ArrayList<String>();
		addresses.add("ah4626@cerner.com");
		final Set<String> retAddresses = notifDao.getReceivedAddresses(messageId1, addresses);
		
		assertEquals(0, retAddresses.size());
	}
	
	@Test
	public void testGetReceievedAddressesTest_mutlipleNotificationAddresses_asssertAllAddressesFound() throws Exception
	{
		final String messageId1 = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId1, "gm2552@cerner.com");
		notifDao.addNotification(messageId1, "ah4626@cerner.com");
		
		Collection<String> addresses = new ArrayList<String>();
		addresses.add("ah4626@cerner.com");
		addresses.add("gm2552@cerner.com");		
		
		final Set<String> retAddresses = notifDao.getReceivedAddresses(messageId1, addresses);
		
		assertEquals(2, retAddresses.size());
		
		assertTrue(retAddresses.contains("ah4626@cerner.com"));		
		assertTrue(retAddresses.contains("gm2552@cerner.com"));		
		
	}
	
	@Test
	public void testGetReceievedAddressesTest_singleNotificationAddresses_asssertSingleAddressesFound() throws Exception
	{
		final String messageId1 = UUID.randomUUID().toString();
		
		notifDao.addNotification(messageId1, "gm2552@cerner.com");
		notifDao.addNotification(messageId1, "ah4626@cerner.com");
		
		Collection<String> addresses = new ArrayList<String>();
		addresses.add("ah4626@cerner.com");
		
		final Set<String> retAddresses = notifDao.getReceivedAddresses(messageId1, addresses);
		
		assertEquals(1, retAddresses.size());
		
		assertTrue(retAddresses.contains("ah4626@cerner.com"));		
		assertFalse(retAddresses.contains("gm2552@cerner.com"));		
		
	}
}
