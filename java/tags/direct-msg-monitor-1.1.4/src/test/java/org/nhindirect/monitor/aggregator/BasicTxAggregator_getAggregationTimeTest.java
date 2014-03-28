package org.nhindirect.monitor.aggregator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxTimeoutCondition;

public class BasicTxAggregator_getAggregationTimeTest 
{
	@Test
	public void testGetAggregationTime_nullTimeoutCondtion()
	{
		BasicTxAggregator aggr = new BasicTxAggregator(null, null);
		
		boolean exceptionOccurred = false;
		try
		{	
			Exchange exchange = mock(Exchange.class);
			aggr.getAggregationTime(exchange);
		}
		catch (IllegalStateException e)
		{
			exceptionOccurred = true;
		}
		assertTrue(exceptionOccurred);
	}
	
	@Test
	public void testGetAggregationTime_emptyTxs_assertNull()
	{
		TxTimeoutCondition condition = mock(TxTimeoutCondition.class);
		BasicTxAggregator aggr = new BasicTxAggregator(null, condition);
		
		Message msg = mock(Message.class);
		when(msg.getBody(Collection.class)).thenReturn(null);
		
		Exchange exchange = mock(Exchange.class);
		when(exchange.getIn()).thenReturn(msg);
		
		assertNull(aggr.getAggregationTime(exchange));
		
		verify(exchange, never()).getProperty(Exchange.CREATED_TIMESTAMP, Date.class);
	}
	
	@Test
	public void testIsComplete_txsExists_emptyInitialTimeProperty_assertNull()
	{
		TxTimeoutCondition condition = mock(TxTimeoutCondition.class);
		
		BasicTxAggregator aggr = new BasicTxAggregator(null, condition);
		

		Tx tx = mock(Tx.class);
		Collection<Tx> oldTxs = new ArrayList<Tx>();
		oldTxs.add(tx);
		Message msg = mock(Message.class);
		when(msg.getBody(Collection.class)).thenReturn(oldTxs);
		
		Exchange exchange = mock(Exchange.class);
		when(exchange.getIn()).thenReturn(msg);
		when(exchange.getProperty(Exchange.CREATED_TIMESTAMP, Date.class)).thenReturn(null);
		
		assertNull(aggr.getAggregationTime(exchange));
		
		verify(exchange, times(1)).getProperty(Exchange.CREATED_TIMESTAMP, Date.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsComplete_txsExists_propertyExists_assertTimeoutValue()
	{
		final Date theDate = new Date();
		
		TxTimeoutCondition condition = mock(TxTimeoutCondition.class);
		when(condition.getTimeout((Collection<Tx>)any(), eq(theDate.getTime()))).thenReturn(Long.valueOf(1000));
		BasicTxAggregator aggr = new BasicTxAggregator(null, condition);
		

		Tx tx = mock(Tx.class);
		Collection<Tx> oldTxs = new ArrayList<Tx>();
		oldTxs.add(tx);
		Message msg = mock(Message.class);
		when(msg.getBody(Collection.class)).thenReturn(oldTxs);
		
		Exchange exchange = mock(Exchange.class);
		when(exchange.getIn()).thenReturn(msg);
		when(exchange.getProperty(Exchange.CREATED_TIMESTAMP, Date.class)).thenReturn(theDate);
		
		assertEquals(Long.valueOf(1000), aggr.getAggregationTime(exchange));
		
		verify(exchange, times(1)).getProperty(Exchange.CREATED_TIMESTAMP, Date.class);
	}
}
