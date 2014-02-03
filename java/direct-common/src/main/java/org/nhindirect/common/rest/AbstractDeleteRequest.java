package org.nhindirect.common.rest;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.exceptions.ServiceException;

public abstract class AbstractDeleteRequest<T, E> extends AbstractPutRequest<T, E> 
{
    protected AbstractDeleteRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, T entity) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, entity);
    	
    }
    
    /**
    * {@inheritDoc}
    */
   @Override
   protected final HttpUriRequest createRequest() throws IOException 
   {
	   	try
	   	{
	   		HttpDelete delete = new HttpDelete(getRequestUri());
	   		delete.setHeader("Accept", MediaType.APPLICATION_JSON);
	   		return delete;
	   	}
	   	catch (ServiceException e)
	   	{
	   		throw new IOException("Error creating request URI.", e);
	   	}
   } 
}
