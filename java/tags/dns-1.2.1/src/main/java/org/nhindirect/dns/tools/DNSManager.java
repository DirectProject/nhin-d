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


package org.nhindirect.dns.tools;

import java.net.URL;
import java.util.Arrays;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhindirect.dns.tools.utils.Commands;

/**
 * Command line tool for managing DNS entries in the Direct Project configuration service.
 * <p>
 * The tool can either be run directly from the command prompt by passing arguments directly from
 * the command line or can be run interactively by passing 0 parameters.  The only exception is setting
 * the URL to the configuration service.  By default the manager uses "http://localhost:8081/config-service/ConfigurationService"
 * as the config URL, but can be changed using the configURL command line parameters (it must be the first parameter on the 
 * command line).  
 * <p>
 * <i>DNSManager configURL http://someserver:8081/config-service/ConfigurationService</i>
 * @author Greg Meyer
 *
 * @since 1.0
 */
public class DNSManager 
{
	private static final String DEFAULT_CONFIG_URL = "http://localhost:8081/config-service/ConfigurationService"; 
	
	private final Commands commands;
	
	private static boolean exitOnEndCommands = true;
	
	/**
	 * Application entry point.
	 * @param args Command line arguments.
	 * 
	 * @since 1.0
	 */
	public static void main(String[] args)
	{
		String[] passArgs = null;
		String configURL = null;
		
		// get the config URL if it exist
		if (args.length > 1)
		{
			// check if the first argument is the config url
			if (args[0].equalsIgnoreCase("configurl"))
			{
				//the next argument should be the config URL
				configURL = args[1];
				if (args.length > 2)
					passArgs = (String[])Arrays.copyOfRange(args, 2, args.length);
				else
					passArgs = new String[0];
				
			}
		}
		
		if (configURL == null)
		{
			configURL = DEFAULT_CONFIG_URL;
			passArgs = args;
		}
		
		DNSManager manager = null;
		try
		{
			manager = new DNSManager(new URL(configURL));
		}
		catch (Exception e)
		{
			System.err.println("Invalid config URL");
		}
		
		boolean runCommand = false;
		
		if (manager != null)
		{
			runCommand = manager.run(passArgs);
		}

		if (exitOnEndCommands)
			System.exit(runCommand ? 0 : -1);			
	}	
	
	/**
	 * Constructor with the location of the configuration service.
	 * @param configURL URL containing the locations of the configuration service.
	 * 
     * @since 1.0
	 */
	public DNSManager(URL configURL)
	{
		ConfigurationServiceProxy proxy = new ConfigurationServiceProxy(configURL.toExternalForm());
		
		commands = new Commands("DNS Management Console");
		commands.register(new DNSRecordCommands(proxy));
		
		System.out.println("Configuration service URL: " + configURL.toExternalForm());
		
	}
	
	/**
	 * Either executes commands from the command line or runs the manager interactively.
	 * @param args Command arguments.  If the arguments are empty, then the manager runs interactively.
	 * @return True if the command was run successfully.  False otherwise.
	 * 
     * @since 1.0
	 */
	public boolean run(String[] args)
	{
        if (args != null && args.length > 0)
        {
            return commands.run(args);
        }
        
        commands.runInteractive();
        System.out.println("Shutting Down DNS Manager Console");
        return true;		
	}
	
	/**
	 * Determines if the application should exit when command processing is complete.  It may be desirable to set this 
	 * to false if calling from another application context.  The default is true.
	 * @param exit True if the application should terminate on completing processing commands.  False otherwise.
	 */
	public static void setExitOnEndCommands(boolean exit)
	{
		exitOnEndCommands = exit;
	}
}
