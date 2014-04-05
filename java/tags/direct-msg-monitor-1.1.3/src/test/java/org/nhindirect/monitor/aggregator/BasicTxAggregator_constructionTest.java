package org.nhindirect.monitor.aggregator;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.nhindirect.monitor.condition.TxCompletionCondition;
import org.nhindirect.monitor.condition.TxTimeoutCondition;

import static org.mockito.Mockito.mock;

public class BasicTxAggregator_constructionTest 
{
	@Test
	public void costructAggregator()
	{
		TxTimeoutCondition timoutCondition = mock(TxTimeoutCondition.class);
		TxCompletionCondition condition = mock(TxCompletionCondition.class);
		
		BasicTxAggregator aggr = new BasicTxAggregator(condition, timoutCondition);

		assertNotNull(aggr);
		
		assertEquals(timoutCondition, aggr.timeoutCondition);
		assertEquals(condition, aggr.completionCondition);
	}
}
