package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.common.crypto.impl.BootstrappedKeyStoreProtectionManager;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.utils.TestUtils;

public class CacheableKeyStoreManagerCertificateStore_addTest extends BaseKeyStoreManagerCertStoreTest
{
	public void testAdd_addNewCert_assertAdded() throws Exception
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
	
	public void testAdd_nonMutableStore_assertException() throws Exception
	{
		if (store != null)
		{
			final BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager();
			store.setKeyStoreManager(mgr);
			
			boolean exceptionOccured = false;
			try
			{
				final X509CertificateEx user1 = (X509CertificateEx)TestUtils.getInternalCert("user1");
				store.add(user1);
			}
			catch (IllegalStateException ex)
			{
				exceptionOccured = true;
			}
			assertTrue(exceptionOccured);
		}
	}
	
	public void testAdd_nonPrivateKeyCert_assertException() throws Exception
	{
		if (store != null)
		{
			
			boolean exceptionOccured = false;
			try
			{
				final X509Certificate caCert = TestUtils.getExternalCert("cacert");
				store.add(caCert);
			}
			catch (IllegalArgumentException ex)
			{
				exceptionOccured = true;
			}
			assertTrue(exceptionOccured);
		}
	}
}
