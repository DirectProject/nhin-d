package org.nhindirect.stagent.cert;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class CertificateStore_getUsableCertsTest extends TestCase
{
	
	
	public void testGetUsableCerts_inValidUserCert_retriveDomainCert() throws Exception
	{
		final X509CertificateEx userCert = TestUtils.getInternalCert("user1");
		final X509CertificateEx domainCert = TestUtils.getInternalCert("gm2552");
		
		CertificateStore store = new CertificateStoreAdapter()
		{
		    protected Collection<X509Certificate> filterUsable(Collection<X509Certificate> certs)
		    {
		    	if (certs.iterator().next().getSubjectDN().getName().contains("user1"))
		    		return null;
		    	else
		    		return certs;
		    }
		    
		    public Collection<X509Certificate> getCertificates(String subjectName)
		    {
		    	if (subjectName.contains("user1@domain.com"))
		    		return Arrays.asList((X509Certificate)userCert);
		    	else
		    		return Arrays.asList((X509Certificate)domainCert);
		    }
		};
		
		Collection<X509Certificate> foundCert = store.getCertificates(new InternetAddress("user1@domain.com"));
		assertEquals(domainCert, foundCert.iterator().next());
	}

	public void testGetUsableCerts_allCertsInvalid_assertNoCerts() throws Exception
	{
		final X509CertificateEx userCert = TestUtils.getInternalCert("user1");
		final X509CertificateEx domainCert = TestUtils.getInternalCert("gm2552");
		
		CertificateStore store = new CertificateStoreAdapter()
		{
		    protected Collection<X509Certificate> filterUsable(Collection<X509Certificate> certs)
		    {
		    	return null;
		    }
		    
		    public Collection<X509Certificate> getCertificates(String subjectName)
		    {
		    	if (subjectName.contains("user1@domain.com"))
		    		return Arrays.asList((X509Certificate)userCert);
		    	else
		    		return Arrays.asList((X509Certificate)domainCert);
		    }
		};
		
			Collection<X509Certificate> foundCert = store.getCertificates(new InternetAddress("user1@domain.com"));
			assertNull(foundCert);
	}
	
	public void testGetUsableCerts_getUserCert() throws Exception
	{
		final X509CertificateEx userCert = TestUtils.getInternalCert("user1");
		final X509CertificateEx domainCert = TestUtils.getInternalCert("gm2552");
		
		CertificateStore store = new CertificateStoreAdapter()
		{
		    protected Collection<X509Certificate> filterUsable(Collection<X509Certificate> certs)
		    {
		    	if (certs.iterator().next().getSubjectDN().getName().contains("user1"))
		    		return certs;
		    	else
		    		return certs;
		    }
		    
		    public Collection<X509Certificate> getCertificates(String subjectName)
		    {
		    	if (subjectName.contains("user1@domain.com"))
		    		return Arrays.asList((X509Certificate)userCert);
		    	else
		    		return Arrays.asList((X509Certificate)domainCert);
		    }
		};
		
		Collection<X509Certificate> foundCert = store.getCertificates(new InternetAddress("user1@domain.com"));
		assertEquals(userCert, foundCert.iterator().next());
	}
	
	static class CertificateStoreAdapter extends CertificateStore
	{

		@Override
		public boolean contains(X509Certificate cert) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void add(X509Certificate cert) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void remove(X509Certificate cert) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Collection<X509Certificate> getAllCertificates() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
