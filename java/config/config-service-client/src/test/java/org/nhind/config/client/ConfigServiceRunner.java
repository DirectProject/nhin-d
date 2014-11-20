package org.nhind.config.client;

import org.apache.mina.util.AvailablePortFinder;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.nhind.config.testbase.TestApplicationContext;
import org.springframework.context.ApplicationContext;

public class ConfigServiceRunner 
{
	private static Server server;
	private static int HTTPPort;
	private static String configServiceURL;
	private static String restAPIBaseURL;
	
	static
	{
		try
		{
			System.setProperty("derby.system.home", "target/data");	
		}
		catch (Exception e)
		{
			
		}
	}	
	
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
	
			
			WebAppContext context = new WebAppContext("src/test/resources/webapp", "/");

			    	
			server.setSendServerVersion(false);
			server.addConnector(connector);
			server.addHandler(context);
			
			server.start();
			
			configServiceURL = "http://localhost:" + HTTPPort + "/ConfigurationService";
			restAPIBaseURL = "http://localhost:" + HTTPPort + "/api";
		
		}
	}
	
	public synchronized static ApplicationContext getSpringApplicationContext()
	{
		return TestApplicationContext.getApplicationContext();
	}

	public synchronized static boolean isServiceRunning()
	{
		return (server != null && server.isRunning());
	}
	
	public synchronized static void shutDownService() throws Exception
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
	
	public synchronized static String getRestAPIBaseURL()
	{
		return restAPIBaseURL;
	}	
}
