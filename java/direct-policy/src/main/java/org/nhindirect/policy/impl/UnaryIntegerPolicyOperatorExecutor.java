package org.nhindirect.policy.impl;

import java.util.Collection;

import org.nhindirect.policy.IntegerPolicyOperatorExecutor;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

public class UnaryIntegerPolicyOperatorExecutor<O> implements IntegerPolicyOperatorExecutor<O>
{
	protected final PolicyValue<O> operand;
	protected final PolicyOperator operator;
	
	public UnaryIntegerPolicyOperatorExecutor(PolicyValue<O> operand, PolicyOperator operator)
	{
		if (!operator.equals(PolicyOperator.SIZE))
			throw new IllegalArgumentException("Operator " + operator.getOperatorText() + " is not allowed for this executor type.");
		
		this.operand = operand;
		this.operator = operator;
	}

	@Override
	public PolicyValue<Integer> execute() 
	{
		int retVal;
		
		switch(operator)
		{
			case SIZE:
			{
				final Collection<?> coll = Collection.class.cast(operand.getPolicyValue());
				
				retVal = coll.size();
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
