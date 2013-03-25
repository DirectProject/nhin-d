package org.nhindirect.monitor.route;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nhindirect.common.tx.impl.DefaultTxDetailParser;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.util.TestUtils;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestNonCompletedToMailServerMonitorRoute extends CamelSpringTestSupport 
{
	@Test
	public void testDummy()
	{
		
	}
	
	/*
	@Test
	public void testNonCompleted_assertDSNGenerated() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");

		String message = TestUtils.readMessageFromFile("MessageWithAttachment.txt");
		
		DefaultTxDetailParser parser = new DefaultTxDetailParser();
		InputStream str = IOUtils.toInputStream(message);
		
		try
		{
			// send original message
			final MimeMessage msg = new MimeMessage(null, str);
			
			// change the message id
			msg.saveChanges();
			
			Map<String, TxDetail> details = parser.getMessageDetails(msg);
			
			Tx originalMessage = new Tx(TxMessageType.IMF, details);
			template.sendBody("direct:start", originalMessage);
	
			// no MDN sent... messages should timeout after 2 seconds
			// sleep 3 seconds to make sure it completes
			Thread.sleep(3000);
			
			List<Exchange> exchanges = mock.getReceivedExchanges();
			
			assertEquals(1, exchanges.size());
		}
		finally
		{
			
		}
	}
	*/
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("routes/monitor-route-to-mail-server.xml");
    }
}
