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
import org.nhindirect.config.model.TrustBundleDomainReltn;

public class GetTrustBundlesByDomainRequest extends AbstractGetRequest<TrustBundleDomainReltn>
{
	private final String domainName;
	private final boolean fetchAnchors;
	
    public GetTrustBundlesByDomainRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String domainName, boolean fetchAnchors) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, true);
        
        if (domainName == null || domainName.isEmpty())
        	throw new IllegalArgumentException("Domain name cannot be null or empty");
        
        this.domainName = domainName;
        this.fetchAnchors = fetchAnchors;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	return serviceUrl + "trustbundle/domains/" + uriEscape(domainName) + "?fetchAnchors=" + fetchAnchors;
    }
    
    @Override
    protected Collection<TrustBundleDomainReltn> interpretResponse(int statusCode, HttpResponse response)
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
