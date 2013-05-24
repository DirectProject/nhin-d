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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.nhindirect.policy.BooleanPolicyOperatorExecutor;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

/**
 * Executor that evaluates operations that take a single parameter and return a boolean value.
 * @author Greg Meyer
 * @since 1.0
 *
 * @param <O> Object type of the single operand.
 */
public class UnaryBooleanPolicyOperatorExecutor<O> implements BooleanPolicyOperatorExecutor<O>
{
	protected static final int DEFAULT_URL_CONNECTION_TIMEOUT = 10000; // 10 seconds	
	protected static final int DEFAULT_URL_READ_TIMEOUT = 10000; // 10 hour seconds	
	
	protected final PolicyValue<O> operand;
	protected final PolicyOperator operator;
	
	/**
	 * Constructor
	 * @param operand  The single operand
	 * @param operator The operation that will be executed.
	 */
	public UnaryBooleanPolicyOperatorExecutor(PolicyValue<O> operand, PolicyOperator operator)
	{
		if (!(operator.equals(PolicyOperator.LOGICAL_NOT) || operator.equals(PolicyOperator.URI_VALIDATE)
				|| operator.equals(PolicyOperator.EMPTY) || operator.equals(PolicyOperator.NOT_EMPTY)))
			throw new IllegalArgumentException("Operator " + operator.getOperatorText() + " is not allowed for this executor type.");
		
		this.operand = operand;
		this.operator = operator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PolicyValue<Boolean> execute() 
	{
		boolean retVal = false;
		
		switch(operator)
		{
			case LOGICAL_NOT:
				retVal = !Boolean.parseBoolean(operand.getPolicyValue().toString());
				break;
			case EMPTY:
			case NOT_EMPTY:
			{
				final Collection<?> container = Collection.class.cast(operand.getPolicyValue());
				
				retVal = (operator.equals(PolicyOperator.EMPTY)) ? container.isEmpty() : !container.isEmpty();
				
				break;
			}
			case URI_VALIDATE:
			{
				InputStream inputStream = null;
				try
				{
					final String uri = String.class.cast(operand.getPolicyValue());
				
					final URL certURL = new URL(uri);
				
					final URLConnection connection = certURL.openConnection();
				
					// the connection is not actually made until the input stream
					// is open, so set the timeouts before getting the stream
					connection.setConnectTimeout(DEFAULT_URL_CONNECTION_TIMEOUT);
					connection.setReadTimeout(DEFAULT_URL_READ_TIMEOUT);
					
					// open the URL as in input stream
					inputStream = connection.getInputStream();
					
					int respCode = ((HttpURLConnection)connection).getResponseCode();
					
					if (respCode >= 200 && respCode < 300)
						retVal = true;
				}
				catch (IOException e)
				{
					retVal = false;
				}
				finally
				{
					IOUtils.closeQuietly(inputStream);
				}
				break;
			}
			///CLOVER:OFF
			default: 
				retVal = false;
			///CLOVER:ON	
		}
		return PolicyValueFactory.getInstance(retVal);
	}
	
	
}
