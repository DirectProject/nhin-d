package org.nhindirect.gateway.smtp.provider;

import java.net.URL;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.config.WSSmtpAgentConfig;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class WSSmtpAgentConfigProvider implements Provider<SmtpAgentConfig>, URLAccessedConfigProvider, NHINDAgentConfigurableProvider
{
	private URL configURL;
	private Provider<NHINDAgent> agentProvider;
	
	public WSSmtpAgentConfigProvider()
	{
		
	}
	
	@Inject
	public WSSmtpAgentConfigProvider(URL configURL, Provider<NHINDAgent> agentProvider)
	{
		this.configURL = configURL;
		this.agentProvider = agentProvider;
	}
	
	@Override
	public void setConfigURL(URL url) 
	{
		this.configURL = url;
		
	}
	
	@Override
	public void setNHINDAgentProvider(Provider<NHINDAgent> provider) 
	{
		this.agentProvider = provider;
	}

	public SmtpAgentConfig get()
	{
		return new WSSmtpAgentConfig(configURL, agentProvider);
	}
}
