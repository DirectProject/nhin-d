package org.nhindirect.monitor.distributedaggregatorroute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.dao.AggregationDAO;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.nhindirect.monitor.util.TestUtils;

public class TestMultithreadedAddUpdateFailureMonitoringRoute extends CamelSpringTestSupport 
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMultithreadedMDNResponseHandling_assertAllMDNsHandled() throws Exception
	{
		final StringBuilder recipBuilder = new StringBuilder(); 
		final Collection<String> recips = new ArrayList<String>();
		
		// create a list of 100 recipients
		for (int i = 0; i < 100; ++i)
		{
			final String recip = "recip" + (i + 1) + "@test.com";
			
			recips.add(recip);
			recipBuilder.append(recip);
			if (i != 99)
				recipBuilder.append(",");
		}
		
		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", recipBuilder.toString(), "");
		template.sendBody("seda:start", originalMessage);
		
		// now send the recipient MDN messages
		for (String recip : recips)
		{
			// send MDN to original messages
			Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, recip, 
					"gm2552@cerner.com",recip);
			
			template.sendBody("seda:start", mdnMessage);
		}
		
		boolean exchangeFound = false;
		int cnt = 0;
		
		List<Exchange> exchanges = null;
		while (cnt < 10)
		{
			exchanges = mock.getReceivedExchanges();
			if (exchanges.size() == 1)
			{
				exchangeFound = true;
				break;
			}
			
			++cnt;
			Thread.sleep(2000);
		}
		
		assertTrue(exchangeFound);
		
		// validate the content of the exchange
		Collection<Tx> exBody = (Collection<Tx>)exchanges.iterator().next().getIn().getBody();
		assertEquals(101, exBody.size());
	}
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("distributedAggregatorRoutes/multithreaded-route-to-mock.xml");
    }
}
