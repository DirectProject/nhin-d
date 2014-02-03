package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPostRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public class AssociateTrustBundleToDomainRequest extends AbstractPostRequest<String, TrustBundle>
{
	private final String bundleName;
	private final String domainName;
	private final boolean incoming;
	private final boolean outgoing;
	
    public AssociateTrustBundleToDomainRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String bundleName, String domainName,
            boolean incoming, boolean outgoing) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, null);
    	
    	if (bundleName == null || bundleName.isEmpty())
    		throw new IllegalArgumentException("Bundle name cannot be null or empty.");
    	
    	if (domainName == null || domainName.isEmpty())
    		throw new IllegalArgumentException("Domain name cannot be null or empty.");
    	
    	this.bundleName = bundleName;
    	this.domainName = domainName;
    	this.incoming = incoming;
    	this.outgoing = outgoing;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		final StringBuilder builder = new StringBuilder("?incoming=").append(incoming);
		builder.append("&outgoing=").append(outgoing);
		
		return serviceUrl + "trustbundle/" + uriEscape(bundleName) + "/" + uriEscape(domainName) + builder.toString();
	}

}
