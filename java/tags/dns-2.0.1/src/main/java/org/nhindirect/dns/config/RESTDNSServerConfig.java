package org.nhindirect.dns.config;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.nhindirect.config.model.Setting;
import org.nhind.config.rest.SettingService;
import org.nhind.config.rest.impl.DefaultSettingService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.module.DNSServerModule;
import org.nhindirect.dns.provider.BasicDNSServerSettingsProvider;
import org.nhindirect.dns.provider.ConfigServiceRESTDNSStoreProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class RESTDNSServerConfig implements DNSServerConfig
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(WSDNSServerConfig.class);
	
	private static final String DNS_SERVER_BINDING = "DNSServerBindings";
	private static final String DNS_SERVER_PORT = "DNSServerPort";
	
	private Provider<DNSStore> storeProvider;
	private Provider<DNSServerSettings> settings;
	private HttpClient httpClient;
	private ServiceSecurityManager secMgr;
	
	private final String configServiceLocation;
	private final SettingService settingService;
	
	public RESTDNSServerConfig(String configServiceLocation, HttpClient httpClient, ServiceSecurityManager secMgr,
			Provider<DNSStore> storeProvider, Provider<DNSServerSettings> settings)
	{		
		this.storeProvider = storeProvider;
		this.configServiceLocation = configServiceLocation;
		this.settings = settings;
		this.httpClient = httpClient;
		this.secMgr = secMgr;
		
		settingService = new DefaultSettingService(configServiceLocation, httpClient, secMgr);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Injector getServerInjector()
	{
		LOGGER.info("Looking up DNS server configuration info from location " + configServiceLocation);
		
		Provider<DNSServerSettings> settingsProv = getServerSettings();
		
		try
		{
			if (storeProvider == null)
				storeProvider = new ConfigServiceRESTDNSStoreProvider(new URL(configServiceLocation), httpClient, secMgr);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Conguration location is not a valid URL", e);
		}
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
			Setting setting = settingService.getSetting(DNS_SERVER_BINDING);
			if (setting != null)
				ipBindings = setting.getValue();
			
			setting = settingService.getSetting(DNS_SERVER_PORT);
			if (setting != null)
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
