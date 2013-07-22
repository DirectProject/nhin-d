package org.nhindirect.policy.impl.machine;

import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValueFactory;

import junit.framework.TestCase;

public class StackMachineEntry_toStringTest extends TestCase
{
	public void testToString_operatorEntry_assertString()
	{
		
		final StackMachineEntry entry = new StackMachineEntry(PolicyOperator.EQUALS);
		
		assertTrue(entry.toString().startsWith("Entry Type"));
		assertTrue(entry.toString().contains("Operator:"));
		assertTrue(entry.toString().contains("equals"));		
	}
	
	public void testToString_valueEntry_assertString()
	{		
		final StackMachineEntry entry = new StackMachineEntry(PolicyValueFactory.getInstance(12345));
		
		assertTrue(entry.toString().startsWith("Entry Type"));
		assertTrue(entry.toString().contains("Value:"));
		assertTrue(entry.toString().contains("12345"));		
	}	
}
