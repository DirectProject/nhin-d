package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Anchor;

public class DeleteAnchorsByOwner extends AbstractDeleteRequest<Anchor, Anchor>
{
	private final String owner;

    public DeleteAnchorsByOwner(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager,  String owner) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, null);
        
        if (owner == null || owner.isEmpty())
        	throw new IllegalArgumentException("Owner name cannot be null or empty");
        
        this.owner = owner;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {	
    	return serviceUrl + "anchor/" + uriEscape(owner);
    }
}