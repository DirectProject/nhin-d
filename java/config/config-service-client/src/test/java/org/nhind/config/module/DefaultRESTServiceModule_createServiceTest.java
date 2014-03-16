package org.nhind.config.module;

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
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.OpenServiceSecurityManager;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class DefaultRESTServiceModule_createServiceTest
{
	@Test 
	public void testCreateServiceFromModule() throws Exception
	{
		final Injector configInjector = Guice.createInjector(DefaultRESTServiceModule.create("http://bogus", new OpenServiceSecurityManager()));
		
		AbstractSecuredService service = (AbstractSecuredService)configInjector.getInstance(AddressService.class);
		assertTrue(service instanceof DefaultAddressService);
		
		service = (AbstractSecuredService)configInjector.getInstance(AnchorService.class);
		assertTrue(service instanceof DefaultAnchorService);
		
		service = (AbstractSecuredService)configInjector.getInstance(CertificateService.class);
		assertTrue(service instanceof DefaultCertificateService);
		
		service = (AbstractSecuredService)configInjector.getInstance(CertPolicyService.class);
		assertTrue(service instanceof DefaultCertPolicyService);
		
		service = (AbstractSecuredService)configInjector.getInstance(DNSService.class);
		assertTrue(service instanceof DefaultDNSService);
		
		service = (AbstractSecuredService)configInjector.getInstance(DomainService.class);
		assertTrue(service instanceof DefaultDomainService);	
		
		service = (AbstractSecuredService)configInjector.getInstance(SettingService.class);
		assertTrue(service instanceof DefaultSettingService);	
		
		service = (AbstractSecuredService)configInjector.getInstance(TrustBundleService.class);
		assertTrue(service instanceof DefaultTrustBundleService);			
	}
}
