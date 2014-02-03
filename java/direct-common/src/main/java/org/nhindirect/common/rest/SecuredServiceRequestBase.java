package org.nhindirect.common.rest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.exceptions.ServiceException;

public abstract class SecuredServiceRequestBase<T, E extends Exception> extends UnsecuredServiceRequestBase<T, E>
{
	protected final ServiceSecurityManager securityManager;
	
	public SecuredServiceRequestBase(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager)
	{
		super(httpClient, serviceUrl, jsonMapper);
		
		if (securityManager == null)
			throw new IllegalArgumentException("Security manager cannot be null");
		
		this.securityManager = securityManager;
	}
	
    /**
     * {@inheritDoc}}
     */
    @Override
    public T call() throws E, IOException, ServiceException 
    {
        HttpUriRequest request = createRequest();
        assert request != null;
        request = securityManager.createAuthenticatedRequest(request);

        final HttpResponse response = httpClient.execute(request);
        try 
        {
            final int statusCode = response.getStatusLine().getStatusCode();
            return interpretResponse(statusCode, response);
        } 
        finally 
        {
            closeConnection(response);
        }
    }
}
