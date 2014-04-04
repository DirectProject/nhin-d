package org.nhindirect.policy.impl.machine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import junit.framework.TestCase;

import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValueFactory;
import org.nhindirect.policy.impl.machine.StackMachine;
import org.nhindirect.policy.impl.machine.StackMachineEntry;

public class StackMachine_evaluateTest extends TestCase
{
	public void testEvaluate_singleBooleanEntry_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}
	
	public void testEvaluate_singleBooleanEntry_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	

	public void testEvaluate_equalsSameStringValues_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_equalsDifferentStringValues_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("22345")));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_notEqualsSameStringValues_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.NOT_EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_notEqualsDifferentStringValues_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("22345")));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.NOT_EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}
	
	public void testEvaluate_equalsDifferentTypeValues_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(12345)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_notEqualsDifferentTypeValues_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(12345)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.NOT_EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_regExStringMatches_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("bbbbb")));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("a|b*")));

		stuffToProcess.add(new StackMachineEntry(PolicyOperator.REG_EX));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_regExStringNotMatch_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("cccc")));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("a|b")));

		stuffToProcess.add(new StackMachineEntry(PolicyOperator.REG_EX));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}
	
	public void testEvaluate_greater_topOfStackIsGreater_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.GREATER));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_greater_bottomOfStockIsGreater_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.GREATER));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_less_topOfStackIsLess_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LESS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_less_bottomOfStockIsLess_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LESS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}		
	
	
	public void testEvaluate_logicalOrBothTrue_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_OR));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	
	public void testEvaluate_logicalOrOneTrue_assertTrue() throws Exception
	{
		Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_OR));
		
		StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
		
		stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_OR));
		
		stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));		
	}	
	
	public void testEvaluate_logicalOrNeitherTrue_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_OR));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	

	public void testEvaluate_logicalAndBothTrue_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_AND));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_logicalAndOneTrue_assertFalse() throws Exception
	{
		Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_AND));
		
		StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
		
		stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_AND));
		
		stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));	
	}		
	
	public void testEvaluate_logicalAndBothFalse_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_AND));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	

	public void testEvaluate_bitwiseOrBitSetOnBoth_assertEquals() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.BITWISE_OR));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	

	public void testEvaluate_bitwiseOrBitSetOnOne_assertEquals() throws Exception
	{
		Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.BITWISE_OR));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
		
		stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.BITWISE_OR));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
		
	}	
	
	public void testEvaluate_bitwiseOrBitSetOnNeither_assertEquals() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.BITWISE_OR));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}
	
	public void testEvaluate_bitwiseAndBitSetOnBoth_assertEquals() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.BITWISE_AND));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_bitwiseAndBitSetOnOne_assertEquals() throws Exception
	{
		Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.BITWISE_AND));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
		
		stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.BITWISE_AND));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));	
	}	
	
	public void testEvaluate_bitwiseAndBitSetOnNeither_assertEquals() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.BITWISE_AND));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_logicalNotFalseValue_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_NOT));

		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_logicalNotTrueValue_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_NOT));

		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_sizeCollection_empty_assertSize0() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(Collections.emptyList())));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.SIZE));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(0)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_sizeCollection_singleEntry_assertSize1() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(Arrays.asList("Hello"))));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.SIZE));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(1)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.EQUALS));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_uriValidate_validUri_assertTrue() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("http://www.cerner.com/CPS")));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.URI_VALIDATE));
		
		final StackMachine stMachine = new StackMachine();
		
		assertTrue(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_uriValidate_notFoundUri_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("http://www.google.com/333333")));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.URI_VALIDATE));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}	
	
	public void testEvaluate_uriValidate_noHostURI_assertFalse() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("http://bogus.unit.test.ccc")));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.URI_VALIDATE));
		
		final StackMachine stMachine = new StackMachine();
		
		assertFalse(stMachine.evaluate(stuffToProcess));
	}		
	
	public void testEvaluate_notEnoughParamsForBinaryOperation_assertException() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(true)));
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_OR));
		
		boolean exceptionOccured = false;
		
		final StackMachine stMachine = new StackMachine();
		
		try
		{

			stMachine.evaluate(stuffToProcess);
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testEvaluate_notEnoughParamsForUnaryOperation_assertException() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyOperator.LOGICAL_NOT));
		
		boolean exceptionOccured = false;
		
		final StackMachine stMachine = new StackMachine();
		
		try
		{

			stMachine.evaluate(stuffToProcess);
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testEvaluate_finalEmptyStack_assertException() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		
		boolean exceptionOccured = false;
		
		final StackMachine stMachine = new StackMachine();
		
		try
		{

			stMachine.evaluate(stuffToProcess);
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testEvaluate_finalStackGreaterThanSingleEntry_assertException() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance(false)));
		
		boolean exceptionOccured = false;
		
		final StackMachine stMachine = new StackMachine();
		
		try
		{

			stMachine.evaluate(stuffToProcess);
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}	
	
	public void testEvaluate_finalNonBoolEntry_assertException() throws Exception
	{
		final Vector<Opcode> stuffToProcess = new Vector<Opcode>();
		stuffToProcess.add(new StackMachineEntry(PolicyValueFactory.getInstance("12345")));
		
		boolean exceptionOccured = false;
		
		final StackMachine stMachine = new StackMachine();
		
		try
		{

			stMachine.evaluate(stuffToProcess);
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}		
}
