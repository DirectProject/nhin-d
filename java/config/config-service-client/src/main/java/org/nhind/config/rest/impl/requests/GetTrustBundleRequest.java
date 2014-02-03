package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public class GetTrustBundleRequest extends AbstractGetRequest<TrustBundle>
{
	private final String bundleName;
	
    public GetTrustBundleRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String bundleName) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, false);
        
        if (bundleName == null || bundleName.isEmpty())
        	throw new IllegalArgumentException("Bundle name cannot be null or empty");
        
        this.bundleName = bundleName;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	return serviceUrl + "trustbundle/" + uriEscape(bundleName);
    }
}
