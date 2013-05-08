package org.nhindirect.policy.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.nhindirect.policy.CollectionPolicyOperatorExecutor;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

public class BinaryCollectionPolicyOperatorExecutor<O1,O2> implements CollectionPolicyOperatorExecutor<O1>
{
	protected final PolicyValue<O1> operand1;
	protected final PolicyValue<O2> operand2;	
	protected final PolicyOperator operator;
	
	public BinaryCollectionPolicyOperatorExecutor(PolicyValue<O1> operand1, PolicyValue<O2> operand2, PolicyOperator operator)
	{
		if (!(operator.equals(PolicyOperator.INTERSECTION)))
			throw new IllegalArgumentException("Operator " + operator.getOperatorText() + " is not allowed for this executor type.");
		
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.operator = operator;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PolicyValue<Collection<?>> execute() 
	{
		
		Collection<?> retVal;
		
		switch (operator)
		{
		    case INTERSECTION:
		    {
		    	final Collection<?> col1 = Collection.class.cast(operand1.getPolicyValue());
		    	final Collection<?> col2 = Collection.class.cast(operand2.getPolicyValue());
		    	
				final HashSet<?> set1 = new HashSet(col1);
				
				set1.retainAll(col2);
				
				retVal = set1;
				
				break;
		    }
		    ///CLOVER:OFF
			default: 
				retVal = Collections.emptyList();
		    ///COLVER:ON
		}
		
		return PolicyValue.class.cast(PolicyValueFactory.getInstance(retVal));
	}
}
