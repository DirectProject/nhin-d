/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.policy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyExpressionSerializer;
import org.nhindirect.policy.PolicyLexicon;
import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.policy.PolicyLexiconParser;

/**
 * Implementation of a {@link PolicyLexiconParser} that parses expressions using the {@link PolicyLexicon#JAVA_SER} lexicon.
 * This parse uses the Java serialized object format to parse and serialize expressions.
 * @author Greg Meyer
 * @since 1.0
 */
public class JavaSerializedObjectLexiconPolicyParser implements PolicyLexiconParser, PolicyExpressionSerializer
{
	/**
	 * Default constructor
	 */
	public JavaSerializedObjectLexiconPolicyParser()
	{
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PolicyExpression parse(InputStream stream) throws PolicyParseException 
	{
		return deserialize(stream);
	}

	/**
	 * {@inheritDoc}
	 */
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
			throw new PolicyParseException("Could not deserialize policy expression from serialized POJO.", e);
		}		
	}

	/**
	 * {@inheritDoc}
	 */
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
		///CLOVER:OFF
		catch (IOException e)
		{
			throw new PolicyParseException("Could not serialize policy expression to serialized POJO.", e);
		}
		///CLOVER:ON
	}
}
