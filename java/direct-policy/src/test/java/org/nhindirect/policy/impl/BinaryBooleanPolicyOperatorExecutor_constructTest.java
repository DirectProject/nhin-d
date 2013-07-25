package org.nhindirect.policy.impl;

import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

import junit.framework.TestCase;

public class BinaryBooleanPolicyOperatorExecutor_constructTest extends TestCase
{
	public void testContruct_validOperators_assertAttributes()
	{
		PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		PolicyValue<Integer> op2 = PolicyValueFactory.getInstance(2);
		
		BinaryBooleanPolicyOperatorExecutor<Integer,Integer> operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.LOGICAL_AND);
		assertNotNull(operator);
		assertEquals(op1, operator.operand1);
		assertEquals(op2, operator.operand2);
		assertEquals(PolicyOperator.LOGICAL_AND, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.LOGICAL_OR);
		assertNotNull(operator);
		assertEquals(PolicyOperator.LOGICAL_OR, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.EQUALS);
		assertNotNull(operator);
		assertEquals(PolicyOperator.EQUALS, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.NOT_EQUALS);
		assertNotNull(operator);
		assertEquals(PolicyOperator.NOT_EQUALS, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.REG_EX);
		assertNotNull(operator);
		assertEquals(PolicyOperator.REG_EX, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.GREATER);
		assertNotNull(operator);
		assertEquals(PolicyOperator.GREATER, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.LESS);
		assertNotNull(operator);
		assertEquals(PolicyOperator.LESS, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.CONTAINS);
		assertNotNull(operator);
		assertEquals(PolicyOperator.CONTAINS, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.NOT_CONTAINS);
		assertNotNull(operator);
		assertEquals(PolicyOperator.NOT_CONTAINS, operator.operator);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.CONTAINS_REG_EX);
		assertNotNull(operator);
		assertEquals(PolicyOperator.CONTAINS_REG_EX, operator.operator);
		
	}
	
	public void testContruct_invalidOperator_assertExcpetion()
	{
		PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		PolicyValue<Integer> op2 = PolicyValueFactory.getInstance(2);
		
		boolean exceptionOccured = false;
		try
		{
			new BinaryBooleanPolicyOperatorExecutor<Integer,Integer>(op1, op2, PolicyOperator.URI_VALIDATE);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
