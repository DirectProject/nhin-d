package org.nhindirect.monitor.route;

import java.util.List;
import java.util.UUID;

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

public class TestMultiRecipNonReliableMessageMonitorRoute extends CamelSpringTestSupport 
{
	
	@Test
    public void testMultiRecip_MDNProcessedReceived_assertConditionNotComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(0, exchanges.size());
    }	
	
	@Test
    public void testMultiRecip_MDNProcessedAndDispatchedReceivedForOneRecip_assertConditionNotComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		// send MDN processed to original message
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Dispatched);
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(0, exchanges.size());
    }
	
	@Test
    public void testMultiRecip_MDNProcessedAndDispatchedReceivedForOneRecip_ProcessedOnlyForOther_assertConditionNotComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		// send MDN processed to original message
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Dispatched);
		template.sendBody("direct:start", mdnMessage);
		
		// send MDN processed to original message from other recip
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(0, exchanges.size());
    }
	
	@Test
    public void testMultiRecip_MDNProcessedAndDispatchedReceivedForAllRecips_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		// send MDN processed to original message
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Dispatched);
		template.sendBody("direct:start", mdnMessage);
		
		// send MDN processed to original message from other recip
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);
		
		// send MDN dispatched to original message from other recip
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", "", MDNStandard.Disposition_Dispatched);
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testMultiRecip_MDNProcessedAndDispatchedReceivedForOneRecip_MDNErrorOnlyForOther_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		// send MDN processed to original message
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Dispatched);
		template.sendBody("direct:start", mdnMessage);
		
		// send MDN error to original message from other recip
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", "", MDNStandard.Disposition_Error);
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testMultiRecip_MDNProcessedAndDispatchedReceivedForOneRecip_MDNDeniedOnlyForOther_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		// send MDN processed to original message
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Dispatched);
		template.sendBody("direct:start", mdnMessage);
		
		// send MDN denied to original message from other recip
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", "", MDNStandard.Disposition_Denied);
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testMultiRecip_MDNProcessedAndDispatchedReceivedForOneRecip_DNSFailedForOther_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		// send MDN processed to original message
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Dispatched);
		template.sendBody("direct:start", mdnMessage);
		
		// send DSN failes to original message from other recip
		Tx dsnMessage = TestUtils.makeReliableMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", DSNStandard.DSNAction.FAILED.toString(), "");
		template.sendBody("direct:start", dsnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	
	@Test
    public void testMultiRecip_MDNProcessedForOneRecip_DNSFailedForOther_assertConditionNotComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN processed to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Processed);
		template.sendBody("direct:start", mdnMessage);

		
		// send DSN Failed to original message from other recip
		Tx dsnMessage = TestUtils.makeReliableMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", DSNStandard.DSNAction.FAILED.toString(), "");
		template.sendBody("direct:start", dsnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(0, exchanges.size());
    }
	
	@Test
    public void testMultiRecip_MDNErrorForAllRecips_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN error to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Error);
		template.sendBody("direct:start", mdnMessage);

		
		// send MDN error to original message from other recip
		mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", "", MDNStandard.Disposition_Error);
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testMultiRecip_DSNFailedForOneRecip_assertConditionNotComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		
		// send DSN Failed to original message from other recip
		Tx dsnMessage = TestUtils.makeReliableMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "ah4626@test.com", 
				"gm2552@cerner.com", "ah4626@test.com", DSNStandard.DSNAction.FAILED.toString(), "");
		template.sendBody("direct:start", dsnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(0, exchanges.size());
    }
	
	@Test
    public void testMultiRecip_DSNFailedForAllRecips_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeReliableMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", 
				"", "", "");
		template.sendBody("direct:start", originalMessage);

		
		// send DSN Failed to original message from all recip
		Tx dsnMessage = TestUtils.makeReliableMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com,ah4626@test.com", DSNStandard.DSNAction.FAILED.toString(), "");
		template.sendBody("direct:start", dsnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("routes/monitor-route-to-mock.xml");
    }
}
