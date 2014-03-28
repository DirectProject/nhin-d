package org.nhindirect.gateway.smtp.james.matcher;

import java.util.Arrays;
import java.util.Collection;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MatcherConfig;
import org.nhindirect.gateway.smtp.SmtpAgentException;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecipAndSenderIsNotLocalTest extends TestCase 
{
	public void testNullDomainList() throws Exception
	{
		final MatcherConfig newConfig = mock(MatcherConfig.class);
		when(newConfig.getCondition()).thenReturn(null);
		
		RecipAndSenderIsNotLocal matcher = new RecipAndSenderIsNotLocal();
		
		boolean exceptionOccured = false;
		
		try
		{
			matcher.init(newConfig);
		}
		catch (SmtpAgentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testEmptyDomainList() throws Exception
	{
		final MatcherConfig newConfig = mock(MatcherConfig.class);
		when(newConfig.getCondition()).thenReturn("");
		
		RecipAndSenderIsNotLocal matcher = new RecipAndSenderIsNotLocal();
		
		boolean exceptionOccured = false;
		
		try
		{
			matcher.init(newConfig);
		}
		catch (SmtpAgentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testMatch_RemoteSender_AssertRecipeintReturned() throws Exception
	{
		final Mail mockMail = mock(Mail.class);
		when(mockMail.getSender()).thenReturn(new MailAddress("me@remoteMail.com"));
		when(mockMail.getRecipients()).thenReturn(Arrays.asList(new MailAddress("you@cerner.com")));
		
		final MatcherConfig newConfig = mock(MatcherConfig.class);
		when(newConfig.getCondition()).thenReturn("cerner.com");
		
		RecipAndSenderIsNotLocal matcher = new RecipAndSenderIsNotLocal();
		matcher.init(newConfig);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(1, matchAddresses.size());
		assertEquals("you@cerner.com", matchAddresses.iterator().next().toString());
	}	
	
	public void testMatch_LocalSender_RemoteRcpt_AssertRecipeintReturned() throws Exception
	{
		final Mail mockMail = mock(Mail.class);
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));
		when(mockMail.getRecipients()).thenReturn(Arrays.asList(new MailAddress("you@remoteMail")));
		
		final MatcherConfig newConfig = mock(MatcherConfig.class);
		when(newConfig.getCondition()).thenReturn("cerner.com");
		
		RecipAndSenderIsNotLocal matcher = new RecipAndSenderIsNotLocal();
		matcher.init(newConfig);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(1, matchAddresses.size());
		assertEquals("you@remoteMail", matchAddresses.iterator().next().toString());
	}	
	
	public void testMatch_LocalSender_LocalRcpt_AssertNoneReturned() throws Exception
	{
		final Mail mockMail = mock(Mail.class);
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));
		when(mockMail.getRecipients()).thenReturn(Arrays.asList(new MailAddress("you@cerner.com")));
		
		final MatcherConfig newConfig = mock(MatcherConfig.class);
		when(newConfig.getCondition()).thenReturn("cerner.com");
		
		RecipAndSenderIsNotLocal matcher = new RecipAndSenderIsNotLocal();
		matcher.init(newConfig);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(0, matchAddresses.size());
	}	
	
	public void testMatch_LocalSender_LocalAndRemoteRcpt_AssertRemoteRcptReturned() throws Exception
	{
		final Mail mockMail = mock(Mail.class);
		when(mockMail.getSender()).thenReturn(new MailAddress("me@cerner.com"));
		when(mockMail.getRecipients()).thenReturn(Arrays.asList(new MailAddress("you@cerner.com"), new MailAddress("someone@remoteMail.com")));
		
		final MatcherConfig newConfig = mock(MatcherConfig.class);
		when(newConfig.getCondition()).thenReturn("cerner.com");
		
		RecipAndSenderIsNotLocal matcher = new RecipAndSenderIsNotLocal();
		matcher.init(newConfig);
		
		Collection<MailAddress> matchAddresses = matcher.match(mockMail);
		
		assertEquals(1, matchAddresses.size());
		assertEquals("someone@remoteMail.com", matchAddresses.iterator().next().toString());
	}		
}
