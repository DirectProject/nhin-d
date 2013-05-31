package org.nhindirect.stagent.provider;

import java.util.Collection;

import org.nhindirect.stagent.MockNHINDAgent;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Provider;

public class MockNHINDAgentProvider implements Provider<NHINDAgent> 
{
	private final Collection<String> domains;
	
	public MockNHINDAgentProvider(Collection<String> domains)
	{
		this.domains = domains;
	}
	
	public NHINDAgent get()
	{
		return new MockNHINDAgent(domains);
	}
}
