package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.nhindirect.policy.util.TestUtils;

import junit.framework.TestCase;

public class AbstractExtensionField_isCriticalTest extends TestCase
{
	public void testIsCritical_extensionNotPresent_assertFalse() throws Exception
	{
		X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		assertFalse(cert.getCriticalExtensionOIDs().contains(ExtensionIdentifier.EXTENDED_KEY_USAGE.getId()));
		
		
		final ExtendedKeyUsageExtensionField field = new ExtendedKeyUsageExtensionField(false);
		
		field.injectReferenceValue(cert);
		assertFalse(field.isCritical());
	}
	
	public void testIsCritical_extensionPresent_notCritical_assertFalse() throws Exception
	{
		X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		assertFalse(cert.getCriticalExtensionOIDs().contains(ExtensionIdentifier.SUBJECT_KEY_IDENTIFIER.getId()));
		
		
		final SubjectKeyIdentifierExtensionField field = new SubjectKeyIdentifierExtensionField(false);
		
		field.injectReferenceValue(cert);
		assertFalse(field.isCritical());
	}
	
	public void testIsCritical_extensionPresent_isCritical_assertTrue() throws Exception
	{
		X509Certificate cert = TestUtils.loadCertificate("CernerDirect DevCert Provider CA.der");
		
		assertTrue(cert.getCriticalExtensionOIDs().contains(ExtensionIdentifier.BASIC_CONSTRAINTS.getId()));
		
		
		final BasicContraintsExtensionField field = new BasicContraintsExtensionField(false);
		
		field.injectReferenceValue(cert);
		assertTrue(field.isCritical());
	}	
	
	public void testIsCritical_certMission_assertException() throws Exception
	{

		final BasicContraintsExtensionField field = new BasicContraintsExtensionField(false);

		boolean exceptionOccured = false;
		
		try
		{
			field.isCritical();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
			
		assertTrue(exceptionOccured);
	}	
}
