package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.nhindirect.policy.util.TestUtils;
import org.nhindirect.policy.x509.SignatureAlgorithmField;
import org.nhindirect.policy.x509.SignatureAlgorithmIdentifier;

import junit.framework.TestCase;

public class SignatureAlgorithmField_injectReferenceValueTest extends TestCase
{
	public void testInjectReferenceValue_sha1RSA_assertAlgorithm() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final SignatureAlgorithmField field = new SignatureAlgorithmField();
		
		field.injectReferenceValue(cert);
		
		assertEquals(SignatureAlgorithmIdentifier.SHA1RSA.getId(), field.getPolicyValue().getPolicyValue());
	}
	
	public void testInjectReferenceValue_sha256RSA_assertAlgorithm() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirect DevCert Provider CA.der");
		
		final SignatureAlgorithmField field = new SignatureAlgorithmField();
		
		field.injectReferenceValue(cert);
		
		assertEquals(SignatureAlgorithmIdentifier.SHA256RSA.getId(), field.getPolicyValue().getPolicyValue());
	}	
	
	public void testInjectRefereneValue_noInjection_getPolicyValue_assertException() throws Exception
	{
		
		final SignatureAlgorithmField field = new SignatureAlgorithmField();
		
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
