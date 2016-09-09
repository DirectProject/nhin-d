package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.common.crypto.impl.BootstrappedKeyStoreProtectionManager;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.utils.TestUtils;

public class CacheableKeyStoreManagerCertificateStore_removeTest extends BaseKeyStoreManagerCertStoreTest
{
	public void testRemove_removingExistingCert_assertRemoved() throws Exception
	{
		if (store != null)
		{
			// add a certificate
			final X509CertificateEx user1 = (X509CertificateEx)TestUtils.getInternalCert("user1");
			store.add(user1);
			
			final Collection<X509Certificate> retrievedCerts = store.getAllCertificates();

			final X509Certificate retrievedCert = retrievedCerts.iterator().next();

			assertEquals(user1, retrievedCert);
			
			// remove it
			store.remove(user1);
			
			assertTrue(store.getAllCertificates().isEmpty());
		}
	}
	
	public void testRemove_removingNonExistantCert_assertNotRemoved() throws Exception
	{
		if (store != null)
		{
			// add a certificate
			final X509CertificateEx user1 = (X509CertificateEx)TestUtils.getInternalCert("user1");
			store.add(user1);
			
			final Collection<X509Certificate> retrievedCerts = store.getAllCertificates();

			final X509Certificate retrievedCert = retrievedCerts.iterator().next();

			assertEquals(user1, retrievedCert);
			
			// try removing another non existant cert
			final X509Certificate cacert = TestUtils.getInternalCACert("cacert");
			store.remove(cacert);
			
			assertFalse(store.getAllCertificates().isEmpty());
		}
	}
	
	public void testRemove_nonMutableStore_assertException() throws Exception
	{
		if (store != null)
		{
			final BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager();
			store.setKeyStoreManager(mgr);
			
			boolean exceptionOccured = false;
			try
			{
				final X509Certificate cacert = TestUtils.getInternalCACert("cacert");
				store.remove(cacert);
			}
			catch (IllegalStateException ex)
			{
				exceptionOccured = true;
			}
			assertTrue(exceptionOccured);
		}
	}
}
