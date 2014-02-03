package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPostRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicy;

public class UpdatePolicyRequest extends AbstractPostRequest<CertPolicy, CertPolicy>
{
	private final String policyName;
	
    public UpdatePolicyRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, 
            String policyName, CertPolicy updatePolicy) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, updatePolicy);
    	
    	if (policyName == null || policyName.isEmpty())
    		throw new IllegalArgumentException("Policy name cannot be null or empty");
    	
    	if (updatePolicy == null)
    		throw new IllegalArgumentException("Update policy attributes cannot be null or empty");
    	
    	this.policyName = policyName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "certpolicy/" + uriEscape(policyName) + "/policyAttributes";
	}

}
