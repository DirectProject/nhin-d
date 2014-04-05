package org.nhindirect.common.tx.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TxTest 
{
	@Test 
	public void testConstruct_defaultConstructor()
	{
		Tx tx = new Tx();
		
		assertEquals(TxMessageType.UNKNOWN, tx.getMsgType());
		assertNotNull(tx.getDetails());
		assertEquals(0, tx.getDetails().values().size());
	}
	
	@Test 
	public void testConstruct_parameterizedConstructor()
	{
		Tx tx = new Tx(TxMessageType.DSN, new HashMap<String, TxDetail>());
		
		assertEquals(TxMessageType.DSN, tx.getMsgType());
		assertNotNull(tx.getDetails());
		assertEquals(0, tx.getDetails().values().size());
	}
	
	@Test 
	public void testConstruct_parameterizedConstructor_nullDetails()
	{
		boolean exceptionOccured = false;
		try
		{
			new Tx(TxMessageType.DSN, null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_parameterizedConstructor_nullType()
	{
		boolean exceptionOccured = false;
		try
		{
			new Tx(null, new HashMap<String, TxDetail>());
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_setGetMessageType()
	{
		Tx tx = new Tx();
		
		assertEquals(TxMessageType.UNKNOWN, tx.getMsgType());

		tx.setMsgType(TxMessageType.IMF);
		assertEquals(TxMessageType.IMF, tx.getMsgType());
	}
	
	@Test 
	public void testConstruct_setType_nullType()
	{
		Tx tx = new Tx();
		boolean exceptionOccured = false;
		try
		{
			tx.setMsgType(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_setGetDetails()
	{
		Tx tx = new Tx();
		
		assertEquals(TxMessageType.UNKNOWN, tx.getMsgType());

		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		
		tx.setDetails(details);
		assertEquals(1, tx.getDetails().size());
		assertEquals(details, tx.getDetails());
	}
	
	@Test 
	public void testConstruct_setDetails_nullDetails()
	{
		Tx tx = new Tx();
		boolean exceptionOccured = false;
		try
		{
			tx.setDetails(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_getDetailByType()
	{
		Tx tx = new Tx();
		
		assertEquals(TxMessageType.UNKNOWN, tx.getMsgType());

		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		
		tx.setDetails(details);
		assertNotNull(tx.getDetail(TxDetailType.FROM.getType()));
		assertNull(tx.getDetail(TxDetailType.RECIPIENTS.getType()));
		assertNull(tx.getDetail((String)null));
		assertNull(tx.getDetail(""));
		
	}
	
	@Test 
	public void testConstruct_getDetailByEnum()
	{
		Tx tx = new Tx();
		
		assertEquals(TxMessageType.UNKNOWN, tx.getMsgType());

		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, "me@test.com"));
		
		tx.setDetails(details);
		assertNotNull(tx.getDetail(TxDetailType.FROM));
		assertNull(tx.getDetail(TxDetailType.RECIPIENTS));
		assertNull(tx.getDetail((TxDetailType)null));
		
	}
}
