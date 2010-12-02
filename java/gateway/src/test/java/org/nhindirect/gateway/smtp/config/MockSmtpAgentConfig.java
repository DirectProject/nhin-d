package org.nhindirect.gateway.smtp.config;

import java.util.Collection;

import org.nhindirect.stagent.module.MockNHINDAgentModule;
import org.nhindirect.stagent.provider.MockNHINDAgentProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MockSmtpAgentConfig implements SmtpAgentConfig 
{	
	private final Collection<String> domains;
	
	public MockSmtpAgentConfig(Collection<String> domains)
	{
		this.domains = domains;
	}
	
	public Injector getAgentInjector()
	{		
		
		Injector injector = Guice.createInjector(MockNHINDAgentModule.create(new MockNHINDAgentProvider(domains)));
	
		//Module smtpAgentModuel = SmtpAgentModule.create(agentProvider);
		
		return injector;
	}
}
