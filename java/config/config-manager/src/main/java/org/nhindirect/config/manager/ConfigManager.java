package org.nhindirect.config.manager;

import java.net.URL;
import java.util.Arrays;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.rest.DNSService;
import org.nhind.config.rest.SettingService;
import org.nhind.config.rest.impl.DefaultDNSService;
import org.nhind.config.rest.impl.DefaultSettingService;
import org.nhindirect.common.rest.BootstrapBasicAuthServiceSecurityManager;
import org.nhindirect.common.rest.BootstrapOAuth1ServiceSecurityManager;
import org.nhindirect.common.rest.HttpClientFactory;
import org.nhindirect.common.rest.OpenServiceSecurityManager;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.dns.tools.utils.Commands;

public class ConfigManager 
{
	private static final String DEFAULT_CONFIG_URL = "http://localhost:8081/config-service/api"; 
	
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
		ConfigMgrAuthType authType = null;
		String subject = "";
		String secret = "";	
		String tokenUrl = "";
		
		// get the config URL if it exist
		
        for (int i = 0; i < args.length; i++)
        {
			// check if the first argument is the config url
			if (args[i].equalsIgnoreCase("configurl"))
			{
				//the next argument should be the config URL
				configURL = args[++i];
	
			}
			else if (args[i].equalsIgnoreCase("authType"))
			{
				//the next argument should be the config URL
				final String authTypeStr = args[++i];
				authType = ConfigMgrAuthType.valueOf(authTypeStr);
			}	
			else if (args[i].equalsIgnoreCase("subject"))
			{
				//the next argument should be the config URL
				subject = args[++i];
	
			}	
			else if (args[i].equalsIgnoreCase("secret"))
			{
				//the next argument should be the config URL
				secret = args[++i];
	
			}		
			else if (args[i].equalsIgnoreCase("tokenurl"))
			{
				//the next argument should be the config URL
				tokenUrl = args[++i];
	
			}			
			else if ((args.length - i) > 2)
				passArgs = (String[])Arrays.copyOfRange(args, i, args.length);
			else
				passArgs = new String[0];
        }
		
		if (configURL == null)
		{
			configURL = DEFAULT_CONFIG_URL;
			passArgs = args;
		}
		
		if (authType == null)
		{
			authType = ConfigMgrAuthType.OPEN;
		}
		
		ConfigManager manager = null;
		try
		{
			manager = new ConfigManager(new URL(configURL), authType, subject, secret, tokenUrl);
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
	public ConfigManager(URL configURL, ConfigMgrAuthType authType, String subject, String secret, String tokenUrl)
	{
		ConfigurationServiceProxy proxy = new ConfigurationServiceProxy(configURL.toExternalForm());
		
		
		commands = new Commands("Configuration Management Console");
		ServiceSecurityManager mgr = null;
		
		switch (authType)
		{
			case OPEN:
			{
				mgr = new OpenServiceSecurityManager();
				break;
			}
			case BASIC:
			{
				mgr = new BootstrapBasicAuthServiceSecurityManager(subject, secret);
				break;
			}
			case OAUTH1:
			{
				mgr = new BootstrapOAuth1ServiceSecurityManager(subject, secret, tokenUrl, HttpClientFactory.createHttpClient());
				break;
						
			}
			default:
				mgr = new OpenServiceSecurityManager();
		}
		
		final SettingService settingService = new DefaultSettingService(configURL.toExternalForm(), HttpClientFactory.createHttpClient(), mgr);
		final DNSService dnsService = new DefaultDNSService(configURL.toExternalForm(), HttpClientFactory.createHttpClient(), mgr);
		
		commands.register(new SettingsCommands(settingService));
		
		commands.register(new DNSRecordCommands(dnsService));
		/*
		commands.register(new CertCommands(proxy));
		commands.register(new PolicyCommands(proxy));
		
		commands.register(new DomainCommands(proxy));
		commands.register(new AnchorCommands(proxy));
		commands.register(new TrustBundleCommands(proxy));
		commands.register(new AddressCommands(proxy));
		*/
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
