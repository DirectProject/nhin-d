package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicy;

public class GetPolicyByNameRequest extends AbstractGetRequest<CertPolicy>
{
	private final String policyName;

    public GetPolicyByNameRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String policyName) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, false);
        
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