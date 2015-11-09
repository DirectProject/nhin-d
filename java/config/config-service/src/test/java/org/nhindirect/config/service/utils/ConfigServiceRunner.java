package org.nhindirect.config.service.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.mina.util.AvailablePortFinder;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class ConfigServiceRunner 
{
	private static Server server;
	private static int HTTPPort;
	private static String serviceURL;
	
	@Test
	public void dummy()
	{
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			startConfigService();
			
			System.out.println("\r\nServer running....  Press Enter or Return to stop.");
			
			InputStreamReader input = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(input);
			
			try
			{
				reader.readLine();
				
				System.out.println("Shutting down server.  Wait 5 seconds for cleanup.");
				
				
				shutDownService();
			
				Thread.sleep(5000);
				
				System.out.println("Server stopped");
			}
			catch (Exception e)
			{
				
			}
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
			
			HTTPPort = AvailablePortFinder.getNextAvailable( 8081 );
			connector.setPort(HTTPPort);

			// certificate service
			WebAppContext context = new WebAppContext("src/main/webapp", "/config-service");
			    	
			server.setSendServerVersion(false);
			server.addConnector(connector);
			server.addHandler(context);
		
			server.start();
			
			serviceURL = "http://localhost:" + HTTPPort + "/";
		
		}
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
	
	public synchronized static String getHPDServiceURL()
	{
		return serviceURL;
	}
}
