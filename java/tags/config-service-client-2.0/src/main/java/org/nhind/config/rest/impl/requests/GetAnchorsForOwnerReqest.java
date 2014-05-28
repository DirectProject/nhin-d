package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Anchor;

public class GetAnchorsForOwnerReqest extends AbstractGetRequest<Anchor>
{
	private final String owner;
	private final boolean incoming;
	private final boolean outgoing;
	private final String thumbprint;

    public GetAnchorsForOwnerReqest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String owner,
			boolean incoming, boolean outgoing, String thumbprint) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, true);
        
        if (owner == null || owner.isEmpty())
        	throw new IllegalArgumentException("Owner name cannot be null or empty");
        
        this.owner = owner;
        this.thumbprint = thumbprint;
        this.incoming = incoming;
        this.outgoing = outgoing;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	final StringBuilder queryString = new StringBuilder("?incoming=").append(Boolean.toString(incoming));
    	queryString.append("&outgoing=").append(Boolean.toString(outgoing));
    	
    	if (thumbprint != null && !thumbprint.isEmpty())
    		queryString.append("&thumbprint=").append(uriEscape(thumbprint));
    	
    	return serviceUrl + "anchor/" + uriEscape(owner) + queryString.toString();
    }
    
}