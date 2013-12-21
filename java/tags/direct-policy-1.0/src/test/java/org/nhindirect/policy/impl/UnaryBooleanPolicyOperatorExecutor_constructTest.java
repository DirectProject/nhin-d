package org.nhindirect.policy.impl;

import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

import junit.framework.TestCase;

public class UnaryBooleanPolicyOperatorExecutor_constructTest extends TestCase
{
	public void testContruct_validOperators_assertAttributes()
	{
		PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		
		UnaryBooleanPolicyOperatorExecutor<Integer> operator = new UnaryBooleanPolicyOperatorExecutor<Integer>(op1,PolicyOperator.LOGICAL_NOT);
		assertNotNull(operator);
		assertEquals(op1, operator.operand);
		assertEquals(PolicyOperator.LOGICAL_NOT, operator.operator);
		
		operator= new UnaryBooleanPolicyOperatorExecutor<Integer>(op1,PolicyOperator.URI_VALIDATE);
		assertNotNull(operator);
		assertEquals(PolicyOperator.URI_VALIDATE, operator.operator);
		
		operator = new UnaryBooleanPolicyOperatorExecutor<Integer>(op1,PolicyOperator.EMPTY);
		assertNotNull(operator);
		assertEquals(PolicyOperator.EMPTY, operator.operator);	
		
		operator = new UnaryBooleanPolicyOperatorExecutor<Integer>(op1,PolicyOperator.NOT_EMPTY);
		assertNotNull(operator);
		assertEquals(PolicyOperator.NOT_EMPTY, operator.operator);	
	}
	
	public void testContruct_invalidOperator_assertExcpetion()
	{
		PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		
		boolean exceptionOccured = false;
		try
		{
			new UnaryBooleanPolicyOperatorExecutor<Integer>(op1, PolicyOperator.LOGICAL_AND);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
