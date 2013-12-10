package org.nhindirect.monitor.route;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.dao.NotificationDuplicationDAO;
import org.nhindirect.monitor.util.TestUtils;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestTimeoutToDupStateManager extends CamelSpringTestSupport 
{
	public void purgeNotifDAO(NotificationDuplicationDAO dao) throws Exception
	{
		final Calendar qualTime = Calendar.getInstance(Locale.getDefault());
		qualTime.add(Calendar.YEAR, 10);
		
		dao.purgeNotifications(qualTime);
	}
	
	@Test
	public void testTimeoutReliableMessage_conditionNotComplete_assertDupAdded() throws Exception
	{
		NotificationDuplicationDAO dao = context.getRegistry().lookup("notificationDuplicationDAO", NotificationDuplicationDAO.class); 
		assertNotNull(dao);
		purgeNotifDAO(dao);
		
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
		MimeMessage message = exchange.getIn().getBody(MimeMessage.class);
		assertNotNull(message);
		
		
		assertEquals("timeout", exchange.getProperty(Exchange.AGGREGATED_COMPLETED_BY));
		
		Set<String> addresses = dao.getReceivedAddresses(originalMessageId + "\t" + message.getMessageID(), Arrays.asList("gm2552@direct.securehealthemail.com"));
		assertEquals(1, addresses.size());
		assertTrue(addresses.contains("gm2552@direct.securehealthemail.com"));
		
		addresses = dao.getReceivedAddresses(originalMessageId, Arrays.asList("gm2552@direct.securehealthemail.com"));
		assertEquals(1, addresses.size());
		assertTrue(addresses.contains("gm2552@direct.securehealthemail.com"));		
	}	
	
	@Test
	public void testTimeoutReliableMessage_conditionNotComplete_msgNotReliable_assertDupNotAdded() throws Exception
	{
		NotificationDuplicationDAO dao = context.getRegistry().lookup("notificationDuplicationDAO", NotificationDuplicationDAO.class); 
		assertNotNull(dao);
		purgeNotifDAO(dao);
		
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
		
		// make sure there is only 1 message in the exchange
		MimeMessage messages = exchange.getIn().getBody(MimeMessage.class);
		assertNotNull(messages);
		
		assertEquals("timeout", exchange.getProperty(Exchange.AGGREGATED_COMPLETED_BY));
		
		Set<String> addresses = dao.getReceivedAddresses(originalMessageId, Arrays.asList("gm2552@direct.securehealthemail.com"));
		assertEquals(0, addresses.size());
	}	
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("routes/monitor-route-to-timeout-dupstate-manager.xml");
    }
}
