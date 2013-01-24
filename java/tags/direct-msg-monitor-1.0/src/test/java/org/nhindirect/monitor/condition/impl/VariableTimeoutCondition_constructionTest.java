package org.nhindirect.monitor.condition.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.nhindirect.monitor.condition.TxTimeoutCondition;

public class VariableTimeoutCondition_constructionTest 
{
	@Test
	public void testConstruction()
	{
		TxTimeoutCondition cond1 = mock(TxTimeoutCondition.class);
		TxTimeoutCondition cond2 = mock(TxTimeoutCondition.class);
		
		VariableTimeoutCondition cond = new VariableTimeoutCondition(cond1, cond2);
		
		assertEquals(cond1, cond.timelyExpression);
		assertEquals(cond2, cond.generalExpression);
	}
	
	@Test
	public void testConstruction_nullTimelyCondition_assertException()
	{
		TxTimeoutCondition cond = mock(TxTimeoutCondition.class);
		
		boolean exceptionOccured = false;
		
		try
		{
			new VariableTimeoutCondition(null, cond);
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
		TxTimeoutCondition cond = mock(TxTimeoutCondition.class);
		
		boolean exceptionOccured = false;
		
		try
		{
			new VariableTimeoutCondition(cond, null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
}
