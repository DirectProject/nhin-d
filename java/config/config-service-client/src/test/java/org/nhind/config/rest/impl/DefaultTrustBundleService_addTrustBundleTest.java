package org.nhind.config.rest.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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


public class DefaultTrustBundleService_addTrustBundleTest 
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
			
			doAssertions();
		}
			
		protected void doAssertions() throws Exception
		{
			
		}
	}	
	
	@Test
	public void testAddBundle_assertBundlesAdded()  throws Exception
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
		
					
					bundle = new TrustBundle();
					bundle.setBundleName("testBundle2");
					bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
					bundle.setRefreshInterval(12);
					bundle.setSigningCertificateData(null);
					
					
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			protected void doAssertions() throws Exception
			{
				final Collection<org.nhindirect.config.store.TrustBundle> bundles = bundleDao.getTrustBundles();
				
				assertNotNull(bundles);
				assertEquals(2, bundles.size());
				
				final Iterator<TrustBundle> addedBundlesIter = this.bundles.iterator();
				
				for (org.nhindirect.config.store.TrustBundle retrievedBundle : bundles)
				{	
					final TrustBundle addedBundle = addedBundlesIter.next(); 
					
					assertEquals(addedBundle.getBundleName(), retrievedBundle.getBundleName());
					assertEquals(addedBundle.getBundleURL(), retrievedBundle.getBundleURL());
					assertEquals(addedBundle.getRefreshInterval(), retrievedBundle.getRefreshInterval());
					assertNull(retrievedBundle.getSigningCertificateData());
					assertTrue(retrievedBundle.getTrustBundleAnchors().size() > 0);
				}

				
			}
		}.perform();
	}		
	
	@Test
	public void testAddBundle_bundleAlreadyExists_assertConflict()  throws Exception
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
		
					
					bundle = new TrustBundle();
					bundle.setBundleName("testBundle1");
					bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
					bundle.setRefreshInterval(12);
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(409, ex.getResponseCode());
			}
		}.perform();
	}	
	
	@Test
	public void testAddBundle_errorInLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<TrustBundle> bundles;
			
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}		
	
	@Test
	public void testAddBundle_errorInAdd_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<TrustBundle> bundles;
			
			protected TrustBundleResource bundleService;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					bundleService = (TrustBundleResource)ConfigServiceRunner.getSpringApplicationContext().getBean("trustBundleResource");

					TrustBundleDao mockDAO = mock(TrustBundleDao.class);
					
					doThrow(new RuntimeException()).when(mockDAO).addTrustBundle((org.nhindirect.config.store.TrustBundle)any());
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}			
}
