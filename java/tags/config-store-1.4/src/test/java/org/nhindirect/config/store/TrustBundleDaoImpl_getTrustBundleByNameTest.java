package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Calendar;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_getTrustBundleByNameTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testTetTrustBundleByName_emptyStore_assertNoBundleReturned()
	{
		assertNull(tbDao.getTrustBundleByName("Test Bundle"));
	}
	
	@Test
	public void testTetTrustBundleByName_singleBundleInStore_nameNotInStore_assertNoBundleReturned()
	{
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		
		assertNull(tbDao.getTrustBundleByName("Test Bundle X"));
	}
	
	@Test
	public void testTetTrustBundleByName_singleBundleInStore_assertBundleReturned()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		TrustBundle addedBundle = tbDao.getTrustBundleByName("Test BunDLE");
		
		assertEquals("Test Bundle", addedBundle.getBundleName());
		assertEquals("http://testBundle/bundle.p7b", addedBundle.getBundleURL());	
		assertEquals("12345", addedBundle.getCheckSum());
		assertEquals(5, addedBundle.getRefreshInterval());
		assertTrue(now.getTimeInMillis() <= addedBundle.getCreateTime().getTimeInMillis());
		assertNull(addedBundle.getLastRefreshAttempt());
		assertNull(addedBundle.getLastSuccessfulRefresh());
		assertNull(addedBundle.getLastRefreshError());
		assertNull(addedBundle.getSigningCertificateData());
		assertTrue(addedBundle.getTrustBundleAnchors().isEmpty());
	}	
	
	@Test
	public void testGetTrustBundleByName_exceptionInQuery_assertException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getTrustBundleByName("any");
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testGetTrustBundleByName_noEntityManager_assertException()
	{

		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.getTrustBundleByName("any");
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}		
}
