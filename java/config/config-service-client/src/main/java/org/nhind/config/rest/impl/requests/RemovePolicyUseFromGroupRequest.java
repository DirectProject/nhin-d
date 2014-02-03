package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPostRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicyGroupUse;

public class RemovePolicyUseFromGroupRequest extends AbstractPostRequest<CertPolicyGroupUse, CertPolicyGroupUse>
{
	private final String groupName;
	
    public RemovePolicyUseFromGroupRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String groupName, CertPolicyGroupUse use) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, use);
    	
    	if (groupName == null || groupName.isEmpty())
    		throw new IllegalArgumentException("Group name cannot be null or empty.");
    	
    	this.groupName = groupName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "certpolicy/groups/uses/" + uriEscape(groupName) + "/removePolicy";
	}

}
