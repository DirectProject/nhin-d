package org.nhindirect.gateway.smtp.james.mailet;

import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;

import org.nhindirect.gateway.smtp.MessageProcessResult;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentError;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.DefaultMessageEnvelope;
import org.nhindirect.stagent.MockNHINDAgent;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Matchers.any;


import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;



public class NHINDSecurityAndTrustMailet_service_Test extends TestCase
{
	private NHINDAddressCollection usedRecipients;
	private NHINDAddress usedSender;

	private static class MyMessageEnvelope extends DefaultMessageEnvelope
	{
		public MyMessageEnvelope(Message message)
		{
			super(message);
		}
		
		public MyMessageEnvelope(String rawMessage)
		{
			super(rawMessage);
		}
		
		public MyMessageEnvelope(Message message, NHINDAddressCollection recipients, NHINDAddress sender)
		{
			super(message, recipients, sender);
		}
		
		@Override
		protected void categorizeRecipients(TrustEnforcementStatus minTrustStatus)
		{
			super.categorizeRecipients(minTrustStatus);
		}

	}
	
	public void testService_UseRcpt_AssertRecipientsUsed() throws Exception
	{		
		final MimeMessage mimeMsg = EntitySerializer.Default.deserialize(TestUtils.readMessageResource("PlainOutgoingMessage.txt"));		
		
		
		final SmtpAgent mockAgent = mock(SmtpAgent.class);
		
		when(mockAgent.processMessage((MimeMessage)any(), 
				(NHINDAddressCollection)any(), (NHINDAddress)any())).thenAnswer(new Answer<MessageProcessResult>()
				{
					public MessageProcessResult answer(InvocationOnMock invocation) throws Throwable 
					{
						usedRecipients = (NHINDAddressCollection)invocation.getArguments()[1];						
						usedSender = (NHINDAddress)invocation.getArguments()[2];
						return new MessageProcessResult(new DefaultMessageEnvelope(new Message(mimeMsg), usedRecipients, usedSender), null);
				    }
				});
						

		
		final Mail mockMail = mock(MockMail.class, CALLS_REAL_METHODS);
		when(mockMail.getRecipients()).thenReturn(Arrays.asList(new MailAddress("you@cerner.com")));
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));

		mockMail.setMessage(mimeMsg);
		
		NHINDSecurityAndTrustMailet mailet = new NHINDSecurityAndTrustMailet();
			
		mailet.agent = mockAgent;
		
		mailet.service(mockMail);
		
		assertNotNull(usedRecipients);
		assertEquals(1, usedRecipients.size());
		assertEquals("you@cerner.com", usedRecipients.iterator().next().toString());
		
	}
	
	public void testService_UseToHeader_AssertRecipientsUsed() throws Exception
	{		
		final MimeMessage mimeMsg = EntitySerializer.Default.deserialize(TestUtils.readMessageResource("PlainOutgoingMessage.txt"));		
		
		
		final SmtpAgent mockAgent = mock(SmtpAgent.class);
		when(mockAgent.processMessage((MimeMessage)any(), 
				(NHINDAddressCollection)any(), (NHINDAddress)any())).thenAnswer(new Answer<MessageProcessResult>()
				{
					public MessageProcessResult answer(InvocationOnMock invocation) throws Throwable 
					{
						usedRecipients = (NHINDAddressCollection)invocation.getArguments()[1];
						return new MessageProcessResult(new DefaultMessageEnvelope(new Message(mimeMsg)), null);
				    }
				});
						

		final Mail mockMail = mock(MockMail.class, CALLS_REAL_METHODS);
		when(mockMail.getRecipients()).thenReturn(null);
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));

		mockMail.setMessage(mimeMsg);
		
		NHINDSecurityAndTrustMailet mailet = new NHINDSecurityAndTrustMailet();
			
		mailet.agent = mockAgent;
		
		mailet.service(mockMail);
		
		assertNotNull(usedRecipients);
		assertEquals(1, usedRecipients.size());
		assertEquals("externUser1@starugh-stateline.com", usedRecipients.iterator().next().toString());
		
	}	
	
	public void testService_ProcessIsNull_AssertGhostState() throws Exception
	{		
		final MimeMessage mimeMsg = EntitySerializer.Default.deserialize(TestUtils.readMessageResource("PlainOutgoingMessage.txt"));		
		
		
		final SmtpAgent mockAgent = mock(SmtpAgent.class);
						
		final Mail mockMail = mock(MockMail.class, CALLS_REAL_METHODS);
		when(mockMail.getRecipients()).thenReturn(null);
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));

		mockMail.setMessage(mimeMsg);
		
		NHINDSecurityAndTrustMailet mailet = new NHINDSecurityAndTrustMailet();
			
		mailet.agent = mockAgent;
		
		mailet.service(mockMail);
		
		assertEquals(Mail.GHOST, mockMail.getState());
		
	}		
	
	public void testService_ProcessThrowsRuntimeException_AssertExceptionAndGhostState() throws Exception
	{		
		final MimeMessage mimeMsg = EntitySerializer.Default.deserialize(TestUtils.readMessageResource("PlainOutgoingMessage.txt"));		
		
		
		final SmtpAgent mockAgent = mock(SmtpAgent.class);
						
		final Mail mockMail = mock(MockMail.class, CALLS_REAL_METHODS);
		when(mockMail.getRecipients()).thenReturn(null);
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));
		
		doThrow(new RuntimeException("Just Passing Through")).when(mockAgent).processMessage((MimeMessage)any(), 
				(NHINDAddressCollection)any(), (NHINDAddress)any());
		
		mockMail.setMessage(mimeMsg);
		
		NHINDSecurityAndTrustMailet mailet = new NHINDSecurityAndTrustMailet();
			
		mailet.agent = mockAgent;
		
		boolean exceptionOccured = false;
		try
		{
			mailet.service(mockMail);
		}
		catch (MessagingException e)
		{
			assertEquals("Failed to process message: Just Passing Through", e.getMessage());
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		assertEquals(Mail.GHOST, mockMail.getState());
		
	}	
	
	public void testService_ProcessThrowsSmtpAgentException_AssertExceptionAndGhostState() throws Exception
	{		
		final MimeMessage mimeMsg = EntitySerializer.Default.deserialize(TestUtils.readMessageResource("PlainOutgoingMessage.txt"));		
		
		
		final SmtpAgent mockAgent = mock(SmtpAgent.class);
						
		final Mail mockMail = mock(MockMail.class, CALLS_REAL_METHODS);
		when(mockMail.getRecipients()).thenReturn(null);
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));
		
		doThrow(new SmtpAgentException(SmtpAgentError.Unknown, "Just Passing Through")).when(mockAgent).processMessage((MimeMessage)any(), 
				(NHINDAddressCollection)any(), (NHINDAddress)any());
		
		mockMail.setMessage(mimeMsg);
		
		NHINDSecurityAndTrustMailet mailet = new NHINDSecurityAndTrustMailet();
			
		mailet.agent = mockAgent;
		
		boolean exceptionOccured = false;
		try
		{
			mailet.service(mockMail);
		}
		catch (SmtpAgentException e)
		{
			assertEquals(SmtpAgentError.Unknown, e.getError());
			assertEquals("Just Passing Through", e.getMessage());
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		assertEquals(Mail.GHOST, mockMail.getState());
		
	}
	
	public void testService_NullProcessedMessage_GhostState() throws Exception
	{		
		final MimeMessage mimeMsg = EntitySerializer.Default.deserialize(TestUtils.readMessageResource("PlainOutgoingMessage.txt"));		
		
		
		final SmtpAgent mockAgent = mock(SmtpAgent.class);
		when(mockAgent.processMessage((MimeMessage)any(), 
				(NHINDAddressCollection)any(), (NHINDAddress)any())).thenAnswer(new Answer<MessageProcessResult>()
				{
					public MessageProcessResult answer(InvocationOnMock invocation) throws Throwable 
					{
						usedRecipients = (NHINDAddressCollection)invocation.getArguments()[1];
						return new MessageProcessResult(null, null);
				    }
				});
						

		
		final Mail mockMail = mock(MockMail.class, CALLS_REAL_METHODS);
		when(mockMail.getRecipients()).thenReturn(Arrays.asList(new MailAddress("you@cerner.com")));
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));

		mockMail.setMessage(mimeMsg);
		
		NHINDSecurityAndTrustMailet mailet = new NHINDSecurityAndTrustMailet();
			
		mailet.agent = mockAgent;
		
		mailet.service(mockMail);
		assertEquals(Mail.GHOST, mockMail.getState());
		
	}		

	@SuppressWarnings("unused")
	public void testService_RejectRecipients_AssertRejectedList() throws Exception
	{		
		final MimeMessage mimeMsg = EntitySerializer.Default.deserialize(TestUtils.readMessageResource("PlainOutgoingMessage.txt"));		
		
		
		
		final SmtpAgent mockAgent = mock(SmtpAgent.class);
		when(mockAgent.processMessage((MimeMessage)any(), 
				(NHINDAddressCollection)any(), (NHINDAddress)any())).thenAnswer(new Answer<MessageProcessResult>()
				{
					public MessageProcessResult answer(InvocationOnMock invocation) throws Throwable 
					{
						
						usedRecipients = (NHINDAddressCollection)invocation.getArguments()[1];	
						usedRecipients.get(0).setStatus(TrustEnforcementStatus.Failed);
						usedRecipients.get(1).setStatus(TrustEnforcementStatus.Success);		
						usedSender = (NHINDAddress)invocation.getArguments()[2];
						MyMessageEnvelope env = new MyMessageEnvelope(new Message(mimeMsg), usedRecipients, usedSender);
						env.setAgent(new MockNHINDAgent(Arrays.asList("cerner.com")));
						env.categorizeRecipients(TrustEnforcementStatus.Success);
						
						NHINDAddressCollection rejectedRecips = env.getRejectedRecipients();
						return new MessageProcessResult(env, null);
				    }
				});
						

		
		final Mail mockMail = mock(MockMail.class, CALLS_REAL_METHODS);
		mockMail.setRecipients(Arrays.asList(new MailAddress("you@cerner.com"), new MailAddress("they@cerner.com")));
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));

		mockMail.setMessage(mimeMsg);
		
		NHINDSecurityAndTrustMailet mailet = new NHINDSecurityAndTrustMailet();
			
		mailet.agent = mockAgent;
		
		mailet.service(mockMail);
		
		assertEquals(1, mockMail.getRecipients().size());
		
	}			
	

}
