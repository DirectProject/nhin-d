package org.nhindirect.gateway.smtp.james.matcher;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.MailAddress;
import org.nhindirect.gateway.smtp.james.mailet.MockMail;
import org.nhindirect.gateway.testutils.TestUtils;

public class IsNotificationTest extends TestCase
{
	@SuppressWarnings("unchecked")
	public void testIsNotification_MDNMessage_assertAllRecips() throws Exception
	{
		MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("MDNMessage.txt")));
		
		
		IsNotification matcher = new IsNotification();
		
		final Collection<MailAddress> initialRecips = new ArrayList<MailAddress>();
		for (InternetAddress addr : (InternetAddress[])msg.getAllRecipients())
			initialRecips.add(new MailAddress(addr.getAddress()));
		
		final MockMail mockMail = new MockMail(msg);
		mockMail.setRecipients(initialRecips);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(1, matchAddresses.size());
		assertEquals(initialRecips.iterator().next().toString(), matchAddresses.iterator().next().toString());
	}
	
	@SuppressWarnings("unchecked")
	public void testIsNotification_DSNMessage_assertAllRecips() throws Exception
	{
		MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("DSNMessage.txt")));
		
		
		IsNotification matcher = new IsNotification();
		
		final Collection<MailAddress> initialRecips = new ArrayList<MailAddress>();
		for (InternetAddress addr : (InternetAddress[])msg.getAllRecipients())
			initialRecips.add(new MailAddress(addr.getAddress()));
		
		final MockMail mockMail = new MockMail(msg);
		mockMail.setRecipients(initialRecips);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(1, matchAddresses.size());
		assertEquals(initialRecips.iterator().next().toString(), matchAddresses.iterator().next().toString());
	}
	
	@SuppressWarnings("unchecked")
	public void testIsNotification_ecryptedMessage_assertNull() throws Exception
	{
		MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("EncryptedMessage.txt")));
		
		
		IsNotSMIMEEncrypted matcher = new IsNotSMIMEEncrypted();
		
		final Collection<MailAddress> initialRecips = new ArrayList<MailAddress>();
		for (InternetAddress addr : (InternetAddress[])msg.getAllRecipients())
			initialRecips.add(new MailAddress(addr.getAddress()));
		
		final MockMail mockMail = new MockMail(msg);
		mockMail.setRecipients(initialRecips);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(null, matchAddresses);
	}

	@SuppressWarnings("unchecked")
	public void testIsNotification_plainMessage_assertNull() throws Exception
	{
		MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("PlainOutgoingMessage.txt")));
		
		
		IsNotification matcher = new IsNotification();
		
		final Collection<MailAddress> initialRecips = new ArrayList<MailAddress>();
		for (InternetAddress addr : (InternetAddress[])msg.getAllRecipients())
			initialRecips.add(new MailAddress(addr.getAddress()));
		
		final MockMail mockMail = new MockMail(msg);
		mockMail.setRecipients(initialRecips);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(null, matchAddresses);
	}
	
	@SuppressWarnings("unchecked")
	public void testIsNoticiation_nullMail_assertNull() throws Exception
	{
		
		IsNotification matcher = new IsNotification();
		
		
		Collection<MailAddress> matchAddresses = matcher.match(null);
		
		assertEquals(null, matchAddresses);
	}
	
	@SuppressWarnings("unchecked")
	public void testIsNotification_nullMessage_assertNull() throws Exception
	{
		
		IsNotification matcher = new IsNotification();
		final MockMail mockMail = new MockMail(null);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(null, matchAddresses);
	}
	
}