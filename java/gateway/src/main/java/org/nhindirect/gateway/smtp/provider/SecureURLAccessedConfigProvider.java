package org.nhindirect.gateway.smtp.provider;

import org.nhindirect.common.rest.ServiceSecurityManager;

import com.google.inject.Provider;

public interface SecureURLAccessedConfigProvider extends URLAccessedConfigProvider
{	
	public void setServiceSecurityManager(Provider<ServiceSecurityManager> mgrProvder);
}
