package org.nhindirect.policy.impl;

import org.nhindirect.policy.IntegerPolicyOperatorExecutor;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

public class BinaryIntegerPolicyOperatorExecutor<O1,O2> implements IntegerPolicyOperatorExecutor<O1>
{
	protected final PolicyValue<O1> operand1;
	protected final PolicyValue<O2> operand2;	
	protected final PolicyOperator operator;
	
	public BinaryIntegerPolicyOperatorExecutor(PolicyValue<O1> operand1, PolicyValue<O2> operand2, PolicyOperator operator)
	{
		if (!(operator.equals(PolicyOperator.BITWISE_AND) || operator.equals(PolicyOperator.BITWISE_OR)))
			throw new IllegalArgumentException("Operator " + operator.getOperatorText() + " is not allowed for this executor type.");
		
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.operator = operator;
	}
	
	@Override
	public PolicyValue<Integer> execute() 
	{	
		int retVal = 0;
		
		switch(operator)
		{
			case BITWISE_AND:
			case BITWISE_OR:
			{
				int int1 = Integer.class.cast(operand1.getPolicyValue());
				int int2 = Integer.class.cast(operand2.getPolicyValue());
				
				if (operator.equals(PolicyOperator.BITWISE_AND))
					retVal = int1 & int2;
				else 
					retVal = int1 | int2;
				
				break;
			}
			///CLOVER:OFF
			default:
				retVal = 0;
			///CLOVER:ON
		}
		return PolicyValueFactory.getInstance(retVal);
	}
}
