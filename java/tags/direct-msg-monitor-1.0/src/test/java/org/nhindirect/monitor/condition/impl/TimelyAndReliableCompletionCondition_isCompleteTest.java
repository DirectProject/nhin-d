package org.nhindirect.monitor.condition.impl;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.mail.dsn.DSNStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.util.TestUtils;

public class TimelyAndReliableCompletionCondition_isCompleteTest 
{
	@Test
	public void testIsComplete_nullTxs_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		assertFalse(condition.isComplete(null));
	}
	
	@Test
	public void testIsComplete_nullEmptyTx_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		assertFalse(condition.isComplete(new ArrayList<Tx>()));
	}
	
	@Test
	public void testIsComplete_noMessageToTrack_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		Tx tx = new Tx(TxMessageType.DSN, new HashMap<String, TxDetail>());
		List<Tx> txs = Arrays.asList(tx);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_noFinalRecips_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		
		String msgId = UUID.randomUUID().toString();
		details.put(TxDetailType.MSG_ID.getType(), new TxDetail(TxDetailType.MSG_ID, msgId));
		Tx tx = new Tx(TxMessageType.IMF, details);
		List<Tx> txs = Arrays.asList(tx);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_MDNDispotionNull_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", "");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_FinalRicipNull_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message
		Tx mdnMessage = TestUtils.makeReliableMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "", "", MDNStandard.Disposition_Processed);
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_MDNDispatched_nullOptionsHeader_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message, no reliable headers
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Dispatched);
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_MDNDispatched_nonReliableDispotionOption_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message, no reliable headers
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.MDN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", MDNStandard.Disposition_Dispatched, "X-NON-RELIABLEs");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}	
	
	@Test
	public void testIsComplete_noDSNAction_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message, no reliable headers
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", "");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_nonFailedDSNAction_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message, no reliable headers
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "gm2552@direct.securehealthemail.com", DSNStandard.DSNAction.DELAYED.toString(), "");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_failedDSNAction_finalRecipNotInOriginalMessage_assertFalse()
	{
		TimelyAndReliableCompletionCondition condition = new TimelyAndReliableCompletionCondition();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, originalMessageId, "", "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");

		// MDN to original message, no reliable headers
		Tx mdnMessage = TestUtils.makeMessage(TxMessageType.DSN, UUID.randomUUID().toString(), originalMessageId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "me@test.com", DSNStandard.DSNAction.FAILED.toString(), "");
		
		List<Tx> txs = Arrays.asList(originalMessage, mdnMessage);
		
		assertFalse(condition.isComplete(txs));
	}
}
