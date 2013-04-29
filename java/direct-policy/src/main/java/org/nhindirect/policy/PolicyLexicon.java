package org.nhindirect.policy;

import org.nhindirect.policy.impl.JavaSerializedObjectLexiconPolicyParser;
import org.nhindirect.policy.impl.XMLLexiconPolicyParser;

public enum PolicyLexicon 
{
	XML(XMLLexiconPolicyParser.class),
	
	JAVA_SER(JavaSerializedObjectLexiconPolicyParser.class);
	
	protected final Class<? extends PolicyLexiconParser> parserClass;
	
	private PolicyLexicon(Class<? extends PolicyLexiconParser> parserClass)
	{
		this.parserClass = parserClass;
	}
	
	public Class<? extends PolicyLexiconParser> getParserClass()
	{
		return parserClass;
	}
}
