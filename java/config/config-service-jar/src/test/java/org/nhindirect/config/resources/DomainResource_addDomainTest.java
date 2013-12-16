package org.nhindirect.config.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.store.dao.DomainDao;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class DomainResource_addDomainTest 
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

		protected abstract Domain getDomainToAdd();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Domain addDomain = getDomainToAdd();
		
			
			try
			{
				resource.path("/api/domain").entity(addDomain, MediaType.APPLICATION_JSON).put(addDomain);
			}
			catch (UniformInterfaceException e)
			{
				throw e;
			}
			
			try
			{
				final Domain getDomain = resource.path("/api/domain/" + TestUtils.uriEscape(addDomain.getDomainName())).get(Domain.class);
				doAssertions(getDomain);
			}
			catch (UniformInterfaceException e)
			{
				throw e;
			}
			
		}
		
		
		protected void doAssertions(Domain domain) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testAddDomain_addNewDomain_assertDomainCreated() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;
			
			@Override
			protected Domain getDomainToAdd()
			{
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
			}
			
			@Override
			protected void doAssertions(Domain domain) throws Exception
			{
				assertNotNull(domain);
				assertEquals(this.domain.getDomainName(), domain.getDomainName());
				assertEquals(this.domain.getStatus(), domain.getStatus());
				assertEquals(this.domain.getPostmasterAddress().getEmailAddress(), domain.getPostmasterAddress().getEmailAddress());
			}
		}.perform();
	}
		
	@Test
	public void testAddDomain_addNewDomain_alreadyExists_assertConfilic() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;
			
			@Override
			protected void setupMocks()
			{
				super.setupMocks();
				
				org.nhindirect.config.store.Domain domain = new org.nhindirect.config.store.Domain();
				domain.setDomainName("test.com");
				domain.setStatus(org.nhindirect.config.store.EntityStatus.ENABLED);
				domainDao.add(domain);
				
			}
			
			@Override
			protected Domain getDomainToAdd()
			{
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
			}

			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof UniformInterfaceException);
				UniformInterfaceException ex = (UniformInterfaceException)exception;
				assertEquals(409, ex.getResponse().getStatus());
			}
		}.perform();
	}	
	
	
	@Test
	public void testAddDomain_errorInLookup_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;
			
			DomainResource domainService;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					domainService = (DomainResource)ConfigServiceRunner.getSpringApplicationContext().getBean("domainResource");

					DomainDao mockDAO = mock(DomainDao.class);
					doThrow(new RuntimeException()).when(mockDAO).getDomainByName((String)any());
					
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
			protected Domain getDomainToAdd()
			{
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
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
	public void testAddDomain_errorInAdd_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;
			
			DomainResource domainService;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					domainService = (DomainResource)ConfigServiceRunner.getSpringApplicationContext().getBean("domainResource");

					DomainDao mockDAO = mock(DomainDao.class);
					doThrow(new RuntimeException()).when(mockDAO).add((org.nhindirect.config.store.Domain)any());
					
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
			protected Domain getDomainToAdd()
			{
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
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
