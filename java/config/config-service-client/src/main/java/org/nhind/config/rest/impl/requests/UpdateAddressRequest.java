package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPostRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Address;

public class UpdateAddressRequest extends AbstractPostRequest<Address, Address>
{
    public UpdateAddressRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, Address address) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, address);
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "address";
	}

}
