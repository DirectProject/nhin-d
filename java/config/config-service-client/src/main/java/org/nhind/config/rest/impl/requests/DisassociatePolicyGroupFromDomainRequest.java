package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;

public class DisassociatePolicyGroupFromDomainRequest extends AbstractDeleteRequest<String, CertPolicyGroupDomainReltn>
{
	private final String groupName;
	private final String domainName;
	
    public DisassociatePolicyGroupFromDomainRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String groupName, String domainName) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, null);
    	
    	if (groupName == null || groupName.isEmpty())
    		throw new IllegalArgumentException("Group name cannot be null or empty.");
    	
    	if (domainName == null || domainName.isEmpty())
    		throw new IllegalArgumentException("Domain name cannot be null or empty.");
    	
    	this.groupName = groupName;
    	this.domainName = domainName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "certpolicy/groups/domain/" + uriEscape(groupName) + "/" + uriEscape(domainName);
	}

}
