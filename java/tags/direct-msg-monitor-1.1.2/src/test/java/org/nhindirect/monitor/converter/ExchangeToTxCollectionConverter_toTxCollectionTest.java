package org.nhindirect.monitor.converter;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;

public class ExchangeToTxCollectionConverter_toTxCollectionTest 
{
	@Test
	public void testToTxCollection_convertExchangeToTxCollection()
	{
		Collection<Tx> txs = new ArrayList<Tx>();
		
		Tx tx = mock(Tx.class);
		txs.add(tx);
		
		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		exchange.getIn().setBody(txs);
		
		
		Collection<Tx> retrievedTxs = ExchangeToTxCollectionConverter.toTxCollection(exchange);
		assertEquals(txs, retrievedTxs);
	}
}
