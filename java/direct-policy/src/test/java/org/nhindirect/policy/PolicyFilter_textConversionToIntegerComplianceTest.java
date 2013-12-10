package org.nhindirect.policy;

import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.apache.commons.io.IOUtils;
import org.nhindirect.policy.util.TestUtils;

import junit.framework.TestCase;

public class PolicyFilter_textConversionToIntegerComplianceTest extends TestCase
{
	public void testCompliance_simpleTextLexicon_equalsIntegerCertValue_assertTrue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final InputStream stream = IOUtils.toInputStream("X509.TBS.EXTENSION.KeyUsage = 224");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		
		assertTrue(filter.isCompliant(cert, stream, PolicyLexicon.SIMPLE_TEXT_V1));
	}
	
	public void testCompliance_simpleTextLexicon_notEqualsIntegerCertValue_assertTrue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final InputStream stream = IOUtils.toInputStream("X509.TBS.EXTENSION.KeyUsage != 223");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		
		assertTrue(filter.isCompliant(cert, stream, PolicyLexicon.SIMPLE_TEXT_V1));
	}	
	
	public void testCompliance_simpleTextLexicon_lessThanAndGreaterThanIntegerCertValue_assertTrue() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final InputStream stream = IOUtils.toInputStream("(X509.TBS.EXTENSION.KeyUsage > 0) && (X509.TBS.EXTENSION.KeyUsage < 225)");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		
		assertTrue(filter.isCompliant(cert, stream, PolicyLexicon.SIMPLE_TEXT_V1));
	}
	
	public void testCompliance_simpleTextLexicon_bitWiseAndIntegerCertValue_assertEquals() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final InputStream stream = IOUtils.toInputStream("(X509.TBS.EXTENSION.KeyUsage & 224) = 224");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		
		assertTrue(filter.isCompliant(cert, stream, PolicyLexicon.SIMPLE_TEXT_V1));
	}
	
	public void testCompliance_simpleTextLexicon_bitWiseAndIntegerCertValue_assertNotEquals() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final InputStream stream = IOUtils.toInputStream("(X509.TBS.EXTENSION.KeyUsage & 200) != 224");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		
		assertTrue(filter.isCompliant(cert, stream, PolicyLexicon.SIMPLE_TEXT_V1));
	}
	
	public void testCompliance_simpleTextLexicon_bitWiseOrIntegerCertValue_assertEquals() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final InputStream stream = IOUtils.toInputStream("(X509.TBS.EXTENSION.KeyUsage | 0) = 224");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		
		assertTrue(filter.isCompliant(cert, stream, PolicyLexicon.SIMPLE_TEXT_V1));
	}
	
	public void testCompliance_simpleTextLexicon_bitWiseOrIntegerCertValue_assertNotEquals() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		final InputStream stream = IOUtils.toInputStream("(X509.TBS.EXTENSION.KeyUsage | 255) != 224");
		
		final PolicyFilter filter = PolicyFilterFactory.getInstance();
		
		assertTrue(filter.isCompliant(cert, stream, PolicyLexicon.SIMPLE_TEXT_V1));
	}
}
