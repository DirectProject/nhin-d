package org.nhind.config.rest.impl;

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
import org.nhind.config.testbase.TestUtils;
import org.nhindirect.common.rest.OpenServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.resources.TrustBundleResource;

import org.nhindirect.config.store.dao.TrustBundleDao;

public class DefaultTrustBundleService_getTrustBundlesTest 
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
		
		protected String getFetchAnchors()
		{
			return "true";
		}
		
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
				final Collection<TrustBundle> getBundles = resource.getTrustBundles(Boolean.parseBoolean(getFetchAnchors()));
				doAssertions(getBundles);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
					doAssertions(new ArrayList<TrustBundle>());
				else
					throw e;
			}
			
		}
			
		protected void doAssertions(Collection<TrustBundle> certs) throws Exception
		{
			
		}
	}	

	@Test
	public void testGetAllBundles_noSigningCert_assertBundlesRetrieved()  throws Exception
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
					bundle.setBundleURL("http://10.2.3.2/bundle");
					bundle.setRefreshInterval(24);
					bundle.setSigningCertificateData(null);		
					bundles.add(bundle);
		
					
					bundle = new TrustBundle();
					bundle.setBundleName("testBundle2");
					bundle.setBundleURL("http://10.2.3.2/bundle2");
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

			
			protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
			{
				assertNotNull(bundles);
				assertEquals(2, bundles.size());
				
				final Iterator<TrustBundle> addedBundlesIter = this.bundles.iterator();
				
				for (TrustBundle retrievedBundle : bundles)
				{	
					final TrustBundle addedBundle = addedBundlesIter.next(); 
					
					assertEquals(addedBundle.getBundleName(), retrievedBundle.getBundleName());
					assertEquals(addedBundle.getBundleURL(), retrievedBundle.getBundleURL());
					assertEquals(addedBundle.getRefreshInterval(), retrievedBundle.getRefreshInterval());
					assertNull(retrievedBundle.getSigningCertificateData());
					assertEquals(addedBundle.getTrustBundleAnchors().size(), retrievedBundle.getTrustBundleAnchors().size());
				}
				
			}
		}.perform();
	}		
	
	@Test
	public void testGetAllBundles_hasSigningCert_assertBundlesRetrieved()  throws Exception
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
					bundle.setBundleURL("http://localhost:9999/bundle");
					bundle.setRefreshInterval(24);
					bundle.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());		
					bundles.add(bundle);
		
					
					bundle = new TrustBundle();
					bundle.setBundleName("testBundle2");
					bundle.setBundleURL("http://localhost:9999/bundle2");
					bundle.setRefreshInterval(12);
					bundle.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
					
					
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			
			protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
			{
				assertNotNull(bundles);
				assertEquals(2, bundles.size());
				
				final Iterator<TrustBundle> addedBundlesIter = this.bundles.iterator();
				
				for (TrustBundle retrievedBundle : bundles)
				{	
					final TrustBundle addedBundle = addedBundlesIter.next(); 
					
					assertEquals(addedBundle.getBundleName(), retrievedBundle.getBundleName());
					assertEquals(addedBundle.getBundleURL(), retrievedBundle.getBundleURL());
					assertEquals(addedBundle.getRefreshInterval(), retrievedBundle.getRefreshInterval());
					assertEquals(TestUtils.loadSigner("bundleSigner.der"), retrievedBundle.getSigningCertificateAsX509Certificate());
					assertEquals(addedBundle.getTrustBundleAnchors().size(), retrievedBundle.getTrustBundleAnchors().size());
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAllBundles_bundleHasAnchors_requestAnchors_assertBundlesRetrieved()  throws Exception
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

			
			protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
			{
				assertNotNull(bundles);
				assertEquals(2, bundles.size());
				
				final Iterator<TrustBundle> addedBundlesIter = this.bundles.iterator();
				
				for (TrustBundle retrievedBundle : bundles)
				{	
					final TrustBundle addedBundle = addedBundlesIter.next(); 
					
					assertEquals(addedBundle.getBundleName(), retrievedBundle.getBundleName());
					assertEquals(addedBundle.getBundleURL(), retrievedBundle.getBundleURL());
					assertEquals(addedBundle.getRefreshInterval(), retrievedBundle.getRefreshInterval());
					assertNull(retrievedBundle.getSigningCertificateAsX509Certificate());
					assertTrue(retrievedBundle.getTrustBundleAnchors().size() > 0);
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAllBundles_bundleHasAnchors_suppressAnchors_assertBundlesRetrievedWithNoAnchors()  throws Exception
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

			@Override
			protected String getFetchAnchors()
			{
				return "false";
			}
			
			protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
			{
				assertNotNull(bundles);
				assertEquals(2, bundles.size());
				
				final Iterator<TrustBundle> addedBundlesIter = this.bundles.iterator();
				
				for (TrustBundle retrievedBundle : bundles)
				{	
					final TrustBundle addedBundle = addedBundlesIter.next(); 
					
					assertEquals(addedBundle.getBundleName(), retrievedBundle.getBundleName());
					assertEquals(addedBundle.getBundleURL(), retrievedBundle.getBundleURL());
					assertEquals(addedBundle.getRefreshInterval(), retrievedBundle.getRefreshInterval());
					assertNull(retrievedBundle.getSigningCertificateAsX509Certificate());
					assertTrue(retrievedBundle.getTrustBundleAnchors().isEmpty());
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAllBundles_noBundlesInStore_assertNoBundlesRetrieved()  throws Exception
	{
		new TestPlan()
		{	
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				return null;
			}

			
			protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
			{
				assertNotNull(bundles);
				assertEquals(0, bundles.size());
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAllBundles_errorInLookup_assertServiceError()  throws Exception
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
					doThrow(new RuntimeException()).when(mockDAO).getTrustBundles();
					
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}			
}
