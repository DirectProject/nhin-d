package org.nhind.config.rest.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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
import org.nhindirect.config.resources.TrustBundleResource;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.TrustBundleDao;

public class DefaultTrustBundleService_disassociateTrustBundleFromDomainTest 
{
	   protected TrustBundleDao bundleDao;
	   
	   protected DomainDao domainDao; 
	   
		static TrustBundleService resource;
		
		static DomainService domainResource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			protected Collection<TrustBundle> bundles;
			
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
			
			protected String getBundleNameToAssociate()
			{
				return "testBundle1";
			}
			
			protected String getDomainNameToAssociate()
			{
				return "test.com";
			}
			
			protected abstract String getBundleNameToDisassociate();
			
			protected abstract String getDomainNameToDisassociate();
			
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
				
				// associate the bundle and domain
				if (bundlesToAdd != null && addDomain != null)
					resource.associateTrustBundleToDomain(getBundleNameToAssociate(), getDomainNameToAssociate(), true, true);

				resource.disassociateTrustBundleFromDomain(getBundleNameToDisassociate(), getDomainNameToDisassociate());

				doAssertions();

			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}	
		
		@Test
		public void testDisassociateBundleFromDomain_disassociateExistingDomainAndBundle_assertBundlesDisassociated()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
				{
					return "test.com";
				}
				
				protected void doAssertions() throws Exception
				{
					final Collection<org.nhindirect.config.store.TrustBundleDomainReltn> bundleRelts =  
							bundleDao.getTrustBundlesByDomain(domainDao.getDomainByName(getDomainNameToDisassociate()).getId());
					
					assertTrue(bundleRelts.isEmpty());
					
				}
			}.perform();
		}	
		
		@Test
		public void testDisassociateBundleFromDomain_unknownBundle_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1333";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
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
		public void testDisassociateBundleFromDomain_unknownDomain_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
				{
					return "test.com123";
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
		public void testDisassociateBundleFromDomain_errorInBundleLookup_assertServiceError()  throws Exception
		{
			new TestPlan()
			{
				
				protected TrustBundleResource bundleService;
				
				protected Collection<TrustBundle> getBundlesToAdd()
				{
					return null;
				}
				
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						bundleService = (TrustBundleResource)ConfigServiceRunner.getSpringApplicationContext().getBean("trustBundleResource");

						TrustBundleDao mockBundleDAO = mock(TrustBundleDao.class);
						DomainDao mockDomainDAO = mock(DomainDao.class);
						
						doThrow(new RuntimeException()).when(mockBundleDAO).getTrustBundleByName((String)any());
						
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
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
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
		public void testDisassociateBundleFromDomain_errorInDomainLookup_assertServiceError()  throws Exception
		{
			new TestPlan()
			{
				
				protected TrustBundleResource bundleService;
				
				protected Collection<TrustBundle> getBundlesToAdd()
				{
					return null;
				}
				
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						bundleService = (TrustBundleResource)ConfigServiceRunner.getSpringApplicationContext().getBean("trustBundleResource");

						TrustBundleDao mockBundleDAO = mock(TrustBundleDao.class);
						DomainDao mockDomainDAO = mock(DomainDao.class);
						
						when(mockBundleDAO.getTrustBundleByName("testBundle1")).thenReturn(new org.nhindirect.config.store.TrustBundle());
						doThrow(new RuntimeException()).when(mockDomainDAO).getDomainByName((String)any());
						
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
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
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
		public void testDisassociateBundleFromDomain_errorInDisassociate_assertServiceError()  throws Exception
		{
			new TestPlan()
			{
				
				protected TrustBundleResource bundleService;
				
				protected Collection<TrustBundle> getBundlesToAdd()
				{
					return null;
				}
				
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						bundleService = (TrustBundleResource)ConfigServiceRunner.getSpringApplicationContext().getBean("trustBundleResource");

						TrustBundleDao mockBundleDAO = mock(TrustBundleDao.class);
						DomainDao mockDomainDAO = mock(DomainDao.class);
						
						when(mockBundleDAO.getTrustBundleByName("testBundle1")).thenReturn(new org.nhindirect.config.store.TrustBundle());
						when(mockDomainDAO.getDomainByName("test.com")).thenReturn(new org.nhindirect.config.store.Domain());
						doThrow(new RuntimeException()).when(mockBundleDAO).disassociateTrustBundleFromDomain(eq(0L), eq(0L));
						
						
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
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
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
