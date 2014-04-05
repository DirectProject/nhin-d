package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPostRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Domain;

public class UpdateDomainRequest extends AbstractPostRequest<Domain, Domain>
{
    public UpdateDomainRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, Domain domain) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, domain);
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "domain";
	}

}
