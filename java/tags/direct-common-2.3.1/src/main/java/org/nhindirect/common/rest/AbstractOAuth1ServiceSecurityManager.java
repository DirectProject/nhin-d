package org.nhindirect.common.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import oauth.signpost.exception.OAuthException;

public class AbstractOAuth1ServiceSecurityManager implements ServiceSecurityManager
{
	protected String consumerKey;
	protected String consumerSecret;
	protected String tokenURL;
	protected HttpClient httpClient;
	
	protected OAuthManager oauthManager;
	
	public AbstractOAuth1ServiceSecurityManager()
	{
		
	}

	@Override
	public void init()
	{
        if (StringUtils.isEmpty(consumerKey) || StringUtils.isEmpty(consumerSecret) || StringUtils.isEmpty(tokenURL) || httpClient == null)
 		{
            throw new IllegalStateException("Invalid parameter received. Got:, consumerKey: "
                    + consumerKey + ", consumerSecret: " + showSensitive(consumerSecret)
                    + ", accessTokenUrl: " + tokenURL + ", httpClient: " + httpClient);
        }
	
        try
        {
        	oauthManager = new OAuthManager(consumerKey, consumerSecret, tokenURL, httpClient);
        }
        catch (OAuthException e) 
        {
            throw new IllegalStateException("Could not initialize oAuthManager", e);
        }
	}

	@Override
	public void authenticateSession()
	{
		try
		{
			oauthManager.updateToken();
		}
        catch (OAuthException e) 
        {
            throw new IllegalStateException("Token could not be updated.", e);
        }
	}

	@Override
	public HttpUriRequest createAuthenticatedRequest(HttpUriRequest request)
	{
        try
        {
        	oauthManager.getOAuthConsumer().sign(request);
        	return request;
        }
        catch (OAuthException e)
        {
        	throw new IllegalStateException("Failed to sign request.", e);
        }
	}
	
	/*
    * Hides all but the last 4 characters of the string representation of the given object.
    */
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
}
