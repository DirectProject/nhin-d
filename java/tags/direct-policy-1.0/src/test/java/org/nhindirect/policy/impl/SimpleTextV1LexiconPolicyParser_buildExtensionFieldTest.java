package org.nhindirect.policy.impl;

import junit.framework.TestCase;

import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.policy.x509.AuthorityInfoAccessExtentionField;
import org.nhindirect.policy.x509.AuthorityInfoAccessOCSPLocExtentionField;
import org.nhindirect.policy.x509.AuthorityKeyIdentifierKeyIdExtensionField;
import org.nhindirect.policy.x509.BasicContraintsExtensionField;
import org.nhindirect.policy.x509.CRLDistributionPointNameExtentionField;
import org.nhindirect.policy.x509.CertificatePolicyCpsUriExtensionField;
import org.nhindirect.policy.x509.CertificatePolicyIndentifierExtensionField;
import org.nhindirect.policy.x509.ExtendedKeyUsageExtensionField;
import org.nhindirect.policy.x509.ExtensionField;
import org.nhindirect.policy.x509.KeyUsageExtensionField;
import org.nhindirect.policy.x509.SubjectAltNameExtensionField;
import org.nhindirect.policy.x509.SubjectKeyIdentifierExtensionField;


public class SimpleTextV1LexiconPolicyParser_buildExtensionFieldTest extends TestCase
{
	public void testBuildExtensionField_keyUsage_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.KeyUsage"));
		assertNotNull(field);
		assertTrue(field instanceof KeyUsageExtensionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.KeyUsage+"));
		assertNotNull(field);
		assertTrue(field instanceof KeyUsageExtensionField);
		assertTrue(field.isRequired());
	}
	
	public void testBuildExtensionField_subjectAltName_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.SubjectAltName"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAltNameExtensionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.SubjectAltName+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectAltNameExtensionField);
		assertTrue(field.isRequired());
	}	
	
	public void testBuildExtensionField_subjectDirectoryAttributes_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.SubjectDirectoryAttributes");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}		
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.SubjectDirectoryAttributes+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}		
		assertTrue(exceptionOccured);
		
	}	
	
	public void testBuildExtensionField_subjectKeyIdentifier_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.SubjectKeyIdentifier"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectKeyIdentifierExtensionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.SubjectKeyIdentifier+"));
		assertNotNull(field);
		assertTrue(field instanceof SubjectKeyIdentifierExtensionField);
		assertTrue(field.isRequired());
	}	
	
	public void testBuildExtensionField_issuierAltName_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.IssuerAltName");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.IssuerAltName+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
	}	
	
	public void testBuildExtensionField_autorityKeyId_assertFields() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.KeyId"));
		assertNotNull(field);
		assertTrue(field instanceof AuthorityKeyIdentifierKeyIdExtensionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.KeyId+"));
		assertNotNull(field);
		assertTrue(field instanceof AuthorityKeyIdentifierKeyIdExtensionField);
		assertTrue(field.isRequired());
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.CertIssuers");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.CertIssuers+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.SerialNumber");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.SerialNumber+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}	
	
	public void testBuildExtensionField_certificatePolicies_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs"));
		assertNotNull(field);
		assertTrue(field instanceof CertificatePolicyIndentifierExtensionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs+"));
		assertNotNull(field);
		assertTrue(field instanceof CertificatePolicyIndentifierExtensionField);
		assertTrue(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.CertificatePolicies.CPSUrls"));
		assertNotNull(field);
		assertTrue(field instanceof CertificatePolicyCpsUriExtensionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.CertificatePolicies.CPSUrls+"));
		assertNotNull(field);
		assertTrue(field instanceof CertificatePolicyCpsUriExtensionField);
		assertTrue(field.isRequired());
	}
	
	public void testBuildExtensionField_policyMappings_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.PolicyMappings");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.PolicyMappings+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}	
	
	public void testBuildExtensionField_basicContraints_assertFields() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.BasicConstraints.CA"));
		assertNotNull(field);
		assertTrue(field instanceof BasicContraintsExtensionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.BasicConstraints.CA+"));
		assertNotNull(field);
		assertTrue(field instanceof BasicContraintsExtensionField);
		assertTrue(field.isRequired());
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.BasicConstraints.MaxPathLength");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.BasicConstraints.MaxPathLength+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testBuildExtensionField_nameConstraints_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.NameConstraints");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.NameConstraints+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
	}	
	
	public void testBuildExtensionField_policyConstraints_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.PolicyConstraints");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.PolicyConstraints+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
	}
	
	public void testBuildExtensionField_extendedKeyUsage_assertBuilt() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.ExtKeyUsageSyntax"));
		assertNotNull(field);
		assertTrue(field instanceof ExtendedKeyUsageExtensionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.ExtKeyUsageSyntax+"));
		assertNotNull(field);
		assertTrue(field instanceof ExtendedKeyUsageExtensionField);
		assertTrue(field.isRequired());
		
	}
	
	public void testBuildExtensionField_crlDispPoints_assertFields() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.FullName"));
		assertNotNull(field);
		assertTrue(field instanceof CRLDistributionPointNameExtentionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.FullName+"));
		assertNotNull(field);
		assertTrue(field instanceof CRLDistributionPointNameExtentionField);
		assertTrue(field.isRequired());
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.RelativeToIssuer");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.RelativeToIssuer+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.Reasons");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.Reasons+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.CRLIssuer");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.CRLIssuer+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testBuildExtensionField_inhibitAnyPolicy_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.InhibitAnyPolicy");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.InhibitAnyPolicy+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testBuildExtensionField_freshestCRL_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.FreshestCRL.FullName");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.FreshestCRL.FullName+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.FreshestCRL.RelativeToIssuer");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.FreshestCRL.RelativeToIssuer+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.FreshestCRL.Reasons");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.FreshestCRL.Reasons+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.FreshestCRL.CRLIssuer");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.FreshestCRL.CRLIssuer+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}	
		assertTrue(exceptionOccured);
	}
	
	public void testBuildExtensionField_auhtorityInformationAccess_assertFields() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url"));
		assertNotNull(field);
		assertTrue(field instanceof AuthorityInfoAccessExtentionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url+"));
		assertNotNull(field);
		assertTrue(field instanceof AuthorityInfoAccessExtentionField);
		assertTrue(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation"));
		assertNotNull(field);
		assertTrue(field instanceof AuthorityInfoAccessOCSPLocExtentionField);
		assertFalse(field.isRequired());
		
		field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation+"));
		assertNotNull(field);
		assertTrue(field instanceof AuthorityInfoAccessOCSPLocExtentionField);
		assertTrue(field.isRequired());
		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.AccessMethod");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.AccessMethod+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testBuildExtensionField_auhtorityInformationAccess_assertFieldNotImplemented() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

		
		boolean exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.Url");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.Url+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.AccessMethod");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.AccessMethod+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.OCSPLocation");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		exceptionOccured = false;
		try
		{
			parser.buildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.OCSPLocation+");
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testBuildExtensionField_unknownField_assertNull() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		ExtensionField<?> field = ExtensionField.class.cast(parser.buildExtensionField("X509.TBS.EXTENSION.Bogus"));
		assertNull(field);
	}
}
