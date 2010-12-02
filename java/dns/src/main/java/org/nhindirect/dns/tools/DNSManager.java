package org.nhindirect.dns.tools;

import java.net.URL;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhindirect.dns.tools.utils.Commands;

import edu.emory.mathcs.backport.java.util.Arrays;

public class DNSManager 
{
	private static final String DEFAULT_CONFIG_URL = "http://localhost:8080/config-service/ConfigurationService"; 
	
	final Commands commands;
	
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
		
		if (manager != null)
		{
			manager.run(passArgs);
		}
	}	
	
	public DNSManager(URL configURL)
	{
		ConfigurationServiceProxy proxy = new ConfigurationServiceProxy(configURL.toExternalForm());
		
		commands = new Commands("DNS Management Console");
		commands.register(new DNSRecordCommands(proxy));
		
		System.out.println("Configuration service URL: " + configURL.toExternalForm());
		
	}
	
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
	

}
