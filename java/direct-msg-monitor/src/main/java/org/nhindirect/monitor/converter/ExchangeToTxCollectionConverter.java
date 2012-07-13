package org.nhindirect.monitor.converter;

import java.util.Collection;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.nhindirect.common.tx.model.Tx;

@Converter
public class ExchangeToTxCollectionConverter 
{
	@Converter 
	public static Collection<Tx> toTxCollection(Exchange exchange)
	{
        @SuppressWarnings("unchecked")
		final Collection<Tx> txs = exchange.getIn().getBody(Collection.class);
        
        return txs;
	}
}
