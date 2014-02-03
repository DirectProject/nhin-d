package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPutRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;

public class AddCertificateRequest extends AbstractPutRequest<Certificate, Certificate>
{
    public AddCertificateRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, Certificate cert) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, cert);
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "certificate";
	}
}