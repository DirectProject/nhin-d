package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPutRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Domain;

public class AddDomainRequest extends AbstractPutRequest<Domain, Domain>
{
    public AddDomainRequest(HttpClient httpClient, String serviceUrl,
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
