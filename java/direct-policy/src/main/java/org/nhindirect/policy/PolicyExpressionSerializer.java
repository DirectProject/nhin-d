package org.nhindirect.policy;

import java.io.InputStream;
import java.io.OutputStream;

public interface PolicyExpressionSerializer 
{
	public PolicyExpression deserialize(InputStream stream) throws PolicyParseException; 
	
	public void serialize(PolicyExpression expression, OutputStream stream) throws PolicyParseException; 
}
