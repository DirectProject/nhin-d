package org.nhindirect.policy;

import org.nhindirect.policy.impl.JavaSerializedObjectLexiconPolicyParser;
import org.nhindirect.policy.impl.XMLLexiconPolicyParser;

import junit.framework.TestCase;

public class PolicyLexiconParserFactory_getInstanceTest extends TestCase
{
	public void testGetInstance_assertParser() throws Exception
	{
		PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(PolicyLexicon.XML);
		assertTrue(parser instanceof XMLLexiconPolicyParser);
		
		parser = PolicyLexiconParserFactory.getInstance(PolicyLexicon.JAVA_SER);
		assertTrue(parser instanceof JavaSerializedObjectLexiconPolicyParser);
	}

}
