package org.nhindirect.monitor.condition.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.condition.TxCompletionCondition;
import org.nhindirect.monitor.util.TestUtils;

public class VariableCompletionCondition_getIncompleteRecipientsEmptyTxsTest 
{
	@Test
	public void testIsComplete_noMessageToTrack_emptyTxs()
	{
		TxCompletionCondition cond = mock(TxCompletionCondition.class);
		
		VariableCompletionCondition condition = new VariableCompletionCondition(cond, cond);
		
		Tx originalMessage = TestUtils.makeMessage(TxMessageType.DSN, "", UUID.randomUUID().toString(), "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "");
		Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(0, recips.size());
	}
}
