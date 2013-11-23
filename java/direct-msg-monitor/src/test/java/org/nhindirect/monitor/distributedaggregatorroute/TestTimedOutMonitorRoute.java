package org.nhindirect.monitor.distributedaggregatorroute;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.dao.AggregationDAO;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.nhindirect.monitor.util.TestUtils;

public class TestTimedOutMonitorRoute extends CamelSpringTestSupport 
{
	@Override
	public void postProcessTest() throws Exception
	{
		super.postProcessTest();
		
		final AggregationDAO dao = (AggregationDAO)context.getRegistry().lookup("aggregationDAO");
		dao.purgeAll();
		
		assertEquals(0,dao.getAggregationKeys().size());
		assertEquals(0,dao.getAggregationCompletedKeys().size());
	}
	
	@Test
	public void testTimeoutNonReliableMessage_conditionNotComplete_assertTimedOut() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");


		// send original message
		final String originalMessageId = UUID.randomUUID().toString();

		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");
		template.sendBody("direct:start", originalMessage);

		// no MDN sent... messages should timeout after 2 seconds
		// sleep 3 seconds to make sure it completes
		Thread.sleep(3000);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
		Exchange exchange = exchanges.iterator().next();
		assertEquals("timeout", exchange.getProperty(Exchange.AGGREGATED_COMPLETED_BY));
	}
	
	@Test
	public void testTimeoutReliableMessage_conditionNotComplete_assertTimedOut() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", "", "");
		template.sendBody("direct:start", originalMessage);

		// no MDN sent... messages should timeout after 2 seconds
		// sleep 3 seconds to make sure it completes
		Thread.sleep(3000);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
		Exchange exchange = exchanges.iterator().next();
		
		// make sure there is only 1 message in the exchange
		@SuppressWarnings("unchecked")
		Collection<Tx> messages = exchange.getIn().getBody(Collection.class);
		assertEquals(1, messages.size());
		
		assertEquals("timeout", exchange.getProperty(Exchange.AGGREGATED_COMPLETED_BY));
	}
	
	@Test
	public void testTimeoutReliableMessage_conditionNotComplete_assertTimedOutAndAggregatedTimeoutDecayed() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", "", "");
		template.sendBody("direct:start", originalMessage);

		// sleep .5 second then send the next part of the message
		Thread.sleep(500);
		
		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		// no MDN sent... messages should timeout after 1 second from now
		// sleep 2 seconds to make sure it completes
		Thread.sleep(2000);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
		Exchange exchange = exchanges.iterator().next();
		
		// make sure there are 2 messages in the exchange
		@SuppressWarnings("unchecked")
		Collection<Tx> messages = exchange.getIn().getBody(Collection.class);
		assertEquals(2, messages.size());
		
		assertEquals("timeout", exchange.getProperty(Exchange.AGGREGATED_COMPLETED_BY));
		// make sure the aggregated timeout decayed properly... it should now be <= 500 ms
		assertTrue((Long)exchange.getProperty(Exchange.AGGREGATED_TIMEOUT) <= 500);
	}
	
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("distributedAggregatorRoutes/monitor-route-to-mock-with-short-timeout.xml");
    }
}
