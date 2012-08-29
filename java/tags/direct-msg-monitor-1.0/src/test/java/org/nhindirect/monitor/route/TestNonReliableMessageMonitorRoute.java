package org.nhindirect.monitor.route;

import java.util.List;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.util.TestUtils;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestNonReliableMessageMonitorRoute extends CamelSpringTestSupport 
{
	
	@Test
    public void testSingleRecipMDNReceived_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");
		template.sendBody("direct:start", originalMessage);

		// send MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com");
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testSingleRecipMDNReceived_multipleMessage_assertSingleConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send first message
		final String originalMessageId = UUID.randomUUID().toString();
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");
		template.sendBody("direct:start", originalMessage);

		// send second message
		Tx secondMessage = TestUtils.makeMessage(TxMessageType.IMF, UUID.randomUUID().toString(), "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");
		template.sendBody("direct:start", secondMessage);
		
		// send MDN to first message
		Tx mdnMessage =  TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com");
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testSingleRecipNoMDNReceived_assertConditionNotComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");


		// send original message
		final String originalMessageId = UUID.randomUUID().toString();

		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");
		template.sendBody("direct:start", originalMessage);

		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(0, exchanges.size());
    }
	
	@Test
    public void testSingleRecipFailedDSNReceived_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");	
		template.sendBody("direct:start", originalMessage);

		// send DSN to first message	
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com");
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }	
	
	
	@Test
    public void testMulitipleRecips_SingleMDNReceived_assertConditionNotComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();

		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com," +
				"ah4626@direct.securehealthemail.com", "");			
		template.sendBody("direct:start", originalMessage);

		
		// send MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", "gm2552@cerner.com", 
				 "gm2552@direct.securehealthemail.com");			
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(0, exchanges.size());
    }
	
	@Test
    public void testMulitipleRecips_allMDNsReceived_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();
	
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com," +
				"ah4626@direct.securehealthemail.com", "");				
		template.sendBody("direct:start", originalMessage);

		
		// send MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", "gm2552@cerner.com", 
				 "gm2552@direct.securehealthemail.com");	
		template.sendBody("direct:start", mdnMessage);
		
		// send MDN to original message with the second recipient
		mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@direct.securehealthemail.com", "gm2552@cerner.com", 
				"ah4626@direct.securehealthemail.com");	
		template.sendBody("direct:start", mdnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testMulitipleRecips_MDNReceivedandDSNReceived_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();
	
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com," +
				"ah4626@direct.securehealthemail.com", "");					
		template.sendBody("direct:start", originalMessage);

		
		// send MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com");			
		template.sendBody("direct:start", mdnMessage);
		
		// send DSN to original message with the second recipient
		Tx dsnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "ah4626@direct.securehealthemail.com", "gm2552@cerner.com", 
				"ah4626@direct.securehealthemail.com");	
		template.sendBody("direct:start", dsnMessage);
				
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testMulitipleRecips_singleDSNReceivedWithAllRecipeints_assertConditionComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com," +
				"ah4626@direct.securehealthemail.com", "");		
		template.sendBody("direct:start", originalMessage);
		
		// send DSN to original message with the second recipient
		Tx dsnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "ah4626@direct.securehealthemail.com", "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,ah4626@direct.securehealthemail.com");	
		template.sendBody("direct:start", dsnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(1, exchanges.size());
    }
	
	@Test
    public void testMulitipleRecips_singleDSNReceivedWithOneRecipeints_assertConditionNotComplete() throws Exception 
    {

		MockEndpoint mock = getMockEndpoint("mock:result");

		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		Tx originalMessage =  TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com," +
				"ah4626@direct.securehealthemail.com", "");	
		
		template.sendBody("direct:start", originalMessage);
		
		// send DSN to original message with the second recipient
		Tx dsnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "ah4626@direct.securehealthemail.com", "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com");	
		template.sendBody("direct:start", dsnMessage);
		
		List<Exchange> exchanges = mock.getReceivedExchanges();
		
		assertEquals(0, exchanges.size());
    }
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("routes/monitor-route-to-mock.xml");
    }
}
