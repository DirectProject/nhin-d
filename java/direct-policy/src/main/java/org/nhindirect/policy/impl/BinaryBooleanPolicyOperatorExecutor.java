package org.nhindirect.policy.impl;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nhindirect.policy.BooleanPolicyOperatorExecutor;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

public class BinaryBooleanPolicyOperatorExecutor<O1,O2> implements BooleanPolicyOperatorExecutor<O1>
{
	protected final PolicyValue<O1> operand1;
	protected final PolicyValue<O2> operand2;	
	protected final PolicyOperator operator;
	
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
	
	@Override
	public PolicyValue<Boolean> execute() 
	{
		
		boolean retVal = false;
		
		switch (operator)
		{
		    case EQUALS:
				retVal = operand1.getPolicyValue().equals(operand2.getPolicyValue());
				break;
		    case NOT_EQUALS:
				retVal = !operand1.getPolicyValue().equals(operand2.getPolicyValue());	
				break;
		    case GREATER:
		    case LESS:
		    {
				// get the two operands as booleans
				final Integer op1 = Integer.class.cast(operand1.getPolicyValue());
				final Integer op2 = Integer.class.cast(operand2.getPolicyValue());
				
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
				final Boolean op1 = Boolean.class.cast(operand1.getPolicyValue());
				final Boolean op2 = Boolean.class.cast(operand2.getPolicyValue());
				
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
}
