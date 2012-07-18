package org.nhindirect.common.tx;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.common.util.TestUtils;

public class TxUtil_isRelAndTimelyTest 
{
	@Test
	public void testIsTimelyAndRequired_nullMessage_assertFalse()
	{		
		assertFalse(TxUtil.isReliableAndTimelyRequested((Tx)null));
	}
	
	@Test
	public void testIsTimelyAndRequired_nullMimeMessage_assertFalse()
	{		
		assertFalse(TxUtil.isReliableAndTimelyRequested((MimeMessage)null));
	}
	
	@Test
	public void testIsTimelyAndRequired_emptyDetails_assertFalse()
	{
		Tx msg = new Tx(TxMessageType.IMF, new HashMap<String, TxDetail>());
		
		assertFalse(TxUtil.isReliableAndTimelyRequested(msg));
	}
	
	@Test
	public void testIsTimelyAndRequired_NoMNDOptionDetails_assertFalse()
	{
			
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		Tx msg = new Tx(TxMessageType.IMF, details);
		
		assertFalse(TxUtil.isReliableAndTimelyRequested(msg));
	}
	
	@Test
	public void testIsTimelyAndRequired_MDNOptionNotForTimely_assertFalse()
	{		
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.DISPOSITION_OPTIONS.getType(), new TxDetail(TxDetailType.DISPOSITION_OPTIONS, "X-NOT-TIMELY"));
		Tx msg = new Tx(TxMessageType.IMF, details);
		
		assertFalse(TxUtil.isReliableAndTimelyRequested(msg));
	}
	
	@Test
	public void testIsTimelyAndRequired_MDNOptionForTimely_assertTrue()
	{			
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.DISPOSITION_OPTIONS.getType(), new TxDetail(TxDetailType.DISPOSITION_OPTIONS, MDNStandard.DispositionOption_TimelyAndReliable));
		Tx msg = new Tx(TxMessageType.IMF, details);
		
		assertTrue(TxUtil.isReliableAndTimelyRequested(msg));
	}
	
	@Test
	public void testIsTimelyAndRequired_MDNOptionForTimelyMimeMessage_assertTrue() throws Exception
	{			
		MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		msg.addHeader(MDNStandard.Headers.DispositionNotificationOptions, MDNStandard.DispositionOption_TimelyAndReliable);
		msg.saveChanges();
		
		assertTrue(TxUtil.isReliableAndTimelyRequested(msg));
	}
	
	@Test
	public void testIsTimelyAndRequired_NoMDNOptionForTimelyMimeMessage_assertFalse() throws Exception
	{			
		MimeMessage msg = TestUtils.readMimeMessageFromFile("MessageWithAttachment.txt");
		assertFalse(TxUtil.isReliableAndTimelyRequested(msg));
	}
}
