package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public class DisassociateTrustBundlesFromDomainRequest extends AbstractDeleteRequest<String, TrustBundle>
{
	private final String domainName;
	
    public DisassociateTrustBundlesFromDomainRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String domainName) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, null);
    	
    	if (domainName == null || domainName.isEmpty())
    		throw new IllegalArgumentException("Domain name cannot be null or empty.");
    	
    	this.domainName = domainName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "trustbundle/" + uriEscape(domainName) + "/deleteFromDomain";
	}

}
