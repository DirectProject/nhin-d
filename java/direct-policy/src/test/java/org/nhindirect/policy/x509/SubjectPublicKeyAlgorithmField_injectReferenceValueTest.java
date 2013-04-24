package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.nhindirect.policy.util.TestUtils;
import org.nhindirect.policy.x509.PublicKeyAlgorithmIdentifier;
import org.nhindirect.policy.x509.SubjectPublicKeyAlgorithmField;

public class SubjectPublicKeyAlgorithmField_injectReferenceValueTest extends TestCase
{		
	public void testInjectRefereneValue_keyAlgorithmExists_assertValue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final SubjectPublicKeyAlgorithmField field = new SubjectPublicKeyAlgorithmField();
		
		field.injectReferenceValue(cert);
		
		final String value = field.getPolicyValue().getPolicyValue();
		
		assertEquals(PublicKeyAlgorithmIdentifier.RSA.getId(), value);
		
	}	
	
	public void testInjectRefereneValue_noInjection_getPolicyValue_assertException() throws Exception
	{
		
		final SubjectPublicKeyAlgorithmField field = new SubjectPublicKeyAlgorithmField();
		
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
