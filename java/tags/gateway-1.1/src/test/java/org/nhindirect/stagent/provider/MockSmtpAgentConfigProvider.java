package org.nhindirect.stagent.provider;

import java.util.Collection;

import org.nhindirect.gateway.smtp.config.MockSmtpAgentConfig;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;

import com.google.inject.Provider;

public class MockSmtpAgentConfigProvider implements Provider<SmtpAgentConfig> 
{
	
	private Collection<String> domains;
	
	public MockSmtpAgentConfigProvider(Collection<String> domains)
	{
		this.domains = domains;
	}
	
	public SmtpAgentConfig get()
	{
		return new MockSmtpAgentConfig(domains);
	}
}