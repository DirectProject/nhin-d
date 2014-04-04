package org.nhindirect.policy.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import junit.framework.TestCase;

public class SimpleTextV1LexiconPolicyParser_parseToTokensTest extends TestCase
{
	public void testParse_simpleExpression_validateTokens() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/simpleLexiconSamp1.txt"));
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		assertEquals(11, tokens.size());
		
		stream.close();
	}
	
	public void testParse_logicalAndOperator_validateSingleTokens() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/logicalAndOperator.txt"));
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		assertEquals(1, tokens.size());
		assertEquals("&&", tokens.iterator().next().getToken());
		
		stream.close();
	}	
	
	public void testParse_CertificateStruct_validateTokens() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/lexiconWithCertificateStruct.txt"));
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		assertEquals(3, tokens.size());
		assertEquals(SimpleTextV1LexiconPolicyParser.TokenType.CERTIFICATE_REFERENCE_EXPRESSION, tokens.iterator().next().getType());
		
		stream.close();
	}	
	
	public void testParse_literalWithSpaces_validateTokens() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/literalWithSpaces.txt"));
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		assertEquals(3, tokens.size());
		
		stream.close();
	}

	public void testParse_requiredCertField_validateTokens() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = IOUtils.toInputStream("X509.TBS.EXTENSION.SubjectKeyIdentifier+ = 1.3.2.3");
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		assertEquals(3, tokens.size());
		
		stream.close();
	}	
}
