/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns.module;

import java.net.URL;

import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.config.DNSServerConfig;
import org.nhindirect.dns.provider.WSDNSServerConfigProvider;


import com.google.inject.AbstractModule;
import com.google.inject.Provider;

/**
 * Guice module for generating a configuration provider based on the URL protocol.
 * @author Greg Meyer
 * 
 * @since 1.0
 */
public class DNSServerConfigModule extends AbstractModule 
{
	private final URL configLocation;
	private final Provider<DNSStore> storeProvider;
	private final Provider<DNSServerSettings> settings;
	
	/**
	 * Creates a DNSServerConfigModule that is used by a Guice to create a {@link DNSServerConfig} object.  The DNSServerConfig
	 * object is subsequently used to create {@link DNSServer} objects.
	 * @param configLocation The URL that the {@link DNSServerConfig} will use to lookup configuration information.
	 * @param storeProvider An optional {@link DNSStore} provider that will create instances of DNSStore objects.
	 * @param settings Optional DNS server settings.  Overridden by settings from the configuration service.
	 * @return A configured Guice module used for create {@link DNSServerConfig} objects.
	 */
	public static DNSServerConfigModule create(URL configLocation,  Provider<DNSStore> storeProvider, Provider<DNSServerSettings> settings)
	{
		return new DNSServerConfigModule(configLocation, storeProvider, settings);
	}
	
	/*
	 * Private constructor. 
	 */
	private DNSServerConfigModule(URL configLocation,  Provider<DNSStore> storeProvider, Provider<DNSServerSettings> settings)
	{
		this.configLocation = configLocation;
		this.storeProvider = storeProvider;
		this.settings = settings;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure()
	{	
		Provider<DNSServerConfig> provider = null;
		
		if (provider == null)  
		{
			if (configLocation.getProtocol().equalsIgnoreCase("HTTP") || configLocation.getProtocol().equalsIgnoreCase("HTTPS"))
			{
				// web services based
				provider = new WSDNSServerConfigProvider(configLocation, storeProvider, settings); 
			}
			else 
			{
				throw new IllegalArgumentException("Configuration URL uses an unsupported protocol: " + configLocation.getProtocol());
			}
		}
		bind(DNSServerConfig.class).toProvider(provider);
	}
}
