package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.nhindirect.policy.util.TestUtils;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.x509.RDNAttributeIdentifier;
import org.nhindirect.policy.x509.SubjectAttributeField;

public class SubjectAttributeField_injectReferenceValueTest extends TestCase
{
	public void testInjectRefereneValue_rdnAttributeDoesNotExist_notRequired_assertValueCollection() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final SubjectAttributeField field = new SubjectAttributeField(false, RDNAttributeIdentifier.INITIALS);
		
		field.injectReferenceValue(cert);
		
		final Collection<String> values = field.getPolicyValue().getPolicyValue();
		
		assertEquals(0, values.size());
		
	}
	
	public void testInjectRefereneValue_rdnAttributeDoesNotExist_required_throwException() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final SubjectAttributeField field = new SubjectAttributeField(true, RDNAttributeIdentifier.INITIALS);
		
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
	
	public void testInjectRefereneValue_rdnSingleAttributeExists_assertValue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final SubjectAttributeField field = new SubjectAttributeField(true, RDNAttributeIdentifier.COMMON_NAME);
		
		field.injectReferenceValue(cert);
		
		final Collection<String> values = field.getPolicyValue().getPolicyValue();
		
		assertEquals(1, values.size());
		
		Iterator<String> str = values.iterator();
		assertEquals("altNameOnly", str.next());
	}	

	public void testInjectRefereneValue_distinguishedName_assertValue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("altNameOnly.der");
		
		final SubjectAttributeField field = new SubjectAttributeField(true, RDNAttributeIdentifier.DISTINGUISHED_NAME);
		
		field.injectReferenceValue(cert);
		
		final Collection<String> values = field.getPolicyValue().getPolicyValue();
		
		assertEquals(1, values.size());
		
		Iterator<String> str = values.iterator();
		assertEquals("O=Cerner,L=Kansas City,ST=MO,C=US,CN=altNameOnly", str.next());
	}
	
	public void testInjectRefereneValue_noInjection_getPolicyValue_assertException() throws Exception
	{
		
		final SubjectAttributeField field = new SubjectAttributeField(true, RDNAttributeIdentifier.COMMON_NAME);
		
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
