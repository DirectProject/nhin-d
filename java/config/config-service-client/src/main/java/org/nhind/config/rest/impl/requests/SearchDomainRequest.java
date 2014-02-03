package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;

public class SearchDomainRequest extends AbstractGetRequest<Domain>
{
	private final String domainName;
	private final EntityStatus entityStatus;

    public SearchDomainRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String domainName,
            EntityStatus entityStatus) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, true);
        
        if ((domainName == null && entityStatus == null))
        	throw new IllegalArgumentException("Both entity status and domain name cannot be null.");
        
        this.domainName = domainName;
        this.entityStatus = entityStatus;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	final StringBuilder builder = new StringBuilder("?");
    	if (domainName != null)
    	{
    		builder.append("domainName=").append(uriEscape(domainName));
    		if (entityStatus != null)
    			builder.append("&");
    	}

    	if (entityStatus != null)
    		builder.append("entityStatus=").append(entityStatus.toString());
    	
    	return serviceUrl + "domain" + builder.toString();
    }
}