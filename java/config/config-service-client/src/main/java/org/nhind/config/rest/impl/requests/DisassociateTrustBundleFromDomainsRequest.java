package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public class DisassociateTrustBundleFromDomainsRequest extends AbstractDeleteRequest<String, TrustBundle>
{
	private final String bundleName;
	
    public DisassociateTrustBundleFromDomainsRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String bundleName) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, null);
    	
    	if (bundleName == null || bundleName.isEmpty())
    		throw new IllegalArgumentException("Bundle name cannot be null or empty.");
    
    	
    	this.bundleName = bundleName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "trustbundle/" + uriEscape(bundleName) + "/deleteFromBundle";
	}

}
