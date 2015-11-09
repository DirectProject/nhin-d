package org.nhindirect.config.ui.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;


public class ConifgUIRunner 
{
	private static Server server;
	private static int HTTPPort;
	
	@Test
	public void dummy()
	{
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			startConfigUI();
			
			System.out.println("\r\nServer running....  Press Enter or Return to stop.");
			
			InputStreamReader input = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(input);
			
			try
			{
				reader.readLine();
				
				System.out.println("Shutting down server.  Wait 5 seconds for cleanup.");
				
				
				shutDownConfigUI();
			
				Thread.sleep(5000);
				
				System.out.println("Server stopped");
			}
			catch (Exception e)
			{
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized static void startConfigUI() throws Exception
	{
		
		if (server == null)
		{
			/*
			 * Setup the config UI
			 */
			HTTPPort = AvailablePortFinder.getNextAvailable( 8080 );
			server = new Server(HTTPPort);
			

			//connector.setPort(HTTPPort);

			// certificate service
			WebAppContext context = new WebAppContext("src/main/webapp", "/");
			context.setContextPath("/config-ui");    	
			
			//server.setSendServerVersion(false);
			//server.addConnector(connector);
			//server.addHandler(context);
		
			Configuration.ClassList classlist = Configuration.ClassList
	                .setServerDefault( server );
	        classlist.addBefore(
	                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
	                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
	 
	        // Set the ContainerIncludeJarPattern so that jetty examines these
	        // container-path jars for tlds, web-fragments etc.
	        // If you omit the jar that contains the jstl .tlds, the jsp engine will
	        // scan for them instead.
	        context.setAttribute(
	                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
	                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );		
			
	        server.setHandler( context );
	        
			server.start();
		
		}
	}
	
	public synchronized static boolean isServiceRunning()
	{
		return (server != null && server.isRunning());
	}
	
	public synchronized static void shutDownConfigUI() throws Exception
	{
		if (isServiceRunning())
		{
			server.stop();
			server = null;
		}
	}	
}
