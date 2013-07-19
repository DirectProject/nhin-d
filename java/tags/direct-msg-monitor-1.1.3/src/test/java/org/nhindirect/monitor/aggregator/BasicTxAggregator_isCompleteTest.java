package org.nhindirect.monitor.aggregator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxCompletionCondition;


public class BasicTxAggregator_isCompleteTest 
{
	@Test
	public void testIsComplete_nullCompletionCondtion()
	{
		BasicTxAggregator aggr = new BasicTxAggregator(null, null);
		
		boolean exceptionOccurred = false;
		try
		{	
			Exchange exchange = mock(Exchange.class);
			aggr.isAggregationComplete(exchange);
		}
		catch (IllegalStateException e)
		{
			exceptionOccurred = true;
		}
		assertTrue(exceptionOccurred);
	}
	
	@Test
	public void testIsComplete_emptyTxs_assertFalse()
	{
		TxCompletionCondition condition = mock(TxCompletionCondition.class);
		BasicTxAggregator aggr = new BasicTxAggregator(condition, null);
		

		Message msg = mock(Message.class);
		when(msg.getBody(Collection.class)).thenReturn(null);
		
		Exchange exchange = mock(Exchange.class);
		when(exchange.getIn()).thenReturn(msg);

		assertFalse(aggr.isAggregationComplete(exchange));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsComplete_txsExists_assertFalse()
	{
		TxCompletionCondition condition = mock(TxCompletionCondition.class);
		
		when(condition.isComplete((Collection<Tx>)any())).thenReturn(false);
		BasicTxAggregator aggr = new BasicTxAggregator(condition, null);
		
		Tx tx = mock(Tx.class);
		Collection<Tx> oldTxs = new ArrayList<Tx>();
		oldTxs.add(tx);
		Message msg = mock(Message.class);
		when(msg.getBody(Collection.class)).thenReturn(oldTxs);
		
		Exchange exchange = mock(Exchange.class);
		when(exchange.getIn()).thenReturn(msg);

		assertFalse(aggr.isAggregationComplete(exchange));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsComplete_txsExists_assertTrue()
	{
		TxCompletionCondition condition = mock(TxCompletionCondition.class);
		
		when(condition.isComplete((Collection<Tx>)any())).thenReturn(true);
		BasicTxAggregator aggr = new BasicTxAggregator(condition, null);
		
		Tx tx = mock(Tx.class);
		Collection<Tx> oldTxs = new ArrayList<Tx>();
		oldTxs.add(tx);
		Message msg = mock(Message.class);
		when(msg.getBody(Collection.class)).thenReturn(oldTxs);
		
		Exchange exchange = mock(Exchange.class);
		when(exchange.getIn()).thenReturn(msg);

		assertTrue(aggr.isAggregationComplete(exchange));
	}
}
