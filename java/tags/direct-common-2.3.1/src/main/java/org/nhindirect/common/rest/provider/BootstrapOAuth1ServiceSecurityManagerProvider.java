package org.nhindirect.common.rest.provider;

import org.apache.http.client.HttpClient;
import org.nhindirect.common.rest.BootstrapBasicAuthServiceSecurityManager;
import org.nhindirect.common.rest.BootstrapOAuth1ServiceSecurityManager;
import org.nhindirect.common.rest.ServiceSecurityManager;

import com.google.inject.Provider;

/**
 * Google Guice provider for the BootstrapOAuth1ServiceSecurityManager.
 * @author Greg Meyer
 * @since 2.2
 */
public class BootstrapOAuth1ServiceSecurityManagerProvider implements Provider<ServiceSecurityManager>
{
	protected final String consumerKey;
	protected final String consumerSecret;
	protected final String tokenURL;
	protected final HttpClient httpClient;
	
	/**
	 * Constructor
	 * @param user Username
	 * @param pass Password
	 */
	public BootstrapOAuth1ServiceSecurityManagerProvider(String consumerKey, String consumerSecret, String tokenURL, HttpClient httpClient)
	{
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.tokenURL = tokenURL;
		this.httpClient = httpClient;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceSecurityManager get()
	{
		return new BootstrapOAuth1ServiceSecurityManager(consumerKey, consumerSecret, tokenURL, httpClient);
	}
}
