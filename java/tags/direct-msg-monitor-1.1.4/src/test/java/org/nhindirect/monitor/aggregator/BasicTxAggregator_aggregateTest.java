package org.nhindirect.monitor.aggregator;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxCompletionCondition;
import org.nhindirect.monitor.condition.TxTimeoutCondition;


public class BasicTxAggregator_aggregateTest 
{
	@Test
	public void testAggregate_emptyExchanged_addTx()
	{
		TxTimeoutCondition timoutCondition = mock(TxTimeoutCondition.class);
		TxCompletionCondition condition = mock(TxCompletionCondition.class);
		BasicTxAggregator aggr = new BasicTxAggregator(condition, timoutCondition);
		
		CamelContext context = mock(CamelContext.class);
		DefaultExchange newExchange = new DefaultExchange(context);
		
		Tx tx = mock(Tx.class);
		newExchange.getIn().setBody(tx);
		
		Exchange ex = aggr.aggregate(null, newExchange);
		
		@SuppressWarnings("unchecked")
		Collection<Tx> txs = ex.getIn().getBody(Collection.class);
		assertEquals(1, txs.size());
		assertEquals(tx, txs.iterator().next());
	}
	
	@Test
	public void testAggregate_singleEntryExchanged_addSingleTx()
	{
		TxTimeoutCondition timoutCondition = mock(TxTimeoutCondition.class);
		TxCompletionCondition condition = mock(TxCompletionCondition.class);
		BasicTxAggregator aggr = new BasicTxAggregator(condition, timoutCondition);
		
		CamelContext context = mock(CamelContext.class);
		DefaultExchange oldExchange = new DefaultExchange(context);
		Tx tx = mock(Tx.class);
		Collection<Tx> oldTxs = new ArrayList<Tx>();
		oldTxs.add(tx);
		oldExchange.getIn().setBody(oldTxs);
		
		DefaultExchange newExchange = new DefaultExchange(context);
		
		tx = mock(Tx.class);
		newExchange.getIn().setBody(tx);
		
		Exchange ex = aggr.aggregate(oldExchange, newExchange);
		
		@SuppressWarnings("unchecked")
		Collection<Tx> txs = ex.getIn().getBody(Collection.class);
		assertEquals(2, txs.size());
	}
}
