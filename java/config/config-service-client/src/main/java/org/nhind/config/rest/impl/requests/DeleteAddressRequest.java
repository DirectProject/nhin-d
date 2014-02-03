package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Address;

public class DeleteAddressRequest extends AbstractDeleteRequest<Address, Address>
{
	private final String addressDomain;

    public DeleteAddressRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String addressDomain) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, null);
        
        if (addressDomain == null || addressDomain.isEmpty())
        	throw new IllegalArgumentException("Address name cannot be null or empty");
        
        this.addressDomain = addressDomain;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {

    	return serviceUrl + "address/" + uriEscape(addressDomain);
    }
}