package org.nhind.config.rest.impl;

import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.AnchorService;
import org.nhind.config.rest.impl.requests.AddAnchorRequest;
import org.nhind.config.rest.impl.requests.DeleteAnchorsByIdsRequest;
import org.nhind.config.rest.impl.requests.DeleteAnchorsByOwner;
import org.nhind.config.rest.impl.requests.GetAnchorsForOwnerReqest;
import org.nhind.config.rest.impl.requests.GetAnchorsRequest;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Anchor;

public class DefaultAnchorService extends AbstractSecuredService implements AnchorService
{
    public DefaultAnchorService(String serviceUrl, HttpClient httpClient, 
    		ServiceSecurityManager securityManager) 
    {	
        super(serviceUrl, httpClient, securityManager);
    }

	@Override
	public Collection<Anchor> getAnchors()  throws ServiceException
	{
		return callWithRetry(new GetAnchorsRequest(httpClient, serviceURL, jsonMapper, securityManager));	
	}

	@Override
	public Collection<Anchor> getAnchorsForOwner(String owner,
			boolean incoming, boolean outgoing, String thumbprint) throws ServiceException
	{
		return callWithRetry(new GetAnchorsForOwnerReqest(httpClient, serviceURL, jsonMapper, securityManager, owner, incoming,
				outgoing, thumbprint));
	}

	@Override
	public void addAnchor(Anchor anchor) throws ServiceException
	{
		callWithRetry(new AddAnchorRequest(httpClient, serviceURL, jsonMapper, securityManager, anchor));
	}

	@Override
	public void deleteAnchorsByIds(Collection<Long> ids) throws ServiceException
	{
		callWithRetry(new DeleteAnchorsByIdsRequest(httpClient, serviceURL, jsonMapper, securityManager, ids));
	}

	@Override
	public void deleteAnchorsByOwner(String owner) throws ServiceException
	{
		callWithRetry(new DeleteAnchorsByOwner(httpClient, serviceURL, jsonMapper, securityManager, owner));
	}
    
    
}
