package org.nhindirect.stagent.mail.notifications;

import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMultipart;

import junit.framework.TestCase;

import org.apache.mailet.base.mail.MimeMultipartReport;
import org.nhindirect.stagent.NHINDException;

import com.sun.mail.dsn.DispositionNotification;


public class MDNFactory_createTest extends TestCase
{
	public static InternetHeaders getNotificationFieldsAsHeaders(MimeMultipart mm)
	{
		InternetHeaders retVal = null;
		
		if (mm == null)
			throw new IllegalArgumentException("Multipart can not be null");
		
		try
		{
			if (mm.getCount() < 2)
				throw new IllegalArgumentException("Multipart can not be null");
			
			// the second part should be the notification
			BodyPart part = mm.getBodyPart(1);
			
			if (part.getContent() instanceof DispositionNotification)
			{
				return ((DispositionNotification)part.getContent()).getNotifications();
			}
			// parse fields
			retVal = new InternetHeaders();	
			String[] fields = Notification.getPartContentBodyAsString(part).split("\r\n");
			for (String field : fields)
			{
				int idx = field.indexOf(":");
				if (idx > -1)
				{
					String name = field.substring(0, idx);
					String value = field.substring(idx + 1).trim();
					retVal.setHeader(name, value);
				}
			}

		}
		catch (Exception e)
		{
			throw new NHINDException("Failed to parse notification fields.", e);
		}
		
		return retVal;
		
	}	

	public void testCreate_withGernalAttributes() throws Exception
	{
		final Disposition disp = new Disposition(NotificationType.Processed);
		final MdnGateway gateway = new MdnGateway("junitGateway");
		
		MimeMultipartReport report = MDNFactory.create("test", "junitUA", "junitProduct", "sender@send.com", 
				"final@final.com", "12345", "junit error", gateway, disp, "", "", new ArrayList<String>());
		
		assertNotNull(report);
		
		final InternetHeaders headers = getNotificationFieldsAsHeaders(report);
		
		assertTrue(headers.getHeader(MDNStandard.Headers.ReportingAgent, ",").startsWith("junitUA"));
		assertTrue(headers.getHeader(MDNStandard.Headers.ReportingAgent, ",").endsWith("junitProduct"));
		assertEquals("rfc822; sender@send.com", headers.getHeader(MDNStandard.Headers.OriginalRecipeint, ","));
		assertEquals("rfc822; final@final.com", headers.getHeader(MDNStandard.Headers.FinalRecipient, ","));
		assertTrue(headers.getHeader(MDNStandard.Headers.Gateway, ",").endsWith("junitGateway"));
		assertTrue(headers.getHeader(MDNStandard.Headers.Disposition, ",").endsWith(NotificationType.Processed.toString()));	
		
		BodyPart part0 = report.getBodyPart(0);
		Object obj = part0.getContent();
		assertEquals("test", obj);
	}
	
	public void testCreate_withError() throws Exception
	{
		final Disposition disp = new Disposition(NotificationType.Processed);
		final MdnGateway gateway = new MdnGateway("junitGateway");
		
		MimeMultipartReport report = MDNFactory.create("test", "junitUA", "junitProduct", "sender@send.com", 
				"final@final.com", "12345", "junit error", gateway, disp, "", "", new ArrayList<String>());
		
		assertNotNull(report);
		
		final InternetHeaders headers = getNotificationFieldsAsHeaders(report);
		
		assertEquals("junit error", headers.getHeader("Error", ","));
	}
	
	public void testCreate_withError_oldConstructor() throws Exception
	{
		final Disposition disp = new Disposition(NotificationType.Processed);
		final MdnGateway gateway = new MdnGateway("junitGateway");
		
		MimeMultipartReport report = MDNFactory.create("test", "junitUA", "junitProduct", "sender@send.com", 
				"final@final.com", "12345", "junit error", gateway, disp);
		
		assertNotNull(report);
		
		final InternetHeaders headers = getNotificationFieldsAsHeaders(report);
		
		assertEquals("junit error", headers.getHeader("Error", ","));
	}
	
	public void testCreate_withWarning() throws Exception
	{
		final Disposition disp = new Disposition(NotificationType.Processed);
		final MdnGateway gateway = new MdnGateway("junitGateway");
		
		MimeMultipartReport report = MDNFactory.create("test", "junitUA", "junitProduct", "sender@send.com", 
				"final@final.com", "12345", "", gateway, disp, "junit warning", "", new ArrayList<String>());
		
		assertNotNull(report);
		
		final InternetHeaders headers = getNotificationFieldsAsHeaders(report);
		
		assertEquals("junit warning", headers.getHeader("Warning", ","));
	}
	
	public void testCreate_withFailure() throws Exception
	{
		final Disposition disp = new Disposition(NotificationType.Processed);
		final MdnGateway gateway = new MdnGateway("junitGateway");
		
		MimeMultipartReport report = MDNFactory.create("test", "junitUA", "junitProduct", "sender@send.com", 
				"final@final.com", "12345", "", gateway, disp, "", "junit failure", new ArrayList<String>());
		
		assertNotNull(report);
		
		final InternetHeaders headers = getNotificationFieldsAsHeaders(report);
		
		assertEquals("junit failure", headers.getHeader("Failure", ","));
	}		
	
	public void testCreate_withExtensionNameOnly() throws Exception
	{
		final Disposition disp = new Disposition(NotificationType.Processed);
		final MdnGateway gateway = new MdnGateway("junitGateway");
		
		MimeMultipartReport report = MDNFactory.create("test", "junitUA", "junitProduct", "sender@send.com", 
				"final@final.com", "12345", "", gateway, disp, "", "junit failure", Arrays.asList("X-EXTENSION"));
		
		assertNotNull(report);
		
		final InternetHeaders headers = getNotificationFieldsAsHeaders(report);
		
		assertEquals("", headers.getHeader("X-EXTENSION", ","));
	}	
	
	public void testCreate_withExtensionNameWithValue() throws Exception
	{
		final Disposition disp = new Disposition(NotificationType.Processed);
		final MdnGateway gateway = new MdnGateway("junitGateway");
		
		MimeMultipartReport report = MDNFactory.create("test", "junitUA", "junitProduct", "sender@send.com", 
				"final@final.com", "12345", "", gateway, disp, "", "junit failure", Arrays.asList("X-EXTENSION:junit value"));
		
		assertNotNull(report);
		
		final InternetHeaders headers = getNotificationFieldsAsHeaders(report);
		
		assertEquals("junit value", headers.getHeader("X-EXTENSION", ","));
	}
	
	public void testCreate_noDisposition_assertException() throws Exception
	{
		final MdnGateway gateway = new MdnGateway("junitGateway");

		boolean exceptionOccured = false;
		
		try
		{
			MDNFactory.create("test", "junitUA", "junitProduct", "sender@send.com", 
					"final@final.com", "12345", "", gateway, null, "", "junit failure", Arrays.asList("X-EXTENSION:junit value"));
			
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
}
