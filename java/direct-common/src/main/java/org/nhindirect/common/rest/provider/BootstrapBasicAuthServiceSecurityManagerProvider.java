package org.nhindirect.common.rest.provider;

import org.nhindirect.common.rest.BootstrapBasicAuthServiceSecurityManager;
import org.nhindirect.common.rest.ServiceSecurityManager;

import com.google.inject.Provider;

public class BootstrapBasicAuthServiceSecurityManagerProvider implements Provider<ServiceSecurityManager>
{
	protected final String user;
	protected final String pass;
	
	public BootstrapBasicAuthServiceSecurityManagerProvider(String user, String pass)
	{
		this.user = user;
		this.pass = pass;
	}
	
	public ServiceSecurityManager get()
	{
		return new BootstrapBasicAuthServiceSecurityManager(user, pass);
	}
}