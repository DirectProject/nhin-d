package org.nhindirect.install;

import java.net.HttpURLConnection;
import java.net.URL;

public class ValidateConfigServiceRunning 
{
	public static void main(String[] args)
	{
		final String configServiceUrl = args[0];
		
		int idx = configServiceUrl.lastIndexOf("ConfigurationService");
		
		String checkConfigServiceURL = "";
		
		if (idx > -1)
			checkConfigServiceURL = configServiceUrl.substring(0, idx);
		else
			checkConfigServiceURL = configServiceUrl;
		
		final int maxTries = 10;
		int attemps = 0;
		boolean connected = false;
		
		while ((attemps < maxTries) && !connected)
		{
			try
			{
				HttpURLConnection con = (HttpURLConnection) new URL(checkConfigServiceURL).openConnection();
				con.setRequestMethod("HEAD");
				con.setConnectTimeout(5000); 
				      
				if (con.getResponseCode() == HttpURLConnection.HTTP_OK)
				{	
					connected = true;
					break;
				}
				
				++attemps;
			}
			catch (Exception e)
			{
				try
				{ Thread.sleep(10000);} catch (Exception ex){};
				++attemps;
			}
		}
		
		if (!connected)
			throw new RuntimeException("Coulnd not successfully validate tomcat service availablility at " + checkConfigServiceURL);
	}
}
