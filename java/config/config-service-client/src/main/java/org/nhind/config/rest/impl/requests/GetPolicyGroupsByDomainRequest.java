package org.nhind.config.rest.impl.requests;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.CertPolicyGroup;

public class GetPolicyGroupsByDomainRequest extends AbstractGetRequest<CertPolicyGroup>
{
	private final String domainName;

    public GetPolicyGroupsByDomainRequest(HttpClient httpClient, String certServerUrl,
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
    	return serviceUrl + "certpolicy/groups/domain/" + uriEscape(domainName);
    }
    
    @Override
    protected Collection<CertPolicyGroup> interpretResponse(int statusCode, HttpResponse response)
            throws IOException, ServiceException 
    {
        switch (statusCode) 
        {
        	case 200:
        		return super.interpretResponse(statusCode, response);        		
        	case 204:	
        		return Collections.emptyList();
        	case 404:
	            throw new ServiceMethodException(404, "Failed to locate target service. Is '"
	                    + serviceUrl + "' the correct URL?");
        	///CLOVER:OFF
        	default:
        		return super.interpretResponse(statusCode, response);
        	///CLOVER:ON
        }
    }    
}