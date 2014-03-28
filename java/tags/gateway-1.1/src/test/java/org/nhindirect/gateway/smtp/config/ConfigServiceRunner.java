package org.nhindirect.gateway.smtp.config;

import org.apache.mina.util.AvailablePortFinder;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class ConfigServiceRunner 
{

	private static Server server;
	private static int HTTPPort;
	private static String configServiceURL;
	
	public synchronized static void startConfigService() throws Exception
	{
		
		if (server == null)
		{
			/*
			 * Setup the configuration service server
			 */
			server = new Server();
			SocketConnector connector = new SocketConnector();
			
			HTTPPort = AvailablePortFinder.getNextAvailable( 1024 );
			connector.setPort(HTTPPort);
			
			WebAppContext context = new WebAppContext();
		    
			context.setContextPath("/config");	 
			context.setServer(server);
			context.setWar("war/config-service.war");
			    	
			server.setSendServerVersion(false);
			server.addConnector(connector);
			server.addHandler(context);
			
			server.start();
			
			configServiceURL = "http://localhost:" + HTTPPort + "/config/ConfigurationService";
		
		}
	}
	

	public synchronized static boolean isServiceRunning()
	{
		return (server != null && server.isRunning());
	}
	
	public synchronized static void shutDownConfigService() throws Exception
	{
		if (isServiceRunning())
		{
			server.stop();
			server = null;
		}
	}
	
	public synchronized static String getConfigServiceURL()
	{
		return configServiceURL;
	}
}
