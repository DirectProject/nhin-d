package org.nhindirect.common.tx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TxDetailTest 
{
	@Test 
	public void testConstruct_defaultConstructor()
	{
		TxDetail detail = new TxDetail();
		
		assertEquals(TxDetailType.UNKNOWN.getType(), detail.getDetailName());
		assertTrue(detail.getDetailValue().isEmpty());
	}
	
	@Test 
	public void testConstruct_paramaterizedTypeConstructor()
	{
		TxDetail detail = new TxDetail(TxDetailType.FROM, "");
		
		assertEquals(TxDetailType.FROM.getType(), detail.getDetailName());
		assertTrue(detail.getDetailValue().isEmpty());
		
		detail = new TxDetail(TxDetailType.FROM, "value");
		
		assertEquals(TxDetailType.FROM.getType(), detail.getDetailName());
		assertEquals("value", detail.getDetailValue());
	}
	
	@Test 
	public void testConstruct_paramaterizedStringConstructor()
	{
		TxDetail detail = new TxDetail(TxDetailType.FROM.getType(), "");
		
		assertEquals(TxDetailType.FROM.getType(), detail.getDetailName());
		assertTrue(detail.getDetailValue().isEmpty());
		
		detail = new TxDetail(TxDetailType.FROM, "value");
		
		assertEquals(TxDetailType.FROM.getType(), detail.getDetailName());
		assertEquals("value", detail.getDetailValue());
	}
	
	@Test 
	public void testConstruct_paramaterizedStringConstructor_nullType()
	{
		boolean exceptionOccured = false;
		try
		{
			new TxDetail((String)null, "");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_paramaterizedStringConstructor_emptyType()
	{
		boolean exceptionOccured = false;
		try
		{
			new TxDetail("", "");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_paramaterizedStringConstructor_nullValue()
	{
		boolean exceptionOccured = false;
		try
		{
			new TxDetail(TxDetailType.FROM.getType(), null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_setGetName()
	{
		TxDetail detail = new TxDetail(TxDetailType.FROM.getType(), "");
		
		detail.setDetailName(TxDetailType.DISPOSITION.getType());
		
		assertEquals(TxDetailType.DISPOSITION.getType(), detail.getDetailName());
	}
	
	@Test 
	public void testConstruct_setName_nullName()
	{
		
		TxDetail detail = new TxDetail();
		
		boolean exceptionOccured = false;
		try
		{
			detail.setDetailName(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_setName_emptyName()
	{
		
		TxDetail detail = new TxDetail();
		
		boolean exceptionOccured = false;
		try
		{
			detail.setDetailName(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test 
	public void testConstruct_setGetValue()
	{
		TxDetail detail = new TxDetail(TxDetailType.FROM.getType(), "");
		
		detail.setDetailValue("value");
		
		assertEquals("value", detail.getDetailValue());
	}
	
	@Test 
	public void testConstruct_setValue_nullValue()
	{
		
		TxDetail detail = new TxDetail();
		
		boolean exceptionOccured = false;
		try
		{
			detail.setDetailValue(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
