package org.nhind.config.rest.impl;

import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.AddressService;
import org.nhind.config.rest.impl.requests.AddAddressRequest;
import org.nhind.config.rest.impl.requests.DeleteAddressRequest;
import org.nhind.config.rest.impl.requests.GetAddressRequest;
import org.nhind.config.rest.impl.requests.GetAddressesByDomainRequest;
import org.nhind.config.rest.impl.requests.UpdateAddressRequest;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Address;

public class DefaultAddressService extends AbstractSecuredService  implements AddressService
{
    public DefaultAddressService(String serviceUrl, HttpClient httpClient, 
    		ServiceSecurityManager securityManager) 
    {	
        super(serviceUrl, httpClient, securityManager);
    }

	@Override
	public Address getAddress(String addressName) throws ServiceException 
	{
		final Collection<Address> addresses = callWithRetry(new GetAddressRequest(httpClient, serviceURL, jsonMapper, securityManager,
				addressName));
		
		return (addresses.isEmpty()) ? null : addresses.iterator().next();
	}

	@Override
	public Collection<Address> getAddressesByDomain(String domainName) throws ServiceException 
	{
		return callWithRetry(new GetAddressesByDomainRequest(httpClient, serviceURL, jsonMapper, securityManager,
				domainName));
	}

	@Override
	public void addAddress(Address address) throws ServiceException 
	{
		callWithRetry(new AddAddressRequest(httpClient, serviceURL, jsonMapper, securityManager,
				address));
	}

	@Override
	public void updateAddress(Address address) throws ServiceException 
	{
		callWithRetry(new UpdateAddressRequest(httpClient, serviceURL, jsonMapper, securityManager,
				address));
	}

	@Override
	public void deleteAddress(String address) throws ServiceException 
	{	
		callWithRetry(new DeleteAddressRequest(httpClient, serviceURL, jsonMapper, securityManager,
				address));
	}    
}
