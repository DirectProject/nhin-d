package org.nhindirect.common.tx.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.util.TestUtils;

public class DefaultTxDetailParser_getMessageDetails_OptionalInputsTest 
{
	@Test
	public void testGetMessageDetailsFromInputStream_getDetailsFromCommonMessage() throws Exception
	{	
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		final InputStream inStream = IOUtils.toInputStream(TestUtils.readMessageFromFile("MessageWithAttachment.txt"));
		
		try
		{
			final DefaultTxDetailParser parser = new DefaultTxDetailParser();
			
			final Map<String, TxDetail> details = parser.getMessageDetails(inStream);
			
			assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.MessageID),
					details.get(TxDetailType.MSG_ID.getType()).getDetailValue());
			
			assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
					details.get(TxDetailType.FROM.getType()).getDetailValue());
			
			assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.To).toLowerCase(Locale.getDefault()),
					details.get(TxDetailType.RECIPIENTS.getType()).getDetailValue());
			
			assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.Subject).toLowerCase(Locale.getDefault()),
					details.get(TxDetailType.SUBJECT.getType()).getDetailValue());
		}
		finally
		{
			IOUtils.closeQuietly(inStream);
		}
	}
	
	@Test
	public void testGetMessageDetailsFromInputStream_nullStream_assertException() throws Exception
	{	
		final DefaultTxDetailParser parser = new DefaultTxDetailParser();
		
		boolean exceptionOccured = false;
		try
		{
			
			parser.getMessageDetails((InputStream)null);
			
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	@Test
	public void testGetMessageDetailsFromHeaders_getDetailsFromCommonMessage() throws Exception
	{	
		final MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		final InputStream inStream = IOUtils.toInputStream(TestUtils.readMessageFromFile("MessageWithAttachment.txt"));
		
		try
		{
			final InternetHeaders headers = new InternetHeaders(inStream);
			final DefaultTxDetailParser parser = new DefaultTxDetailParser();
			
			final Map<String, TxDetail> details = parser.getMessageDetails(headers);
			
			assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.MessageID),
					details.get(TxDetailType.MSG_ID.getType()).getDetailValue());
			
			assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.From).toLowerCase(Locale.getDefault()),
					details.get(TxDetailType.FROM.getType()).getDetailValue());
			
			assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.To).toLowerCase(Locale.getDefault()),
					details.get(TxDetailType.RECIPIENTS.getType()).getDetailValue());
			
			assertEquals(MailStandard.getHeader(msg, MailStandard.Headers.Subject).toLowerCase(Locale.getDefault()),
					details.get(TxDetailType.SUBJECT.getType()).getDetailValue());
		}
		finally
		{
			IOUtils.closeQuietly(inStream);
		}
	}
}
