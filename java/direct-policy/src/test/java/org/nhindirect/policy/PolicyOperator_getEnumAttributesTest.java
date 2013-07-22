package org.nhindirect.policy;

import org.nhindirect.policy.impl.BinaryBooleanPolicyOperatorExecutor;

import junit.framework.TestCase;

public class PolicyOperator_getEnumAttributesTest extends TestCase
{
	public void testGetEnumAttributes_assertAttributeValues()
	{
		PolicyOperator equals = PolicyOperator.EQUALS;
		assertEquals("=", equals.getOperatorToken());
		assertEquals("equals", equals.getOperatorText());
		assertEquals(BinaryBooleanPolicyOperatorExecutor.class, equals.getExecutorClass());
		assertEquals(PolicyOperatorParamsType.BINARY, equals.getParamsType());
	}
}
