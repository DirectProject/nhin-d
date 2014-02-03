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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.exceptions.AuthorizationException;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

/**
 * Abstract implementation of an unsecured service request.  Includes HTTP request execution, JSON marshalling/unmarshalling, and error
 * status handling.
 * @author gm2552
 * @since 1.0
 * @param <T> Return type of the request.
 * @param <E> Error type specific to the request.
 */
public abstract class UnsecuredServiceRequestBase<T, E extends Exception> implements ServiceRequest<T, E>
{

    protected final ObjectMapper jsonMapper;
    protected final HttpClient httpClient;
    protected final String serviceUrl;

    /**
     * Constructor.
     * 
     * @param httpClient
     *            the {@link HttpClient} to use to make requests.
     * @param certServerUrl
     *            the base URL of the target service.
     * @param jsonMapper
     *            the {@link ObjectMapper} to use for (de)serialization of request/response objects.
     */
    protected UnsecuredServiceRequestBase(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper) 
    {
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
        this.serviceUrl = serviceUrl;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public T call() throws E, IOException, ServiceException 
    {
        HttpUriRequest request = createRequest();
        if (request == null)
        	throw new ServiceException("Could not create request object");
        
        HttpResponse response = httpClient.execute(request);
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

    // --------------Derived Class Interface Methods--------------

    /**
     * Primary extension point for this class, though many derived classes will find the default
     * implementation sufficient. This method is invoked from {@link #call()} and should decide how
     * to handle the response from the server. It should either produce a result of type {@code T}
     * or throw an exception of type {@code E, IOException,} or {@code RuntimeException}.
     * 
     * @param statusCode
     *            the HTTP status associated with {@code response}, provided as a parameter for
     *            convenience.
     * @param response
     *            the {@link HttpResponse} object received from the remote server.
     * @return the result of calling {@link #parseResponse(InputStream)} on {@code response}'s input
     *         stream.
     * @throws IOException
     *             if any {@code IOException}s occur during processing, typically by reading from
     *             the response or parsing JSON.
     * @throws E
     *             if a checked exception should be conveyed to the caller.
     */
    protected T interpretResponse(int statusCode, HttpResponse response) throws IOException, E, ServiceException
    {
        switch (statusCode) 
        {
	        case 200:
	        case 201:
	        case 204:
	            return parseResponse(response.getEntity());
	        case 401:
	            throw handleUnauthorized(response);
	        case 404:
	            throw new ServiceMethodException(404, "Failed to locate target service. Is '"
	                    + serviceUrl + "' the correct URL?");
	        default:
	            throw unexpectedStatus(statusCode, response.getEntity());
        }
    }
    
    /**
     * Extension point invoked from {@link #call()}. This method should provide a request to send to
     * the remote server.
     * 
     * @return an instance of {@link HttpUriRequest}.
     * @throws IOException
     *             if errors are encountered in building the request.
     */
    protected abstract HttpUriRequest createRequest() throws IOException;

    /**
     * Extension point invoked from the default implementation of
     * {@link #interpretResponse(int, HttpResponse)}. This method should parse the response from the
     * remote server into an object of type T, or throw an exception if unable to do so.
     * 
     * @param response
     *            the body of the response from the remote server. May be {@code null} if no
     *            response was sent.
     * @return an object of type {@code T}, or {@code null}.
     * @throws IOException
     *             if an error is encountered while parsing the response.
     */
    protected abstract T parseResponse(HttpEntity response) throws IOException;

    // --------------Utility Methods--------------

    /**
     * Escapes the given value so that it is fit for use in a URI. The only known difference from
     * {@link URLEncoder#encode(String)} is that spaces should be escaped as {@code %20}, rather
     * than {@code +}.
     * 
     * @param val
     *            the value to encode.
     * @return the encoded value.
     */
    protected static final String uriEscape(String val) throws ServiceException
    {
        try 
        {
            final String escapedVal = URLEncoder.encode(val, "UTF-8");
            // Spaces are treated differently in actual URLs. There don't appear to be any other
            // differences...
            return escapedVal.replace("+", "%20");
        } 
        ///CLOVER:OFF
        catch (UnsupportedEncodingException e) 
        {
            throw new ServiceException("Failed to encode value: " + val, e);
        }
        ///CLOVER:ON
    }

    /**
     * Creates an appropriate exception for an unexpected HTTP status code.
     * 
     * @param statusCode
     *            the status code received in the HTTP response.
     * @param responseEntity
     *            the contents of the response, if any.
     * @return an exception.
     * @throws IOException
     *             if errors are encountered reading from {@code responseEntity}.
     */
    protected static final ServiceMethodException unexpectedStatus(int statusCode,
            HttpEntity responseEntity) throws IOException 
            
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (responseEntity != null) 
        {
            responseEntity.writeTo(out);
        }
        return new ServiceMethodException(statusCode,
                "Unexpected HTTP status code received from target service: " + statusCode
                        + ". Response body contained: " + out.toString("UTF-8"));
    }
    
    /**
     * Closes the HTTP connection so that the connection manager can release the associated
     * resources.
     */
    protected static final void closeConnection(HttpResponse response) throws IOException 
    {
        final HttpEntity e = response.getEntity();
        if (e != null) 
        {
            e.getContent().close();
        }
    }

    /**
     * Invokes {@link #buildEntityRequest(HttpEntityEnclosingRequest, byte[], String, String)} with
     * a default encoding.
     */
    ///CLOVER:OFF
    protected final <R extends HttpEntityEnclosingRequest> R buildEntityRequest(R request,
            byte[] contents, String contentType) 
    {
        return buildEntityRequest(request, contents, contentType, "UTF-8");
    }
    ///CLOVER:ON
    
    /**
     * Generic logic for building an HttpEntityEnclosingRequest.
     * 
     * @param <R>
     *            the type of the request object.
     * @param request
     *            the request object to modify.
     * @param contents
     *            the HTTP entity to enclose.
     * @param contentType
     *            the content type of the request.
     * @param contentEncoding
     *            the content encoding of the request.
     * @return the modified request object.
     * @throws OAuthMessageSignerException
     *             see {@link OAuthConsumer#sign(Object)}.
     * @throws OAuthExpectationFailedException
     *             see {@link OAuthConsumer#sign(Object)}.
     * @throws OAuthCommunicationException
     *             see {@link OAuthConsumer#sign(Object)}.
     * @see OAuthConsumer#sign(Object)
     */
    ///CLOVER:OFF
    protected final <R extends HttpEntityEnclosingRequest> R buildEntityRequest(R request,
            byte[] contents, String contentType, String contentEncoding) 
    {
        final ByteArrayEntity entity = new ByteArrayEntity(contents);
        entity.setContentType(contentType);
        entity.setContentEncoding(contentEncoding);
        request.setEntity(entity);
        return request;
    }
    ///CLOVER:ON
    /**
     * Handles a {@code 401 UNAUTHORIZED} response code by generating an appropriate exception. In
     * particular, if the response specified an {@code oauth_problem} in the
     * {@code WWW-Authenticate} header, it will be extracted and conveyed to the request's caller
     * via an {@link OAuthAuthorizationException}.
     * 
     * @param response
     *            the incoming {@link HttpResponse}.
     * @return an appropriate {@link AuthorizationException}.
     */
    protected final AuthorizationException handleUnauthorized(HttpResponse response) 
    {
        return new AuthorizationException("Action not authorized");
    }
    
    /**
     * Convenience method to throw an exception if the received content type does not match the
     * expected type.
     * 
     * @param expected
     *            the type this request expects to parse. Must not be {@code null}.
     * @param actual
     *            the {@link HttpEntity} received, if any.
     * @throws CommunicationException
     *             if {@code expected} and {@code password} do not match.
     */
    protected final void checkContentType(String expected, HttpEntity entity) throws ServiceException
    {
        try 
        {
            if (!entity.getContentType().getValue().contains(expected)) 
            {
                throw incompatibleClientException();
            }
        } 
        catch (NullPointerException e) 
        {	
            throw incompatibleClientException();
        }
    }

    /**
     * @return an exception indicating that the current version of the client is incompatible with
     *         the server being communicated with.
     */
    protected final ServiceException incompatibleClientException() 
    {
        return new ServiceException(
                "This version of target service is incompatible with the server located at "
                        + serviceUrl + ".");
    }
    
    /**
     * {@inheritDoc}}
     */
    @Override
    public void destroy()
    {

    }
}
