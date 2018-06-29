package org.nhindirect.monitor;

import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationContext;

public class TxsServiceRunner 
{
	private static Server server;
	private static int HTTPPort;
	private static String txsServiceURL;
	
	public synchronized static void startTxsService() throws Exception
	{
		
		if (server == null)
		{
			/*
			 * Setup the configuration service server
			 */
			HTTPPort = AvailablePortFinder.getNextAvailable( 1024 );
			server = new Server(HTTPPort);
	
			
			new WebAppContext(server, "src/test/resources/webapp", "/");
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
