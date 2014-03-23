package org.nhindirect.dns.provider;

import java.net.URL;

import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.MockDNSStore;

public class MockConfigDNSStoreProvider extends AbstractConfigDNSStoreProvider
{
	
	public MockConfigDNSStoreProvider(URL configServiceURL)
	{
		super(configServiceURL);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DNSStore get()
	{
		return new MockDNSStore();
	}
}
