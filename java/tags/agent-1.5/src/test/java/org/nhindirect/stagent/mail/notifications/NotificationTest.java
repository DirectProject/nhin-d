package org.nhindirect.stagent.mail.notifications;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.mail.BodyPart;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class NotificationTest extends TestCase 
{
	public void testCreateNotification_AssertMultipart() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		MimeMultipart mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		BodyPart part = mm.getBodyPart(0);
		assertTrue(part.getContentType().startsWith("text/plain"));
		assertEquals("Your message was successfully processed.", part.getContent().toString());
			
		part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		part.writeTo(outStream);
		String content = new String(outStream.toByteArray());
		
		assertTrue(content.contains("automatic-action/MDN-sent-automatically;processed"));
	}
	
	public void testCreateNotification_AssertGetParts() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		ArrayList<MimeEntity> entities = (ArrayList<MimeEntity>)noti.getParts();
		
		MimeEntity entity = entities.get(0);
		assertEquals("Your message was successfully processed.", entity.getContent().toString());
		
		entity = entities.get(1);
		assertTrue(entity.getContentType().startsWith("message/disposition-notification"));
		ByteArrayInputStream str = (ByteArrayInputStream)entity.getContent();
		byte[] bytes = new byte[str.available()];
		str.read(bytes);
		String content = new String(bytes);
		assertTrue(content.contains("automatic-action/MDN-sent-automatically;processed"));	
	}
	
	public void testCreateNotification_AssertDispatched() throws Exception
	{
		Notification noti = new Notification(NotificationType.Dispatched);
		
		ArrayList<MimeEntity> entities = (ArrayList<MimeEntity>)noti.getParts();
		
		MimeEntity entity = entities.get(0);
		assertEquals("Your message was successfully processed.", entity.getContent().toString());
		
		entity = entities.get(1);
		assertTrue(entity.getContentType().startsWith("message/disposition-notification"));
		ByteArrayInputStream str = (ByteArrayInputStream)entity.getContent();
		byte[] bytes = new byte[str.available()];
		str.read(bytes);
		String content = new String(bytes);
		assertTrue(content.contains("automatic-action/MDN-sent-automatically;dispatched"));	
	}
	
	public void testCreateNotification_AssertInputStream() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		ByteArrayDataSource dataSource = new ByteArrayDataSource(noti.getInputStream(), noti.getAsMultipart().getContentType());
		MimeMultipart mm = new MimeMultipart(dataSource);
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		BodyPart part = mm.getBodyPart(0);
		assertTrue(part.getContentType().startsWith("text/plain"));
		assertEquals("Your message was successfully processed.", part.getContent().toString());
			
		part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		ByteArrayInputStream str = (ByteArrayInputStream)part.getContent();
		byte[] bytes = new byte[str.available()];
		str.read(bytes);
		String content = new String(bytes);
		assertTrue(content.contains("automatic-action/MDN-sent-automatically;processed"));	
	}	
	
	public void testSetExplanation_AssertExplanation() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		MimeMultipart mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		BodyPart part = mm.getBodyPart(0);
		assertTrue(part.getContentType().startsWith("text/plain"));
		assertEquals("Your message was successfully processed.", part.getContent().toString());			

		// set a new explanation
		noti.setExplanation("Testing this explantation");
		mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		part = mm.getBodyPart(0);
		assertTrue(part.getContentType().startsWith("text/plain"));
		assertEquals("Testing this explantation", noti.getExplanation());
		assertEquals(noti.getExplanation(), part.getContent().toString());		
		
		// make sure this didn't change
		part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		part.writeTo(outStream);
		String content = new String(outStream.toByteArray());		
		assertTrue(content.contains("automatic-action/MDN-sent-automatically;processed"));		
	}		
	
	
	public void testSetUserAgent_AssertUseragent() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		MimeMultipart mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		BodyPart part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		InternetHeaders headers = noti.getNotificationFieldsAsHeaders();
		assertNull(headers.getHeader(MDNStandard.Headers.ReportingAgent));
		
		// set a new UA
		noti.setReportingAgent(new ReportingUserAgent("Junit Agent Name", "Junit Product Name"));
		mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		headers = noti.getNotificationFieldsAsHeaders();
		assertNotNull(headers.getHeader(MDNStandard.Headers.ReportingAgent));
		assertEquals("Junit Agent Name; Junit Product Name", headers.getHeader(MDNStandard.Headers.ReportingAgent, ","));
		assertEquals(headers.getHeader(MDNStandard.Headers.ReportingAgent, ","), noti.getReportingAgent().toString());
	}		
	
	public void testGateway_AssertGateway() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		MimeMultipart mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		BodyPart part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		InternetHeaders headers = noti.getNotificationFieldsAsHeaders();
		assertNull(headers.getHeader(MDNStandard.Headers.Gateway));
		
		// set a new gateway

		noti.setGateway(new MdnGateway("Junit domain", "Junit type"));
		mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		headers = noti.getNotificationFieldsAsHeaders();
		assertNotNull(headers.getHeader(MDNStandard.Headers.Gateway));
		assertEquals("Junit type; Junit domain", headers.getHeader(MDNStandard.Headers.Gateway, ","));
		assertEquals(headers.getHeader(MDNStandard.Headers.Gateway, ","), noti.getGateway().toString());
		
	}		
	
	public void testOriginalMessageId_AssertOriginalMessageId() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		MimeMultipart mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		BodyPart part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		InternetHeaders headers = noti.getNotificationFieldsAsHeaders();
		assertNull(headers.getHeader(MDNStandard.Headers.OriginalMessageID));
		
		// set a new gateway

		noti.setOriginalMessageId("Orig Msg Id");
		mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		headers = noti.getNotificationFieldsAsHeaders();
		assertNotNull(headers.getHeader(MDNStandard.Headers.OriginalMessageID));
		assertEquals("Orig Msg Id", headers.getHeader(MDNStandard.Headers.OriginalMessageID, ","));
		assertEquals(headers.getHeader(MDNStandard.Headers.OriginalMessageID, ","), noti.getOriginalMessageId());
		
	}		
	
	public void testFinalRecip_AssertFinalRecip() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		MimeMultipart mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		BodyPart part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		InternetHeaders headers = noti.getNotificationFieldsAsHeaders();
		assertNull(headers.getHeader(MDNStandard.Headers.FinalRecipient));
		
		// set a new gateway

		noti.setFinalRecipient("Test Final Recip");
		mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		headers = noti.getNotificationFieldsAsHeaders();
		assertNotNull(headers.getHeader(MDNStandard.Headers.FinalRecipient));
		assertEquals("rfc822; Test Final Recip", headers.getHeader(MDNStandard.Headers.FinalRecipient, ","));
		assertEquals(headers.getHeader(MDNStandard.Headers.FinalRecipient, ","), "rfc822; " + noti.getFinalRecipeint());
		
	}		
	
	public void testError_AssertError() throws Exception
	{
		Notification noti = new Notification(NotificationType.Processed);
		
		MimeMultipart mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		BodyPart part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		InternetHeaders headers = noti.getNotificationFieldsAsHeaders();
		assertNull(headers.getHeader(MDNStandard.Headers.Error));
		
		// set a new gateway

		noti.setError("Junit Error");
		mm = noti.getAsMultipart();
		
		assertNotNull(mm);
		assertEquals(2, mm.getCount());
		
		part = mm.getBodyPart(1);
		assertTrue(part.getContentType().startsWith("message/disposition-notification"));
		headers = noti.getNotificationFieldsAsHeaders();
		assertNotNull(headers.getHeader(MDNStandard.Headers.Error));
		assertEquals("Junit Error", headers.getHeader(MDNStandard.Headers.Error, ","));
		assertEquals(headers.getHeader(MDNStandard.Headers.Error, ","), noti.getError());
		
	}		
	
	public void testParseFieldsFromMimeMessage() throws Exception
	{
		String testMessage = TestUtils.readResource("MDNMessage.txt");
		
		MimeMessage msg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		InternetHeaders headers = Notification.getNotificationFieldsAsHeaders(msg);
		
		assertNotNull(headers.getHeader(MDNStandard.Headers.Disposition));
		assertEquals("automatic-action/MDN-sent-automatically;processed", headers.getHeader(MDNStandard.Headers.Disposition, ","));
		
		assertNotNull(headers.getHeader(MDNStandard.Headers.ReportingAgent));
		assertEquals("starugh-stateline.com;NHIN Direct Security Agent", headers.getHeader(MDNStandard.Headers.ReportingAgent, ","));
		
		assertNotNull(headers.getHeader(MDNStandard.Headers.FinalRecipient));
		assertEquals("externUser1@starugh-stateline.com", headers.getHeader(MDNStandard.Headers.FinalRecipient, ","));	
		
		assertNotNull(headers.getHeader(MDNStandard.Headers.OriginalMessageID));
		assertEquals("<9501051053.aa04167@IETF.CNR I.Reston.VA.US>", headers.getHeader(MDNStandard.Headers.OriginalMessageID, ","));			
	}
	
	public void testParseFieldsFromMimeMessage_NonMDNMessage_AssertExecption() throws Exception
	{
		String testMessage = TestUtils.readResource("MessageWithAttachment.txt");
		
		MimeMessage msg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		boolean exceptionOccured = false;
		try
		{
			Notification.getNotificationFieldsAsHeaders(msg);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
				
	}
}
