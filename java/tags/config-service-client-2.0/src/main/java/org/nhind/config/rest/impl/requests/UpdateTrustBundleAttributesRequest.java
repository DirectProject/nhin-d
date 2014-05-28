package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPostRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public class UpdateTrustBundleAttributesRequest extends AbstractPostRequest<TrustBundle, TrustBundle>
{
	private final String bundleName;
	
    public UpdateTrustBundleAttributesRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, 
            String bundleName, TrustBundle bundleData) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, bundleData);
    	
    	if (bundleName == null || bundleName.isEmpty())
    		throw new IllegalArgumentException("Bundle name cannot be null or empty");
    	
    	if (bundleData == null)
    		throw new IllegalArgumentException("Update bundle attributes cannot be null or empty");
    	
    	this.bundleName = bundleName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "trustbundle/" + uriEscape(bundleName) + "/bundleAttributes";
	}

}
