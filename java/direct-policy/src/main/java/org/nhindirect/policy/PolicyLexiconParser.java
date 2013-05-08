package org.nhindirect.policy;

import java.io.InputStream;

public interface PolicyLexiconParser 
{
	public PolicyExpression parse(InputStream stream) throws PolicyParseException; 
}
