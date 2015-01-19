package org.nhindirect.gateway.smtp.config.cert.impl.provider;

import org.nhind.config.rest.CertificateService;
import org.nhindirect.gateway.smtp.config.cert.impl.ConfigServiceRESTCertificateStore;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.CertificateStore;

import com.google.inject.Provider;

public class ConfigServiceRESTCertificateStoreProvider implements Provider<CertificateResolver> 
{
	private final CertificateService certService;
	private final CertificateStore bootstrapStore;
	private final CertStoreCachePolicy policy;
	
	public ConfigServiceRESTCertificateStoreProvider(CertificateService certService, CertificateStore bootstrapStore, CertStoreCachePolicy policy)
	{
		this.certService = certService;
		this.bootstrapStore = bootstrapStore;
		this.policy = policy;
	}
	
	public CertificateResolver get()
	{
		return new ConfigServiceRESTCertificateStore(certService, bootstrapStore, policy);
	}
}
