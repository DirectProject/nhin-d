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
import org.nhindirect.config.store.dao.AddressDao;
import org.nhindirect.config.store.dao.DomainDao;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class AddressResource_updateAddressTest 
{
    protected AddressDao addressDao;
    
    protected DomainDao domainDao;
    
	static WebResource resource;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				addressDao = (AddressDao)ConfigServiceRunner.getSpringApplicationContext().getBean("addressDao");
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
		
		protected abstract Address getAddressToAdd();
		
		protected abstract String getDomainToAdd();
		
		protected abstract Address getAddressToUpdate();
		
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
					resource.path("/api/address").entity(addAddress, MediaType.APPLICATION_JSON).put();
				}
				catch (UniformInterfaceException e)
				{
					throw e;
				}
			}
			
			try
			{
				resource.path("/api/address").entity(getAddressToUpdate(), MediaType.APPLICATION_JSON).post();
			}
			catch (UniformInterfaceException e)
			{
				throw e;
			}
			
			try
			{
				final Address getAddress = resource.path("/api/address/" + TestUtils.uriEscape(getAddressToUpdate().getEmailAddress())).get(Address.class);
				doAssertions(getAddress);
			}
			catch (UniformInterfaceException e)
			{
				throw e;
			}
			
		}
		
		
		protected void doAssertions(Address address) throws Exception
		{
			
		}
	}		
	
	@Test
	public void testUpdateAddress_updateExistingAddress_assertAddressUpdated() throws Exception
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
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName(getDomainToAdd());
				
				return address;
			}
			
			@Override
			protected void doAssertions(Address address) throws Exception
			{
				assertNotNull(address);
				assertEquals(this.address.getEmailAddress(), address.getEmailAddress());
				assertEquals(this.address.getType(), address.getType());
				assertEquals(this.address.getEndpoint(), address.getEndpoint());
				assertEquals(this.address.getDisplayName(), address.getDisplayName());
				assertEquals(this.address.getDomainName(), address.getDomainName());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateAddress_nonExistentDomain_assertNotFound() throws Exception
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
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName("test2.com");
				
				return address;
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
	public void testUpdateAddress_nonExistentAddress_assertNotFound() throws Exception
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
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me2@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName(getDomainToAdd());
				
				return address;
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
	public void testUpdateAddress_emptyDomainName_assertBadRequest() throws Exception
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
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me2@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName("");
				
				return address;
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof UniformInterfaceException);
				UniformInterfaceException ex = (UniformInterfaceException)exception;
				assertEquals(400, ex.getResponse().getStatus());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateAddress_nullDomainName_assertBadRequest() throws Exception
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
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me2@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName(null);
				
				return address;
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof UniformInterfaceException);
				UniformInterfaceException ex = (UniformInterfaceException)exception;
				assertEquals(400, ex.getResponse().getStatus());
			}
		}.perform();
	}
	
	@Test
	public void testRemoveAddress_nonErrorInDomainLookup_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected AddressResource addressService;
			
			protected Address address;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					addressService = (AddressResource)ConfigServiceRunner.getSpringApplicationContext().getBean("addressResource");

					DomainDao mockDAO = mock(DomainDao.class);
					doThrow(new RuntimeException()).when(mockDAO).getDomainByName(eq("test.com"));
					
					addressService.setDomainDao(mockDAO);
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
				
				addressService.setDomainDao(domainDao);
			}
			
			@Override
			protected  Address getAddressToAdd()
			{
				
				return null;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return null;
			}
			
			@Override
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName("test.com");
				
				return address;
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
	public void testRemoveAddress_nonErrorInAddressLookup_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected AddressResource addressService;
			
			protected Address address;
			
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
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName("test.com");
				
				return address;
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
	public void testRemoveAddress_nonErrorInUpdate_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected AddressResource addressService;
			
			protected Address address;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					addressService = (AddressResource)ConfigServiceRunner.getSpringApplicationContext().getBean("addressResource");

					AddressDao mockDAO = mock(AddressDao.class);
					when(mockDAO.get((String)any())).thenReturn(new org.nhindirect.config.store.Address());
					doThrow(new RuntimeException()).when(mockDAO).update((org.nhindirect.config.store.Address)any());
					
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
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName(getDomainToAdd());
				
				return address;
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
