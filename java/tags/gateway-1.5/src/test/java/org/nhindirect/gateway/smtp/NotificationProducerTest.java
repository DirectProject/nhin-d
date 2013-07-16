package org.nhindirect.gateway.smtp;

import java.util.Arrays;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.MockNHINDAgent;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.mail.notifications.Notification;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;


import junit.framework.TestCase;

public class NotificationProducerTest extends TestCase 
{
	private IncomingMessage getMessageFromFile(String fileName, Collection<String> domains) throws Exception
	{
		String text =  TestUtils.readMessageResource(fileName);
		
		IncomingMessage retVal = new IncomingMessage(text);
		
		MockNHINDAgent mockAgent = new MockNHINDAgent(domains);
		
		retVal.setAgent(mockAgent);
		
		return retVal;
	}
	
	public void testConstructProducer()
	{
		NotificationProducer prod = new NotificationProducer(new NotificationSettings());
		
		assertNotNull(prod);
	}
	
	public void testConstructProducer_NullSettings_AssertException()
	{
		boolean exceptionOccured = false;
		
		try
		{
			new NotificationProducer(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	public void testCreateAckWithText() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(true, "", "Some Text");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		Notification note = prod.createNotification(new InternetAddress("me@domain1"));
		
		assertNotNull(note);
		assertEquals("Some Text", note.getExplanation());
		
	}		
	
	public void testCreateAckWithNoText() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(true, "", "");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		Notification note = prod.createNotification(new InternetAddress("me@domain1"));
		
		assertNotNull(note);
		assertTrue(note.getExplanation().startsWith("Your message was successfully"));
		
	}	
	
	public void testProduce_NullEnvelope_AssertException() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(true, "", "");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		boolean exceptionOccured = false;
		
		try
		{
			prod.produce(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);		
		
	}		
	
	public void testProduce_AuthResponseFalse_AssertEmptyList() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(false, "", "");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		IncomingMessage msg = getMessageFromFile("PlainIncomingMessage.txt", Arrays.asList("cerner.com"));
		
		Collection<NotificationMessage> notes = prod.produce(msg);

		assertNotNull(notes);
		assertEquals(0, notes.size());
		
	}
	
	public void testProduce_NoDomainRecipients_AssertEmptyList() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(true, "", "");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		IncomingMessage msg = getMessageFromFile("PlainIncomingMessage.txt", Arrays.asList("otherdomain.com"));
		
		Collection<NotificationMessage> notes = prod.produce(msg);

		assertNotNull(notes);
		assertEquals(0, notes.size());
		
	}	
	
	public void testProduce_MessageIsMDN_AssertEmptyList() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(true, "", "");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		IncomingMessage msg = getMessageFromFile("MDNMessage.txt", Arrays.asList("cerner.com"));
		
		Collection<NotificationMessage> notes = prod.produce(msg);

		assertNotNull(notes);
		assertEquals(0, notes.size());
		
	}	
	
	public void testProduceSingleMDN() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(true, "", "");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		IncomingMessage msg = getMessageFromFile("PlainIncomingMessage.txt", Arrays.asList("cerner.com"));
		
		Collection<NotificationMessage> notes = prod.produce(msg);

		assertNotNull(notes);
		assertEquals(1, notes.size());
		
		NotificationMessage noteMsg = notes.iterator().next();
		assertEquals(msg.getDomainRecipients().get(0).toString(), noteMsg.getFrom()[0].toString());		
	}	
	
	public void testProduceMDN_MultipleRecipients() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(true, "", "");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		IncomingMessage msg = getMessageFromFile("MultipleRecipientsIncomingMessage.txt", Arrays.asList("cerner.com", "securehealthemail.com"));
		
		Collection<NotificationMessage> notes = prod.produce(msg);

		assertNotNull(notes);
		assertEquals(2, notes.size());
		
		boolean foundCernerCom = false;
		boolean foundSecureHealth = false;
		
		for (NHINDAddress noteMsg : msg.getDomainRecipients())
		{	
			if (noteMsg.toString().contains("cerner.com"))
				foundCernerCom = true;
			else if (noteMsg.toString().contains("securehealthemail.com"))
				foundSecureHealth = true;
		}
		
		assertTrue(foundCernerCom);
		assertTrue(foundSecureHealth);
	}	
	
	public void testProduceMDN_MultipleRecipients_SingleDomain_AssertOneMDN() throws Exception
	{
		NotificationSettings setting = new NotificationSettings(true, "", "");
		
		NotificationProducer prod = new NotificationProducer(setting);
		
		IncomingMessage msg = getMessageFromFile("MultipleRecipientsIncomingMessage.txt", Arrays.asList("cerner.com"));
		
		Collection<NotificationMessage> notes = prod.produce(msg);

		assertNotNull(notes);
		assertEquals(1, notes.size());
		
		NotificationMessage noteMsg = notes.iterator().next();
		assertEquals(msg.getDomainRecipients().get(0).toString(), noteMsg.getFrom()[0].toString());	
	}		
}

