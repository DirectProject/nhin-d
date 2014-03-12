package org.nhindirect.gateway.smtp.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.Mailet;
import org.nhindirect.common.rest.BootstrapBasicAuthServiceSecurityManager;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.gateway.GatewayConfiguration;
import org.nhindirect.gateway.smtp.james.mailet.SecurityAndTrustMailetOptions;
import org.nhindirect.stagent.options.OptionsManager;

import com.google.inject.Provider;

public class ConfigBasicAuthServiceSecurityManagerProvider implements Provider<ServiceSecurityManager>, MailetAwareProvider
{
	static
	{		
		initJVMParams();
	}
	
	private synchronized static void initJVMParams()
	{
		/*
		 * Mailet configuration parameters
		 */
		final Map<String, String> JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SUBJECT, "org.nhindirect.gateway.smtp.james.mailet.ServiceSecurityAuthSubject");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SECRET, "org.nhindirect.gateway.smtp.james.mailet.ServiceSecurityAuthSecret");
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	protected Mailet mailet;

	public ConfigBasicAuthServiceSecurityManagerProvider()
	{
	}
	
	@Override
	public void setMailet(Mailet mailet) 
	{
		this.mailet = mailet;
	}
	
	public ServiceSecurityManager get()
	{
		// load information via the options manager
		
		final String user = GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SUBJECT, mailet, "");
		final String pass = GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SECRET, mailet, "");
		
		return new BootstrapBasicAuthServiceSecurityManager(user, pass);
	}
}