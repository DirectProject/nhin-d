package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Address;


public class GetAddressRequest extends AbstractGetRequest<Address>
{
	private final String addressName;
	
    public GetAddressRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String addressName) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, false);
        
        if (addressName == null || addressName.isEmpty())
        	throw new IllegalArgumentException("Addresss name cannot be null or empty");
        
        this.addressName = addressName;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {

    	return serviceUrl + "address/" + uriEscape(addressName);
    }
}
