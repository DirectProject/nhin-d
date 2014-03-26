package org.nhindirect.monitor.condition.impl;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.util.TestUtils;

public class GeneralCompletionCondition_getIncompleteRecipientsEmptyTxsTest 
{
	@Test
	public void testIsComplete_nullTxs_assertEmptyList()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		Collection<String> recips = condition.getIncompleteRecipients(null);
		
		assertEquals(0, recips.size());
	}
	
	@Test
	public void testIsComplete_nullTxs_emptyTxs()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		Collection<String> recips = condition.getIncompleteRecipients(new ArrayList<Tx>());
		
		assertEquals(0, recips.size());
	}
	
	@Test
	public void testIsComplete_noMessageToTrack_emptyTxs()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.DSN, "", UUID.randomUUID().toString(), "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");
		Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(0, recips.size());
	}
	
	@Test
	public void testIsComplete_noRecips_emptyTxs()
	{
		GeneralCompletionCondition condition = new GeneralCompletionCondition();
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.IMF, "", UUID.randomUUID().toString(), "gm2552@cerner.com", "", "");
		Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(0, recips.size());
	}
}
