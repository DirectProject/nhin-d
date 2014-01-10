package org.nhindirect.config.manager;

import java.net.URL;
import java.util.Arrays;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhindirect.dns.tools.DNSRecordCommands;
import org.nhindirect.dns.tools.utils.Commands;

public class ConfigManager 
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
		
		ConfigManager manager = null;
		try
		{
			manager = new ConfigManager(new URL(configURL));
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
	public ConfigManager(URL configURL)
	{
		ConfigurationServiceProxy proxy = new ConfigurationServiceProxy(configURL.toExternalForm());
		
		commands = new Commands("Configuration Management Console");
		commands.register(new DNSRecordCommands(proxy));
		commands.register(new CertCommands(proxy));
		commands.register(new PolicyCommands(proxy));
		
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
        System.out.println("Shutting Down Configuration Manager Console");
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
