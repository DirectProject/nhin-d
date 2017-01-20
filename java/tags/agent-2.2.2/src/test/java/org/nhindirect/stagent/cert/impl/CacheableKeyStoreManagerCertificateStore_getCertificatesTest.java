package org.nhindirect.stagent.cert.impl;


import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.utils.TestUtils;

public class CacheableKeyStoreManagerCertificateStore_getCertificatesTest extends BaseKeyStoreManagerCertStoreTest
{
	public void testGetCertificate_noCertsInStore_assertNoFound() throws Exception
	{
		if (store != null)
		{
			assertTrue(store.getCertificates("user1").isEmpty());
		}
	}
	
	public void testGetCertificate_existingCertsInStore_assertFound() throws Exception
	{
		if (store != null)
		{
			// add a certificate
			final X509CertificateEx user1 = (X509CertificateEx)TestUtils.getInternalCert("user1");
			store.add(user1);
			
			final Collection<X509Certificate> retrievedCerts = store.getCertificates("user1@cerner.com");
			
			assertEquals(1, retrievedCerts.size());
			final X509Certificate retrievedCert = retrievedCerts.iterator().next();
			
			assertTrue(retrievedCert instanceof X509CertificateEx);
			assertEquals(user1, retrievedCert);
		}
	}
	
	public void testGetCertificate_existingCertsInStore_findByEmailAddress_assertFound() throws Exception
	{
		if (store != null)
		{
			// add a certificate
			final X509CertificateEx user1 = (X509CertificateEx)TestUtils.getInternalCert("user1");
			store.add(user1);
			
			final Collection<X509Certificate> retrievedCerts = store.getCertificates(new InternetAddress("user1@cerner.com"));
			
			assertEquals(1, retrievedCerts.size());
			final X509Certificate retrievedCert = retrievedCerts.iterator().next();
			
			assertTrue(retrievedCert instanceof X509CertificateEx);
			assertEquals(user1, retrievedCert);
		}
	}
	
}
