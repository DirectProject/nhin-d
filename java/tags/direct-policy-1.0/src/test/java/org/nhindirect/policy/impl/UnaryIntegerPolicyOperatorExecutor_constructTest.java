package org.nhindirect.policy.impl;

import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

import junit.framework.TestCase;

public class UnaryIntegerPolicyOperatorExecutor_constructTest extends TestCase
{
	public void testContruct_validOperators_assertAttributes()
	{
		PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		
		UnaryIntegerPolicyOperatorExecutor<Integer> operator = new UnaryIntegerPolicyOperatorExecutor<Integer>(op1, PolicyOperator.SIZE);
		assertNotNull(operator);
		assertEquals(op1, operator.operand);
		assertEquals(PolicyOperator.SIZE, operator.operator);
	}
	
	public void testContruct_invalidOperator_assertExcpetion()
	{
		PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		
		boolean exceptionOccured = false;
		try
		{
			new UnaryIntegerPolicyOperatorExecutor<Integer>(op1, PolicyOperator.URI_VALIDATE);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
