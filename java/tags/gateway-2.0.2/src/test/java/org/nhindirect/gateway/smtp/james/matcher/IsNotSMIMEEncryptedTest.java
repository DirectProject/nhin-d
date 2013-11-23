package org.nhindirect.gateway.smtp.james.matcher;


import java.util.ArrayList;
import java.util.Collection;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.MailAddress;
import org.nhindirect.gateway.smtp.james.mailet.MockMail;
import org.nhindirect.gateway.testutils.TestUtils;

import junit.framework.TestCase;

public class IsNotSMIMEEncryptedTest extends TestCase
{
	@SuppressWarnings("unchecked")
	public void testIsNotSMIMEMessage_unecryptedMessage_assertAllRecips() throws Exception
	{
		MimeMessage msg = new MimeMessage(null, IOUtils.toInputStream(TestUtils.readMessageResource("PlainOutgoingMessage.txt")));
		
		
		IsNotSMIMEEncrypted matcher = new IsNotSMIMEEncrypted();
		
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
	public void testIsNotSMIMEMessage_ecryptedMessage_assertNull() throws Exception
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
	public void testIsNotSMIMEMessage_nullMail_assertNull() throws Exception
	{
		
		IsNotSMIMEEncrypted matcher = new IsNotSMIMEEncrypted();
		
		
		Collection<MailAddress> matchAddresses = matcher.match(null);
		
		assertEquals(null, matchAddresses);
	}
	
	@SuppressWarnings("unchecked")
	public void testIsNotSMIMEMessage_nullMessage_assertNull() throws Exception
	{
		
		IsNotSMIMEEncrypted matcher = new IsNotSMIMEEncrypted();
		final MockMail mockMail = new MockMail(null);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(null, matchAddresses);
	}
}
