package org.nhindirect.policy.impl;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyGrammarException;

import junit.framework.TestCase;

public class SimpleTextV1LexiconPolicyParser_parseTest extends TestCase
{
	public void testParse_simpleExpression_validateParsed() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/simpleLexiconSamp1.txt"));
		
		final PolicyExpression expressions = parser.parse(stream);

		assertNotNull(expressions);
		
		stream.close();
	}
	
	public void testParse_unclosedGroup_assertGrammarException() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = IOUtils.toInputStream("(1 = 1");
		
		boolean exceptionOccured = false;
		
		try
		{
			parser.parse(stream);
		}
		catch (PolicyGrammarException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
		
	}	
	
	public void testParse_noOperator_assertGrammarException() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = IOUtils.toInputStream("1");
		
		boolean exceptionOccured = false;
		
		try
		{
			parser.parse(stream);
		}
		catch (PolicyGrammarException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
		
	}	
	
	public void testParse_extraniousOperator_assertGrammarException() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = IOUtils.toInputStream("1 = 1 =");
		
		boolean exceptionOccured = false;
		
		try
		{
			parser.parse(stream);
		}
		catch (PolicyGrammarException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
		
	}	
}
