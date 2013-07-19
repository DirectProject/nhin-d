package org.nhindirect.policy.impl;

import java.util.Arrays;
import java.util.List;

import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;

import junit.framework.TestCase;

public class BinaryBooleanPolicyOperatorExecutor_executeTest extends TestCase
{
	public void testExecute_logicalAnd_assertResults()
	{
		// both true
		PolicyValue<Boolean> op1 = PolicyValueFactory.getInstance(true);
		PolicyValue<Boolean> op2 = PolicyValueFactory.getInstance(true);
		
		BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean> operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.LOGICAL_AND);
		assertTrue(operator.execute().getPolicyValue());
		
		// one false one true
		op1 = PolicyValueFactory.getInstance(true);
		op2 = PolicyValueFactory.getInstance(false);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.LOGICAL_AND);
		assertFalse(operator.execute().getPolicyValue());
	
		// both false
		op1 = PolicyValueFactory.getInstance(false);
		op2 = PolicyValueFactory.getInstance(false);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.LOGICAL_AND);
		assertFalse(operator.execute().getPolicyValue());
	}
	
	public void testExecute_logicalAnd_stringArguments_assertResults()
	{
		// both true
		PolicyValue<String> op1 = PolicyValueFactory.getInstance("true");
		PolicyValue<String> op2 = PolicyValueFactory.getInstance("true");
		
		BinaryBooleanPolicyOperatorExecutor<String, String> operator = new BinaryBooleanPolicyOperatorExecutor<String, String>(op1, op2, PolicyOperator.LOGICAL_AND);
		assertTrue(operator.execute().getPolicyValue());
		
		// one false one true
		op1 = PolicyValueFactory.getInstance("true");
		op2 = PolicyValueFactory.getInstance("false");
		
		operator = new BinaryBooleanPolicyOperatorExecutor<String, String>(op1, op2, PolicyOperator.LOGICAL_AND);
		assertFalse(operator.execute().getPolicyValue());
	
		// both false
		op1 = PolicyValueFactory.getInstance("false");
		op2 = PolicyValueFactory.getInstance("false");
		
		operator = new BinaryBooleanPolicyOperatorExecutor<String, String>(op1, op2, PolicyOperator.LOGICAL_AND);
		assertFalse(operator.execute().getPolicyValue());
	}
	
	public void testExecute_logicalOr_assertResults()
	{
		// both true
		PolicyValue<Boolean> op1 = PolicyValueFactory.getInstance(true);
		PolicyValue<Boolean> op2 = PolicyValueFactory.getInstance(true);
		
		BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean> operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.LOGICAL_OR);
		assertTrue(operator.execute().getPolicyValue());
		
		// one false one true
		op1 = PolicyValueFactory.getInstance(true);
		op2 = PolicyValueFactory.getInstance(false);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.LOGICAL_OR);
		assertTrue(operator.execute().getPolicyValue());
	
		// both false
		op1 = PolicyValueFactory.getInstance(false);
		op2 = PolicyValueFactory.getInstance(false);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.LOGICAL_OR);
		assertFalse(operator.execute().getPolicyValue());
	}	
	
	public void testExecute_equals_assertResults()
	{
		// boolean equals
		PolicyValue<Boolean> op1 = PolicyValueFactory.getInstance(true);
		PolicyValue<Boolean> op2 = PolicyValueFactory.getInstance(true);
		
		BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean> operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.EQUALS);
		assertTrue(operator.execute().getPolicyValue());
		
		// boolean not equals
		op1 = PolicyValueFactory.getInstance(true);
		op2 = PolicyValueFactory.getInstance(false);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.EQUALS);
		assertFalse(operator.execute().getPolicyValue());
	
		// integer equals
		PolicyValue<Integer> opInt1 = PolicyValueFactory.getInstance(123);
		PolicyValue<Integer> opInt2 = PolicyValueFactory.getInstance(123);
		
		BinaryBooleanPolicyOperatorExecutor<Integer, Integer> intOperator = new BinaryBooleanPolicyOperatorExecutor<Integer, Integer>(opInt1, opInt2, PolicyOperator.EQUALS);
		assertTrue(intOperator.execute().getPolicyValue());
		
		// integer not equals
		opInt1 = PolicyValueFactory.getInstance(123);
		opInt2 = PolicyValueFactory.getInstance(456);
		
		intOperator = new BinaryBooleanPolicyOperatorExecutor<Integer, Integer>(opInt1, opInt2, PolicyOperator.EQUALS);
		assertFalse(intOperator.execute().getPolicyValue());
	}	
	
	public void testExecute_notEquals_assertResults()
	{
		// boolean equals
		PolicyValue<Boolean> op1 = PolicyValueFactory.getInstance(true);
		PolicyValue<Boolean> op2 = PolicyValueFactory.getInstance(true);
		
		BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean> operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.NOT_EQUALS);
		assertFalse(operator.execute().getPolicyValue());
		
		// boolean not equals
		op1 = PolicyValueFactory.getInstance(true);
		op2 = PolicyValueFactory.getInstance(false);
		
		operator = new BinaryBooleanPolicyOperatorExecutor<Boolean, Boolean>(op1, op2, PolicyOperator.NOT_EQUALS);
		assertTrue(operator.execute().getPolicyValue());
	
		// integer equals
		PolicyValue<Integer> opInt1 = PolicyValueFactory.getInstance(123);
		PolicyValue<Integer> opInt2 = PolicyValueFactory.getInstance(123);
		
		BinaryBooleanPolicyOperatorExecutor<Integer, Integer> intOperator = new BinaryBooleanPolicyOperatorExecutor<Integer, Integer>(opInt1, opInt2, PolicyOperator.NOT_EQUALS);
		assertFalse(intOperator.execute().getPolicyValue());
		
		// integer not equals
		opInt1 = PolicyValueFactory.getInstance(123);
		opInt2 = PolicyValueFactory.getInstance(456);
		
		intOperator = new BinaryBooleanPolicyOperatorExecutor<Integer, Integer>(opInt1, opInt2, PolicyOperator.NOT_EQUALS);
		assertTrue(intOperator.execute().getPolicyValue());
	}	
	
	public void testExecute_greater_assertResults()
	{	
		// op1 greater than op2
		// compiler uses reverse polish notation, so the operands need to be backwards
		PolicyValue<Integer> opInt1 = PolicyValueFactory.getInstance(5);
		PolicyValue<Integer> opInt2 = PolicyValueFactory.getInstance(4);
		
		BinaryBooleanPolicyOperatorExecutor<Integer, Integer> intOperator = new BinaryBooleanPolicyOperatorExecutor<Integer, Integer>(opInt1, opInt2, PolicyOperator.GREATER);
		assertFalse(intOperator.execute().getPolicyValue());
		
		// op1 less than op2
		opInt1 = PolicyValueFactory.getInstance(4);
		opInt2 = PolicyValueFactory.getInstance(5);
		
		intOperator = new BinaryBooleanPolicyOperatorExecutor<Integer, Integer>(opInt1, opInt2, PolicyOperator.GREATER);
		assertTrue(intOperator.execute().getPolicyValue());
	}		
	
	public void testExecute_less_assertResults()
	{	
		// op1 less than op2
		// compiler uses reverse polish notation, so the operands need to be backwards
		PolicyValue<Integer> opInt1 = PolicyValueFactory.getInstance(4);
		PolicyValue<Integer> opInt2 = PolicyValueFactory.getInstance(5);
		
		BinaryBooleanPolicyOperatorExecutor<Integer, Integer> intOperator = new BinaryBooleanPolicyOperatorExecutor<Integer, Integer>(opInt1, opInt2, PolicyOperator.LESS);
		assertFalse(intOperator.execute().getPolicyValue());
		
		// op1 greater than op2
		opInt1 = PolicyValueFactory.getInstance(5);
		opInt2 = PolicyValueFactory.getInstance(4);
		
		intOperator = new BinaryBooleanPolicyOperatorExecutor<Integer, Integer>(opInt1, opInt2, PolicyOperator.LESS);
		assertTrue(intOperator.execute().getPolicyValue());
	}	
	
	public void testExecute_contains_assertResults()
	{	
		// container contains string
		PolicyValue<String> op1 = PolicyValueFactory.getInstance("689");
		PolicyValue<List<String>> op2 = PolicyValueFactory.getInstance(Arrays.asList("123","456","689"));
		
		BinaryBooleanPolicyOperatorExecutor<String, List<String>> operator = 
				new BinaryBooleanPolicyOperatorExecutor<String, List<String>>(op1, op2, PolicyOperator.CONTAINS);
		assertTrue(operator.execute().getPolicyValue());
		
		// container does not contain string
		op1 = PolicyValueFactory.getInstance("777");
		op2 = PolicyValueFactory.getInstance(Arrays.asList("123","456","689"));
		
		operator = new BinaryBooleanPolicyOperatorExecutor<String, List<String>>(op1, op2, PolicyOperator.CONTAINS);
		assertFalse(operator.execute().getPolicyValue());
	}	
	
	public void testExecute_notContains_assertResults()
	{	
		// container contains string
		PolicyValue<String> op1 = PolicyValueFactory.getInstance("689");
		PolicyValue<List<String>> op2 = PolicyValueFactory.getInstance(Arrays.asList("123","456","689"));
		
		BinaryBooleanPolicyOperatorExecutor<String, List<String>> operator = 
				new BinaryBooleanPolicyOperatorExecutor<String, List<String>>(op1, op2, PolicyOperator.NOT_CONTAINS);
		assertFalse(operator.execute().getPolicyValue());
		
		// container does not contain string
		op1 = PolicyValueFactory.getInstance("777");
		op2 = PolicyValueFactory.getInstance(Arrays.asList("123","456","689"));
		
		operator = new BinaryBooleanPolicyOperatorExecutor<String, List<String>>(op1, op2, PolicyOperator.NOT_CONTAINS);
		assertTrue(operator.execute().getPolicyValue());
	}		
	
	public void testExecute_containsRegEx_assertResults()
	{	
		// container contains string reg ex
		PolicyValue<String> op1 = PolicyValueFactory.getInstance("http");
		PolicyValue<List<String>> op2 = PolicyValueFactory.getInstance(Arrays.asList("http://thisis.aurl.com"));
		
		BinaryBooleanPolicyOperatorExecutor<String, List<String>> operator = 
				new BinaryBooleanPolicyOperatorExecutor<String, List<String>>(op1, op2, PolicyOperator.CONTAINS_REG_EX);
		assertTrue(operator.execute().getPolicyValue());
		
		// container does not contain string reg ex
		op1 = PolicyValueFactory.getInstance("777");
		op2 = PolicyValueFactory.getInstance(Arrays.asList("http://thisis.aurl.com"));
		
		operator = new BinaryBooleanPolicyOperatorExecutor<String, List<String>>(op1, op2, PolicyOperator.CONTAINS_REG_EX);
		assertFalse(operator.execute().getPolicyValue());
	}	
	
	public void testExecute_regEx_assertResults()
	{	
		// string contains string reg ex
		PolicyValue<String> op1 = PolicyValueFactory.getInstance("http");
		PolicyValue<String> op2 = PolicyValueFactory.getInstance("http://thisis.aurl.com");
		
		BinaryBooleanPolicyOperatorExecutor<String, String> operator = 
				new BinaryBooleanPolicyOperatorExecutor<String, String>(op1, op2, PolicyOperator.REG_EX);
		assertTrue(operator.execute().getPolicyValue());
		
		// container does not contain string reg ex
		op1 = PolicyValueFactory.getInstance("777");
		op2 = PolicyValueFactory.getInstance("http://thisis.aurl.com");
		
		operator = new BinaryBooleanPolicyOperatorExecutor<String, String>(op1, op2, PolicyOperator.REG_EX);
		assertFalse(operator.execute().getPolicyValue());
	}		
}
