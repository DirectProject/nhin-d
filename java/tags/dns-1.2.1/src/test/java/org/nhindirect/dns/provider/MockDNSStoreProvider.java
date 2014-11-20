package org.nhindirect.dns.provider;

import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.MockDNSStore;

import com.google.inject.Provider;

public class MockDNSStoreProvider implements Provider<DNSStore>
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DNSStore get()
	{
		return new MockDNSStore();
	}
}
