package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.config.store.dao.impl.TrustBundleDaoImpl;

public class TrustBundlDaoImpl_deleteTrustBundlesTest extends TrustBundleDaoBaseTest
{
	@Test
	public void testDeleteTrustBundlesTest_singleBundle_assertBundleDeleted()
	{
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		Collection<TrustBundle> bundles = tbDao.getTrustBundles();
		
		assertEquals(1, bundles.size());
		
		tbDao.deleteTrustBundles(new long[] {bundles.iterator().next().getId()});
		
		bundles = tbDao.getTrustBundles();
		
		assertEquals(0, bundles.size());
	}
	
	@Test
	public void testDeleteTrustBundlesTest_multipleBundles_assertSingleBundleDeleted()
	{
		
		TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle2");
		bundle.setBundleURL("http://testBundle/bundle2.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("67890");
		
		tbDao.addTrustBundle(bundle);
		
		Collection<TrustBundle> bundles = tbDao.getTrustBundles();
		
		assertEquals(2, bundles.size());
		
		tbDao.deleteTrustBundles(new long[] {bundles.iterator().next().getId()});
		
		bundles = tbDao.getTrustBundles();
		
		assertEquals(1, bundles.size());
	}
	
	@Test
	public void testDeleteTrustBundlesTest_multipleBundles_assertAllBundlesDeleted()
	{
		
		TrustBundle bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle");
		bundle.setBundleURL("http://testBundle/bundle.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("12345");
		
		tbDao.addTrustBundle(bundle);
		
		bundle = new TrustBundle();
		bundle.setBundleName("Test Bundle2");
		bundle.setBundleURL("http://testBundle/bundle2.p7b");
		bundle.setRefreshInterval(5);
		bundle.setCheckSum("67890");
		
		tbDao.addTrustBundle(bundle);
		
		Collection<TrustBundle> bundles = tbDao.getTrustBundles();
		
		assertEquals(2, bundles.size());
		
		Iterator<TrustBundle> iter = bundles.iterator();
		tbDao.deleteTrustBundles(new long[] {iter.next().getId(), iter.next().getId()});
		
		bundles = tbDao.getTrustBundles();
		
		assertEquals(0, bundles.size());
	}	
	
	@Test
	public void testDeleteTrustBundlesTest_nullArray_assertNoError()
	{
		tbDao.deleteTrustBundles(null);
	}
	
	@Test
	public void testDeleteTrustBundlesTest_emptyArray_assertNoError()
	{
		tbDao.deleteTrustBundles(new long[]{});
	}	
	
	@Test
	public void testDeleteTrustBundlesTest_exceptionInQuery_assertNoException()
	{
		final EntityManager manager = mock(EntityManager.class);
		doThrow(new RuntimeException("Just Passing Through")).when(manager).remove((TrustBundle)any());
		
		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		dao.setEntityManager(manager);
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.deleteTrustBundles(new long[] {1234});
		}
		catch (ConfigurationStoreException ex)
		{
			exceptionOccured = true;
		}
		
		assertFalse(exceptionOccured);
		verify(manager, never()).remove((TrustBundle)any());
		verify(manager, never()).flush();
	}
	
	@Test
	public void testDeleteTrustBundlesTest_noEntityManager_assertException()
	{

		final TrustBundleDaoImpl dao = new TrustBundleDaoImpl();
		
		boolean exceptionOccured = false;
		
		try
		{
			dao.deleteTrustBundles(new long[] {1234});
		}
		catch (IllegalStateException ex)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}		
}