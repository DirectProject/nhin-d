package org.nhindirect.config.resources;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;

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

public class TrustBundleResource_associateTrustBundleToDomainTest 
{
	   protected TrustBundleDao bundleDao;
	   protected DomainDao domainDao; 
	   
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
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

			protected abstract Collection<TrustBundle> getBundlesToAdd();
			
			protected abstract Domain getDomainToAdd();
			
			protected abstract String getBundleNameToAssociate();
			
			protected abstract String getDomainNameToAssociate();
			
			protected String isIncoming()
			{
				return "true";
			}
			
			protected String isOutgoing()
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
				

				resource.path("/api/trustbundle/" + TestUtils.uriEscape(getBundleNameToAssociate()) + "/" + TestUtils.uriEscape(getDomainNameToAssociate())).
					queryParam("incoming", isIncoming()).queryParam("outgoing", isOutgoing()).post();

				doAssertions();

			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}	
		
		@Test
		public void testAssociateBundleToDomain_associateExistingDomainAndBundle_assertBundlesAssociated()  throws Exception
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
				
				protected void doAssertions() throws Exception
				{
					
					final Collection<org.nhindirect.config.store.TrustBundleDomainReltn> bundleRelts =  
							bundleDao.getTrustBundlesByDomain(domainDao.getDomainByName(getDomainNameToAssociate()).getId());
					
					assertEquals(1, bundleRelts.size());
					
					final org.nhindirect.config.store.TrustBundleDomainReltn bundleReltn = bundleRelts.iterator().next();
					assertEquals(getDomainNameToAssociate(), bundleReltn.getDomain().getDomainName());
					assertEquals(getBundleNameToAssociate(), bundleReltn.getTrustBundle().getBundleName());
					assertTrue(bundleReltn.isIncoming());
					assertTrue(bundleReltn.isOutgoing());
				}
			}.perform();
		}	
		
		@Test
		public void testAssociateBundleToDomain_associateExistingDomainAndBundle_falseIncomingAndOutgoing_assertBundlesAssociated()  throws Exception
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
				
				protected String isIncoming()
				{
					return "false";
				}
				
				protected String isOutgoing()
				{
					return "false";
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
				
				protected void doAssertions() throws Exception
				{
					
					final Collection<org.nhindirect.config.store.TrustBundleDomainReltn> bundleRelts =  
							bundleDao.getTrustBundlesByDomain(domainDao.getDomainByName(getDomainNameToAssociate()).getId());
					
					assertEquals(1, bundleRelts.size());
					
					final org.nhindirect.config.store.TrustBundleDomainReltn bundleReltn = bundleRelts.iterator().next();
					assertEquals(getDomainNameToAssociate(), bundleReltn.getDomain().getDomainName());
					assertEquals(getBundleNameToAssociate(), bundleReltn.getTrustBundle().getBundleName());
					assertFalse(bundleReltn.isIncoming());
					assertFalse(bundleReltn.isOutgoing());
				}
			}.perform();
		}	
		
		@Test
		public void testAssociateBundleToDomain_unknownDomain_assertNotFound()  throws Exception
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
					return "test2.com";
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
		public void testAssociateBundleToDomain_unknownBundle_assertNotFound()  throws Exception
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
					return "testBundle2";
				}
				
				@Override
				protected String getDomainNameToAssociate()
				{
					return "test.com";
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
		public void testAssociateBundleToDomain_errorInBundleLookup_assertServiceError()  throws Exception
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
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}	
		
		
		@Test
		public void testAssociateBundleToDomain_errorInDomainLookup_assertServiceError()  throws Exception
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
						when(mockBundleDAO.getTrustBundleByName("testBundle1")).thenReturn(new org.nhindirect.config.store.TrustBundle());
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
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}	
		
		@Test
		public void testAssociateBundleToDomain_errorInAssociate_assertServiceError()  throws Exception
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
						when(mockBundleDAO.getTrustBundleByName("testBundle1")).thenReturn(new org.nhindirect.config.store.TrustBundle());
						when(mockDomainDAO.getDomainByName("test.com")).thenReturn(new org.nhindirect.config.store.Domain());
						doThrow(new RuntimeException()).when(mockBundleDAO).associateTrustBundleToDomain(eq(0L), eq(0L), eq(true), eq(true));
						
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
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}		
		
		
}
