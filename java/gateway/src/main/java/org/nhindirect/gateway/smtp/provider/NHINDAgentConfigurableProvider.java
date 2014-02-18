package org.nhindirect.gateway.smtp.provider;

import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Provider;

public interface NHINDAgentConfigurableProvider 
{
	public void setNHINDAgentProvider(Provider<NHINDAgent> provider);
}
