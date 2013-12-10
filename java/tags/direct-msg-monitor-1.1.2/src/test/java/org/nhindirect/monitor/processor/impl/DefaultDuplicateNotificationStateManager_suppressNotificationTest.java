package org.nhindirect.monitor.processor.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.dao.NotificationDAOException;
import org.nhindirect.monitor.dao.NotificationDuplicationDAO;
import org.nhindirect.monitor.processor.DuplicateNotificationStateManagerException;
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
public class DefaultDuplicateNotificationStateManager_suppressNotificationTest 
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
	public void testSuppressNotification_nullDAO_assertException() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		boolean execptionOccured = false;
		
		try
		{
			mgr.suppressNotification(mock(Tx.class));
		}
		catch (IllegalArgumentException e)
		{
			execptionOccured = true;
		}
		
		assertTrue(execptionOccured);
	}
	
	@Test
	public void testSuppressNotification_nullTx_assertException() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		
		boolean execptionOccured = false;
		
		try
		{
			mgr.suppressNotification(null);
		}
		catch (IllegalArgumentException e)
		{
			execptionOccured = true;
		}
		
		assertTrue(execptionOccured);
	}
	
	@Test
	public void testSuppressNotification_nonNotificationTx_assertFalse() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		
		Tx tx = TestUtils.makeMessage(TxMessageType.IMF, "1234", "", "", "", "gm2552@cerner.com");

		assertFalse(mgr.suppressNotification(tx));
	}
	
	@Test
	public void testSuppressNotification_displayedDisposition_assertFalse() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		
		Tx tx = TestUtils.makeMessage(TxMessageType.MDN, "1234", "5678", "", "",
				"", "", MDNStandard.Disposition_Displayed);

		assertFalse(mgr.suppressNotification(tx));
	}
	
	@Test
	public void testSuppressNotification_noOrigMessageId_assertFalse() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		
		Tx tx = TestUtils.makeMessage(TxMessageType.MDN, "1234", "", "", "",
				"gm2552@cerner.com", "", MDNStandard.Disposition_Error);

		assertFalse(mgr.suppressNotification(tx));
	}
	
	@Test
	public void testSuppressNotification_noFinalRecip_assertFalse() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		
		Tx tx = TestUtils.makeMessage(TxMessageType.MDN, "1234", "5678", "", "",
				"", "", MDNStandard.Disposition_Error);

		assertFalse(mgr.suppressNotification(tx));
	}
	
	@Test
	public void testSuppressNotification_recipNotInStore_assertFalse() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		
		Tx tx = TestUtils.makeMessage(TxMessageType.MDN, "1234", "5678", "", "",
				"gm2552@cerner.com", "", MDNStandard.Disposition_Error);

		assertFalse(mgr.suppressNotification(tx));
	}
	
	@Test
	public void testSuppressNotification_recipInStore_assertTrue() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		mgr.setDao(notifDao);
		
		Tx tx = TestUtils.makeMessage(TxMessageType.MDN, "1234", "5678", "", "",
				"gm2552@cerner.com", "", MDNStandard.Disposition_Error);

		mgr.addNotification(tx);
		
		assertTrue(mgr.suppressNotification(tx));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAddNotification_daoError_assertException() throws Exception
	{
		DefaultDuplicateNotificationStateManager mgr = new DefaultDuplicateNotificationStateManager();
		boolean execptionOccured = false;
		
		NotificationDuplicationDAO spyDao = mock(NotificationDuplicationDAO.class);
		doThrow(new NotificationDAOException("")).when(spyDao).getReceivedAddresses((String)any(), (Collection<String>)any());
		mgr.setDao(spyDao);
		
		try
		{
			Tx tx = TestUtils.makeMessage(TxMessageType.DSN, "1234", "5678", "", "", "gm2552@cerner.com,ah4626@cerner.com");

			mgr.suppressNotification(tx);
		}
		catch (DuplicateNotificationStateManagerException e)
		{
			execptionOccured = true;
		}
		
		assertTrue(execptionOccured);
	}
}
