package org.nhindirect.monitor.converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;

import org.apache.camel.Converter;
import org.nhindirect.common.tx.model.Tx;

/**
 * Camel type converter that translates a Collection of Tx objects to a serialized byte input stream for sending to a dead
 * letter queue
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@Converter
public class TxCollectionToInputStreamConverter 
{
	/**
	 * Converts a collection of Tx objects to a byte stream
	 * @param exchange The collection of tx objects
	 * @return A byte stream containing a string representation of the collection.
	 */
	@Converter 
	public static InputStream toBytes(Collection<Tx> txs)
	{
		
		if (txs == null || txs.isEmpty())
			return new ByteArrayInputStream(new byte[]{});
		
		final StringBuilder builder = new StringBuilder();
		
		for (Tx tx : txs)
			builder.append(tx.toString()).append("\r\n\r\n");
			
		return new ByteArrayInputStream(builder.toString().getBytes(Charset.defaultCharset()));
		
	}
}
///CLOVER:ON
