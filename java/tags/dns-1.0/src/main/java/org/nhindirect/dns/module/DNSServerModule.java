/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns.module;

import java.net.URL;

import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.annotation.ConfigServiceURL;
import org.nhindirect.dns.provider.BasicDNSServerSettingsProvider;


import com.google.inject.AbstractModule;
import com.google.inject.Provider;

/**
 * Guice module for configuring and creating {@link DNSServer} instances.  Allows configuration either using just a URL to 
 * a configuration service, or more advanced options by using {@link Provider providers}. 
 * @author Greg Meyer
 * @since 1.0
 */
public class DNSServerModule extends AbstractModule
{
	private final URL configServiceURL;
	private final Provider<DNSStore> dnsStore;
	private final Provider<DNSServerSettings> settings;
	
	/**
	 * Creates a module using just a configuration URL.  The server is created using the ConfigServiceDNSStore class with
	 * default server settings. 
	 * @param configServiceURL A URL that provides the location to the configuration service.
	 * @return A DNSServerModule used to create instances of the DNS server.
	 */
	public static DNSServerModule create(URL configServiceURL)
	{
		if (configServiceURL == null)
			throw new IllegalArgumentException("URL cannot be null.");
		
		return new DNSServerModule(configServiceURL, null, null);
	}
	
	/**
	 * Creates a module using a specific {@link DNSStore} provider and default server settings.
	 * @param dnsStore A provider used to create instances of the {@link DNSStore}.
	 * @return A DNSServerModule used to create instances of the DNS server.
	 */
	public static DNSServerModule create(Provider<DNSStore> dnsStore)
	{
		if (dnsStore == null)
			throw new IllegalArgumentException("dnsStore cannot be null.");
		
		return new DNSServerModule(null, dnsStore, new BasicDNSServerSettingsProvider());
	}
	
	/**
	 * Creates a module using specific {@link DNSStore} and {@link DNSServerSettings} providers
	 * @param dnsStore A provider used to create instances of the {@link DNSStore}.
	 * @param settings A provider used to create instances of the {@link DNSServerSettings}.
	 * @return A DNSServerModule used to create instances of the DNS server.
	 */
	public static DNSServerModule create(Provider<DNSStore> dnsStore, Provider<DNSServerSettings> settings)
	{
		if (dnsStore == null)
			throw new IllegalArgumentException("dnsStore cannot be null.");
		
		if (settings == null)
			settings = new BasicDNSServerSettingsProvider();
		
		return new DNSServerModule(null, dnsStore, settings);
	}
	
	/*
	 * Private constructor.
	 */
	private DNSServerModule(URL configServiceURL, Provider<DNSStore> dnsStore, Provider<DNSServerSettings> settings)
	{
		this.configServiceURL = configServiceURL;
		this.dnsStore = dnsStore;
		this.settings = settings;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure()
	{
		if (configServiceURL != null)
			this.bind(URL.class).annotatedWith(ConfigServiceURL.class).toInstance(configServiceURL);
		else
		{
			this.bind(DNSStore.class).toProvider(dnsStore);
			this.bind(DNSServerSettings.class).toProvider(settings);
		}
	}
}

