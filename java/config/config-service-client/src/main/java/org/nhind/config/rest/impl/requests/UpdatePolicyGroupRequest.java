package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPostRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicyGroup;

public class UpdatePolicyGroupRequest extends AbstractPostRequest<String, CertPolicyGroup>
{
	private final String groupName;
	
    public UpdatePolicyGroupRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, 
            String groupName, String policyUpdateName) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, policyUpdateName);
    	
    	if (groupName == null || groupName.isEmpty())
    		throw new IllegalArgumentException("Policy group name cannot be null or empty");
    	
    	
    	this.groupName = groupName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "certpolicy/groups/" + uriEscape(groupName)  + "/groupAttributes";
	}

}
