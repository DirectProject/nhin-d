package org.nhindirect.gateway.smtp.config.cert.impl.provider;


import org.nhindirect.gateway.smtp.config.cert.impl.ConfigServiceCertificateStore;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhind.config.ConfigurationServiceProxy;

import com.google.inject.Provider;

public class ConfigServiceCertificateStoreProvider implements Provider<CertificateResolver> 
{
	private final ConfigurationServiceProxy proxy;
	private final CertificateStore bootstrapStore;
	private final CertStoreCachePolicy policy;
	
	public ConfigServiceCertificateStoreProvider(ConfigurationServiceProxy proxy, CertificateStore bootstrapStore, CertStoreCachePolicy policy)
	{
		this.proxy = proxy;
		this.bootstrapStore = bootstrapStore;
		this.policy = policy;
	}
	
	public CertificateResolver get()
	{
		return new ConfigServiceCertificateStore(proxy, bootstrapStore, policy);
	}
}
