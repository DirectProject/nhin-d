package org.nhindirect.common.rest;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.nhindirect.common.rest.exceptions.ServiceException;

public abstract class AbstractGetRequest<T> extends SecuredServiceRequestBase<Collection<T>, ServiceException>
{
	protected final boolean collectionRequest;
	
    protected AbstractGetRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, boolean collectionRequest) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager);
    	
    	this.collectionRequest = collectionRequest;
    }
    
    protected abstract String getRequestUri() throws ServiceException;
    
    @Override
    protected Collection<T> interpretResponse(int statusCode, HttpResponse response)
            throws IOException, ServiceException 
    {
        switch (statusCode) 
        {
        	case 200:
        		return super.interpretResponse(statusCode, response);        		
        	case 404:
        	case 204:	
        		return Collections.emptyList();
        	///CLOVER:OFF
        	default:
        		return super.interpretResponse(statusCode, response);
        	///CLOVER:ON
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final HttpUriRequest createRequest() throws IOException
    {
    	try
    	{
    		final HttpGet get = new HttpGet(getRequestUri());
    		return get;
    	}
    	catch (ServiceException e)
    	{
    		throw new IOException("Error creating request.", e);
    	}
    }    
    
    /**
     * {@inheritDoc}
     */
	@SuppressWarnings({ "unchecked" })
	@Override
    protected List<T> parseResponse(HttpEntity response)
            throws IOException
    {
    	
    	final Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    	
    	List<T> retVal;
    	
    	if (collectionRequest)
    	{
    		retVal = jsonMapper.readValue(response.getContent(),
                TypeFactory.collectionType(ArrayList.class, persistentClass));
    	}
    	else
    	{
    		final T single = (T)jsonMapper.readValue(response.getContent(), persistentClass);
    		retVal = Arrays.asList(single);
    	}
    	
        return retVal;
    }   
}
