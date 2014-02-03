package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicyGroup;

public class GetPolicyGroupRequest extends AbstractGetRequest<CertPolicyGroup>
{
	private final String policyGroupName;

    public GetPolicyGroupRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String policyGroupName) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, false);
        
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