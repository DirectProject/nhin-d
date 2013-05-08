package org.nhindirect.policy.impl;


import java.io.InputStream;
import java.io.OutputStream;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyExpressionSerializer;
import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.policy.PolicyLexiconParser;

import com.thoughtworks.xstream.XStream;

public class XMLLexiconPolicyParser implements PolicyLexiconParser, PolicyExpressionSerializer
{
	public XMLLexiconPolicyParser()
	{
	}
	
	@Override
	public PolicyExpression parse(InputStream stream) throws PolicyParseException 
	{
		return deserialize(stream);
	}
	
	@Override
	public PolicyExpression deserialize(InputStream xml) throws PolicyParseException
	{
		if (xml == null)
			throw new IllegalArgumentException("XML input stream cannot be null.");
		
		final XStream xStream = XStreamFactory.getXStreamInstance();
		
		try
		{
			final PolicyExpression retVal = (PolicyExpression)xStream.fromXML(xml);
			return retVal;
		}
		catch (Exception e)
		{
			throw new PolicyParseException("Could not deseriale policy expression from XML.", e);
		}
	}
	
	public void serialize(PolicyExpression expression, OutputStream out) throws PolicyParseException
	{
		if (expression == null)
			throw new IllegalArgumentException("Policy expression cannot be null.");
		
		if (out == null)
			throw new IllegalArgumentException("XML output stream cannot be null.");
		
		final XStream xStream = XStreamFactory.getXStreamInstance();
		
		xStream.toXML(expression, out);
	}
}
