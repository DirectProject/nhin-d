package org.nhindirect.monitor.condition.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.util.TestUtils;

public class GeneralCompletionCondition_isCompleteTest 
{
	
	@Test
	public void testIsComplete_nullTxs_assertFalse()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		assertFalse(condition.isComplete(null));
	}
	
	@Test
	public void testIsComplete_nullEmptyTx_assertFalse()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		assertFalse(condition.isComplete(new ArrayList<Tx>()));
	}
	
	@Test
	public void testIsComplete_noMessageToTrack_assertFalse()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		Tx tx = new Tx(TxMessageType.DSN, new HashMap<String, TxDetail>());
		List<Tx> txs = Arrays.asList(tx);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_noFinalRecips_assertFalse()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		
		String msgId = UUID.randomUUID().toString();
		details.put(TxDetailType.MSG_ID.getType(), new TxDetail(TxDetailType.MSG_ID, msgId));
		Tx tx = new Tx(TxMessageType.IMF, details);
		List<Tx> txs = Arrays.asList(tx);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_finalMDNRecipNotAnOriginalRecips_assertFalse()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "ah4626@direct.securehealthemail.com", 
				"gm2552@cerner.com", "ah4626@direct.securehealthemail.com");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_finalDNSRecipNotAnOriginalRecips_assertFalse()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// DSN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "ah4626@direct.securehealthemail.com", 
				"gm2552@cerner.com", "ah4626@direct.securehealthemail.com");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_MDNMessageReceived_assertTrue()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertTrue(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_MDNMessageReceived_plusNotationOnMDNRecip_rfc822NotactionOnFinalRecip_assertTrue()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552+readreciept@cerner.com", "rfc822; gm2552@direct.securehealthemail.com");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertTrue(condition.isComplete(txs));
	}
	
	
	@Test
	public void testIsComplete_DSNMessageReceived_assertTrue()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertTrue(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_DSNMessageReceived_plusNotationOnDSNRecip_rfc822NotactionOnFinalRecip_assertTrue()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552+readreciept@cerner.com", "rfc822; gm2552@direct.securehealthemail.com");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertTrue(condition.isComplete(txs));
	}
}
