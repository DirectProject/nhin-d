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

public class UnaryBooleanPolicyOperatorExecutor<O> implements BooleanPolicyOperatorExecutor<O>
{
	protected static final int DEFAULT_URL_CONNECTION_TIMEOUT = 10000; // 10 seconds	
	protected static final int DEFAULT_URL_READ_TIMEOUT = 10000; // 10 hour seconds	
	
	protected final PolicyValue<O> operand;
	protected final PolicyOperator operator;
	
	public UnaryBooleanPolicyOperatorExecutor(PolicyValue<O> operand, PolicyOperator operator)
	{
		if (!(operator.equals(PolicyOperator.LOGICAL_NOT) || operator.equals(PolicyOperator.URI_VALIDATE)
				|| operator.equals(PolicyOperator.EMPTY) || operator.equals(PolicyOperator.NOT_EMPTY)))
			throw new IllegalArgumentException("Operator " + operator.getOperatorText() + " is not allowed for this executor type.");
		
		this.operand = operand;
		this.operator = operator;
	}

	@Override
	public PolicyValue<Boolean> execute() 
	{
		boolean retVal = false;
		
		switch(operator)
		{
			case LOGICAL_NOT:
				retVal = !Boolean.class.cast(operand.getPolicyValue());
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
