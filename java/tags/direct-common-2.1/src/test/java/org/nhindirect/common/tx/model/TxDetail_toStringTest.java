package org.nhindirect.common.tx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TxDetail_toStringTest 
{
	@Test 
	public void toStringTest_unknownEmptyDetail()
	{
		TxDetail detail = new TxDetail();
		
		assertEquals(detail.getDetailName() + "\r\n", detail.toString());
		
	}
	
	@Test 
	public void toStringTest_nonEmptyDetailsDetail()
	{
		TxDetail detail = new TxDetail(TxDetailType.FROM, "gm2552@cerner.com");
		
		assertTrue(detail.toString().startsWith(detail.getDetailName()));
		assertTrue(detail.toString().endsWith(detail.getDetailValue()));
	}
}
