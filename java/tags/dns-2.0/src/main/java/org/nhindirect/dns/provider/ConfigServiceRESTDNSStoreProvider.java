package org.nhindirect.dns.provider;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.RESTServiceDNSStore;

public class ConfigServiceRESTDNSStoreProvider extends AbstractConfigDNSStoreProvider
{
	protected final HttpClient httpClient;
	protected final ServiceSecurityManager secManager;
	
	/**
	 * Provider constructor.
	 * @param configServiceURL  A URL to the location of the DNS configuration service.
	 * @throws MalformedURLException 
	 */
	public ConfigServiceRESTDNSStoreProvider(URL configServiceURL, HttpClient httpClient, ServiceSecurityManager secManager)
	{
		super(configServiceURL);
		this.httpClient = httpClient;
		this.secManager = secManager;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DNSStore get()
	{
		return new RESTServiceDNSStore(configServiceURL.toExternalForm(), httpClient, secManager);
	}	
}
