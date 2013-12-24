package org.nhindirect.config.resources;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
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

import org.nhindirect.config.store.dao.AddressDao;
import org.nhindirect.config.store.dao.DomainDao;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class AddressResource_getAddressesByDomainTest 
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
		
		protected abstract String getDomainNameToGet();
		
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
					resource.path("/api/address").entity(addAddress, MediaType.APPLICATION_JSON).put(addAddress);
				}
				catch (UniformInterfaceException e)
				{
	
					throw e;
				}
			}
			
			try
			{
				final GenericType<ArrayList<Address>> genType = new GenericType<ArrayList<Address>>(){};
				
				final Collection<Address> getAddresses = resource.path("/api/address/domain/" + TestUtils.uriEscape(getDomainNameToGet())).get(genType);
				doAssertions(getAddresses);
			}
			catch (UniformInterfaceException e)
			{
				
				if (e.getResponse().getStatus() == 404)
					doAssertions(new ArrayList<Address>());
				else
					throw e;
			}
			
		}
		
		
		protected void doAssertions(Collection<Address> addresses) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testGetAddresseseByDomain_getExistingAddress_assertAddressRetrieved() throws Exception
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
			protected String getDomainNameToGet()
			{
				return "test.com";
			}
			
			@Override
			protected void doAssertions(Collection<Address> addresses) throws Exception
			{
				
				
				assertNotNull(addresses);
				assertEquals(1, addresses.size());
				final Address address = addresses.iterator().next();
				
				assertEquals(this.address.getEmailAddress(), address.getEmailAddress());
				assertEquals(this.address.getType(), address.getType());
				assertEquals(this.address.getEndpoint(), address.getEndpoint());
				assertEquals(this.address.getDisplayName(), address.getDisplayName());
				assertEquals(this.address.getDomainName(), address.getDomainName());
			}
		}.perform();
	}		
	
	@Test
	public void testGetAddressesByDomain_nonExistentDomain_assertNull() throws Exception
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
			protected String getDomainNameToGet()
			{
				return "test2.com";
			}
			
			@Override
			protected void doAssertions(Collection<Address> addresses) throws Exception
			{
				assertTrue(addresses.isEmpty());
			}
		}.perform();
	}	
	
	@Test
	public void testGetAddressesByDomain_nonExistentAddress_assertNull() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected  Address getAddressToAdd()
			{
				return null;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return "test2.com";
			}
			
			@Override
			protected String getDomainNameToGet()
			{
				return "test2.com";
			}
			
			@Override
			protected void doAssertions(Collection<Address> addresses) throws Exception
			{
				assertTrue(addresses.isEmpty());
			}
		}.perform();
	}	
	
	@Test
	public void testGetAddress_errorInDomainLookup_assertServerError() throws Exception
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

					DomainDao mockDAO = mock(DomainDao.class);
					doThrow(new RuntimeException()).when(mockDAO).getDomainByName(eq("blowup.com"));
					
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
			protected String getDomainNameToGet()
			{
				return "blowup.com";
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
	public void testGetAddress_errorInAddressLookup_assertServerError() throws Exception
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
					doThrow(new RuntimeException()).when(mockDAO).getByDomain((org.nhindirect.config.store.Domain)any(), eq((org.nhindirect.config.store.EntityStatus)null));
					
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
			protected String getDomainNameToGet()
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
