package org.nhind.config.rest.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.TrustBundleService;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhind.config.testbase.TestUtils;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.resources.TrustBundleResource;
import org.nhindirect.config.store.dao.TrustBundleDao;

public class DefaultTrustBundleService_updateSigningCertTest 
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

		protected abstract X509Certificate getNewSigningCertificate() throws Exception;
		
		protected abstract String getBundleToUpdate();
		
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
			
			resource.updateSigningCert(getBundleToUpdate(), getNewSigningCertificate());

			
			final TrustBundle getBundle = resource.getTrustBundle(getBundleToUpdate());
			doAssertions(getBundle);
			
		}
			
		protected void doAssertions(TrustBundle bundle) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testUpdateSigningCert_assertSigningCertUpdated()  throws Exception
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
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return TestUtils.loadSigner("bundleSigner.der");
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				assertEquals(getBundleToUpdate(), bundle.getBundleName());
				assertEquals(TestUtils.loadSigner("bundleSigner.der"), bundle.getSigningCertificateAsX509Certificate());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateSigningCert_changeToNull_assertSigningCertUpdated()  throws Exception
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
					bundle.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return null;
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				assertEquals(getBundleToUpdate(), bundle.getBundleName());
				assertNull(bundle.getSigningCertificateData());
			}
		}.perform();
	}			
	
	@Test
	public void testUpdateSigningCert_nonExistentBundle_assertNotFound()  throws Exception
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
					bundle.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return null;
			}
			
			@Override
			protected String getBundleToUpdate()
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
	public void testUpdateSigningCert_invalidCert_assertBadRequest()  throws Exception
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
					bundle.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				X509Certificate mockCert = mock(X509Certificate.class);
				when(mockCert.getEncoded()).thenReturn(new byte[] {124,3,2,1});
				return mockCert;
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(400, ex.getResponseCode());
			}
		}.perform();
	}			
	
	@Test
	public void testUpdateSigningCert_errorInLookup_assertServiceError()  throws Exception
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
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return null;
			}
			
			@Override
			protected String getBundleToUpdate()
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
	
	@Test
	public void testUpdateSigningCert_errorUpdate_assertServiceError()  throws Exception
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
					when(mockDAO.getTrustBundleByName("testBundle1")).thenReturn(new org.nhindirect.config.store.TrustBundle());
					doThrow(new RuntimeException()).when(mockDAO).updateTrustBundleSigningCertificate(eq(0L), eq((X509Certificate)null));
					
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
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return null;
			}
			
			@Override
			protected String getBundleToUpdate()
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
