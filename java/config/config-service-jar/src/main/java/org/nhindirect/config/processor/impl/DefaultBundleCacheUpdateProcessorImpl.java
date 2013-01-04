package org.nhindirect.config.processor.impl;

import java.util.Calendar;
import java.util.Collection;

import org.apache.camel.Handler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.processor.BundleCacheUpdateProcessor;
import org.nhindirect.config.processor.BundleRefreshProcessor;
import org.nhindirect.config.store.TrustBundle;
import org.nhindirect.config.store.dao.TrustBundleDao;

public class DefaultBundleCacheUpdateProcessorImpl implements BundleCacheUpdateProcessor
{
    private static final Log log = LogFactory.getLog(DefaultBundleCacheUpdateProcessorImpl.class);
	
	protected TrustBundleDao dao;

	protected BundleRefreshProcessor refreshProcessor;
	
	public DefaultBundleCacheUpdateProcessorImpl()
	{
		
	}
	
	public void setDao(TrustBundleDao dao)
	{
		this.dao = dao;
	}
	
	public void setRefreshProcessor(BundleRefreshProcessor refreshProcessor)
	{
		this.refreshProcessor = refreshProcessor;
	}
	
	@Handler
	public void updateBundleCache()
	{
		Collection<TrustBundle> bundles;
		try
		{
			bundles = dao.getTrustBundles();
			for (TrustBundle bundle : bundles)
			{
				boolean refresh = false;
				
				// if the refresh interval is 0 or less, then we won't ever auto refresh the bundle
				if (bundle.getRefreshInterval() <= 0)
					continue;  
				
				// see if this bundle needs to be checked for updating
				final Calendar lastAttempt = bundle.getLastSuccessfulRefresh();
			
				if (lastAttempt == null)
					// never been attempted successfully... better go get it
					refresh = true;
				else
				{
					// check the the last attempt date against now and see if we need to refresh
					long now = System.currentTimeMillis();
					Calendar lastAttemptCheck = (Calendar)lastAttempt.clone();
					lastAttemptCheck.add(Calendar.SECOND, bundle.getRefreshInterval());
					
					if (lastAttemptCheck.getTimeInMillis() <= now)
						refresh = true;
				}
				
				if (refresh)
				{
					// refresh the bundle
					try
					{
						refreshProcessor.refreshBundle(bundle);		
					}
					catch (Exception e)
					{
						log.warn("Failed to check the status of bundle " + bundle.getBundleName(), e);
					}
				}
			}
		}
		catch (Exception e)
		{
			log.warn("Failed to check the status of trust bundles ", e);
		}
		
	}
}
