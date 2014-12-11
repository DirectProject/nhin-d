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

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.nhindirect.common.rest.exceptions.ServiceException;

/**
 * Abstract service implementation for calling an unsecured service.
 * @author Greg Meyer
 * @since 1.1
 */
public abstract class AbstractUnsecuredService 
{
     
     protected final String serviceURL;
     protected final HttpClient httpClient;
     protected final ObjectMapper jsonMapper;
     
     /**
      * Constructs an instance with the service URL.
      * @param serviceUrl The URL to the target service.
      * @param httpClient An {@link HttpClient} instance used to communicate over http.
      */
     public AbstractUnsecuredService(String serviceURL, HttpClient httpClient)
     {
         if (httpClient == null || serviceURL == null || serviceURL.isEmpty()) {
             throw new IllegalArgumentException("Invalid parameter received. Got: serviceURL: "
                     + serviceURL + ", httpClient: " + httpClient);
         }
         this.httpClient = httpClient;
         this.serviceURL = serviceURL.endsWith("/") ? serviceURL : serviceURL + "/";
         this.jsonMapper = new ObjectMapper();
         this.jsonMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
         this.jsonMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
     }
     
     
     /*
      * Hides all but the last 4 characters of the string representation of the given object.
      */
     ///CLOVER:OFF
     protected static String showSensitive(Object o) 
     {
         final int charsToShow = 4;
         if (o == null)
             return "null";
         
         final String s = o.toString();
         
         final int len = s.length();
         if (len <= charsToShow)
             return s;
         
         return (s.substring(0, len - charsToShow).replaceAll(".", "*"))
                 + s.substring(len - charsToShow);
     }   
     ///CLOVER:ON
     
     /**
      * Invokes the given request with retry logic if necessary.  This implementation does not
      * actually invoke any type of retry, but is extensible for other implementations that do need
      * to execute retry logic.
      */
     protected <T, E extends Exception> T callWithRetry(ServiceRequest<T, E> request) throws E, ServiceException
     {

        try 
        {
            return request.call();
        } 
        catch (IOException e) 
        {
            throw new ServiceException(e);
        }
        finally
        {
        	request.destroy();
        }
     }
     
}

