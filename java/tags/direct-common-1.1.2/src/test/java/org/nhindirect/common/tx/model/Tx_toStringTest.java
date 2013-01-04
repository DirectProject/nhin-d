package org.nhindirect.common.tx.model;

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class Tx_toStringTest 
{
	@Test 
	public void testToString_unknownTypeEmptyDetails()
	{
		Tx tx = new Tx();
		
		assertTrue(tx.toString().startsWith("TxType: UNKNOWN"));
		assertTrue(tx.toString().endsWith("No Details"));
	}
	
	@Test 
	public void testToString_IMFtypeMultipleDetails()
	{

	
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		details.put(TxDetailType.RECIPIENTS.getType(), new TxDetail(TxDetailType.RECIPIENTS, "you@test.com"));
		
		Tx tx = new Tx(TxMessageType.IMF, details);
		
		assertTrue(tx.toString().startsWith("TxType: IMF"));
		assertTrue(tx.toString().contains("you@test.com"));		
		assertTrue(tx.toString().contains("me@test.com"));		
	}
}
