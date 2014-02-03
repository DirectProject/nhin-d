package org.nhindirect.common.rest;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.nhindirect.common.rest.exceptions.AuthException;
import org.nhindirect.common.rest.exceptions.ServiceException;


public abstract class AbstractSecuredService extends AbstractUnsecuredService
{
	protected final ServiceSecurityManager securityManager;
	
    public AbstractSecuredService(String serviceURL, HttpClient httpClient, ServiceSecurityManager securityManager)
    {
        super(serviceURL, httpClient);
    	
    	if (securityManager == null) 
        {
            throw new IllegalArgumentException("Security manager cannot be null");
        }
        
        this.securityManager = securityManager;
    }
    
    
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
               catch (AuthException e) 
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
