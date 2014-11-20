package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.util.TestUtils;

import junit.framework.TestCase;

public class AbstractX509Field_testGetDERObjectTest extends TestCase
{
	public void testGetObject_validObjectEncoding() throws Exception
	{
		// load cert
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirect DevCert Provider CA.der");
		
		// use a concreate class and check for key usage
		final SignatureAlgorithmField field = new SignatureAlgorithmField();
		assertNotNull(field.getDERObject(cert.getTBSCertificate()));
	}
	
	public void testGetObject_invalidObjectEncoding() throws Exception
	{
		
		// use a concreate class
		final SignatureAlgorithmField field = new SignatureAlgorithmField();
		boolean exceptionOccured = false;
		
		try
		{
			assertNotNull(field.getDERObject(new byte[]{1,2,3}));
		}
		catch(PolicyProcessException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
