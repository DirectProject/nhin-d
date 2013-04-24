package org.nhindirect.policy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyExpressionSerializer;
import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.policy.PolicyLexiconParser;

public class JavaSerializedObjectLexiconPolicyParser implements PolicyLexiconParser, PolicyExpressionSerializer
{
	public JavaSerializedObjectLexiconPolicyParser()
	{
		
	}

	
	@Override
	public PolicyExpression parse(InputStream stream) throws PolicyParseException 
	{
		return deserialize(stream);
	}


	@Override
	public PolicyExpression deserialize(InputStream stream) throws PolicyParseException 
	{
		if (stream == null)
			throw new IllegalArgumentException("POJO serialization input stream cannot be null.");
		
		try
		{
			final ObjectInputStream in = new ObjectInputStream(stream);
			final PolicyExpression retVal = (PolicyExpression)in.readObject();
			return retVal;
		}
		catch (Exception e)
		{
			throw new PolicyParseException("Could not deseriale policy expression from serialized POJO.", e);
		}		
	}

	@Override
	public void serialize(PolicyExpression expression, OutputStream out) throws PolicyParseException 
	{
		if (expression == null)
			throw new IllegalArgumentException("Policy expression cannot be null.");
		
		if (out == null)
			throw new IllegalArgumentException("POJO output stream cannot be null.");
		
		try
		{			

			final ObjectOutputStream objectStream = new ObjectOutputStream(out);
			objectStream.writeObject(expression);
			objectStream.close();	

		}
		catch (IOException e)
		{
			throw new PolicyParseException("Could not serialize policy expression to serialized POJO.", e);
		}
	}
	
	
}
