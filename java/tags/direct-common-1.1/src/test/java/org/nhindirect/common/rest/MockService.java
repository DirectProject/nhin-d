package org.nhindirect.common.rest;

import org.apache.http.client.HttpClient;

public class MockService extends AbstractUnsecuredService
{
    public MockService(String transportServiceUrl, HttpClient httpClient) 
    {
        super(transportServiceUrl, httpClient);
    }

    
    public void mockCallNoReturn(ServiceRequest<Object, Exception> req) throws Exception
    {
    	this.callWithRetry(req);
    }
}
