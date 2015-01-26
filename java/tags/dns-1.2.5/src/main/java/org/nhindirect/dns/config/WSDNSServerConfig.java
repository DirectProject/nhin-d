/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns.config;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Setting;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.module.DNSServerModule;
import org.nhindirect.dns.provider.BasicDNSServerSettingsProvider;
import org.nhindirect.dns.provider.ConfigServiceDNSStoreProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Loads DNS server configuration settings from the configuration service to create the injector.
 * @author Greg Meyer 
 * 
 * @since 1.0
 */
public class WSDNSServerConfig implements DNSServerConfig
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(WSDNSServerConfig.class);
	
	private static final String DNS_SERVER_BINDING = "DNSServerBindings";
	private static final String DNS_SERVER_PORT = "DNSServerPort";
	
	private Provider<DNSStore> storeProvider;
	private Provider<DNSServerSettings> settings;
	
	
	private final ConfigurationServiceProxy cfService;
	private final URL configServiceLocation;
	
	/**
	 * Construct and configuration component with the location of the configuration file and an optional provider for creating
	 * instances of the DNSServer.
	 * @param configFile The full path of the XML configuration file.
	 * @param storeProvider An option provider used for creating instances of the {@link DNSStore}.  If the provider is
	 * null, a default provider is used.
	 */
	public WSDNSServerConfig(URL configServiceLocation, Provider<DNSStore> storeProvider, Provider<DNSServerSettings> settings)
	{		
		this.storeProvider = storeProvider;
		this.configServiceLocation = configServiceLocation;
		this.settings = settings;
		
		cfService = new ConfigurationServiceProxy(configServiceLocation.toExternalForm());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Injector getServerInjector()
	{
		LOGGER.info("Looking up DNS server configuration info from location " + configServiceLocation.toExternalForm());
		
		Provider<DNSServerSettings> settingsProv = getServerSettings();
		
		if (storeProvider == null)
			storeProvider = new ConfigServiceDNSStoreProvider(configServiceLocation);
		
		DNSServerModule module = DNSServerModule.create(storeProvider, settingsProv);
		
		return Guice.createInjector(module);		
	}
	
	/*
	 * Just use the basic settings provider for now.  Will only allow setting the port and IP bindings.
	 */
	private Provider<DNSServerSettings> getServerSettings()
	{
		String ipBindings = "";
		int port = 0;
		
		try
		{
			Setting[] settings = cfService.getSettingsByNames(new String[] {DNS_SERVER_BINDING, DNS_SERVER_PORT});
		
			if (settings != null && settings.length > 0)
			{
				for (Setting setting : settings)
				{
					if (setting.getName().equalsIgnoreCase(DNS_SERVER_BINDING))
					{
						ipBindings = setting.getValue();
					}
					else if (setting.getName().equalsIgnoreCase(DNS_SERVER_PORT))
					{
						String sPort = setting.getValue();
						try
						{
							port = Integer.parseInt(sPort);
						}
						catch (Exception e)
						{
							LOGGER.warn("Could not parse port setting " + port + " from configuration service");
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Could not get DNS setting from web service.");
		}
		
		if ((ipBindings == null || ipBindings.length() == 0) && port == 0 && settings != null)
		{
			LOGGER.info("Using DNS server settings from injected provider.");
			return settings;
		}
		
		LOGGER.info("Using DNS server settings from configuration service.");
		return new BasicDNSServerSettingsProvider(ipBindings, port);
	}
}
