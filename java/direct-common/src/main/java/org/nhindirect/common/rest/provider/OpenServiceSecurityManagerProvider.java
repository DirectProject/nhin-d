package org.nhindirect.common.rest.provider;

import org.nhindirect.common.rest.OpenServiceSecurityManager;
import org.nhindirect.common.rest.ServiceSecurityManager;

import com.google.inject.Provider;

public class OpenServiceSecurityManagerProvider implements Provider<ServiceSecurityManager>
{
	public OpenServiceSecurityManagerProvider()
	{
		/* empty constructor */
	}
	
	public ServiceSecurityManager get()
	{
		return new OpenServiceSecurityManager();
	}
}
