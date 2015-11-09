package org.nhind.config.rest.impl;

import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.DomainService;
import org.nhind.config.rest.impl.requests.AddDomainRequest;
import org.nhind.config.rest.impl.requests.DeleteDomainRequest;
import org.nhind.config.rest.impl.requests.GetDomainRequest;
import org.nhind.config.rest.impl.requests.SearchDomainRequest;
import org.nhind.config.rest.impl.requests.UpdateDomainRequest;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;

public class DefaultDomainService extends AbstractSecuredService implements DomainService
{
    public DefaultDomainService(String serviceUrl, HttpClient httpClient, 
    		ServiceSecurityManager securityManager) 
    {	
        super(serviceUrl, httpClient, securityManager);
    }

	@Override
	public Domain getDomain(String domainName) throws ServiceException
	{
		final Collection<Domain> domains = callWithRetry(new GetDomainRequest(httpClient, serviceURL, jsonMapper, securityManager,
				domainName));
		
		return (domains.isEmpty()) ? null : domains.iterator().next();
	}

	@Override
	public Collection<Domain> searchDomains(String domainName, EntityStatus status) throws ServiceException
	{
		return callWithRetry(new SearchDomainRequest(httpClient, serviceURL, jsonMapper, securityManager,
				domainName, status));
	}

	@Override
	public void addDomain(Domain domain) throws ServiceException
	{
		callWithRetry(new AddDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, domain));
		
	}

	@Override
	public void updateDomain(Domain domain) throws ServiceException
	{
		callWithRetry(new UpdateDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, domain));
	}

	@Override
	public void deleteDomain(String domainName) throws ServiceException
	{
		callWithRetry(new DeleteDomainRequest(httpClient, serviceURL, jsonMapper, securityManager, domainName));
		
	}
    
}
