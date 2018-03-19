package org.nhindirect.dns.provider;

import org.apache.http.client.HttpClient;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.config.DNSServerConfig;
import org.nhindirect.dns.config.RESTDNSServerConfig;

import com.google.inject.Provider;

public class RESTDNSServerConfigProvider implements Provider<DNSServerConfig>
{
	private final String configURL;
	private final Provider<DNSStore> storeProvider;
	private final Provider<DNSServerSettings> settings;
	private final HttpClient httpClient;
	private final ServiceSecurityManager secMgr;
	
	public RESTDNSServerConfigProvider(String configURL, HttpClient httpClient, ServiceSecurityManager secMgr, 
			Provider<DNSStore> storeProvider, Provider<DNSServerSettings> settings)
	{
		this.configURL = configURL;
		this.storeProvider = storeProvider;
		this.settings = settings;
		this.httpClient = httpClient;
		this.secMgr = secMgr;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DNSServerConfig get()
	{
		return new RESTDNSServerConfig(configURL, httpClient, secMgr, storeProvider, settings);
	}
}
