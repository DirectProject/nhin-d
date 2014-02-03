package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Address;

public class GetAddressesByDomainRequest extends AbstractGetRequest<Address>
{
	private final String domainName;
	
    public GetAddressesByDomainRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String domainName) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, true);
        
        if (domainName == null || domainName.isEmpty())
        	throw new IllegalArgumentException("Domain name cannot be null or empty");
        
        this.domainName = domainName;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {

    	return serviceUrl + "address/domain/" + uriEscape(domainName);
    }
}
