package org.nhindirect.dns.provider;

import java.net.URL;

import org.nhindirect.dns.DNSStore;

import com.google.inject.Provider;

/**
 * Abstract Guice provider for DNSStoreProviders that use the configuration service to retrieve runtime configuration information.
 * @author Greg Meyer
 *
 * @since 1.0
 */

public abstract class AbstractConfigDNSStoreProvider implements Provider<DNSStore>
{
	protected final URL configServiceURL;
	
	/**
	 * Provider constructor.
	 * @param configServiceURL  A URL to the location of the DNS configuration service.
	 */
	public AbstractConfigDNSStoreProvider(URL configServiceURL)
	{
		this.configServiceURL = configServiceURL;
	}
	
}
