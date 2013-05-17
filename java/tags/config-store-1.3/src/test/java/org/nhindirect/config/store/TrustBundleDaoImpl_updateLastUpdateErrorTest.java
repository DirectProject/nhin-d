package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Calendar;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundleDaoImpl_updateLastUpdateErrorTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testUpdateLastUpdateError_updateUpdate_assertErrorUpdate()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		tbDao.updateLastUpdateError(bundle.getId(), now, BundleRefreshError.SUCCESS);
		
		TrustBundle updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals( BundleRefreshError.SUCCESS, updatedBundle.getLastRefreshError());
		assertEquals(now, updatedBundle.getLastRefreshAttempt());
		
		tbDao.updateLastUpdateError(bundle.getId(), now, BundleRefreshError.NOT_FOUND);
		
		updatedBundle = tbDao.getTrustBundleById(bundle.getId());
		
		assertEquals( BundleRefreshError.NOT_FOUND, updatedBundle.getLastRefreshError());	
	}
	
	@Test
	public void testUpdateLastUpdateError_bundleDoesntExist_assertException()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		
		boolean exceptionOccured = false;
		
		try
		{
			tbDao.updateLastUpdateError(1234, now, BundleRefreshError.NOT_FOUND);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testUpdateLastUpdateError_exceptionInQuery_assertException()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).createQuery((String)any());
		
		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			tbDao.updateLastUpdateError(1234, now, BundleRefreshError.NOT_FOUND);
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
	
	@Test
	public void testUpdateLastUpdateError_noEntityManager_assertException()
	{
		final Calendar now = Calendar.getInstance(Locale.getDefault());
		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.updateLastUpdateError(1234, now, BundleRefreshError.NOT_FOUND);
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
}
