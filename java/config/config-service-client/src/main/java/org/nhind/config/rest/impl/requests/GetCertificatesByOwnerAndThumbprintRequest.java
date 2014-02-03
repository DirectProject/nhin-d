package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;

public class GetCertificatesByOwnerAndThumbprintRequest extends AbstractGetRequest<Certificate>
{
	private final String owner;
	private final String thumbprint;

    public GetCertificatesByOwnerAndThumbprintRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String owner, String thumbprint) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, false);
        
        if (owner == null || owner.isEmpty())
        	throw new IllegalArgumentException("Owner name cannot be null or empty");
        
        if (thumbprint == null || thumbprint.isEmpty())
        	throw new IllegalArgumentException("Thumbprint name cannot be null or empty");
        
        this.owner = owner;
        this.thumbprint = thumbprint;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	
    	return serviceUrl + "certificate/" + uriEscape(owner) + "/" + uriEscape(thumbprint);
    }
    
}