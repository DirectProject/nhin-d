package org.nhindirect.policy.impl;

import java.util.Arrays;
import java.util.List;

import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

import junit.framework.TestCase;

public class UnaryBooleanPolicyOperatorExecutor_executeTest extends TestCase
{
	public void testExecute_logicalNot_assertResults()
	{
		// true, return false
		PolicyValue<Boolean> op1 = PolicyValueFactory.getInstance(true);
		
		UnaryBooleanPolicyOperatorExecutor<Boolean> operator = new UnaryBooleanPolicyOperatorExecutor<Boolean>(op1, PolicyOperator.LOGICAL_NOT);
		assertFalse(operator.execute().getPolicyValue());
		
		// false, return true
		op1 = PolicyValueFactory.getInstance(false);
		
		operator = new UnaryBooleanPolicyOperatorExecutor<Boolean>(op1, PolicyOperator.LOGICAL_NOT);
		assertTrue(operator.execute().getPolicyValue());
	
	}
	
	public void testExecute_empty_assertResults()
	{
		// not empty
		PolicyValue<List<String>> op1 = PolicyValueFactory.getInstance(Arrays.asList("A"));
		
		UnaryBooleanPolicyOperatorExecutor<List<String>> operator = new UnaryBooleanPolicyOperatorExecutor<List<String>>(op1, PolicyOperator.EMPTY);
		assertFalse(operator.execute().getPolicyValue());
		
		// empty
		op1 = PolicyValueFactory.getInstance(Arrays.asList(new String[] {}));
		
		operator = new UnaryBooleanPolicyOperatorExecutor<List<String>>(op1, PolicyOperator.EMPTY);
		assertTrue(operator.execute().getPolicyValue());
	
	}
	
	public void testExecute_notEmpty_assertResults()
	{
		// not empty
		PolicyValue<List<String>> op1 = PolicyValueFactory.getInstance(Arrays.asList("A"));
		
		UnaryBooleanPolicyOperatorExecutor<List<String>> operator = new UnaryBooleanPolicyOperatorExecutor<List<String>>(op1, PolicyOperator.NOT_EMPTY);
		assertTrue(operator.execute().getPolicyValue());
		
		// empty
		op1 = PolicyValueFactory.getInstance(Arrays.asList(new String[] {}));
		
		operator = new UnaryBooleanPolicyOperatorExecutor<List<String>>(op1, PolicyOperator.NOT_EMPTY);
		assertFalse(operator.execute().getPolicyValue());
	
	}
	
	public void testExecute_uriValidate_assertResults()
	{
		// valid URI
		PolicyValue<String> op1 = PolicyValueFactory.getInstance("http://www.cerner.com/CPS");
		
		UnaryBooleanPolicyOperatorExecutor<String> operator = new UnaryBooleanPolicyOperatorExecutor<String>(op1, PolicyOperator.URI_VALIDATE);
		assertTrue(operator.execute().getPolicyValue());
		
		// uri not found
		op1 = PolicyValueFactory.getInstance("http://www.google.com/333333");
		
		operator = new UnaryBooleanPolicyOperatorExecutor<String>(op1, PolicyOperator.URI_VALIDATE);
		assertFalse(operator.execute().getPolicyValue());
		
		// host not found
		op1 = PolicyValueFactory.getInstance("http://bogus.unit.test.ccc");
		
		operator = new UnaryBooleanPolicyOperatorExecutor<String>(op1, PolicyOperator.URI_VALIDATE);
		assertFalse(operator.execute().getPolicyValue());
	
	}
}
