package org.nhindirect.stagent.mail;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.NHINDStandard;
import org.nhindirect.stagent.OutgoingMessage;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class WrappedMessageTest extends TestCase 
{
	public void testCopyNHINDStandard_assertPromotedCCHeaders() throws Exception
	{
		Message msg = new Message();
		
		String msgId = UUID.randomUUID().toString();
		
		msg.addHeader(MailStandard.Headers.MessageID, msgId);
		
		msg.addHeader(MailStandard.Headers.CC, "test@testdomain.com, gm2552@cerner.com");
		msg.addHeader(MimeStandard.VersionHeader, "1.0");
		msg.setContent("SomeText", "text/plain");
		
		
		
		Message wrappedMessage = WrappedMessage.create(msg, NHINDStandard.MailHeadersUsed);
		
		String ccHeader = wrappedMessage.getHeader(MailStandard.Headers.CC, ",");
		
		assertNotNull(ccHeader);
		assertEquals("test@testdomain.com, gm2552@cerner.com", ccHeader);		
	}
	
	public void testCopyNHINDStandard_fromFullMessage_assertPromotedCCHeaders() throws Exception
	{
		String testMessage = TestUtils.readResource("CCTestMessage.txt");
		Message msg = new Message(new ByteArrayInputStream(testMessage.getBytes("ASCII")));		
		
		Message wrappedMessage = WrappedMessage.create(msg, NHINDStandard.MailHeadersUsed);
		
		String ccHeader = wrappedMessage.getHeader(MailStandard.Headers.CC, ",");
		
		assertNotNull(ccHeader);
		assertEquals("User1@Cerner.com, gm2552@cerner.com", ccHeader);		
	}	
	
	public void testCopyNHINDStandard_encyrptMessage_assertPromotedInvalidCCHeaders() throws Exception
	{
		DefaultNHINDAgent agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"cerner.com"}));		
		
		String testMessage = TestUtils.readResource("CCTestMessage.txt");
		
		OutgoingMessage SMIMEenvMessage = agent.processOutgoing(testMessage);
		
		String ccHeader = SMIMEenvMessage.getMessage().getHeader(MailStandard.Headers.CC, ",");
		assertNotNull(ccHeader);	
		
		assertEquals("User1@Cerner.com", ccHeader);			
	}		
}
