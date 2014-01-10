package org.nhindirect.config.resources;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class TrustBundleResource_getTrustBundlesByDomainTest 
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
			
			protected abstract String getDomainNameToRetrieve();
			
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
				

				// associate bundle to domain
				if (addDomain != null && bundlesToAdd != null)
					resource.path("/api/trustbundle/" + TestUtils.uriEscape(getBundleNameToAssociate()) + "/" + TestUtils.uriEscape(getDomainNameToAssociate())).
						queryParam("incoming", "true").queryParam("outgoing", "true").post();

				
				try
				{
					
					final GenericType<ArrayList<TrustBundle>> genType = new GenericType<ArrayList<TrustBundle>>(){};
					final Collection<TrustBundle> getBundles = resource.path("/api/trustbundle/domains/" + TestUtils.uriEscape(getDomainNameToRetrieve())).
							queryParam("fetchAnchors", getFetchAnchors()).get(genType);

					doAssertions(getBundles);
				}
				catch (UniformInterfaceException e)
				{
					if (e.getResponse().getStatus() == 204)
						doAssertions(new ArrayList<TrustBundle>());
					else
						throw e;
				}

			}
				
			protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testGetBundlesByDomain_assertBundlesRetrieved()  throws Exception
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
				
				@Override
				protected String getDomainNameToRetrieve()
				{
					return "test.com";
				}
				
				
				protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
				{
					assertNotNull(bundles);
					assertEquals(1, bundles.size());
					
					final Iterator<TrustBundle> addedBundlesIter = this.bundles.iterator();
					
					for (TrustBundle retrievedBundle : bundles)
					{	
						final TrustBundle addedBundle = addedBundlesIter.next(); 
						
						assertEquals(addedBundle.getBundleName(), retrievedBundle.getBundleName());
						assertEquals(addedBundle.getBundleURL(), retrievedBundle.getBundleURL());
						assertEquals(addedBundle.getRefreshInterval(), retrievedBundle.getRefreshInterval());
						assertNull(retrievedBundle.getSigningCertificateData());
					}
				}
			}.perform();
		}		
		
		@Test
		public void testGetBundlesByDomain_suppressAnchors_assertBundlesRetrievedWithNoAnchors()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<TrustBundle> bundles;
				
				@Override
				protected String getFetchAnchors()
				{
					return "false";
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
				
				
				protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
				{
					assertNotNull(bundles);
					assertEquals(1, bundles.size());
					
					final Iterator<TrustBundle> addedBundlesIter = this.bundles.iterator();
					
					for (TrustBundle retrievedBundle : bundles)
					{	
						final TrustBundle addedBundle = addedBundlesIter.next(); 
						
						assertEquals(addedBundle.getBundleName(), retrievedBundle.getBundleName());
						assertEquals(addedBundle.getBundleURL(), retrievedBundle.getBundleURL());
						assertEquals(addedBundle.getRefreshInterval(), retrievedBundle.getRefreshInterval());
						assertNull(retrievedBundle.getSigningCertificateData());
						assertTrue(retrievedBundle.getTrustBundleAnchors().isEmpty());
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
				protected Collection<TrustBundle> getBundlesToAdd()
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
				
				
				protected void doAssertions(Collection<TrustBundle> bundles) throws Exception
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
				protected String getDomainNameToRetrieve()
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
				protected String getDomainNameToRetrieve()
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
				protected String getDomainNameToRetrieve()
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
