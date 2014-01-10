package org.nhindirect.config.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.store.dao.DomainDao;
import org.nhindirect.config.store.dao.TrustBundleDao;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class TrustBundleResource_disassociateTrustBundleFromDomainsTest 
{
	   protected TrustBundleDao bundleDao;
	   protected DomainDao domainDao; 
	   
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			protected Collection<TrustBundle> bundles;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					bundleDao = (TrustBundleDao)ConfigServiceRunner.getSpringApplicationContext().getBean("trustBundleDao");
					domainDao =  (DomainDao)ConfigServiceRunner.getSpringApplicationContext().getBean("domainDao");
					resource = 	getResource(ConfigServiceRunner.getConfigServiceURL());		
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
							resource.path("/api/trustbundle").entity(addBundle, MediaType.APPLICATION_JSON).put(addBundle);
						}
						catch (UniformInterfaceException e)
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
						resource.path("/api/domain").entity(addDomain, MediaType.APPLICATION_JSON).put(addDomain);
					}
					catch (UniformInterfaceException e)
					{
						throw e;
					}
				}
				
				// associate the bundle and domain
				if (bundlesToAdd != null && addDomain != null)
					resource.path("/api/trustbundle/" + TestUtils.uriEscape(getBundleNameToAssociate()) + "/" + TestUtils.uriEscape(getDomainNameToAssociate())).post();

				// disassociate the domain from all bundles
				resource.path("/api/trustbundle/" +  TestUtils.uriEscape(getBundleNameToDisassociate()) + "/deleteFromBundle").delete();

				doAssertions();

			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}	
		
		@Test
		public void testDisassociateBundleFromDomains_disassociateExistingDomainAndBundle_assertBundlesDisassociated()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				protected void doAssertions() throws Exception
				{
					final Collection<org.nhindirect.config.store.TrustBundleDomainReltn> bundleRelts =  
							bundleDao.getTrustBundlesByDomain(domainDao.getDomainByName("test.com").getId());
					
					assertTrue(bundleRelts.isEmpty());
					
				}
			}.perform();
		}
		
		@Test
		public void testDisassociateBundleFromDomains_nonexistantBundle_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle12";
				}
				
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(404, ex.getResponse().getStatus());
				}
			}.perform();
		}
		
		
		@Test
		public void testDisassociateBundleFromDomains_errorInBundleLookup_assertServiceError()  throws Exception
		{
			new TestPlan()
			{
				
				protected TrustBundleResource bundleService;
				
				@Override
				protected Collection<TrustBundle> getBundlesToAdd()
				{
					return null;
				}
				
				@Override
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
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}		
		
		@Test
		public void testDisassociateBundleFromDomains_errorInDisassociate_assertServiceError()  throws Exception
		{
			new TestPlan()
			{
				
				protected TrustBundleResource bundleService;
				
				@Override
				protected Collection<TrustBundle> getBundlesToAdd()
				{
					return null;
				}
				
				@Override
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
						
						when(mockBundleDAO.getTrustBundleByName(getBundleNameToDisassociate())).thenReturn(new org.nhindirect.config.store.TrustBundle());
						doThrow(new RuntimeException()).when(mockBundleDAO).disassociateTrustBundleFromDomains(eq(0L));
						
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
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}			
}
