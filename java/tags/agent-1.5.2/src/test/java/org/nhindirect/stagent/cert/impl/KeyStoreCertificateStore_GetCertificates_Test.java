package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.utils.BaseTestPlan;

import junit.framework.TestCase;

@SuppressWarnings("deprecation")
public class KeyStoreCertificateStore_GetCertificates_Test extends TestCase 
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected CertificateResolver keyStore = null;
		
		protected boolean certFoundInAltSubject = false;
		
			
		@Override
		protected void performInner() throws Exception 
		{
			keyStore = new KeyStoreCertificateStore("src/test/resources/keystores/internalKeystore",
					"h3||0 wor|d", "pKpa$$wd")
			{
			    public Collection<X509Certificate> getCertificates(String subjectName)
			    {
			        Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();

			        Collection<X509Certificate> certs = getAllCertificates();

			        if (certs == null)
			            return retVal;

			        for (X509Certificate cert : certs) 
			        {
			            if (CryptoExtensions.containsEmailAddressInSubjectAltName(cert, subjectName)) 
			            {
			            	certFoundInAltSubject = true;
			                retVal.add(cert);
			            } 
			            else if (cert.getSubjectDN().getName().toLowerCase().contains(subjectName.toLowerCase(Locale.getDefault()))) 
			            {
			                retVal.add(cert);
			            }
			        }

			        return retVal;
			    }
			};
			
			
			InternetAddress address = new InternetAddress(getSubjectToSearch());
			
			Collection<X509Certificate> foundCerts = keyStore.getCertificates(address);
			
			doAssertions(foundCerts);
		}
		
		protected abstract String getSubjectToSearch();
		
		protected abstract void doAssertions(Collection<X509Certificate> certs) throws Exception;

	}
	
	public void testKeyStoreSearch_GetWithAltName_AssertCertsFoundUsingAltSubject() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected String getSubjectToSearch()
			{
				return "test.email.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(1, certs.size());		
				assertTrue(certFoundInAltSubject);
			}
			
		}.perform();
		
	}
	
	public void testKeyStoreSearch_GetUserCertByDomain_AssertCertsNotFound() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected String getSubjectToSearch()
			{
				return "hospitalA.direct.visionshareinc.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNull(certs);	
			}
			
		}.perform();
		
	}	
	
	public void testKeyStoreSearch_GetUnknownUserCert_AssertCertsNotFound() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected String getSubjectToSearch()
			{
				return "joe@hospitalA.direct.visionshareinc.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNull(null);
			}
			
		}.perform();
		
	}	
	
	
	public void testKeyStoreSearch_GetOrgCertAltName_AssertCertsFoundUsingAltSubject() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected String getSubjectToSearch()
			{
				return "test.email.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(1, certs.size());		
				assertTrue(certFoundInAltSubject);
			}
			
		}.perform();
		
	}	
	
	public void testKeyStoreSearch_GetWithDN_AssertCertsFoundNotUsingAltSubject() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected String getSubjectToSearch()
			{
				return "gm2552@securehealthemail.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(1, certs.size());		
				assertFalse(certFoundInAltSubject);
			}
			
		}.perform();
		
	}	
	
	public void testKeyStoreSearch_GetExpiredCert_AssertCertsNotFound() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected String getSubjectToSearch()
			{
				return "expired@testexpired.email.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNull(certs);
			}
			
		}.perform();
		
	}		
}
