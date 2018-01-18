/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

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

/**
 * Abstract implementation of a GET request.  This implementation handles creating the request URI and unmarshalling the response. 
 * @author Greg Meyer
 * @since 1.3
 *
 * @param <T> Return type of the GET request.
 */
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
