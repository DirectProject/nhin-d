package org.nhindirect.common.rest;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.exceptions.ServiceException;

public abstract class AbstractPutRequest<T, E>  extends SecuredServiceRequestBase<E, ServiceException>
{
	protected final T entity;
	
    protected AbstractPutRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, T entity) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager);
    	
    	this.entity = entity;
    }
    
    protected abstract String getRequestUri() throws ServiceException;
    
    @Override
    protected E interpretResponse(int statusCode, HttpResponse response)
            throws IOException, ServiceException 
    {
        switch (statusCode) 
        {
        	case 200:
        	case 201:
        	case 204:
        		return super.interpretResponse(statusCode, response);        		
        	default:
        		return super.interpretResponse(statusCode, response);
        	///CLOVER:ON
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpUriRequest createRequest() throws IOException 
    {
    	try
    	{
    		HttpPut post = new HttpPut(getRequestUri());
    		post.setHeader("Accept", MediaType.APPLICATION_JSON);
    		return buildEntityRequest(post, makeContent(), MediaType.APPLICATION_JSON);
    	}
       	catch (ServiceException e)
    	{
    		throw new IOException("Error creating request URI.", e);
    	}
    }    
    
    
    /*
     * make the content payload to be sent
     */
    protected byte[] makeContent() throws IOException 
    {
    	if (entity instanceof String)
    		return ((String) entity).getBytes();
    	else if (entity instanceof byte[])
    		return (byte[])entity;
    	
    	return (entity != null) ? jsonMapper.writeValueAsBytes(entity) : new byte[]{};
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
    protected E parseResponse(HttpEntity response)
            throws IOException
    {
		// most put actions don't return anything
		// so the default will be returning null
		return null;
    }   
}
