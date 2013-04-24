package org.nhindirect.policy.impl;

import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

import junit.framework.TestCase;

public class BinaryIntegerPolicyOperatorExecutor_constructTest extends TestCase
{
	public void testContruct_validOperators_assertAttributes()
	{
		PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		PolicyValue<Integer> op2 = PolicyValueFactory.getInstance(2);
		
		BinaryIntegerPolicyOperatorExecutor<Integer,Integer> operator = new BinaryIntegerPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.BITWISE_AND);
		assertNotNull(operator);
		assertEquals(op1, operator.operand1);
		assertEquals(op2, operator.operand2);
		assertEquals(PolicyOperator.BITWISE_AND, operator.operator);
		
		operator = new BinaryIntegerPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.BITWISE_OR);
		assertNotNull(operator);
		assertEquals(PolicyOperator.BITWISE_OR, operator.operator);
	}
	
	public void testContruct_invalidOperator_assertExcpetion()
	{
		PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		PolicyValue<Integer> op2 = PolicyValueFactory.getInstance(2);
		
		boolean exceptionOccured = false;
		try
		{
			new BinaryIntegerPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.URI_VALIDATE);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
