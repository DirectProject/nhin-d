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
import org.nhindirect.common.rest.exceptions.AuthorizationException;
import org.nhindirect.common.rest.exceptions.ServiceException;


/**
 * Abstract implementation that can access secured resources.  
 * @author Greg Meyer
 * @since 1.3
 */
public abstract class AbstractSecuredService extends AbstractUnsecuredService
{
	protected final ServiceSecurityManager securityManager;
	
    /**
     * Constructs an instance with the service URL and security manager
     * @param serviceUrl The URL to the target service.
     * @param httpClient An {@link HttpClient} instance used to communicate over http.
     * @param securityManager The security manager used to authenticate requests.
     */
    public AbstractSecuredService(String serviceURL, HttpClient httpClient, ServiceSecurityManager securityManager)
    {
        super(serviceURL, httpClient);
    	
    	if (securityManager == null) 
        {
            throw new IllegalArgumentException("Security manager cannot be null");
        }
        
        this.securityManager = securityManager;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected <T, E extends Exception> T callWithRetry(ServiceRequest<T, E> request) throws E, ServiceException
    {
        try 
        {
            return new AuthRetryRequest<T, E>(request).call();
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

   /*
    * Internal class for attempting service retries.  This is important for secured services that may 
    * implement expired tokens that must be renewed or reauthenticated.
    */
   private class AuthRetryRequest<T, E extends Exception> implements ServiceRequest<T, E> 
   {

       private final ServiceRequest<T, E> request;

       public AuthRetryRequest(ServiceRequest<T, E> other) 
       {
           this.request = other;
       }

       @Override
       public T call() throws E, IOException, ServiceException 
       {
           int retries = 1;
           while (true) 
           {
               try 
               {
                   return request.call();
               } 
               ///CLOVER:OFF
               catch (AuthorizationException e) 
               {
                   if ((retries-- > 0)) 
                   {
                	   securityManager.authenticateSession();
                       continue;
                   }
                   throw e;
               }
               ///CLOVER:ON
           }
       }
       
       @Override
       public void destroy()
       {
    	   request.destroy();
       }
   }
}
