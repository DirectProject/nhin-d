package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.bouncycastle.asn1.x509.KeyUsage;
import org.nhindirect.policy.util.TestUtils;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.x509.KeyUsageExtensionField;

public class KeyUsageExtensionField_injectReferenceValueTest extends TestCase
{
	public void testInjectRefereneValue_keyUsageDoesNotExist_notRequired_assertValue0() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final KeyUsageExtensionField field = new KeyUsageExtensionField(false);
		
		field.injectReferenceValue(cert);
		
		assertEquals(0, (long)field.getPolicyValue().getPolicyValue());
	}
	
	public void testInjectRefereneValue_keyUsageDoesNotExist_required_assertException() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final KeyUsageExtensionField field = new KeyUsageExtensionField(true);
		
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
	
	public void testInjectRefereneValue_keyUsageExists_assertValue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final KeyUsageExtensionField field = new KeyUsageExtensionField(false);
		
		field.injectReferenceValue(cert);
		
		int value = field.getPolicyValue().getPolicyValue();
		
		assertTrue((KeyUsage.keyEncipherment & value) != 0);
		assertTrue((KeyUsage.nonRepudiation & value) != 0);	
		assertTrue((KeyUsage.digitalSignature & value) != 0);	
		
	}	
	
	public void testInjectRefereneValue_noInjection_getPolicyValue_assertException() throws Exception
	{
		
		final KeyUsageExtensionField field = new KeyUsageExtensionField(true);
		
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
