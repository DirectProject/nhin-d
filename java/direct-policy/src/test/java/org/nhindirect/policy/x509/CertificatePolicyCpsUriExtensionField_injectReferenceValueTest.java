package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.policy.util.TestUtils;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.x509.CertificatePolicyCpsUriExtensionField;

import junit.framework.TestCase;

public class CertificatePolicyCpsUriExtensionField_injectReferenceValueTest extends TestCase
{
	public void testInjectRefereneValue_policyQualUriDoesNotExist_notRequired_assertValueEmpty() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final CertificatePolicyCpsUriExtensionField field = new CertificatePolicyCpsUriExtensionField(false);
		
		field.injectReferenceValue(cert);
		
		assertTrue(field.getPolicyValue().getPolicyValue().isEmpty());
	}
	
	public void testInjectRefereneValue_policyQualUriNotExist_required_assertException() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final CertificatePolicyCpsUriExtensionField field = new CertificatePolicyCpsUriExtensionField(true);
		
		boolean exceptionOccured = false;
		
		try
		{
			field.injectReferenceValue(cert);
		}
		catch (PolicyRequiredException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}	
	
	public void testInjectRefereneValue_policyQualUriExists_assertValue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirectProviderCA.der");
		
		final CertificatePolicyCpsUriExtensionField field = new CertificatePolicyCpsUriExtensionField(false);
		
		field.injectReferenceValue(cert);
		
		Collection<String> pols = field.getPolicyValue().getPolicyValue();
		
		assertTrue(pols.contains("http://www.cerner.com/CPS"));
		
	}	
	
	public void testInjectRefereneValue_policyQualUriExistsAndRequired_assertValue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirectProviderCA.der");
		
		final CertificatePolicyCpsUriExtensionField field = new CertificatePolicyCpsUriExtensionField(true);
		
		field.injectReferenceValue(cert);
		
		Collection<String> pols = field.getPolicyValue().getPolicyValue();
		
		assertTrue(pols.contains("http://www.cerner.com/CPS"));
		
	}	
	
	public void testInjectRefereneValue_mixedCPS_assertValue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("policyMixedQualifier.der");
		
		final CertificatePolicyCpsUriExtensionField field = new CertificatePolicyCpsUriExtensionField(true);
		
		field.injectReferenceValue(cert);
		
		Collection<String> pols = field.getPolicyValue().getPolicyValue();
		assertEquals(2, pols.size());
		
		
		//assertTrue(pols.contains("http://www.cerner.com/CPS"));
		
	}	
	
	public void testInjectRefereneValue_noInjection_getPolicyValue_assertException() throws Exception
	{
		
		final CertificatePolicyCpsUriExtensionField field = new CertificatePolicyCpsUriExtensionField(true);
		
		boolean exceptionOccured = false;
		
		try
		{
			field.getPolicyValue();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
}
