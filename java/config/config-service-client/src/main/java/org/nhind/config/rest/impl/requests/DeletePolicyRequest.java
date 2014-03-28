package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicy;

public class DeletePolicyRequest extends AbstractDeleteRequest<CertPolicy, CertPolicy>
{
	private final String policyName;

    public DeletePolicyRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String policyName) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, null);
        
        if (policyName == null || policyName.isEmpty())
        	throw new IllegalArgumentException("Policy name cannot be null or empty");
        
        this.policyName = policyName;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {

    	return serviceUrl + "certpolicy/" + uriEscape(policyName);
    }
}