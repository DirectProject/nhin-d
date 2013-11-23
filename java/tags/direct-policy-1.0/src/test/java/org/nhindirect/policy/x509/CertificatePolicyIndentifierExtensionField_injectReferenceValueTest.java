package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.policy.util.TestUtils;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.x509.CertificatePolicyIndentifierExtensionField;

import junit.framework.TestCase;

public class CertificatePolicyIndentifierExtensionField_injectReferenceValueTest extends TestCase
{
	public void testInjectRefereneValue_policyDoesNotExist_notRequired_assertValueEmpty() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final CertificatePolicyIndentifierExtensionField field = new CertificatePolicyIndentifierExtensionField(false);
		
		field.injectReferenceValue(cert);
		
		assertTrue(field.getPolicyValue().getPolicyValue().isEmpty());
	}
	
	public void testInjectRefereneValue_policyDoesNotExist_required_assertException() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final CertificatePolicyIndentifierExtensionField field = new CertificatePolicyIndentifierExtensionField(true);
		
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
	
	public void testInjectRefereneValue_policyExists_assertValue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirectProviderCA.der");
		
		final CertificatePolicyIndentifierExtensionField field = new CertificatePolicyIndentifierExtensionField(false);
		
		field.injectReferenceValue(cert);
		
		Collection<String> usages = field.getPolicyValue().getPolicyValue();
		assertFalse(field.getPolicyValue().getPolicyValue().isEmpty());
		
		assertTrue(usages.contains("2.16.840.1.113883.3.1313.0.1"));
		
	}	
	
	public void testInjectRefereneValue_noInjection_getPolicyValue_assertException() throws Exception
	{
		
		final CertificatePolicyIndentifierExtensionField field = new CertificatePolicyIndentifierExtensionField(true);
		
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
