package org.nhindirect.monitor.condition.impl;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nhindirect.monitor.condition.TxCompletionCondition;

public class VariableCompletionCondition_constructionTest 
{
	@Test
	public void testConstruction()
	{
		TxCompletionCondition cond1 = mock(TxCompletionCondition.class);
		TxCompletionCondition cond2 = mock(TxCompletionCondition.class);
		
		VariableCompletionCondition cond = new VariableCompletionCondition(cond1, cond2);
		
		assertEquals(cond1, cond.timelyRelCondition);
		assertEquals(cond2, cond.generalCondition);
	}
	
	@Test
	public void testConstruction_nullTimelyCondition_assertException()
	{
		TxCompletionCondition cond = mock(TxCompletionCondition.class);
		
		boolean exceptionOccured = false;
		
		try
		{
			new VariableCompletionCondition(null, cond);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testConstruction_nullGeneralCondition_assertException()
	{
		TxCompletionCondition cond = mock(TxCompletionCondition.class);
		
		boolean exceptionOccured = false;
		
		try
		{
			new VariableCompletionCondition(cond, null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
}
