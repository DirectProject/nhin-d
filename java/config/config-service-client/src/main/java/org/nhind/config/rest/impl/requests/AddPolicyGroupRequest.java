package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPutRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicyGroup;

public class AddPolicyGroupRequest extends AbstractPutRequest<CertPolicyGroup, CertPolicyGroup>
{
    public AddPolicyGroupRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, CertPolicyGroup group) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, group);
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "certpolicy/groups";
	}
       
}
