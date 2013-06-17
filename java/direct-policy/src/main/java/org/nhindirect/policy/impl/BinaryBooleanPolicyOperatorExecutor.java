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

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nhindirect.policy.BooleanPolicyOperatorExecutor;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

/**
 * Executor that evaluates operations that take two parameters and return a boolean value.
 * @author Greg Meyer
 * @since 1.0
 * 
 * @param <O1> Object type of the first operand.
 * @param <O2> Object type of the second operand.
 */
public class BinaryBooleanPolicyOperatorExecutor<O1,O2> implements BooleanPolicyOperatorExecutor<O1>
{
	protected final PolicyValue<O1> operand1;
	protected final PolicyValue<O2> operand2;	
	protected final PolicyOperator operator;
	
	/**
	 * Constructor
	 * @param operand1 The first operand
	 * @param operand2 The second operand
	 * @param operator The operation that will be executed.
	 */
	public BinaryBooleanPolicyOperatorExecutor(PolicyValue<O1> operand1, PolicyValue<O2> operand2, PolicyOperator operator)
	{
		if (!(operator.equals(PolicyOperator.LOGICAL_AND) || operator.equals(PolicyOperator.LOGICAL_OR) || 
				operator.equals(PolicyOperator.EQUALS) || operator.equals(PolicyOperator.NOT_EQUALS) ||
				operator.equals(PolicyOperator.REG_EX) || operator.equals(PolicyOperator.GREATER ) ||
				operator.equals(PolicyOperator.LESS) || operator.equals(PolicyOperator.CONTAINS) || 
				operator.equals(PolicyOperator.NOT_CONTAINS) || operator.equals(PolicyOperator.CONTAINS_REG_EX)))
			throw new IllegalArgumentException("Operator " + operator.getOperatorText() + " is not allowed for this executor type.");
		
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.operator = operator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PolicyValue<Boolean> execute() 
	{
		
		boolean retVal = false;
		
		switch (operator)
		{
		    case EQUALS:
		    {
		    	Object op1 = (operand1.getPolicyValue() instanceof Integer || operand1.getPolicyValue() instanceof Boolean) 
		    			? operand1.getPolicyValue().toString() : operand1.getPolicyValue();
		    	Object op2 = (operand2.getPolicyValue() instanceof Integer || operand2.getPolicyValue() instanceof Boolean)
		    	        ? operand2.getPolicyValue().toString() : operand2.getPolicyValue();
				retVal = op1.equals(op2);
				break;
		    }
		    case NOT_EQUALS:
		    {
		    	Object op1 = (operand1.getPolicyValue() instanceof Integer || operand1.getPolicyValue() instanceof Boolean) 
		    			? operand1.getPolicyValue().toString() : operand1.getPolicyValue();
		    	Object op2 = (operand2.getPolicyValue() instanceof Integer || operand2.getPolicyValue() instanceof Boolean) ? 
		    			operand2.getPolicyValue().toString() : operand2.getPolicyValue();
				retVal = !op1.equals(op2);	
				break;
		    }
		    case GREATER:
		    case LESS:
		    {
				// get the two operands as booleans
				final Integer op1 = (operand1.getPolicyValue() instanceof Integer) ? Integer.class.cast(operand1.getPolicyValue()) :
					Integer.valueOf(operand1.getPolicyValue().toString());
				final Integer op2 = (operand2.getPolicyValue() instanceof Integer) ? Integer.class.cast(operand2.getPolicyValue()) :
					Integer.valueOf(operand2.getPolicyValue().toString());
				
				// needs to be backwards because we are using RPN to push parameters
				// in the stack machine
				if (operator.equals(PolicyOperator.GREATER))
					retVal = op2 > op1;
				else
					retVal = op2 < op1;
				
				break;
		    }	
		    case CONTAINS:
		    {
				// using RPN so op2 is the collection and op1 is the value
				final Collection<?> container = Collection.class.cast(operand2.getPolicyValue());
				final Object value = operand1.getPolicyValue();
				
				retVal = container.contains(value);
				
				break;
		    }
		    case NOT_CONTAINS:
		    {
				// using RPN so op2 is the collection and op1 is the value
				final Collection<?> container = Collection.class.cast(operand2.getPolicyValue());
				final Object value = operand1.getPolicyValue();
				
				retVal = !container.contains(value);
				
				break;
		    }
		    case CONTAINS_REG_EX:
		    {
				final Collection<?> container = Collection.class.cast(operand2.getPolicyValue());
				final String pattern = operand1.getPolicyValue().toString();
				final Pattern regExPatt = Pattern.compile(pattern);
				
				
				for (Object obj : container)
				{
					final Matcher match = regExPatt.matcher(obj.toString());

					if ((retVal = match.find()) == true)
						break;
				}
		    	
		    	break;
		    }
		    case REG_EX:
			{
				// two operands assumed to be strings
				final String pattern = operand1.getPolicyValue().toString();
				final String str = operand2.getPolicyValue().toString();
				
				final Pattern regExPatt = Pattern.compile(pattern);
				final Matcher match = regExPatt.matcher(str);
				
				retVal = match.find();
				break;
			}
		    case LOGICAL_AND:
		    case LOGICAL_OR:
			{
				// get the two operands as booleans
				final Boolean op1 = Boolean.parseBoolean(operand1.getPolicyValue().toString());
				final Boolean op2 = Boolean.parseBoolean(operand2.getPolicyValue().toString());
				
				if (operator.equals(PolicyOperator.LOGICAL_AND))
					retVal = op1 && op2;
				else
					retVal = op1 || op2;
				
				break;
			}
			///CLOVER:OFF
			default: 
				retVal = false;
			///CLOVER:ON
		}
		
		return PolicyValueFactory.getInstance(retVal);
	}
	
	/**
	 * {@inheritDoc}
	 */
    ///CLOVER:OFF
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder("Operator: ").append(operator.toString())
		.append("\r\nOperand 1: ").append(operand1)
		.append("\r\nOperand 2: ").append(operand2).append("\r\n");
		
		return builder.toString();
	}
    ///CLOVER:ON
}
