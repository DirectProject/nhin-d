package org.nhind.config.rest.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.AddressService;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.resources.AddressResource;

import org.nhindirect.config.store.dao.AddressDao;
import org.nhindirect.config.store.dao.DomainDao;


public class DefaultAddressService_deleteAddressTest 
{
	   protected AddressDao addressDao;
	    
	    protected DomainDao domainDao;
	    
		static AddressService resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					addressDao = (AddressDao)ConfigServiceRunner.getSpringApplicationContext().getBean("addressDaoImpl");
					domainDao =  (DomainDao)ConfigServiceRunner.getSpringApplicationContext().getBean("domainDao");
					
					resource = 	(AddressService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), ADDRESS_SERVICE);	

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
			
			protected abstract Address getAddressToAdd();
			
			protected abstract String getDomainToAdd();
			
			protected abstract String getAddressNameToRemove();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final Address addAddress = getAddressToAdd();
				final String domainName = getDomainToAdd();
				
				if (domainName != null && !domainName.isEmpty())
				{
					final org.nhindirect.config.store.Domain domain = new org.nhindirect.config.store.Domain();
					domain.setDomainName(domainName);
					domain.setStatus(org.nhindirect.config.store.EntityStatus.ENABLED);
					domainDao.add(domain);
					
					if (addAddress != null)
						addAddress.setDomainName(domainName);
				}
				
				if (addAddress != null)
				{
					try
					{
						resource.addAddress(addAddress);
					}
					catch (ServiceException e)
					{
		
						throw e;
					}
				}
				
				try
				{
					resource.deleteAddress(getAddressNameToRemove());
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
		public void testRemoveAddress_removeExistingAddress_assertAddressRemoved() throws Exception
		{
			new TestPlan()
			{
				protected Address address;
				
				@Override
				protected  Address getAddressToAdd()
				{
					address = new Address();
					
					address.setEmailAddress("me@test.com");
					address.setType("email");
					address.setEndpoint("none");
					address.setDisplayName("me");
					
					return address;
				}
				
				@Override
				protected String getDomainToAdd()
				{
					return "test.com";
				}
				
				@Override
				protected String getAddressNameToRemove()
				{
					return "me@test.com";
				}
				
				@Override
				protected void doAssertions() throws Exception
				{
					assertNull(addressDao.get("me@test.com"));
				}
			}.perform();
		}	
		
		@Test
		public void testRemoveAddress_nonExistentAddress_assertNotFound() throws Exception
		{
			new TestPlan()
			{
				protected Address address;
				
				@Override
				protected  Address getAddressToAdd()
				{
					address = new Address();
					
					address.setEmailAddress("me@test.com");
					address.setType("email");
					address.setEndpoint("none");
					address.setDisplayName("me");
					
					return address;
				}
				
				@Override
				protected String getDomainToAdd()
				{
					return "test.com";
				}
				
				@Override
				protected String getAddressNameToRemove()
				{
					return "me@test2.com";
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
		public void testRemoveAddress_nonErrorInDelete_assertServerError() throws Exception
		{
			new TestPlan()
			{
				protected AddressResource addressService;
				
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						addressService = (AddressResource)ConfigServiceRunner.getSpringApplicationContext().getBean("addressResource");

						AddressDao mockDAO = mock(AddressDao.class);
						when(mockDAO.get((String)any())).thenReturn(new org.nhindirect.config.store.Address());
						doThrow(new RuntimeException()).when(mockDAO).delete(eq("me@test.com"));
						
						addressService.setAddressDao(mockDAO);
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
					
					addressService.setAddressDao(addressDao);
				}
				
				@Override
				protected  Address getAddressToAdd()
				{
					
					return null;
				}
				
				@Override
				protected String getDomainToAdd()
				{
					return "test.com";
				}
				
				@Override
				protected String getAddressNameToRemove()
				{
					return "me@test.com";
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
		public void testRemoveAddress_nonErrorInLookup_assertServerError() throws Exception
		{
			new TestPlan()
			{
				protected AddressResource addressService;
				
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						addressService = (AddressResource)ConfigServiceRunner.getSpringApplicationContext().getBean("addressResource");

						AddressDao mockDAO = mock(AddressDao.class);
						doThrow(new RuntimeException()).when(mockDAO).get(eq("me@test.com"));
						
						addressService.setAddressDao(mockDAO);
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
					
					addressService.setAddressDao(addressDao);
				}
				
				@Override
				protected  Address getAddressToAdd()
				{
					
					return null;
				}
				
				@Override
				protected String getDomainToAdd()
				{
					return "test.com";
				}
				
				@Override
				protected String getAddressNameToRemove()
				{
					return "me@test.com";
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
