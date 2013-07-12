package org.nhindirect.policy;

import java.util.Vector;

import junit.framework.TestCase;

public class OperationPolicyExpression_getInstanceTest extends TestCase
{
	public void testGetInstance_assertExpression()
	{
		Vector<PolicyExpression> operands = new Vector<PolicyExpression>();
		operands.add(LiteralPolicyExpressionFactory.getInstance(true));
		operands.add(LiteralPolicyExpressionFactory.getInstance(false));
		
		OperationPolicyExpression expression = OperationPolicyExpressionFactory.getInstance(PolicyOperator.BITWISE_AND, operands);
		
		assertNotNull(expression);
		assertEquals(PolicyOperator.BITWISE_AND, expression.getPolicyOperator());
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		assertEquals(operands, expression.getOperands());
	}
}
