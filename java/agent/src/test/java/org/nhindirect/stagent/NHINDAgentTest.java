package org.nhindirect.stagent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;

import javax.mail.Header;
import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeStandard;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class NHINDAgentTest extends TestCase 
{	
	static
	{
		// Override the logging system to turn on trace level logging
		// Need to make sure debug logging is tested to check for possible null reference errors
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "trace");
		
		for (Entry<Object, Object> entry : System.getProperties().entrySet())
		{
			System.out.println("Name: " + entry.getKey() + " Value: " + entry.getValue());
		}
	}
	
	public void testEndToEndMessageWithCertKeyStore() throws Exception
	{				
		DefaultNHINDAgent agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"cerner.com"}));
		
		String testMessage = TestUtils.readResource("MultipartMimeMessage.txt");
		MimeMessage originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));

		
		OutgoingMessage SMIMEenvMessage = agent.processOutgoing(testMessage);
		
		assertNotNull(SMIMEenvMessage);
		assertTrue(SMIMEenvMessage.getMessage().toString().length() > 0);

		
		// verify the message
		// need a new agent because this is a different domain
		agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"starugh-stateline.com"})); 
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(SMIMEenvMessage.getMessage().toString());
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		MimeMessage processedMsg = new MimeMessage(null, 
				new ByteArrayInputStream(strippedAndVerifiesMessage.getMessage().toString().getBytes("ASCII")));
		
		assertNotNull(processedMsg);
		
		// can't do a direct compare on headers because the processing may strip some of the recipients
		assertTrue(processedMsg.getContentType().compareTo(originalMsg.getContentType()) == 0);
		assertTrue(originalMsg.getSubject().compareTo(processedMsg.getSubject()) == 0);
				
		// get the message data and compare
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count = 0;
		InputStream inStream = originalMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		String originalPart = new String(oStream.toByteArray(), "ASCII");
		
		oStream = new ByteArrayOutputStream();
		count = 0;
		inStream = processedMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		String processedPart = new String(oStream.toByteArray(), "ASCII");
		
		
		//assertTrue(processedPart.compareTo(originalPart) == 0);
		
		
		// now do a large message with some attachments
		agent =  TestUtils.getStockAgent(Arrays.asList(new String[]{"securehealthemail.com"})); 
		
		testMessage = TestUtils.readResource("LargeMsgWithAttachments.txt");
		originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));

		
		SMIMEenvMessage = agent.processOutgoing(testMessage);
		
		//FileOutputStream ouStream = new FileOutputStream(new File("large.eml"));
		//ouStream.write(SMIMEenvMessage.getBytes());
		
		assertNotNull(SMIMEenvMessage);
		assertTrue(SMIMEenvMessage.getMessage().toString().length() > 0);

		
		// verify the message
		agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"securehealthemail.com"})); 
		strippedAndVerifiesMessage = agent.processIncoming(SMIMEenvMessage);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		processedMsg = new MimeMessage(null, new ByteArrayInputStream(strippedAndVerifiesMessage.
				getMessage().toString().getBytes("ASCII")));
		
		assertNotNull(processedMsg);
		
		// can't do a direct compare on headers because the processing may strip some of the recipients
		assertTrue(processedMsg.getContentType().compareTo(originalMsg.getContentType()) == 0);
		assertTrue(originalMsg.getSubject().compareTo(processedMsg.getSubject()) == 0);
				
		// get the message data and compare
		oStream = new ByteArrayOutputStream();
		buffer = new byte[1024];
		 count = 0;
		inStream = originalMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		originalPart = new String(oStream.toByteArray(), "ASCII");
		
		oStream = new ByteArrayOutputStream();
		count = 0;
		inStream = processedMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		processedPart = new String(oStream.toByteArray(), "ASCII");
		
		
		//assertTrue(processedPart.compareTo(originalPart) == 0);		
	}
	
	
	public void testDecryptProvidedMessage() throws Exception
	{
		/*
		 * EncryptedMessage2
		 */
		
		
		DefaultNHINDAgent agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"securehealthemail.com"})); 
		String testMessage = TestUtils.readResource("EncryptedMessage2.txt");
		MimeMessage originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		
		// verify the message
		// need a new agent because this is a different domain);
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		/*
		 * EncryptedMessage3
		 */				
		
		testMessage = TestUtils.readResource("EncryptedMessage3.txt");
		originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		
		// verify the message
		// need a new agent because this is a different domain);
		strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
	}
	
	
	public void testDecryptAttachmentMessage() throws Exception
	{
		
	
		// get the keystore file		
		DefaultNHINDAgent agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"securehealthemail.com"})); 
		
		String testMessage = TestUtils.readResource("EncAttachment.txt");
		MimeMessage originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
			
		
		// verify the message
		// need a new agent because this is a different domain);
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);	
				
		/*
		 * EncAttachment2.txt
		 */		
		
		agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"securehealthemail.com"})); 
		
		testMessage = TestUtils.readResource("EncAttachment2.txt");
		originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		
		// verify the message
		strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);	

		/*
		 * LargeEncAttachment.txt
		 */		
		agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"securehealthemail.com"})); 
		
		testMessage = TestUtils.readResource("LargeEncAttachment.txt");
		originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		

		// verify the message
		strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);	
	}	
	
	
	public void testEndToEndMessageBase64AttachmentOnly() throws Exception
	{

		DefaultNHINDAgent agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"messaging.cernerdemos.com"})); 
		String testMessage = TestUtils.readResource("raw2.txt");
		MimeMessage originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));		
		
		OutgoingMessage SMIMEenvMessage = agent.processOutgoing(testMessage);
		
		assertNotNull(SMIMEenvMessage);
		assertTrue(SMIMEenvMessage.getMessage().toString().length() > 0);

		
		// verify the message
		// need a new agent because this is a different domain
		agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"securehealthemail.com"}));
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(SMIMEenvMessage);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		MimeMessage processedMsg = new MimeMessage(null, 
				new ByteArrayInputStream(strippedAndVerifiesMessage.getMessage().toString().getBytes("ASCII")));
		
		assertNotNull(processedMsg);
		
		// can't do a direct compare on headers because the processing may strip some of the recipients
		assertTrue(processedMsg.getContentType().compareTo(originalMsg.getContentType()) == 0);
		assertTrue(originalMsg.getSubject().compareTo(processedMsg.getSubject()) == 0);
				
		// get the message data and compare
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count = 0;
		InputStream inStream = originalMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		String originalPart = new String(oStream.toByteArray(), "ASCII");
		
		oStream = new ByteArrayOutputStream();
		count = 0;
		inStream = processedMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		String processedPart = new String(oStream.toByteArray(), "ASCII");
		
	}
	
	@SuppressWarnings("unchecked")
	public void testOutgoingMessageIsWrappedCorrectly() throws Exception
	{

		DefaultNHINDAgent agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"messaging.cernerdemos.com"})); 
		String testMessage = TestUtils.readResource("raw2.txt");
		
		Message wrappedMessage = agent.wrapMessage(testMessage);
		
		assertNotNull(wrappedMessage);
		assertTrue(wrappedMessage.toString().length() > 0);
		assertEquals(MailStandard.MediaType.WrappedMessage, wrappedMessage.getContentType());
		List<String> headers = new ArrayList<String>();
		Enumeration<Header> allHeaders = wrappedMessage.getAllHeaders();
		while(allHeaders.hasMoreElements()) {
			Header header = (Header) allHeaders.nextElement();
			headers.add(header.getName());
		}
		
		// assert that the headers (according to the NHIND spec) are copied correctly from the 
		// original message into the wrapped message
		assertTrue(headers.contains(MimeStandard.VersionHeader));
		assertTrue(headers.contains("From"));
		assertTrue(headers.contains("To"));
		assertTrue(headers.contains("Message-ID"));
		
		// The wrapped message should not contain Subject header
		assertFalse(headers.contains("Subject"));
	}
	
	public void testMessageWithUntrustedRecipient_OutboundMessageHasRejectedRecips() throws Exception
	{

		DefaultNHINDAgent agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"messaging.cernerdemos.com"})); 
		String testMessage = TestUtils.readResource("MessageWithAUntrustedRecipient.txt");
		
		OutgoingMessage SMIMEenvMessage = agent.processOutgoing(testMessage);
		
		assertNotNull(SMIMEenvMessage);
		assertTrue(SMIMEenvMessage.getMessage().toString().length() > 0);
		assertTrue(SMIMEenvMessage.hasRejectedRecipients());
 
		assertNull(SMIMEenvMessage.getMessage().getCCHeader());
		
		// verify the message
		// need a new agent because this is a different domain
		agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"securehealthemail.com"}));
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(SMIMEenvMessage);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		MimeMessage processedMsg = new MimeMessage(null, 
				new ByteArrayInputStream(strippedAndVerifiesMessage.getMessage().toString().getBytes("ASCII")));
		
		assertNotNull(processedMsg);
	}
	
	public void testMessageWithAllUntrustedRecipients_AgentRejectsTheMessageCompletely() throws Exception
	{

		DefaultNHINDAgent agent = TestUtils.getStockAgent(Arrays.asList(new String[]{"messaging.cernerdemos.com"})); 
		String testMessage = TestUtils.readResource("MessageWithAllUntrustedRecipients.txt");
		
		OutgoingMessage SMIMEenvMessage=null;
		try {
			SMIMEenvMessage = agent.processOutgoing(testMessage);
		}
		catch(NHINDException e) {
			AgentException agentexception = (AgentException) e.m_error;
			assertEquals(AgentError.NoTrustedRecipients, agentexception.getError());
		}
		
		assertNull(SMIMEenvMessage);
	}
}

