package org.nhindirect.common.rest;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.nhindirect.common.rest.exceptions.ServiceException;

public class MockServiceRequest extends UnsecuredServiceRequestBase<Object, ServiceException>
{
	protected final String msg;
	
    public MockServiceRequest(HttpClient httpClient, String transportServerUrl,String msg) 
    {
        super(httpClient, transportServerUrl, null);


        this.msg = msg;
        
    }
    
    protected String getRequestUri() 
    {
    	String theURI = serviceUrl.endsWith("/") ? serviceUrl : serviceUrl + "/";
    	
        return theURI + "mock";
    }
    
    protected final HttpPost createRequest() throws IOException 
    {
    	if (msg.isEmpty())
    		return null;
    	
    	HttpPost request = new HttpPost(getRequestUri());
        
        // set the entity content
        final StringEntity entity = new StringEntity(msg);
        entity.setContentType("message/rfc822");
        entity.setContentEncoding("UTF-8");
        request.setEntity(entity);
        return request;
    }
    
    @Override
    protected Object parseResponse(HttpEntity response)
            throws IOException
    {
    	return null;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void destroy()
    {

    }
}
