package org.nhindirect.gateway.smtp.provider;

import java.net.URL;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.config.WSSmtpAgentConfig;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class WSSmtpAgentConfigProvider implements Provider<SmtpAgentConfig> 
{
	private final URL configURL;
	private final Provider<NHINDAgent> agentProvider;
	
	@Inject
	public WSSmtpAgentConfigProvider(URL configURL, Provider<NHINDAgent> agentProvider)
	{
		this.configURL = configURL;
		this.agentProvider = agentProvider;
	}
	
	public SmtpAgentConfig get()
	{
		return new WSSmtpAgentConfig(configURL, agentProvider);
	}
}
