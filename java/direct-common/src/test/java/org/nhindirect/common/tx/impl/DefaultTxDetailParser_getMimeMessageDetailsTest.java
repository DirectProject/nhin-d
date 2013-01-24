package org.nhindirect.common.tx.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.util.TestUtils;

public class DefaultTxDetailParser_getMimeMessageDetailsTest 
{
	@Test
	public void testGetMessageDetails_getDetailsFromCommonMessage() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.MessageID),
				details.get(TxDetailType.MSG_ID.getType()).getDetailValue());
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.FROM.getType()).getDetailValue());
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.To).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.RECIPIENTS.getType()).getDetailValue());
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.Subject).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.SUBJECT.getType()).getDetailValue());
	}
	
	@Test
	public void testGetMessageDetails_noMessageId() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.setHeader(MailStandard.Headers.MessageID, "");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertNull(details.get(TxDetailType.MSG_ID.getType()));
	}
	
	@Test
	public void testGetMessageDetails_noSubject() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.setHeader(MailStandard.Headers.Subject, "");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertNull(details.get(TxDetailType.SUBJECT.getType()));
	}
	
	@Test
	public void testGetMessageDetails_noFullHeaders() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser()
		{
			@Override
			public String getHeadersAsStringInternal(MimeMessage msg)
			{
				return "";
			}
		};
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertNull(details.get(TxDetailType.MSG_FULL_HEADERS.getType()));
	}
	
	@Test
	public void testGetMessageDetails_noFrom() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.setHeader(MailStandard.Headers.From, "");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertNull(details.get(TxDetailType.FROM.getType()));
	}
	
	@Test
	public void testGetMessageDetails_mutlipleFroms() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.setHeader(MailStandard.Headers.From, "gm2552@cerner.com,ah4626@cerner.com");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.FROM.getType()).getDetailValue());
	}
	
	@Test
	public void testGetMessageDetails_noSender() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertNull(details.get(TxDetailType.SENDER.getType()));
	}
	
	@Test
	public void testGetMessageDetails_senderExists() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.setHeader(MailStandard.Headers.Sender, "gm2552@cerner.com");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.Sender).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.SENDER.getType()).getDetailValue());
	}
	
	@Test
	public void testGetMessageDetails_mutlipleTo() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.setHeader(MailStandard.Headers.To, "gm2552@cerner.com,ah4626@cerner.com");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.To).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.RECIPIENTS.getType()).getDetailValue());
	}
	
	@Test
	public void testGetMessageDetails_ToAndCC() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.setHeader(MailStandard.Headers.To, "gm2552@cerner.com");
		msg.setHeader(MailStandard.Headers.CC, "ah4626@cerner.com");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
				
		assertEquals("gm2552@cerner.com,ah4626@cerner.com",
				details.get(TxDetailType.RECIPIENTS.getType()).getDetailValue());
	}
	
	@Test
	public void testGetMessageDetails_generalMDNMessage() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessage.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		
		assertEquals(MDNStandard.getMDNField(msg, MDNStandard.Headers.Disposition).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.DISPOSITION.getType()).getDetailValue());
		
		assertEquals(MDNStandard.getMDNField(msg, MDNStandard.Headers.FinalRecipient).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.FINAL_RECIPIENTS.getType()).getDetailValue());
		
		assertEquals(MDNStandard.getMDNField(msg, MDNStandard.Headers.OriginalMessageID),
				details.get(TxDetailType.PARENT_MSG_ID.getType()).getDetailValue());
		
	}
	
	@Test
	public void testGetMessageDetails_generalMDNMessage_noOrigMessageId_getFromReplyTo() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessageOrigInReplyTo.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		
		assertEquals(MDNStandard.getMDNField(msg, MDNStandard.Headers.Disposition).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.DISPOSITION.getType()).getDetailValue());
		
		assertEquals(MDNStandard.getMDNField(msg, MDNStandard.Headers.FinalRecipient).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.FINAL_RECIPIENTS.getType()).getDetailValue());
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.InReplyTo),
				details.get(TxDetailType.PARENT_MSG_ID.getType()).getDetailValue());
		
	}
	
	@Test
	public void testGetMessageDetails_generalMDNMessage_noOrigMessageId() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessageOrigInReplyTo.txt");
		msg.setHeader(MailStandard.Headers.InReplyTo, "");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		
		assertEquals(MDNStandard.getMDNField(msg, MDNStandard.Headers.Disposition).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.DISPOSITION.getType()).getDetailValue());
		
		assertEquals(MDNStandard.getMDNField(msg, MDNStandard.Headers.FinalRecipient).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.FINAL_RECIPIENTS.getType()).getDetailValue());
		
		assertNull(details.get(TxDetailType.PARENT_MSG_ID.getType()));
		
	}
	
	@Test
	public void testGetMessageDetails_noDisposition() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessageNoDisp.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		
		assertNull(details.get(TxDetailType.DISPOSITION.getType()));
		
	}
	
	@Test
	public void testGetMessageDetails_noFinalRecipient() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MDNMessageNoFinalRecip.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		
		assertNull(details.get(TxDetailType.FINAL_RECIPIENTS.getType()));
		
	}
	
	@Test
	public void testGetMessageDetails_generalDNSMessage() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("DSNMessage.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		
		assertEquals("failed",
				details.get(TxDetailType.DSN_ACTION.getType()).getDetailValue());
		
		assertEquals("5.0.0",
				details.get(TxDetailType.DSN_STATUS.getType()).getDetailValue());
		
		assertEquals("Carol@Ivory.EDU".toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.FINAL_RECIPIENTS.getType()).getDetailValue());
		
		assertEquals("<9501051053.aa04167@IETF.CNR I.Reston.VA.US>",
				details.get(TxDetailType.PARENT_MSG_ID.getType()).getDetailValue());
		
	}
	
	@Test
	public void testGetMessageDetails_generalDNSMessage_noOrigMessageId_getFromReplyTo() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("DSNMessageOrigInReplyTo.txt");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		assertEquals("<9501051053.aa04167@IETF.CNR I.Reston.VA.US>",
				details.get(TxDetailType.PARENT_MSG_ID.getType()).getDetailValue());
		
	}
	
	@Test
	public void testGetMessageDetails_generalDNSMessage_noOrigMessageId() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("DSNMessageOrigInReplyTo.txt");
		msg.setHeader(MailStandard.Headers.InReplyTo, "");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		assertNull(details.get(TxDetailType.PARENT_MSG_ID.getType()));
		
	}
	
	@Test
	public void testGetMessageDetails_generalDNSMessage_noActionOrStatus() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("DSNMessageNoActionOrStatus.txt");
		msg.setHeader(MailStandard.Headers.InReplyTo, "");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		assertNull(details.get(TxDetailType.DSN_ACTION.getType()));
		
		assertNull(details.get(TxDetailType.DSN_STATUS.getType()));
		
	}
	
	@Test
	public void testGetMessageDetails_commonMessage_timelyAndReliable() throws Exception
	{
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.setHeader(MDNStandard.Headers.DispositionNotificationOptions, "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
		
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		final Map<String, TxDetail> details = parser.getMessageDetails(msg);
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.MessageID),
				details.get(TxDetailType.MSG_ID.getType()).getDetailValue());
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.FROM.getType()).getDetailValue());
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.To).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.RECIPIENTS.getType()).getDetailValue());
		
		assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.Subject).toLowerCase(Locale.getDefault()),
				details.get(TxDetailType.SUBJECT.getType()).getDetailValue());
		
	}
}
