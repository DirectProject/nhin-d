package org.nhind.config.rest.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.DomainService;
import org.nhind.config.rest.TrustBundleService;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.model.TrustBundleDomainReltn;
import org.nhindirect.config.resources.TrustBundleResource;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.TrustBundleDao;

public class DefaultTrustBundleService_getTrustBundlesByDomainTest 
{
	   protected TrustBundleDao bundleDao;
	   
	   protected DomainDao domainDao; 
	   
		static TrustBundleService resource;
		
		static DomainService domainResource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					bundleDao = (TrustBundleDao)ConfigServiceRunner.getSpringApplicationContext().getBean("trustBundleDao");
					domainDao = (DomainDao)ConfigServiceRunner.getSpringApplicationContext().getBean("domainDao");
					
					resource = 	(TrustBundleService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), TRUST_BUNDLE_SERVICE);	
					domainResource = (DomainService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), DOMAIN_SERVICE);	

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

			
			protected abstract Collection<TrustBundleDomainReltn> getBundlesToAdd();
			
			protected abstract Domain getDomainToAdd();
			
			protected abstract String getBundleNameToAssociate();
			
			protected abstract String getDomainNameToAssociate();
			
			protected abstract String getDomainNameToRetrieve();
			
			protected String getFetchAnchors()
			{
				return "true";
			}
			
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final Collection<TrustBundleDomainReltn> bundlesToAdd = getBundlesToAdd();
				
				if (bundlesToAdd != null)
				{
					for (TrustBundleDomainReltn addBundle : bundlesToAdd)
					{
						try
						{
							resource.addTrustBundle(addBundle.getTrustBundle());
						}
						catch (ServiceException e)
						{
							throw e;
						}
					}
				}
				
				final Domain addDomain = getDomainToAdd();
				
				if (addDomain != null)
				{
					try
					{
						domainResource.addDomain(addDomain);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
				

				// associate bundle to domain
				if (addDomain != null && bundlesToAdd != null)
					resource.associateTrustBundleToDomain(getBundleNameToAssociate(), getDomainNameToAssociate(), true, true);
				
				try
				{
					
					final Collection<TrustBundleDomainReltn> getBundles = resource.getTrustBundlesByDomain(getDomainNameToRetrieve(), Boolean.parseBoolean(getFetchAnchors()));

					doAssertions(getBundles);
				}
				catch (ServiceMethodException e)
				{
					if (e.getResponseCode() == 204)
						doAssertions(new ArrayList<TrustBundleDomainReltn>());
					else
						throw e;
				}

			}
				
			protected void doAssertions(Collection<TrustBundleDomainReltn> bundles) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testGetBundlesByDomain_assertBundlesRetrieved()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<TrustBundleDomainReltn> bundles;
				
				@Override
				protected Collection<TrustBundleDomainReltn> getBundlesToAdd()
				{
					try
					{
						bundles = new ArrayList<TrustBundleDomainReltn>();
						
						TrustBundle bundle = new TrustBundle();
						bundle.setBundleName("testBundle1");
						File fl = new File("src/test/resources/bundles/providerTestBundle.p7b");
						bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
						bundle.setRefreshInterval(24);
						bundle.setSigningCertificateData(null);		
						
						TrustBundleDomainReltn reltn = new TrustBundleDomainReltn();
						reltn.setDomain(new Domain());
						reltn.setTrustBundle(bundle);
						
						bundles.add(reltn);
			
						
						return bundles;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
				}

				@Override
				protected Domain getDomainToAdd()
				{
					final Address postmasterAddress = new Address();
					postmasterAddress.setEmailAddress("me@test.com");
					
					Domain domain = new Domain();
					
					domain.setDomainName("test.com");
					domain.setStatus(EntityStatus.ENABLED);
					domain.setPostmasterAddress(postmasterAddress);			
					
					return domain;
				}
				
				@Override
				protected String getBundleNameToAssociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected String getDomainNameToRetrieve()
				{
					return "test.com";
				}
				
				
				protected void doAssertions(Collection<TrustBundleDomainReltn> bundles) throws Exception
				{
					assertNotNull(bundles);
					assertEquals(1, bundles.size());
					
					final Iterator<TrustBundleDomainReltn> addedBundlesIter = this.bundles.iterator();
					
					for (TrustBundleDomainReltn retrievedBundle : bundles)
					{	
						final TrustBundleDomainReltn addedBundle = addedBundlesIter.next(); 
						
						assertEquals(addedBundle.getTrustBundle().getBundleName(), retrievedBundle.getTrustBundle().getBundleName());
						assertEquals(addedBundle.getTrustBundle().getBundleURL(), retrievedBundle.getTrustBundle().getBundleURL());
						assertEquals(addedBundle.getTrustBundle().getRefreshInterval(), retrievedBundle.getTrustBundle().getRefreshInterval());
						assertNull(retrievedBundle.getTrustBundle().getSigningCertificateData());
					}
				}
			}.perform();
		}		
		
		@Test
		public void testGetBundlesByDomain_suppressAnchors_assertBundlesRetrievedWithNoAnchors()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<TrustBundleDomainReltn> bundles;
				
				@Override
				protected String getFetchAnchors()
				{
					return "false";
				}
				
				@Override
				protected Collection<TrustBundleDomainReltn> getBundlesToAdd()
				{
					try
					{
						bundles = new ArrayList<TrustBundleDomainReltn>();
						
						TrustBundle bundle = new TrustBundle();
						bundle.setBundleName("testBundle1");
						File fl = new File("src/test/resources/bundles/providerTestBundle.p7b");
						bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
						bundle.setRefreshInterval(24);
						bundle.setSigningCertificateData(null);		
						
						TrustBundleDomainReltn reltn = new TrustBundleDomainReltn();
						reltn.setTrustBundle(bundle);
						reltn.setDomain(new Domain());
						
						bundles.add(reltn);
			
						
						return bundles;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
				}

				@Override
				protected Domain getDomainToAdd()
				{
					final Address postmasterAddress = new Address();
					postmasterAddress.setEmailAddress("me@test.com");
					
					Domain domain = new Domain();
					
					domain.setDomainName("test.com");
					domain.setStatus(EntityStatus.ENABLED);
					domain.setPostmasterAddress(postmasterAddress);			
					
					return domain;
				}
				
				@Override
				protected String getBundleNameToAssociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected String getDomainNameToRetrieve()
				{
					return "test.com";
				}
				
				
				protected void doAssertions(Collection<TrustBundleDomainReltn> bundles) throws Exception
				{
					assertNotNull(bundles);
					assertEquals(1, bundles.size());
					
					final Iterator<TrustBundleDomainReltn> addedBundlesIter = this.bundles.iterator();
					
					for (TrustBundleDomainReltn retrievedBundle : bundles)
					{	
						final TrustBundleDomainReltn addedBundle = addedBundlesIter.next(); 
						
						assertEquals(addedBundle.getTrustBundle().getBundleName(), retrievedBundle.getTrustBundle().getBundleName());
						assertEquals(addedBundle.getTrustBundle().getBundleURL(), retrievedBundle.getTrustBundle().getBundleURL());
						assertEquals(addedBundle.getTrustBundle().getRefreshInterval(), retrievedBundle.getTrustBundle().getRefreshInterval());
						assertNull(retrievedBundle.getTrustBundle().getSigningCertificateData());
						assertTrue(retrievedBundle.getTrustBundle().getTrustBundleAnchors().isEmpty());
					}
				}
			}.perform();
		}	
		
		@Test
		public void testGetBundlesByDomain_noBundlesInDomain_assertNoBundlesRetrieved()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<TrustBundleDomainReltn> getBundlesToAdd()
				{
					return null;
				}

				@Override
				protected Domain getDomainToAdd()
				{
					final Address postmasterAddress = new Address();
					postmasterAddress.setEmailAddress("me@test.com");
					
					Domain domain = new Domain();
					
					domain.setDomainName("test.com");
					domain.setStatus(EntityStatus.ENABLED);
					domain.setPostmasterAddress(postmasterAddress);			
					
					return domain;
				}
				
				@Override
				protected String getBundleNameToAssociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected String getDomainNameToRetrieve()
				{
					return "test.com";
				}
				
				
				protected void doAssertions(Collection<TrustBundleDomainReltn> bundles) throws Exception
				{
					assertNotNull(bundles);
					assertEquals(0, bundles.size());

				}
			}.perform();
		}		
		
		@Test
		public void testGetBundlesByDomain_unknownDomain_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<TrustBundleDomainReltn> getBundlesToAdd()
				{
					return null;
				}

				@Override
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected String getBundleNameToAssociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected String getDomainNameToRetrieve()
				{
					return "test.com";
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
		public void testGetBundlesByDomain_errorInDomainLookup_assertServiceError()  throws Exception
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

						TrustBundleDao mockBundleDAO = mock(TrustBundleDao.class);
						DomainDao mockDomainDAO = mock(DomainDao.class);
						doThrow(new RuntimeException()).when(mockDomainDAO).getDomainByName(eq("test.com"));
						
						bundleService.setTrustBundleDao(mockBundleDAO);
						bundleService.setDomainDao(mockDomainDAO);
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
					bundleService.setDomainDao(domainDao);
				}	
				
				@Override
				protected Collection<TrustBundleDomainReltn> getBundlesToAdd()
				{
					return null;
				}

				@Override
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected String getBundleNameToAssociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected String getDomainNameToRetrieve()
				{
					return "test.com";
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
		public void testGetBundlesByDomain_errorInGroupLookup_assertServiceError()  throws Exception
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

						TrustBundleDao mockBundleDAO = mock(TrustBundleDao.class);
						DomainDao mockDomainDAO = mock(DomainDao.class);
						when(mockDomainDAO.getDomainByName("test.com")).thenReturn(new org.nhindirect.config.store.Domain());
						doThrow(new RuntimeException()).when(mockBundleDAO).getTrustBundlesByDomain(eq(0L));
						
						bundleService.setTrustBundleDao(mockBundleDAO);
						bundleService.setDomainDao(mockDomainDAO);
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
					bundleService.setDomainDao(domainDao);
				}	
				
				@Override
				protected Collection<TrustBundleDomainReltn> getBundlesToAdd()
				{
					return null;
				}

				@Override
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected String getBundleNameToAssociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToAssociate()
				{
					return "test.com";
				}
				
				@Override
				protected String getDomainNameToRetrieve()
				{
					return "test.com";
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
