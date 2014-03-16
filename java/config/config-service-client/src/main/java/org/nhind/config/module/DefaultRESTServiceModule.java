package org.nhind.config.module;

import org.nhind.config.provider.DefaultRESTServiceProvider;
import org.nhind.config.rest.AddressService;
import org.nhind.config.rest.AnchorService;
import org.nhind.config.rest.CertPolicyService;
import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.DNSService;
import org.nhind.config.rest.DomainService;
import org.nhind.config.rest.SettingService;
import org.nhind.config.rest.TrustBundleService;
import org.nhind.config.rest.impl.DefaultAddressService;
import org.nhind.config.rest.impl.DefaultAnchorService;
import org.nhind.config.rest.impl.DefaultCertPolicyService;
import org.nhind.config.rest.impl.DefaultCertificateService;
import org.nhind.config.rest.impl.DefaultDNSService;
import org.nhind.config.rest.impl.DefaultDomainService;
import org.nhind.config.rest.impl.DefaultSettingService;
import org.nhind.config.rest.impl.DefaultTrustBundleService;
import org.nhindirect.common.rest.ServiceSecurityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class DefaultRESTServiceModule extends AbstractModule 
{
	private final ServiceSecurityManager securityManager; 
	private final String serviceURL;
	
	protected static ServiceSecurityManager getInitiziedSecurityManager(Provider<ServiceSecurityManager> securityManagerProvider)
	{
		final ServiceSecurityManager retVal = securityManagerProvider.get();
		retVal.init();
		
		return retVal;
	}
	
	public static DefaultRESTServiceModule create(String serviceURL, Provider<ServiceSecurityManager> securityManagerProvider)
	{
		return new DefaultRESTServiceModule(serviceURL, getInitiziedSecurityManager(securityManagerProvider));
	}
	
	public static DefaultRESTServiceModule create(String serviceURL, ServiceSecurityManager securityManager)
	{
		return new DefaultRESTServiceModule(serviceURL, securityManager);
	}	
	
	private DefaultRESTServiceModule(String serviceURL, ServiceSecurityManager securityManager)
	{
		this.securityManager = securityManager;
		this.serviceURL = serviceURL;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure()
	{   
		bind(AddressService.class).toProvider(new DefaultRESTServiceProvider<AddressService>(serviceURL, securityManager, DefaultAddressService.class));
		bind(AnchorService.class).toProvider(new DefaultRESTServiceProvider<AnchorService>(serviceURL, securityManager, DefaultAnchorService.class));
		bind(CertificateService.class).toProvider(new DefaultRESTServiceProvider<CertificateService>(serviceURL, securityManager, DefaultCertificateService.class));
		bind(CertPolicyService.class).toProvider(new DefaultRESTServiceProvider<CertPolicyService>(serviceURL, securityManager, DefaultCertPolicyService.class));
		bind(DNSService.class).toProvider(new DefaultRESTServiceProvider<DNSService>(serviceURL, securityManager, DefaultDNSService.class));
		bind(DomainService.class).toProvider(new DefaultRESTServiceProvider<DomainService>(serviceURL, securityManager, DefaultDomainService.class));
		bind(SettingService.class).toProvider(new DefaultRESTServiceProvider<SettingService>(serviceURL, securityManager, DefaultSettingService.class));
		bind(TrustBundleService.class).toProvider(new DefaultRESTServiceProvider<TrustBundleService>(serviceURL, securityManager, DefaultTrustBundleService.class));
	}
}
