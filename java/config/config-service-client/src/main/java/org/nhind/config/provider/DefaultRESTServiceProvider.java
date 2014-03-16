package org.nhind.config.provider;

import java.lang.reflect.Constructor;

import org.apache.http.client.HttpClient;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.HttpClientFactory;
import org.nhindirect.common.rest.ServiceSecurityManager;

import com.google.inject.Provider;

public class DefaultRESTServiceProvider<T> implements Provider<T>  
{
	protected final String serviceURL;
	protected final HttpClient client;
	protected final ServiceSecurityManager securityManager;
	protected final Class<? extends AbstractSecuredService> clazz;
	
	protected static ServiceSecurityManager getInitiziedSecurityManager(Provider<ServiceSecurityManager> securityManagerProvider)
	{
		final ServiceSecurityManager retVal = securityManagerProvider.get();
		retVal.init();
		
		return retVal;
	}
	
	public DefaultRESTServiceProvider(String serviceURL, Provider<ServiceSecurityManager> securityManagerProvider, Class<? extends AbstractSecuredService> clazz)
	{
		this(serviceURL, HttpClientFactory.createHttpClient(), getInitiziedSecurityManager(securityManagerProvider), clazz);
	}
	
	public DefaultRESTServiceProvider(String serviceURL, ServiceSecurityManager securityManager, Class<? extends AbstractSecuredService> clazz)
	{
		this(serviceURL, HttpClientFactory.createHttpClient(), securityManager, clazz);
	}
	
	@SuppressWarnings("static-access")
	public DefaultRESTServiceProvider(String serviceURL, HttpClientFactory factory, Provider<ServiceSecurityManager> securityManagerProvider,
			Class<? extends AbstractSecuredService> clazz)
	{
		this(serviceURL, factory.createHttpClient(), getInitiziedSecurityManager(securityManagerProvider), clazz);
	}
	
	@SuppressWarnings("static-access")
	public DefaultRESTServiceProvider(String serviceURL, HttpClientFactory factory, ServiceSecurityManager securityManager, 
			Class<? extends AbstractSecuredService> clazz)
	{
		this(serviceURL, factory.createHttpClient(), securityManager, clazz);
	}
	
	public DefaultRESTServiceProvider(String serviceURL, HttpClient client, ServiceSecurityManager securityManager,
			Class<? extends AbstractSecuredService> clazz)
	{
		this.serviceURL = serviceURL;
		this.client = client;
		this.securityManager = securityManager;
		this.clazz = clazz;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T get()
	{
    	
    	AbstractSecuredService retVal = null;
    	
    	try
    	{
    		final Constructor<?> ctr = clazz.getDeclaredConstructor(String.class, HttpClient.class, ServiceSecurityManager.class);
		
    		retVal = (AbstractSecuredService)ctr.newInstance(serviceURL, client, securityManager);
    	}
    	catch (Exception e)
    	{
    		throw new IllegalStateException("REST service provider is not configured correctly.");
    	}
    	
    	return (T)retVal;
	}
}
