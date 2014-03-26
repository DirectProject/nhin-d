package org.nhindirect.monitor.route;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.mail.dsn.DSNStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.util.TestUtils;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestNonCompletedToDSNGeneratorMonitorRoute extends CamelSpringTestSupport 
{
	@Test
	public void testNonCompleted_assertDSNGenerated() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");


		// send original message
		final String originalMessageId = UUID.randomUUID().toString();

		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,ah4626@direct.securehealthemail.com", "");
		template.sendBody("direct:start", originalMessage);

		// no MDN sent... messages should timeout after 2 seconds
		// sleep 3 seconds to make sure it completes
		Thread.sleep(3000);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
	}
	
	@Test
	public void testNonCompleted_multipleRecipeints_singleCompletedSuccessfully_assertDSNGeneratedAndValidTimedout() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");


		// send original message
		final String originalMessageId = UUID.randomUUID().toString();

		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,ah4626@direct.securehealthemail.com", "");
		template.sendBody("direct:start", originalMessage);

		// now send an MDN success for only recipient
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		
		// single MDN sent... messages should timeout after 2 seconds
		// sleep 3 seconds to make sure it completes
		Thread.sleep(3000);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
		
		MimeMessage dsnMessage = (MimeMessage)exchanges.get(0).getIn().getBody();
	
		ByteArrayOutputStream oStr = new ByteArrayOutputStream();
		dsnMessage.writeTo(oStr);
		String str = new String(oStr.toByteArray());
		
		assertTrue(str.contains("ah4626@direct.securehealthemail.com"));
		assertFalse(str.contains("gm2552@direct.securehealthemail.com"));
	}
	
	@Test
	public void testNonCompleted_multipleRecipeints_singleDSNAndOneIncomplete_assertDSNGeneratedAndValidTimedout() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");


		// send original message
		final String originalMessageId = UUID.randomUUID().toString();

		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,ah4626@direct.securehealthemail.com", "");
		template.sendBody("direct:start", originalMessage);

		// now send a DSN failure for only recipient
		Tx dsntxMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", DSNStandard.DSNAction.FAILED.toString(), "");
		template.sendBody("direct:start", dsntxMessage);

		
		// single MDN sent... messages should timeout after 2 seconds
		// sleep 3 seconds to make sure it completes
		Thread.sleep(3000);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
		
		MimeMessage dsnMessage = (MimeMessage)exchanges.get(0).getIn().getBody();
	
		ByteArrayOutputStream oStr = new ByteArrayOutputStream();
		dsnMessage.writeTo(oStr);
		String str = new String(oStr.toByteArray());
		
		assertTrue(str.contains("ah4626@direct.securehealthemail.com"));
		assertFalse(str.contains("gm2552@direct.securehealthemail.com"));
	}
	
	@Test
	public void testNonCompleted_multipleRecipeints_singleDSNSingleProcessAndOneIncomplete_assertDSNGeneratedAndValidTimedout() throws Exception
	{
		MockEndpoint mock = getMockEndpoint("mock:result");


		// send original message
		final String originalMessageId = UUID.randomUUID().toString();

		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,ah4626@direct.securehealthemail.com,gm2552@test.com", "");
		template.sendBody("direct:start", originalMessage);

		// now send a DSN failure for only recipient
		Tx dsntxMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", DSNStandard.DSNAction.FAILED.toString(), "");
		template.sendBody("direct:start", dsntxMessage);

		// now send an MDN success for only recipient
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@direct.securehealthemail.com", 
				"gm2552@cerner.com", "ah4626@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);
		
		// single MDN sent... messages should timeout after 2 seconds
		// sleep 3 seconds to make sure it completes
		Thread.sleep(3000);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
		
		MimeMessage dsnMessage = (MimeMessage)exchanges.get(0).getIn().getBody();
	
		ByteArrayOutputStream oStr = new ByteArrayOutputStream();
		dsnMessage.writeTo(oStr);
		String str = new String(oStr.toByteArray());
		
		assertFalse(str.contains("ah4626@direct.securehealthemail.com"));
		assertFalse(str.contains("gm2552@direct.securehealthemail.com"));
		assertTrue(str.contains("gm2552@test.com"));	
	}
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("routes/monitor-route-to-error-message-generator.xml");
    }
}
