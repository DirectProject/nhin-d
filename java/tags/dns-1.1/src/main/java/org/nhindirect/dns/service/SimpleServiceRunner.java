/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.URL;
import java.security.Security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.dns.DNSException;
import org.nhindirect.dns.DNSServerSettings;


/**
 * Simple command line based application that launches the DNS server.  Use the -help runtime parameter
 * to see usage.
 * @author Greg Meyer
 * @since 1.0
 */
public class SimpleServiceRunner 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(SimpleServiceRunner.class);
	
	private static final String MODE_STANDALONE = "STANDALONE";
	private static final String MODE_SERVER = "SERVER";
	
	private static int port;
	private static String bind;
	private static URL servURL;
	private static String mode;
	
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		port = 53;
		bind = "0.0.0.0";
		mode = MODE_STANDALONE;
		
		try
		{
			servURL = new URL("http://localhost:8080/config-service/ConfigurationService");
		}
		catch (Exception e) {/* no-op */}
	}

	/**
	 * Main entry point into the application.
	 * @param argv Command line arguments.
	 */
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
                    System.exit(-1);
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
                    System.exit(-1);
                }
            }
            else if (arg.equals("-m"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing mode");
                    System.exit(-1);
                }
                mode = argv[++i];
                if (!mode.equalsIgnoreCase(MODE_STANDALONE) && !mode.equalsIgnoreCase(MODE_SERVER))
                {
                	System.err.println("Unknown mode: " + mode);
                	System.exit(-1);
                    
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
        
        if (mode.equalsIgnoreCase(MODE_STANDALONE))
        	System.exit(0);

	}
	
	/*
	 * Creates, intializes, and runs the server.
	 */
	private static void startAndRun()
	{
		StringBuffer buffer = new StringBuffer("Starting DNS server.  Settings:");
		buffer.append("\r\n\tBind Addresses: ").append(bind);
		buffer.append("\r\n\tListen Port: ").append(port);
		buffer.append("\r\n\tService URL: ").append(servURL.toString());
		LOGGER.info(buffer.toString() + "\n");

		
		DNSServerService server = null;
		try
		{
			DNSServerSettings settings = new DNSServerSettings();
			settings.setPort(port);
			settings.setBindAddress(bind);
			
			server = new DNSServerService(servURL, settings);
		}
		catch (DNSException e)
		{
			LOGGER.error("Server failed to start: " + e.getMessage(), e);
			return;
		}
			
		if (mode.equalsIgnoreCase(MODE_STANDALONE))
		{
			LOGGER.info("\r\nServer running....  Press Enter or Return to stop.");
			
			InputStreamReader input = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(input);
			
			try
			{
				reader.readLine();
				
				LOGGER.info("Shutting down server.  Wait 5 seconds for cleanup.");
				
				server.stopService();
			
				Thread.sleep(5000);
				
				LOGGER.info("Server stopped");
			}
			catch (Exception e)
			{
				
			}
		}				
		else
			LOGGER.info("\r\nServer running.");
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
        use.append("-m    mode	  Run mode: STANDALONE or SERVER.\n");
        use.append("			      Default: STANDALONE\n\n");        

        
        System.err.println(use);        
    }

}
