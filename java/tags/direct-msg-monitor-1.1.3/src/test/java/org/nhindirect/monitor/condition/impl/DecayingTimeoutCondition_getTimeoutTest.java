package org.nhindirect.monitor.condition.impl;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;

public class DecayingTimeoutCondition_getTimeoutTest 
{
	@Test
	public void testGetTimeout()
	{
		final long exchangeStartTime = System.currentTimeMillis();
		
		final DecayingTimeoutCondition cond = new DecayingTimeoutCondition(10000)
		{
			@Override
			protected long getCurrentTime()
			{
				return exchangeStartTime + 1000;
			}
		};
		
		assertEquals(9000, cond.getTimeout(new ArrayList<Tx>(), exchangeStartTime));
	}
	
	@Test
	public void testGetTimeout_zeroTimeRemaining_assert1ms()
	{
		final long exchangeStartTime = System.currentTimeMillis();
		
		final DecayingTimeoutCondition cond = new DecayingTimeoutCondition(10000)
		{
			@Override
			protected long getCurrentTime()
			{
				return exchangeStartTime + 10000;
			}
		};
		
		assertEquals(1, cond.getTimeout(new ArrayList<Tx>(), exchangeStartTime));
	}
	
	@Test
	public void testGetTimeout_negativeTimeRemaining_assert1ms()
	{
		final long exchangeStartTime = System.currentTimeMillis();
		
		final DecayingTimeoutCondition cond = new DecayingTimeoutCondition(10000)
		{
			@Override
			protected long getCurrentTime()
			{
				return exchangeStartTime + 10001;
			}
		};
		
		assertEquals(1, cond.getTimeout(new ArrayList<Tx>(), exchangeStartTime));
	}
}
