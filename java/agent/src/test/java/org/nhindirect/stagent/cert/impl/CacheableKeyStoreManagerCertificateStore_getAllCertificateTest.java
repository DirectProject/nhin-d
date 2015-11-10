package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.utils.TestUtils;

public class CacheableKeyStoreManagerCertificateStore_getAllCertificateTest extends BaseKeyStoreManagerCertStoreTest
{	
	public void testGetGetAllCertificates_noCertificatesInstalled_assertNoCertificates() throws Exception
	{
		if (store != null)
		{
			assertTrue(store.getAllCertificates().isEmpty());
		}
	}
	
	public void testGetGetAllCertificates_singleCertificatesInstalled_assertCertificateRetrieved() throws Exception
	{
		if (store != null)
		{
			// add a certificate
			final X509CertificateEx user1 = (X509CertificateEx)TestUtils.getInternalCert("user1");
			store.add(user1);
			
			final Collection<X509Certificate> retrievedCerts = store.getAllCertificates();
			
			assertEquals(1, retrievedCerts.size());
			final X509Certificate retrievedCert = retrievedCerts.iterator().next();
			
			assertTrue(retrievedCert instanceof X509CertificateEx);
			assertEquals(user1, retrievedCert);
		}
	}
	
	
}
