package org.nhindirect.policy.impl;

import junit.framework.TestCase;

import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.policy.x509.IssuerAttributeField;
import org.nhindirect.policy.x509.RDNAttributeIdentifier;
import org.nhindirect.policy.x509.SerialNumberAttributeField;
import org.nhindirect.policy.x509.SubjectAttributeField;
import org.nhindirect.policy.x509.SubjectPublicKeyAlgorithmField;
import org.nhindirect.policy.x509.SubjectPublicKeySizeField;
import org.nhindirect.policy.x509.TBSField;


public class SimpleTextV1LexiconPolicyParser_buildTBSFieldTest extends TestCase
{
	public void testBuildTBSField_version_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.Version"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
	}
	
	public void testBuildTBSField_serialNumber_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

		TBSField<?> field = TBSField.class.cast(parser.buildTBSField("X509.TBS.SerialNumber"));
		assertNotNull(field);
		assertTrue(field instanceof SerialNumberAttributeField);
		assertTrue(field.isRequired());
	}
	
	public void testBuildTBSField_signature_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.Signature"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.Signature+"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
	}
	
	public void testBuildTBSField_issuer_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

		TBSField<?> field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.CN"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.COMMON_NAME, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.CN+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.COMMON_NAME, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.C"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.COUNTRY, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.C+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.COUNTRY, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.O"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.ORGANIZATION, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.O+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.ORGANIZATION, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.OU"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.ORGANIZATIONAL_UNIT, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.OU+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.ORGANIZATIONAL_UNIT, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.ST"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.STATE, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.ST+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.STATE, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.L"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.LOCALITY, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.L+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.LOCALITY, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.E"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.EMAIL, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.E+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.EMAIL, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.DC"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.DOMAIN_COMPONENT, ((IssuerAttributeField)field).getRDNAttributeFieldId());		
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.DC+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.DOMAIN_COMPONENT, ((IssuerAttributeField)field).getRDNAttributeFieldId());		
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.DNQUALIFIER"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.DISTINGUISHED_NAME_QUALIFIER, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.DNQUALIFIER+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.DISTINGUISHED_NAME_QUALIFIER, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.SERIALNUMBER"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.SERIAL_NUMBER, ((IssuerAttributeField)field).getRDNAttributeFieldId());			
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.SERIALNUMBER+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.SERIAL_NUMBER, ((IssuerAttributeField)field).getRDNAttributeFieldId());			
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.SN"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.SURNAME, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.SN+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.SURNAME, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.TITLE"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.TITLE, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.TITLE+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.TITLE, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.GIVENNAME"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.GIVEN_NAME, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.GIVENNAME+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.GIVEN_NAME, ((IssuerAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.INITIALS"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.INITIALS, ((IssuerAttributeField)field).getRDNAttributeFieldId());		
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.INITIALS+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.INITIALS, ((IssuerAttributeField)field).getRDNAttributeFieldId());		
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.PSEUDONYM"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.PSEUDONYM, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.PSEUDONYM+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.PSEUDONYM, ((IssuerAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.GERNERAL_QUALIFIER"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.GERNERAL_QUALIFIER, ((IssuerAttributeField)field).getRDNAttributeFieldId());		
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.GERNERAL_QUALIFIER+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.GERNERAL_QUALIFIER, ((IssuerAttributeField)field).getRDNAttributeFieldId());		
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.DN"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.DISTINGUISHED_NAME, ((IssuerAttributeField)field).getRDNAttributeFieldId());		
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Issuer.DN+"));
		assertNotNull(field);
		assertTrue(field instanceof IssuerAttributeField);
		assertEquals(RDNAttributeIdentifier.DISTINGUISHED_NAME, ((IssuerAttributeField)field).getRDNAttributeFieldId());		
		assertTrue(field.isRequired());
	}
	
	public void testBuildTBSField_validity_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.Validity.ValidFrom"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.Validity.ValidFrom+"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.Validity.ValidTo"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.Validity.ValidTo+"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}	
	
	public void testBuildTBSField_subject_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

		TBSField<?> field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.CN"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.COMMON_NAME, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.CN+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.COMMON_NAME, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.C"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.COUNTRY, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.C+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.COUNTRY, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.O"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.ORGANIZATION, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.O+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.ORGANIZATION, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.OU"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.ORGANIZATIONAL_UNIT, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.OU+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.ORGANIZATIONAL_UNIT, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.ST"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.STATE, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.ST+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.STATE, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.L"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.LOCALITY, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.L+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.LOCALITY, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.E"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.EMAIL, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.E+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.EMAIL, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.DC"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.DOMAIN_COMPONENT, ((SubjectAttributeField)field).getRDNAttributeFieldId());		
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.DC+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.DOMAIN_COMPONENT, ((SubjectAttributeField)field).getRDNAttributeFieldId());		
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.DNQUALIFIER"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.DISTINGUISHED_NAME_QUALIFIER, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.DNQUALIFIER+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.DISTINGUISHED_NAME_QUALIFIER, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.SERIALNUMBER"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.SERIAL_NUMBER, ((SubjectAttributeField)field).getRDNAttributeFieldId());			
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.SERIALNUMBER+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.SERIAL_NUMBER, ((SubjectAttributeField)field).getRDNAttributeFieldId());			
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.SN"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.SURNAME, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.SN+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.SURNAME, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.TITLE"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.TITLE, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.TITLE+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.TITLE, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.GIVENNAME"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.GIVEN_NAME, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.GIVENNAME+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.GIVEN_NAME, ((SubjectAttributeField)field).getRDNAttributeFieldId());
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.INITIALS"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.INITIALS, ((SubjectAttributeField)field).getRDNAttributeFieldId());		
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.INITIALS+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.INITIALS, ((SubjectAttributeField)field).getRDNAttributeFieldId());		
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.PSEUDONYM"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.PSEUDONYM, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.PSEUDONYM+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.PSEUDONYM, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.GERNERAL_QUALIFIER"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.GERNERAL_QUALIFIER, ((SubjectAttributeField)field).getRDNAttributeFieldId());		
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.GERNERAL_QUALIFIER+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.GERNERAL_QUALIFIER, ((SubjectAttributeField)field).getRDNAttributeFieldId());		
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.DN"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.DISTINGUISHED_NAME, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertFalse(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Subject.DN+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAttributeField);
		assertEquals(RDNAttributeIdentifier.DISTINGUISHED_NAME, ((SubjectAttributeField)field).getRDNAttributeFieldId());	
		assertTrue(field.isRequired());
	}
	
	public void testBuildTBSField_issuerUniqueId_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.IssuerUniqueID"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);	
		
		exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.IssuerUniqueID+"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);	
	}	
	
	public void testBuildTBSField_subjectUniqueId_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.SubjectUniqueID"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);		
		
		exceptionOccured = false;
		try
		{
			TBSField.class.cast(parser.buildTBSField("X509.TBS.SubjectUniqueID+"));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);	
	}	
	
	public void testBuildTBSField_subjectPublicKeyInfo_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

		TBSField<?> field = TBSField.class.cast(parser.buildTBSField("X509.TBS.SubjectPublicKeyInfo.Algorithm"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectPublicKeyAlgorithmField);
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.SubjectPublicKeyInfo.Algorithm+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectPublicKeyAlgorithmField);
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.SubjectPublicKeyInfo.Size"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectPublicKeySizeField);
		assertTrue(field.isRequired());
		
		field = TBSField.class.cast(parser.buildTBSField("X509.TBS.SubjectPublicKeyInfo.Size+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectPublicKeySizeField);
		assertTrue(field.isRequired());
	}		
	
	public void testBuildTBSField_unknownField_assertNull() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		TBSField<?> field = TBSField.class.cast(parser.buildTBSField("X509.TBS.Bugos"));
		assertNull(field);
	}
}
