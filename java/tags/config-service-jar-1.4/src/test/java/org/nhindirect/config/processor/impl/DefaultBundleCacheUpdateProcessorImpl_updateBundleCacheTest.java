package org.nhindirect.config.processor.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import org.nhindirect.config.processor.BundleRefreshProcessor;
import org.nhindirect.config.store.ConfigurationStoreException;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.dao.TrustBundleDao;

import junit.framework.TestCase;

public class DefaultBundleCacheUpdateProcessorImpl_updateBundleCacheTest extends TestCase
{
	protected BundleRefreshProcessor processor;
	
	protected TrustBundleDao dao;
	
	@Override
	public void setUp()
	{
		processor = mock(BundleRefreshProcessor.class);
		dao = mock(TrustBundleDao.class);
	}
	
	public void testUpdateBundleCache_updateCache_bundleNeverUpdated_assertBundleRefreshCalled() throws Exception
	{
		final DefaultBundleCacheUpdateProcessorImpl cacheUpdate = new DefaultBundleCacheUpdateProcessorImpl();
		cacheUpdate.setDao(dao);
		cacheUpdate.setRefreshProcessor(processor);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setRefreshInterval(1);
		
		final Collection<TrustBundle> bundles = Arrays.asList(bundle);
		
		when(dao.getTrustBundles()).thenReturn(bundles);
		
		cacheUpdate.updateBundleCache();
		
		verify(dao, times(1)).getTrustBundles();
		verify(processor, times(1)).refreshBundle(bundle);
	}
	
	public void testUpdateBundleCache_updateCache_zeroRefreshInterval_assertBundleRefreshNotCalled() throws Exception
	{
		final DefaultBundleCacheUpdateProcessorImpl cacheUpdate = new DefaultBundleCacheUpdateProcessorImpl();
		cacheUpdate.setDao(dao);
		cacheUpdate.setRefreshProcessor(processor);
		
		final TrustBundle bundle = new TrustBundle();
		
		final Collection<TrustBundle> bundles = Arrays.asList(bundle);
		
		when(dao.getTrustBundles()).thenReturn(bundles);
		
		cacheUpdate.updateBundleCache();
		
		verify(dao, times(1)).getTrustBundles();
		verify(processor, never()).refreshBundle(bundle);
	}
	
	public void testUpdateBundleCache_updateCache_refreshIntervalNotExpired_assertBundleRefreshNotCalled() throws Exception
	{
		final DefaultBundleCacheUpdateProcessorImpl cacheUpdate = new DefaultBundleCacheUpdateProcessorImpl();
		cacheUpdate.setDao(dao);
		cacheUpdate.setRefreshProcessor(processor);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setRefreshInterval(1000);
		bundle.setLastSuccessfulRefresh(Calendar.getInstance(Locale.getDefault()));
		
		final Collection<TrustBundle> bundles = Arrays.asList(bundle);
		
		when(dao.getTrustBundles()).thenReturn(bundles);
		
		cacheUpdate.updateBundleCache();
		
		verify(dao, times(1)).getTrustBundles();
		verify(processor, never()).refreshBundle(bundle);
	}
	
	public void testUpdateBundleCache_updateCache_refreshIntervalExpired_assertBundleRefreshCalled() throws Exception
	{
		final DefaultBundleCacheUpdateProcessorImpl cacheUpdate = new DefaultBundleCacheUpdateProcessorImpl();
		cacheUpdate.setDao(dao);
		cacheUpdate.setRefreshProcessor(processor);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setRefreshInterval(1000);
		final Calendar lastSuccessRefresh = Calendar.getInstance(Locale.getDefault());
		lastSuccessRefresh.add(Calendar.SECOND, -1200);
		bundle.setLastSuccessfulRefresh(lastSuccessRefresh);
		
		final Collection<TrustBundle> bundles = Arrays.asList(bundle);
		
		when(dao.getTrustBundles()).thenReturn(bundles);
		
		cacheUpdate.updateBundleCache();
		
		verify(dao, times(1)).getTrustBundles();
		verify(processor, times(1)).refreshBundle(bundle);
	}
	
	public void testUpdateBundleCache_updateCache_errorInRetreavingBundles_assertBundleRefreshNotCalled() throws Exception
	{
		final DefaultBundleCacheUpdateProcessorImpl cacheUpdate = new DefaultBundleCacheUpdateProcessorImpl();
		cacheUpdate.setDao(dao);
		cacheUpdate.setRefreshProcessor(processor);
		

		doThrow(new ConfigurationStoreException("Just Passing Through")).when(dao).getTrustBundles();

		cacheUpdate.updateBundleCache();
		
		verify(dao, times(1)).getTrustBundles();
		verify(processor, never()).refreshBundle((TrustBundle)any());
	}	
	
	public void testUpdateBundleCache_updateCache_errorInUpdate_assertBundleRefreshCalled() throws Exception
	{
		final DefaultBundleCacheUpdateProcessorImpl cacheUpdate = new DefaultBundleCacheUpdateProcessorImpl();
		cacheUpdate.setDao(dao);
		cacheUpdate.setRefreshProcessor(processor);
		
		final TrustBundle bundle = new TrustBundle();
		bundle.setRefreshInterval(1);
		
		final Collection<TrustBundle> bundles = Arrays.asList(bundle);
		
		when(dao.getTrustBundles()).thenReturn(bundles);
		
		doThrow(new RuntimeException("Just Passing Through")).when(processor).refreshBundle(bundle);
		
		cacheUpdate.updateBundleCache();
	
		verify(dao, times(1)).getTrustBundles();
		verify(processor, times(1)).refreshBundle(bundle);
	}	
}
