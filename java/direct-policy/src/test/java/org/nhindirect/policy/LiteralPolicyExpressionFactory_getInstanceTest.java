package org.nhindirect.policy;

import junit.framework.TestCase;

public class LiteralPolicyExpressionFactory_getInstanceTest extends TestCase
{
	public void testGetInstance_policyValueT()
	{
		LiteralPolicyExpression<Integer> value = 
				LiteralPolicyExpressionFactory.getInstance(PolicyValueFactory.getInstance(1234));
		
		assertNotNull(value);
		assertEquals(1234, (int)value.getPolicyValue().getPolicyValue());
		assertEquals(PolicyExpressionType.LITERAL, value.getExpressionType());
	}
	
	public void testGetInstance_valueT()
	{
		LiteralPolicyExpression<Integer> value = 
				LiteralPolicyExpressionFactory.getInstance(1234);
		
		assertNotNull(value);
		assertEquals(1234, (int)value.getPolicyValue().getPolicyValue());
		assertEquals(PolicyExpressionType.LITERAL, value.getExpressionType());
	}
}
