package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;

public class GetCertificatesByOwnerRequest extends AbstractGetRequest<Certificate>
{
	private final String owner;

    public GetCertificatesByOwnerRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String owner) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, true);
        
        if (owner == null || owner.isEmpty())
        	throw new IllegalArgumentException("Owner name cannot be null or empty");
        
        this.owner = owner;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	
    	return serviceUrl + "certificate/" + uriEscape(owner);
    }
    
}