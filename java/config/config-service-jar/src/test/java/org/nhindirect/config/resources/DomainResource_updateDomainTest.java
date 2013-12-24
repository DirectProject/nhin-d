package org.nhindirect.config.resources;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

public class DomainResource_updateDomainTest 
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
		
		protected abstract Domain getDomainToUpdate();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Domain addDomain = getDomainToAdd();
			
			if (addDomain != null)
			{
				try
				{
					resource.path("/api/domain").entity(addDomain, MediaType.APPLICATION_JSON).put();
				}
				catch (UniformInterfaceException e)
				{
					throw e;
				}
			}
			
			try
			{
				resource.path("/api/domain").entity(getDomainToUpdate(), MediaType.APPLICATION_JSON).post();
			}
			catch (UniformInterfaceException e)
			{
				throw e;
			}
			
			try
			{
				final Domain getDomain = resource.path("/api/domain/" + TestUtils.uriEscape(getDomainToUpdate().getDomainName())).get(Domain.class);
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
	public void testUpdateDomain_updateExistingDomain_assertDomainUpdated() throws Exception
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
			
			protected Domain getDomainToUpdate()
			{				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.NEW);	
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
	public void testUpdateDomain_nonExistentDomain_assertNonFound() throws Exception
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
			
			protected Domain getDomainToUpdate()
			{				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test2.com");
				
				domain = new Domain();
				
				domain.setDomainName("test2.com");
				domain.setStatus(EntityStatus.NEW);	
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
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
	public void testUpdateDomain_errorInDomain_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;
			
			protected DomainResource domainService;

			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					domainService = (DomainResource)ConfigServiceRunner.getSpringApplicationContext().getBean("domainResource");

					DomainDao mockDAO = mock(DomainDao.class);
					doThrow(new RuntimeException()).when(mockDAO).getDomainByName(eq("test.com"));
					
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
				return null;
			}
			
			protected Domain getDomainToUpdate()
			{				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.NEW);	
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
	public void testUpdateDomain_errorInUpdate_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;
			
			protected DomainResource domainService;

			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					domainService = (DomainResource)ConfigServiceRunner.getSpringApplicationContext().getBean("domainResource");

					DomainDao mockDAO = mock(DomainDao.class);
					when(mockDAO.getDomainByName((String)any())).thenReturn(new org.nhindirect.config.store.Domain());
					doThrow(new RuntimeException()).when(mockDAO).update((org.nhindirect.config.store.Domain)any());
					
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
				return null;
			}
			
			protected Domain getDomainToUpdate()
			{				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.NEW);	
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
