package org.nhindirect.dns.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.URL;
import java.security.Security;

import org.nhindirect.dns.ConfigServiceDNSStore;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerSettings;

public class SimpleServiceRunner 
{
	private static int port;
	private static String bind;
	private static URL servURL;
	
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		port = 53;
		bind = "0.0.0.0";
		
		try
		{
			servURL = new URL("http://localhost:8080/config-service/ConfigurationService");
		}
		catch (Exception e) {/* no-op */}
	}

	
	public static void main(String[] argv)
	{

		// Check parameters
        for (int i = 0; i < argv.length; i++)
        {
            String arg = argv[i];

            // Options
            if (!arg.startsWith("-"))
            {
                System.err.println("Error: Unexpected argument [" + arg + "]\n");
                printUsage();
                System.exit(-1);
            }
            else if (arg.equalsIgnoreCase("-p"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing port.");
                    System.exit(-1);
                }
                
                port = Integer.parseInt(argv[++i]);
                
            }
            else if (arg.equals("-b"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing bind IP address.");
                    System.exit(-1);
                }
                bind = argv[++i];
                
                // validate its a valid IP addresses
                String checkIP = "";
                try
                {
                	String[] ips = bind.split(",");
                	for (String ip : ips)
                	{
                		checkIP = ip;
                		Inet4Address.getByName(checkIP);
                	}
                }
                catch(Exception e)
                {
                	System.err.println("Error in bind IP address " + checkIP + " : " + e.getMessage());
                }
            }
            else if (arg.equals("-u"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing service URL");
                    System.exit(-1);
                }
                String url = argv[++i];
                try
                {
                	servURL = new URL(url);
                }
                catch (Exception e)
                {
                	System.err.println("Error in service URL parameter: " + e.getMessage());
                }
            }
            else if (arg.equals("-help"))
            {
                printUsage();
                System.exit(-1);
            }            
            else
            {
                System.err.println("Error: Unknown argument " + arg + "\n");
                printUsage();
                System.exit(-1);
            }
        }

        startAndRun();
        
        System.exit(0);

	}
	
	private static void startAndRun()
	{
		DNSServerSettings settings = new DNSServerSettings();
		settings.setBindAddress(bind);
		settings.setPort(port);
		
		StringBuffer buffer = new StringBuffer("Starting DNS server.  Settings:");
		buffer.append("\r\n\tBind Addresses: ").append(bind);
		buffer.append("\r\n\tListen Port: ").append(port);
		buffer.append("\r\n\tService URL: ").append(servURL.toString());
		
		System.out.println(buffer.toString() + "\n");
		
		DNSServer server;
		
		try
		{
			ConfigServiceDNSStore store = new ConfigServiceDNSStore(servURL);
			
			server = new DNSServer(store, settings);
			
			server.start();
			
		}
		catch (Exception e)
		{
			System.err.println("Error during server startup: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		System.out.println("\r\nServer running....  Press Enter or Return to stop.");
		
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		try
		{
			reader.readLine();
			
			System.out.println("Shutting down server.  Wait 5 seconds for cleanup.");
			
			server.stop();
		
			Thread.sleep(5000);
			
			System.out.println("Server stopped");
		}
		catch (Exception e)
		{
			
		}
		
		
	}
	
	/*
	 * Prints the command line usage. 
	 */
    private static void printUsage()
    {
        StringBuffer use = new StringBuffer();
        use.append("Usage:\n");
        use.append("java SimpleDNSServiceRunner (options)...\n\n");
        use.append("options:\n");
        use.append("-p    port    IP listener port.\n");
        use.append("			      Default: 53\n\n");
        use.append("-b    bind    Comma limited list of IP addresses to bind to.\n");
        use.append("			      Default: 0.0.0.0 (All IP addresses on local machine)\n\n");        
        use.append("-u    URL	  URL of DNS configuration service.\n");
        use.append("			      Default: http://localhost:8080/config-service/ConfigurationService\n\n");        

        System.err.println(use);        
    }

}
