package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public class GetTrustBundlesRequest extends AbstractGetRequest<TrustBundle>
{
	private final boolean fetchAnchors;
	
    public GetTrustBundlesRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, boolean fetchAnchors) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, true);
        
        this.fetchAnchors = fetchAnchors;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	return serviceUrl + "trustbundle?fetchAnchors=" + fetchAnchors;
    }
}
