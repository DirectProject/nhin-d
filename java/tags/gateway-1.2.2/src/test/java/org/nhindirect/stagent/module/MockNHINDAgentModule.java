package org.nhindirect.stagent.module;

import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class MockNHINDAgentModule extends AbstractModule 
{
	private Provider<NHINDAgent> provider;
	
	public static MockNHINDAgentModule create(Provider<NHINDAgent> provider)
	{
		return new MockNHINDAgentModule(provider);
	}
	
	private MockNHINDAgentModule(Provider<NHINDAgent> provider)
	{
		this.provider = provider;
	}
	
	protected void configure()
	{
		bind(NHINDAgent.class).toProvider(provider);
	}
}
