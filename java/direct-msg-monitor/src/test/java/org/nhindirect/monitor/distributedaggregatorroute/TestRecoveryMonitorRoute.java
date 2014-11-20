package org.nhindirect.monitor.distributedaggregatorroute;

import java.util.List;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.aggregator.repository.ConcurrentJPAAggregationRepository;
import org.nhindirect.monitor.dao.AggregationDAO;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.nhindirect.monitor.util.TestUtils;

public class TestRecoveryMonitorRoute extends CamelSpringTestSupport 
{
	@Override
	public void postProcessTest() throws Exception
	{
		super.postProcessTest();
		
		final AggregationDAO dao = (AggregationDAO)context.getRegistry().lookup("shortRecoveryIntervalAggregationDAO");
		dao.purgeAll();
		
		assertEquals(0,dao.getAggregationKeys().size());
		assertEquals(0,dao.getAggregationCompletedKeys().size());
		
		// pre populate some recovery data
		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		final Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@direct.securehealthemail.com", "");
		final Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(originalMessage);
		
		final ConcurrentJPAAggregationRepository repo = (ConcurrentJPAAggregationRepository)context.getRegistry().lookup("monitoringRepo");
		
		repo.add(context, originalMessageId, exchange);
		
		repo.remove(context, originalMessageId, exchange);
		
		// lock the row to create a delay and ensure we recover 
		// exchange ids that return null at some point
		repo.recover(context, exchange.getExchangeId());
	}
	
	@Test
	public void testRecoverFromRepository() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");
		
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
		Tx originalMessage = (Tx)exchanges.iterator().next().getIn().getBody();
		assertEquals("gm2552@cerner.com", originalMessage.getDetail(TxDetailType.FROM).getDetailValue());
		
		// make sure everything got confirmed
		final AggregationDAO dao = (AggregationDAO)context.getRegistry().lookup("aggregationDAO");
		
		assertEquals(0,dao.getAggregationKeys().size());
		assertEquals(0,dao.getAggregationCompletedKeys().size());
	}
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("distributedAggregatorRoutes/recover-exchange-to-mock.xml");
    }
}
