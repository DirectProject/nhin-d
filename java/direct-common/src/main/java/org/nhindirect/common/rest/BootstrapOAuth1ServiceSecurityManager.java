package org.nhindirect.common.rest;

import org.apache.http.client.HttpClient;

public class BootstrapOAuth1ServiceSecurityManager extends AbstractOAuth1ServiceSecurityManager
{
	public BootstrapOAuth1ServiceSecurityManager()
	{
		super();
	}
	
	public BootstrapOAuth1ServiceSecurityManager(String consumerKey, String consumerSecret, String tokenURL, HttpClient httpClient)
	{
		super();
		setProperties(consumerKey, consumerSecret, tokenURL, httpClient);
	}
	
	public void setProperties(String consumerKey, String consumerSecret, String tokenURL, HttpClient httpClient)
	{
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.tokenURL = tokenURL;
		this.httpClient = httpClient;
		
		init();
	}
}
