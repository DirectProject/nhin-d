package org.nhindirect.gateway.smtp.provider;

import java.net.URL;

import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.provider.OpenServiceSecurityManagerProvider;
import org.nhindirect.gateway.smtp.config.RESTSmtpAgentConfig;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Provider;

public class RESTSmtpAgentConfigProvider implements Provider<SmtpAgentConfig>, SecureURLAccessedConfigProvider, NHINDAgentConfigurableProvider
{
	
	private URL configURL;
	private Provider<NHINDAgent> agentProvider;
	private Provider<ServiceSecurityManager> mgrProvider;
	
	public RESTSmtpAgentConfigProvider()
	{
		
	}
	
	public RESTSmtpAgentConfigProvider(URL configURL, Provider<NHINDAgent> agentProvider, Provider<ServiceSecurityManager> mgrProvider)
	{
		this.configURL = configURL;
		this.agentProvider = agentProvider;
		this.setServiceSecurityManager(mgrProvider);
	}
	
	
	@Override
	public void setConfigURL(URL url) 
	{
		this.configURL = url;
		
	}

	@Override
	public void setServiceSecurityManager(Provider<ServiceSecurityManager> mgrProvider) 
	{
		
		this.mgrProvider = (mgrProvider != null) ? mgrProvider : new OpenServiceSecurityManagerProvider();
	}

	
	@Override
	public void setNHINDAgentProvider(Provider<NHINDAgent> provider) 
	{
		this.agentProvider = provider;
	}

	public SmtpAgentConfig get()
	{
		return new RESTSmtpAgentConfig(configURL, agentProvider, mgrProvider);
	}
}
