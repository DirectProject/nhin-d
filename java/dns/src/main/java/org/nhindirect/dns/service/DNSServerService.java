package org.nhindirect.dns.service;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.dns.DNSException;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerFactory;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.TCPServer;
import org.nhindirect.dns.provider.BasicDNSServerSettingsProvider;

public class DNSServerService 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(TCPServer.class);
	
	private final DNSServer server;
	
	public DNSServerService(URL configLocation, DNSServerSettings settings) throws DNSException
	{
		LOGGER.info("Creating the DNSServer using configuration location " + configLocation.toExternalForm());
		
		
		BasicDNSServerSettingsProvider settingsProv = new BasicDNSServerSettingsProvider(settings.getBindAddress(), settings.getPort());
		server = DNSServerFactory.createDNSServer(configLocation, null, settingsProv);
		
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
	
	public synchronized void stopService() throws DNSException
	{
		if (server != null)
		{
			LOGGER.info("Shutting down DNS server.");
			server.stop();
		}

	}
}
