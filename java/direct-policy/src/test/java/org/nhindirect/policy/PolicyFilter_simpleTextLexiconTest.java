package org.nhindirect.policy;

import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.apache.commons.io.IOUtils;
import org.nhindirect.policy.util.TestUtils;

import junit.framework.TestCase;

public class PolicyFilter_simpleTextLexiconTest extends TestCase
{
	public void testX509SignatureAlgorithm_equals_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.Algorithm = 1.2.840.113549.1.1.5");
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testTBSSerialNumber_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.SerialNumber = f74f1c4fe4e1762e");
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testTBSIssuer_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("(X509.TBS.Issuer.CN {?} SimpleInterop) && (X509.TBS.Issuer.C {?} US) && (X509.TBS.Issuer.ST {?} Missouri) " +
				" && (X509.TBS.Issuer.E {?} cmii@cerner.com) && (X509.TBS.Issuer.OU {?} Medical Informatics)");
		
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testTBSSubject_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("(X509.TBS.Subject.CN {?} umesh) && (X509.TBS.Subject.C {?} US) && (X509.TBS.Subject.ST {?} Missouri) " +
				" && (X509.TBS.Subject.E {?} umesh@securehealthemail.com) && (X509.TBS.Subject.OU {?} Medical Informatics)");
		
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testTBSPublicKeyAlgorithm_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.SubjectPublicKeyInfo.Algorithm = 1.2.840.113549.1.1.1");
		
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testTBSPublicKeySize_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.SubjectPublicKeyInfo.Size = 2024");
		
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionKeyUsage_equals_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.KeyUsage = 224");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}	
	
	public void testExtensionKeyUsage_singleUsageCheck_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("(X509.TBS.EXTENSION.KeyUsage & 32) > 0");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}	
	
	public void testExtensionSubjectAltName_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.SubjectAltName {?} rfc822:AlAnderson@hospitalA.direct.visionshareinc.com");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}	
	
	public void testExtensionSubjectAltName_notContains_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.SubjectAltName {?}! me@you.com");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionSubjectAltName_regexContains_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.SubjectAltName {}$ AlAnderson@hospitalA.direct.visionshareinc.com");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionSubjectAltName_empty_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("{}X509.TBS.EXTENSION.SubjectAltName");
		
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}	
	
	public void testExtensionSubjectAltName_notEmpty_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("{}!X509.TBS.EXTENSION.SubjectAltName");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}	
	
	public void testExtensionSubjectKeyId_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.SubjectKeyIdentifier = e0f63ccfeb5ce3eef5c04efe8084c92bc628682c");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionAuthorityKeyIdent_keyId_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.AuthorityKeyIdentifier.KeyId = 3aa0074b77b2493efb447de5ce6cd055085de3f0");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionPolicy_OIDS_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs {?} 1.3.6.1.4.1.41179.0.1.2");
		
		final X509Certificate cert = TestUtils.loadCertificate("policyMixedQualifier.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionPolicy_intersection_singleQueryOID_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("^(X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs {}& 1.3.6.1.4.1.41179.0.1.2) = 1");
		
		final X509Certificate cert = TestUtils.loadCertificate("policyMixedQualifier.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionPolicy_intersection_multipleQueryOID_singleIntersection_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("^(X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs {}& 1.3.6.1.4.1.41179.0.1.2,12345) = 1");
		
		final X509Certificate cert = TestUtils.loadCertificate("policyMixedQualifier.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionPolicy_intersection_multipleQueryOID_multipleIntersection_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("^(X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs {}& 1.3.6.1.4.1.41179.0.1.2,1.3.6.1.4.1.41179.1.3) = 2");
		
		final X509Certificate cert = TestUtils.loadCertificate("policyMixedQualifier.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionPolicy_OIDS_size_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("^X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs = 4");
		
		final X509Certificate cert = TestUtils.loadCertificate("policyMixedQualifier.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionPolicy_OIDS_empty_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("{}X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs");
		
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionPolicy_policyURL_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.CertificatePolicies.CPSUrls {?} https://www.phicert.com/cps");
		
		final X509Certificate cert = TestUtils.loadCertificate("policyMixedQualifier.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionBasicContraint_CA_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.BasicConstraints.CA = true");
		
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirect DevCert Provider CA.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionExtendedKeyUsage_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.ExtKeyUsageSyntax {?} 1.3.6.1.5.5.7.3.4");
		
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionAIA_URL_caissues_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url " +
				"{?} caIssuers:http://ca.cerner.com/public/root.der");
		
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirectProviderCA.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionAIA_URL_ocsp_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url " +
				"{?} OCSP:http://ca.cerner.com/OCSP");
		
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirectProviderCA.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionAIA_size_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("^X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url = 2");
		
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirectProviderCA.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionAIA_OCSPLocation_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation " +
				"{?} http://ca.cerner.com/OCSP");
		
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirectProviderCA.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
	
	public void testExtensionAIA_OCSPLocation__size_assertTrue() throws Exception
	{
		final InputStream str = IOUtils.toInputStream("^X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation  = 1");
		
		final X509Certificate cert = TestUtils.loadCertificate("CernerDirectProviderCA.der");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		assertTrue(filter.isCompliant(cert, str, PolicyLexicon.SIMPLE_TEXT_V1));	
		
		IOUtils.closeQuietly(str);
	}
}
