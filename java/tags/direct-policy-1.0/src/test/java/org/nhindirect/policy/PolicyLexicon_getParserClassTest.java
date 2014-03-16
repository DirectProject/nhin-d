package org.nhindirect.policy;

import org.nhindirect.policy.impl.JavaSerializedObjectLexiconPolicyParser;
import org.nhindirect.policy.impl.XMLLexiconPolicyParser;

import junit.framework.TestCase;

public class PolicyLexicon_getParserClassTest extends TestCase
{
	public void testGetParserTestClass_assertParser()
	{
		assertEquals(JavaSerializedObjectLexiconPolicyParser.class, PolicyLexicon.JAVA_SER.getParserClass());
		
		assertEquals(XMLLexiconPolicyParser.class, PolicyLexicon.XML.getParserClass());
	}
}
