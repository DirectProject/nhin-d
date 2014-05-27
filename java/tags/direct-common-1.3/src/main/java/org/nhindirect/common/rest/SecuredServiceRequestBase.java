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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.exceptions.ServiceException;

/**
 * Abstract implementation of a secured service.  Requires a security manager that handles authenticating requests to the secured service.  
 * @author Greg Meyer
 * @since 1.3
 * @param <T> Return type of the request.
 * @param <E> Error type specific to the request.
 */
public abstract class SecuredServiceRequestBase<T, E extends Exception> extends UnsecuredServiceRequestBase<T, E>
{
	protected final ServiceSecurityManager securityManager;
	
    /**
     * Constructor.
     * 
     * @param httpClient
     *            the {@link HttpClient} to use to make requests.
     * @param serviceUrl
     *            the base URL of the target service.
     * @param jsonMapper
     *            the {@link ObjectMapper} to use for (de)serialization of request/response objects.
     * @param securityManager
     *            the {@link ServiceSecurityManager} used for authenticating requests
     */
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
