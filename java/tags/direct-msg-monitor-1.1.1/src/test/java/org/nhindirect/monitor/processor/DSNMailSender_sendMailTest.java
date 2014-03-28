package org.nhindirect.monitor.processor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Matchers.any;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

import javax.mail.internet.MimeMessage;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.net.smtp.SMTPClient;
import org.junit.Test;
import org.nhindirect.monitor.util.TestUtils;


public class DSNMailSender_sendMailTest 
{
	protected SMTPClientFactory createFactory(final SMTPClient client)
	{
		final SMTPClientFactory factory = new SMTPClientFactory()
		{
			@Override
			public SMTPClient createInstance()
			{
				return client;
			}
		};
		
		return factory;
	}
	
	@Test
	public void testSendMail_mailSent_noExceptions() throws Exception
	{
		MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		
		final SMTPClient client = mock(SMTPClient.class);
		when(client.setSender((String)any())).thenReturn(true);
		when(client.addRecipient((String)any())).thenReturn(true);
		when(client.sendMessageData()).thenReturn(new StringWriter());
		when(client.getReplyCode()).thenReturn(250);
		
		final SMTPClientFactory factory = createFactory(client);
		
		DSNMailSender sender = new DSNMailSender("smtp://localhost", factory);
		
		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		exchange.getIn().setBody(msg);
		
		sender.sendMail(exchange);
		
		verify(client, times(1)).getReplyCode();
	}
	
	@Test
	public void testSendMail_nullGateway() throws Exception
	{

		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		DSNMailSender sender = new DSNMailSender();
		
		boolean exceptionOccurred = false;
		
		try
		{
			sender.sendMail(exchange);
		}
		catch (IllegalStateException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);
	}
	
	@Test
	public void testSendMail_emptyGateway() throws Exception
	{

		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		DSNMailSender sender = new DSNMailSender("", null);
		
		boolean exceptionOccurred = false;
		
		try
		{
			sender.sendMail(exchange);
		}
		catch (IllegalStateException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);
	}
	
	@Test
	public void testSendMail_emptyFactory() throws Exception
	{

		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		DSNMailSender sender = new DSNMailSender("smtp://localhost", null);
		
		boolean exceptionOccurred = false;
		
		try
		{
			sender.sendMail(exchange);
		}
		catch (IllegalStateException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);
	}
	
	@Test
	public void testSendMail_setSenderError_assertException() throws Exception
	{
		MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		
		final SMTPClient client = mock(SMTPClient.class);
		when(client.setSender((String)any())).thenReturn(false);
		when(client.addRecipient((String)any())).thenReturn(true);
		when(client.sendMessageData()).thenReturn(new StringWriter());
		when(client.getReplyCode()).thenReturn(250);
		
		final SMTPClientFactory factory = createFactory(client);
		
		DSNMailSender sender = new DSNMailSender("smtp://localhost", factory);
		
		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		exchange.getIn().setBody(msg);
		
		boolean exceptionOccurred = false;
		try
		{
			sender.sendMail(exchange);
		}
		catch (IOException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);
		verify(client, times(1)).setSender((String)any());
		verify(client, never()).addRecipient((String)any());
	}
	
	@Test
	public void testSendMail_setAddRecip_assertException() throws Exception
	{
		MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		
		final SMTPClient client = mock(SMTPClient.class);
		when(client.setSender((String)any())).thenReturn(true);
		when(client.addRecipient((String)any())).thenReturn(false);
		when(client.sendMessageData()).thenReturn(new StringWriter());
		when(client.getReplyCode()).thenReturn(250);
		
		final SMTPClientFactory factory = createFactory(client);
		
		DSNMailSender sender = new DSNMailSender("smtp://localhost", factory);
		
		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		exchange.getIn().setBody(msg);
		
		boolean exceptionOccurred = false;
		try
		{
			sender.sendMail(exchange);
		}
		catch (IOException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);

		verify(client, times(1)).addRecipient((String)any());
		verify(client, never()).sendMessageData();
	}
	
	@Test
	public void testSendMail_nullWriter_assertException() throws Exception
	{
		MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		
		final SMTPClient client = mock(SMTPClient.class);
		when(client.setSender((String)any())).thenReturn(true);
		when(client.addRecipient((String)any())).thenReturn(true);
		when(client.sendMessageData()).thenReturn(null);
		when(client.getReplyCode()).thenReturn(250);
		
		final SMTPClientFactory factory = createFactory(client);
		
		DSNMailSender sender = new DSNMailSender("smtp://localhost", factory);
		
		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		exchange.getIn().setBody(msg);
		
		boolean exceptionOccurred = false;
		try
		{
			sender.sendMail(exchange);
		}
		catch (IOException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);


		verify(client, times(1)).sendMessageData();
		verify(client, never()).completePendingCommand();
	}
	
	@Test
	public void testSendMail_failureStatusCode_assertException() throws Exception
	{
		MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		
		final SMTPClient client = mock(SMTPClient.class);
		when(client.setSender((String)any())).thenReturn(true);
		when(client.addRecipient((String)any())).thenReturn(true);
		when(client.sendMessageData()).thenReturn(new StringWriter());
		when(client.getReplyCode()).thenReturn(300);
		
		final SMTPClientFactory factory = createFactory(client);
		
		DSNMailSender sender = new DSNMailSender("smtp://localhost", factory);
		
		Exchange exchange = new DefaultExchange(mock(CamelContext.class));
		exchange.getIn().setBody(msg);
		
		boolean exceptionOccurred = false;
		try
		{
			sender.sendMail(exchange);
		}
		catch (IOException e)
		{
			exceptionOccurred = true;
		}
		
		assertTrue(exceptionOccurred);


		verify(client, times(2)).getReplyCode();
	}
}
