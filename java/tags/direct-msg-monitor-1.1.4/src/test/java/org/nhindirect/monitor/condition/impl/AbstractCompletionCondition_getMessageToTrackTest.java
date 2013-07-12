package org.nhindirect.monitor.condition.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxMessageType;

public class AbstractCompletionCondition_getMessageToTrackTest 
{
	
	static class ConditionAdapter extends AbstractCompletionCondition
	{///CLOVER:OFF
		public boolean isComplete(Collection<Tx> txs)
		{
			return false;
		}
		
		public Collection<String> getIncompleteRecipients(Collection<Tx> txs)
		{
			return Collections.emptyList();
		}
	}///CLOVER:ON
	
	
	@Test
	public void testGetMessageToTrack_nullTx_assertNull()
	{
		ConditionAdapter condition = new ConditionAdapter();
		
		assertNull(condition.getMessageToTrackInternal(null));
	}
	
	@Test
	public void testGetMessageToTrack_emptyTx_assertNull()
	{
		ConditionAdapter condition = new ConditionAdapter();
		
		assertNull(condition.getMessageToTrackInternal(new ArrayList<Tx>()));
	}
	
	@Test
	public void testGetMessageToTrack_noIMF_assertNull()
	{
		ConditionAdapter condition = new ConditionAdapter();
		
		Tx tx = new Tx(TxMessageType.DSN, new HashMap<String, TxDetail>());
		List<Tx> txs = Arrays.asList(tx);
		
		assertNull(condition.getMessageToTrackInternal(txs));
	}
	
	@Test
	public void testGetMessageToTrack_assertMessageFound()
	{
		ConditionAdapter condition = new ConditionAdapter();
		
		Tx tx = new Tx(TxMessageType.IMF, new HashMap<String, TxDetail>());
		List<Tx> txs = Arrays.asList(tx);
		
		Tx foundTx = condition.getMessageToTrackInternal(txs);
		
		assertEquals(tx, foundTx);
	}
}
