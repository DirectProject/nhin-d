package org.nhindirect.config.resources;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

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
import org.nhindirect.config.store.dao.DomainDao;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class DomainResource_searchDomainTest 
{
    protected DomainDao domainDao;
    
	static WebResource resource;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				domainDao = (DomainDao)ConfigServiceRunner.getSpringApplicationContext().getBean("domainDao");
				
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
		
		protected abstract Collection<Domain> getDomainsToAdd();
		
		protected abstract String getDomainNameToSearch();
		
		protected abstract String getEntityStatusToSearch();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<Domain> addDomains = getDomainsToAdd();
			
			if (addDomains != null)
			{
				for (Domain addDomain : addDomains)
				try
				{
					resource.path("/api/domain").entity(addDomain, MediaType.APPLICATION_JSON).put(addDomain);
				}
				catch (UniformInterfaceException e)
				{
	
					throw e;
				}
			}
			
			try
			{
				GenericType<ArrayList<Domain>> genType = new GenericType<ArrayList<Domain>>(){};
				
				String entityStatus = (getEntityStatusToSearch() != null) ? getEntityStatusToSearch() : "";
				
				final Collection<Domain> getDomains = resource.path("/api/domain/").queryParam("domainName", TestUtils.uriEscape(getDomainNameToSearch())).
						queryParam("entityStatus", entityStatus).get(genType);
				
				doAssertions(getDomains);
			}
			catch (UniformInterfaceException e)
			{
				
				if (e.getResponse().getStatus() == 404 || e.getResponse().getStatus() == 204)
					doAssertions(new ArrayList<Domain>());
				else
					throw e;
			}
			
		}
		
		
		protected void doAssertions(Collection<Domain> domains) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testSearchDomains_getExistingDomain_nullEntityStatus_assertDomainRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Domain> domains;
			
			@Override
			protected Collection<Domain> getDomainsToAdd()
			{
				domains = new ArrayList<Domain>();
				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				Domain domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);
				
				return domains;
			}
			
			@Override
			protected String getDomainNameToSearch()
			{
				return "test.com";
			}
			
			protected String getEntityStatusToSearch()
			{
				return null;
			}
			
			@Override
			protected void doAssertions(Collection<Domain> domains) throws Exception
			{
				assertNotNull(domains);
				assertEquals(1, domains.size());
				
				Domain retrievedDomain = domains.iterator().next();
				Domain addedDomain = this.domains.iterator().next();
				
				assertEquals(addedDomain.getDomainName(), retrievedDomain.getDomainName());
				assertEquals(addedDomain.getStatus(), retrievedDomain.getStatus());
				assertEquals(addedDomain.getPostmasterAddress().getEmailAddress(), retrievedDomain.getPostmasterAddress().getEmailAddress());
			}
		}.perform();
	}	
	
	@Test
	public void testSearchDomains_getExistingDomain_invalidEntityStatus_assertDomainRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Domain> domains;
			
			@Override
			protected Collection<Domain> getDomainsToAdd()
			{
				domains = new ArrayList<Domain>();
				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				Domain domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);
				
				return domains;
			}
			
			@Override
			protected String getDomainNameToSearch()
			{
				return "test.com";
			}
			
			protected String getEntityStatusToSearch()
			{
				return "invalid";
			}
			
			@Override
			protected void doAssertions(Collection<Domain> domains) throws Exception
			{
				assertNotNull(domains);
				assertEquals(1, domains.size());
				
				Domain retrievedDomain = domains.iterator().next();
				Domain addedDomain = this.domains.iterator().next();
				
				assertEquals(addedDomain.getDomainName(), retrievedDomain.getDomainName());
				assertEquals(addedDomain.getStatus(), retrievedDomain.getStatus());
				assertEquals(addedDomain.getPostmasterAddress().getEmailAddress(), retrievedDomain.getPostmasterAddress().getEmailAddress());
			}
		}.perform();
	}		
	
	@Test
	public void testSearchDomains_getExistingDomain_newEntityStatus_assertNoDomainRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Domain> domains;
			
			@Override
			protected Collection<Domain> getDomainsToAdd()
			{
				domains = new ArrayList<Domain>();
				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				Domain domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);
				
				return domains;
			}
			
			@Override
			protected String getDomainNameToSearch()
			{
				return "test.com";
			}
			
			protected String getEntityStatusToSearch()
			{
				return "NEW";
			}
			
			@Override
			protected void doAssertions(Collection<Domain> domains) throws Exception
			{
				assertNotNull(domains);
				assertEquals(0, domains.size());
				
			}
		}.perform();
	}	
	
	@Test
	public void testSearchDomains_getNonExistantDomain_assertNoDomainRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Domain> domains;
			
			@Override
			protected Collection<Domain> getDomainsToAdd()
			{
				domains = new ArrayList<Domain>();
				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				Domain domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);
				
				return domains;
			}
			
			@Override
			protected String getDomainNameToSearch()
			{
				return "test2.com";
			}
			
			protected String getEntityStatusToSearch()
			{
				return "";
			}
			
			@Override
			protected void doAssertions(Collection<Domain> domains) throws Exception
			{
				assertNotNull(domains);
				assertEquals(0, domains.size());
				
			}
		}.perform();
	}		
	
	@Test
	public void testSearchDomains_getExistingDomain_emptySearchString_assertDomainRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Domain> domains;
			
			@Override
			protected Collection<Domain> getDomainsToAdd()
			{
				domains = new ArrayList<Domain>();
				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				Domain domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);
				
				return domains;
			}
			
			@Override
			protected String getDomainNameToSearch()
			{
				return "";
			}
			
			protected String getEntityStatusToSearch()
			{
				return "ENABLED";
			}
			
			@Override
			protected void doAssertions(Collection<Domain> domains) throws Exception
			{
				assertNotNull(domains);
				assertEquals(1, domains.size());
				
				Domain retrievedDomain = domains.iterator().next();
				Domain addedDomain = this.domains.iterator().next();
				
				assertEquals(addedDomain.getDomainName(), retrievedDomain.getDomainName());
				assertEquals(addedDomain.getStatus(), retrievedDomain.getStatus());
				assertEquals(addedDomain.getPostmasterAddress().getEmailAddress(), retrievedDomain.getPostmasterAddress().getEmailAddress());
				
			}
		}.perform();
	}		
	
	@Test
	public void testSearchDomains_getExistingDomains_emptySearchString_assertDomainsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Domain> domains;
			
			@Override
			protected Collection<Domain> getDomainsToAdd()
			{
				domains = new ArrayList<Domain>();
				
				// domain 1
				Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				Domain domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);
				
				// domain 2
				postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test2.com");
				
				domain = new Domain();
				
				domain.setDomainName("test2.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);				
				
				return domains;
			}
			
			@Override
			protected String getDomainNameToSearch()
			{
				return "";
			}
			
			protected String getEntityStatusToSearch()
			{
				return null;
			}
			
			@Override
			protected void doAssertions(Collection<Domain> domains) throws Exception
			{
				assertNotNull(domains);
				assertEquals(2, domains.size());
				
			}
		}.perform();
	}	
	
	@Test
	public void testSearchDomains_getExistingDomains_emptySearchString_enabledOnly_assertDomainRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Domain> domains;
			
			@Override
			protected Collection<Domain> getDomainsToAdd()
			{
				domains = new ArrayList<Domain>();
				
				// domain 1
				Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				Domain domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.NEW);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);
				
				// domain 2
				postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test2.com");
				
				domain = new Domain();
				
				domain.setDomainName("test2.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				domains.add(domain);				
				
				return domains;
			}
			
			@Override
			protected String getDomainNameToSearch()
			{
				return "";
			}
			
			protected String getEntityStatusToSearch()
			{
				return "ENABLED";
			}
			
			@Override
			protected void doAssertions(Collection<Domain> domains) throws Exception
			{
				assertNotNull(domains);
				assertEquals(1, domains.size());
				
				assertNotNull(domains);
				assertEquals(1, domains.size());
				
				Domain retrievedDomain = domains.iterator().next();
				
				assertEquals("test2.com", retrievedDomain.getDomainName());

			}
		}.perform();
	}
	
	@Test
	public void testSearchDomains_errorInSearch_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected DomainResource domainService;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					domainService = (DomainResource)ConfigServiceRunner.getSpringApplicationContext().getBean("domainResource");

					DomainDao mockDAO = mock(DomainDao.class);
					doThrow(new RuntimeException()).when(mockDAO).searchDomain(eq("test.com"), eq((org.nhindirect.config.store.EntityStatus)null));
					
					domainService.setDomainDao(mockDAO);
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
				
				domainService.setDomainDao(domainDao);
			}
			
			@Override
			protected Collection<Domain> getDomainsToAdd()
			{
				return null;
			}
			
			@Override
			protected String getDomainNameToSearch()
			{
				return "test.com";
			}
			
			protected String getEntityStatusToSearch()
			{
				return null;
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
