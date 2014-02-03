package org.nhind.config.rest.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.DomainService;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhindirect.common.rest.OpenServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.resources.DomainResource;

import org.nhindirect.config.store.dao.DomainDao;

public class DefaultDomainService_deleteDomainTest 
{
	   protected DomainDao domainDao;
	    
		static DomainService resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					domainDao = (DomainDao)ConfigServiceRunner.getSpringApplicationContext().getBean("domainDao");
					
					resource = 	(DomainService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), new OpenServiceSecurityManager(), DOMAIN_SERVICE);	

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
			
			protected abstract String getDomainNameToRemove();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final Domain addDomain = getDomainToAdd();

				
				if (addDomain != null)
				{
					try
					{
						resource.addDomain(addDomain);
					}
					catch (ServiceException e)
					{
		
						throw e;
					}
				}
				
				try
				{
					resource.deleteDomain(getDomainNameToRemove());
				}
				catch (ServiceException e)
				{
					throw e;
				}
				
				doAssertions();
			}
			
			
			protected void doAssertions() throws Exception
			{
				
			}
		}	
		
		@Test
		public void testRemoveDomain_removeExistingDomain_assertDomainRemoved() throws Exception
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
				protected String getDomainNameToRemove()
				{
					return "test.com";
				}
				
				@Override
				protected void doAssertions() throws Exception
				{
					assertNull(domainDao.getDomainByName("@test.com"));
				}
			}.perform();
		}
		
		@Test
		public void testRemoveDomain_nonExxistentDomain_assertNotFound() throws Exception
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
				protected String getDomainNameToRemove()
				{
					return "test2.com";
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
		public void testRemoveDomain_errorInLookup_assertServerError() throws Exception
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
				
				@Override
				protected String getDomainNameToRemove()
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
		public void testRemoveDomain_errorInDelete_assertServerError() throws Exception
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
						when(mockDAO.getDomainByName((String)any())).thenReturn(new org.nhindirect.config.store.Domain());
						doThrow(new RuntimeException()).when(mockDAO).delete(eq("test.com"));
						
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
				
				@Override
				protected String getDomainNameToRemove()
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
