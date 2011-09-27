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


package org.nhindirect.dns.service;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.dns.DNSException;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerFactory;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.DNSStore;
import org.nhindirect.dns.provider.AbstractConfigDNSStoreProvider;
import org.nhindirect.dns.provider.BasicDNSServerSettingsProvider;

import com.google.inject.Provider;

/**
 * Service wrapper that instantiates and configures the DNS server.
 * @author Greg Meyer
 *
 * @since 1.0
 */
public class DNSServerService 
{
	protected static final String DNS_STORE_PROVIDER_VAR = "org.nhindirect.dns.DNSStoreProviderClass";
	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(DNSServerService.class);
	
	protected final DNSServer server;
	
	/**
	 * Creates the service wrapper with the location of the configuration service and server settings.
	 * @param configLocation URL with the location of the configuration service.
	 * @param settings Default settings for the server.  Settings in the configuration service can over ride these settings.
	 * @throws DNSException
	 * 
	 * @since 1.0
	 */
	public DNSServerService(URL configLocation, DNSServerSettings settings) throws DNSException
	{
		LOGGER.info("Creating the DNSServer using configuration location " + configLocation.toExternalForm());
		
		
		BasicDNSServerSettingsProvider settingsProv = new BasicDNSServerSettingsProvider(settings.getBindAddress(), settings.getPort());
		
		Provider<DNSStore> dnsStoreProvider = getDNSStoreProvider(configLocation);
		
		server = DNSServerFactory.createDNSServer(configLocation, dnsStoreProvider, settingsProv);
		
		LOGGER.info("DNS Server created.  Starting server.");
		server.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() 
		{
		    public void run() 
		    { 
		    	try
		    	{
		    		LOGGER.info("Shutdown hook detected.  Intiate server shutdown.");
		    		stopService();
		    	}
		    	catch (DNSException e) {/* no-op */}
		    	
		    }
		});
	}
	
	/**
	 * Creates a {@link Provider<DNSStore>} instance based on the system property
	 * org.nhindirect.dns.DNSStoreProviderClass.  This property is the fully qualified class name
	 * of the provider.  If the provider extends the {@link AbstractConfigDNSStoreProvider} class, then the configuration service
	 * location will be passed at construction time.
	 * @param configLocation The URL of the configuration service.
	 * @return A constructed instance of a provider.  If the system cannot locate the Provider class specified by the
	 * org.nhindirect.dns.DNSStoreProviderClass system property of if the property does not exist, the method
	 * will return null.
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected Provider<DNSStore> getDNSStoreProvider(URL configLocation)
	{
		Provider<DNSStore> retVal = null;
		
		// get the system property
		String className = System.getProperty(DNS_STORE_PROVIDER_VAR);
		if (className != null && !className.isEmpty())
		{
			try
			{
				Class<?> loadedClazz = DNSServerService.class.getClassLoader().loadClass(className);
				
				if (AbstractConfigDNSStoreProvider.class.isAssignableFrom(loadedClazz))
				{
					// this provider takes a URL for the constructor
					retVal = (Provider<DNSStore>)loadedClazz.getConstructor(URL.class).newInstance(configLocation);
				}
				else
					retVal = (Provider<DNSStore>)loadedClazz.newInstance();
				
				LOGGER.info("Loaded Provider<DNSStore> class " + className + " for creating the DNSStore");
			}
			catch(Throwable e)
			{
				LOGGER.error("Could not load or construct instance of Provider<DNSStore> class " + className + "  A default " +
						"provider will be used." , e);
			}
		}
		else
			LOGGER.info("A DNSStore provider class was not set.  A default provider will be used.");	
		
		return retVal;
	}
	
	/**
	 * Stops and shutdown the service.
	 * @throws DNSException
	 * 
	 * @since 1.0
	 */
	public synchronized void stopService() throws DNSException
	{
		if (server != null)
		{
			LOGGER.info("Shutting down DNS server.");
			server.stop();
		}

	}
}
