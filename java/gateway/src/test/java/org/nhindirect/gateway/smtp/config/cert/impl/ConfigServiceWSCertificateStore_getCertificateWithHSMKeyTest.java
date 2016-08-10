package org.nhindirect.gateway.smtp.config.cert.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.nhind.config.CertificateGetOptions;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.impl.BootstrappedPKCS11Credential;
import org.nhindirect.common.crypto.impl.StaticPKCS11TokenKeyStoreProtectionManager;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.cert.X509CertificateEx;

import junit.framework.TestCase;

public class ConfigServiceWSCertificateStore_getCertificateWithHSMKeyTest extends TestCase 
{
	protected ConfigurationServiceProxy proxy;
	protected ConfigServiceCertificateStore certService;
	
	@Override
	public void setUp() throws Exception
	{
		proxy = mock(ConfigurationServiceProxy.class);
		
		certService = getCertService();
	}
	
	protected ConfigServiceCertificateStore getCertService() throws Exception
	{
    	if (StringUtils.isEmpty(TestUtils.setupSafeNetToken()))
    		return null;
		
		final ConfigServiceCertificateStore certService = new ConfigServiceCertificateStore(proxy);
		
		final PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
		final StaticPKCS11TokenKeyStoreProtectionManager mgr = 
				new StaticPKCS11TokenKeyStoreProtectionManager(cred, "KeyStoreProtKey", "PrivKeyProtKey");
		
		certService.setKeyStoreProectionManager(mgr);
		
		return certService;
	}
	
	public void testGetCertifcateWithPrivKey_noPrivKeyInHSM() throws Exception
	{		
		if (certService == null)
			return;
		
		final X509Certificate cert = TestUtils.loadCertificate("digSigOnly.der", null);
		
		final org.nhind.config.Certificate modelCert = new org.nhind.config.Certificate();
		modelCert.setData(cert.getEncoded());

		
		when(proxy.getCertificatesForOwner((String)any(), (CertificateGetOptions)any())).thenReturn(new org.nhind.config.Certificate[] {modelCert});
		
		final Collection<X509Certificate> retCerts = certService.getCertificates("test.com");
		assertEquals(1, retCerts.size());
		final X509Certificate retCert = retCerts.iterator().next();
		assertTrue(retCert instanceof X509CertificateEx);
		assertTrue(((X509CertificateEx)retCert).hasPrivateKey());
	}
}
