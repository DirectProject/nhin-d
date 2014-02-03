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
import org.nhindirect.common.rest.OpenServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.resources.TrustBundleResource;

import org.nhindirect.config.store.dao.TrustBundleDao;


public class DefaultTrustBundleService_getTrustBundleTest 
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
					
					resource = 	(TrustBundleService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), new OpenServiceSecurityManager(), TRUST_BUNDLE_SERVICE);	

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
			
			protected abstract String getBundleNameToFetch();
			
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
				
				try
				{

					final TrustBundle getBundle = resource.getTrustBundle(getBundleNameToFetch());
					doAssertions(getBundle);
				}
				catch (ServiceMethodException e)
				{
					if (e.getResponseCode() == 404)
						doAssertions(null);
					else
						throw e;
				}
				
			}
				
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testGetBundleByName_assertBundleRetrieved()  throws Exception
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
				protected String getBundleNameToFetch()
				{
					return "testBundle1";
				}
				
				protected void doAssertions(TrustBundle bundle) throws Exception
				{
					assertNotNull(bundle);
					
					final TrustBundle addedBundle = this.bundles.iterator().next();

					assertEquals(addedBundle.getBundleName(), bundle.getBundleName());
					assertEquals(addedBundle.getBundleURL(), bundle.getBundleURL());
					assertEquals(addedBundle.getRefreshInterval(), bundle.getRefreshInterval());
					assertNull(bundle.getSigningCertificateAsX509Certificate());
					assertTrue(bundle.getTrustBundleAnchors().size() > 0);

					
				}
			}.perform();
		}	
		
		@Test
		public void testGetBundleByName_nameNotFound_assertBundleNotRetrieved()  throws Exception
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
				protected String getBundleNameToFetch()
				{
					return "testBundle2";
				}
				
				protected void doAssertions(TrustBundle bundle) throws Exception
				{
					assertNull(bundle);
					
					
				}
			}.perform();
		}	
		
		@Test
		public void testGetBundleByName_errorInLookup_assertServiceError()  throws Exception
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
				protected String getBundleNameToFetch()
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
