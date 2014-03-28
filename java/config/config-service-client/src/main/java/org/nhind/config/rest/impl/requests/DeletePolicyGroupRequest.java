package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicyGroup;

public class DeletePolicyGroupRequest extends AbstractDeleteRequest<CertPolicyGroup, CertPolicyGroup>
{
	private final String policyGroupName;

    public DeletePolicyGroupRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String policyGroupName) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, null);
        
        if (policyGroupName == null || policyGroupName.isEmpty())
        	throw new IllegalArgumentException("Policy group name cannot be null or empty");
        
        this.policyGroupName = policyGroupName;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {

    	return serviceUrl + "certpolicy/groups/" + uriEscape(policyGroupName);
    }
}