package org.nhindirect.stagent.module;

import java.util.Collection;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.stagent.provider.MockSmtpAgentConfigProvider;

import com.google.inject.AbstractModule;


public class MockSmtpConfigModule extends AbstractModule 
{
	
	private final Collection<String> domains;
	
	public static MockSmtpConfigModule create(Collection<String> domains)
	{
		return new MockSmtpConfigModule(domains);
	}
	
	private MockSmtpConfigModule(Collection<String> domains)
	{
		this.domains = domains;
	}
	
	protected void configure()
	{	

		bind(SmtpAgentConfig.class).toProvider(new MockSmtpAgentConfigProvider(domains));
	}
}
