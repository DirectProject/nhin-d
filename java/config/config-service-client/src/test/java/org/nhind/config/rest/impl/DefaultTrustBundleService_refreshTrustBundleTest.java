package org.nhind.config.rest.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.TrustBundleService;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.BundleRefreshError;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.resources.TrustBundleResource;
import org.nhindirect.config.store.dao.TrustBundleDao;

public class DefaultTrustBundleService_refreshTrustBundleTest 
{
    protected TrustBundleDao bundleDao;
    
	static TrustBundleService resource;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				bundleDao = (TrustBundleDao)ConfigServiceRunner.getSpringApplicationContext().getBean("trustBundleDao");
				
				resource = 	(TrustBundleService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), TRUST_BUNDLE_SERVICE);	

			}
			catch (Throwable t)
			{
				throw new RuntimeException(t);
			}
		}
		
		@Override
		protected void tearDownMocks()
		{

		}

		protected abstract Collection<TrustBundle> getBundlesToAdd();
		
		protected abstract String getBundleNameToRefresh();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<TrustBundle> bundlesToAdd = getBundlesToAdd();
			
			if (bundlesToAdd != null)
			{
				for (TrustBundle addBundle : bundlesToAdd)
				{
					try
					{
						resource.addTrustBundle(addBundle);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}
			

			resource.refreshTrustBundle(getBundleNameToRefresh());
			final TrustBundle getBundle = resource.getTrustBundle(getBundleNameToRefresh());
			doAssertions(getBundle);
			
		}
			
		protected void doAssertions(TrustBundle bundle) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testRefershBundle_assertBundleRefreshed()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<TrustBundle> bundles;
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				try
				{
					bundles = new ArrayList<TrustBundle>();
					
					TrustBundle bundle = new TrustBundle();
					bundle.setBundleName("testBundle1");
					File fl = new File("src/test/resources/bundles/providerTestBundle.p7b");
					bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
					bundle.setRefreshInterval(24);
					bundle.setSigningCertificateData(null);		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected String getBundleNameToRefresh()
			{
				return "testBundle1";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				assertTrue(bundle.getLastRefreshAttempt() != null);
				assertEquals(BundleRefreshError.SUCCESS, bundle.getLastRefreshError());
			}
		}.perform();
	}	
	
	@Test
	public void testRefershBundle_bundleDoesNotExist_assertNotFound()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<TrustBundle> bundles;
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				try
				{
					bundles = new ArrayList<TrustBundle>();
					
					TrustBundle bundle = new TrustBundle();
					bundle.setBundleName("testBundle1");
					File fl = new File("src/test/resources/bundles/providerTestBundle.p7b");
					bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
					bundle.setRefreshInterval(24);
					bundle.setSigningCertificateData(null);		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected String getBundleNameToRefresh()
			{
				return "testBundle2";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(404, ex.getResponseCode());
			}
		}.perform();
	}	
	
	@Test
	public void testRefershBundle_errorInRefresh_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			protected TrustBundleResource bundleService;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					bundleService = (TrustBundleResource)ConfigServiceRunner.getSpringApplicationContext().getBean("trustBundleResource");

					TrustBundleDao mockDAO = mock(TrustBundleDao.class);
					
					doThrow(new RuntimeException()).when(mockDAO).getTrustBundleByName(eq("testBundle1"));
					bundleService.setTrustBundleDao(mockDAO);
				}
				catch (Throwable t)
				{
					throw new RuntimeException(t);
				}
			}
			
			@Override
			protected void tearDownMocks()
			{
				super.tearDownMocks();
				
				bundleService.setTrustBundleDao(bundleDao);
			}	
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				return null;
			}
			
			@Override
			protected String getBundleNameToRefresh()
			{
				return "testBundle1";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}		
	
}
