package org.nhindirect.config.resources;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.Address;
import org.nhindirect.config.resources.AddressResource_getAddressesByDomainTest.TestPlan;
import org.nhindirect.config.store.dao.AddressDao;
import org.nhindirect.config.store.dao.DomainDao;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class JamesServerAccountResource_listAccountsTest 
{
	
	static WebResource resource;
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				
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
	
		
		@Override
		protected void performInner() throws Exception
		{				
						
			try
			{
				final GenericType<ArrayList<Address>> genType = new GenericType<ArrayList<Address>>(){};
				final Collection<Address> getAddresses = resource.path("/api/jamesaccounts/3X").get(genType);
				doAssertions(getAddresses);
			}
			catch (UniformInterfaceException e)
			{
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

			
			@Override
			protected void doAssertions(Collection<Address> addresses) throws Exception
			{
				
				
				assertNotNull(addresses);
				assertEquals(1, addresses.size());
				final Address address = addresses.iterator().next();

			}

		}.perform();
	}	
}
