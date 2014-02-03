package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPutRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Anchor;

public class AddAnchorRequest extends AbstractPutRequest<Anchor, Anchor>
{
    public AddAnchorRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, Anchor anchor) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, anchor);
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "anchor";
	}
}
