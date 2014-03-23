package org.nhindirect.monitor;

import org.apache.mina.util.AvailablePortFinder;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationContext;

public class TxsServiceRunner 
{
	private static Server server;
	private static int HTTPPort;
	private static String txsServiceURL;
	
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
	
	public synchronized static void startTxsService() throws Exception
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
			
			txsServiceURL = "http://localhost:" + HTTPPort + "/";
		
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
	
	public synchronized static String getTxsServiceURL()
	{
		return txsServiceURL;
	}
	
}
