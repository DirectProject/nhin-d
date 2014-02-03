package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;

public class DisassociatePolicyGroupFromDomainsRequest extends AbstractDeleteRequest<String, CertPolicyGroupDomainReltn>
{
	private final String groupName;
	
    public DisassociatePolicyGroupFromDomainsRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String groupName) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, null);
    	
    	if (groupName == null || groupName.isEmpty())
    		throw new IllegalArgumentException("Group name cannot be null or empty.");
    
    	
    	this.groupName = groupName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "certpolicy/groups/domain/" + uriEscape(groupName) + "/deleteFromGroup";
	}

}
