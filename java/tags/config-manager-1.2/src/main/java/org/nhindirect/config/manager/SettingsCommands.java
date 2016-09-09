package org.nhindirect.config.manager;


import java.util.Arrays;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhindirect.config.manager.printers.RecordPrinter;
import org.nhindirect.config.manager.printers.SettingRecordPrinter;
import org.nhindirect.dns.tools.utils.Command;

public class SettingsCommands 
{
    private static final String LIST_SETTINGS_USAGE = "Lists all settings in the system";
    
	protected ConfigurationServiceProxy proxy;
    
	protected RecordPrinter<org.nhind.config.Setting> settingsPrinter;
    
	public SettingsCommands(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy;
		
		this.settingsPrinter = new SettingRecordPrinter();
	}  
	
	@Command(name = "ListSettings", usage = LIST_SETTINGS_USAGE)
    public void listCerts(String[] args)
	{
		try
		{
			final org.nhind.config.Setting[] settings = proxy.getSettingsByNames(null);
			if (settings == null || settings.length == 0)
				System.out.println("No settings found");
			else
			{
				settingsPrinter.printRecords(Arrays.asList(settings));
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to lookup certificates: " + e.getMessage());
		}

	}	
}
