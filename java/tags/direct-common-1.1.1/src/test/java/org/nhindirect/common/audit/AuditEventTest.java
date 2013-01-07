package org.nhindirect.common.audit;

import org.junit.Test;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

public class AuditEventTest 
{
	@Test
	public void testConstructAuditEvent() throws Exception
	{
		AuditEvent event = new AuditEvent("category", "type");
		
		assertNotNull(event);
	}
	
	@Test
	public void testGetName() throws Exception
	{
		AuditEvent event = new AuditEvent("category", "type");
		
		assertEquals("category", event.getName());
	}	
	
	@Test
	public void testGetType() throws Exception
	{
		AuditEvent event = new AuditEvent("category", "type");
		
		assertEquals("type", event.getType());
	}		
		
	
	@Test
	public void testConstructAuditEvent_EmptyName_AssertIllgalArgumentException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new AuditEvent("", "value");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testConstructAuditEvent_NullName_AssertIllgalArgumentException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new AuditEvent(null, "value");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testConstructAuditEvent_NullType_AssertIllgalArgumentException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new AuditEvent("category", null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testConstructAuditEvent_EmptyType_AssertIllgalArgumentException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new AuditEvent("category", "");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	

	@Test
	public void testEquals() throws Exception
	{
		AuditEvent event1 = new AuditEvent("category", "type");
		AuditEvent event2 = new AuditEvent("category", "type");

		
		assertTrue(event1.equals(event2));
	}	
	
	@Test
	public void testEquals_DifferentName_AssertNotEqual() throws Exception
	{
		AuditEvent event1 = new AuditEvent("category1", "type");
		AuditEvent event2 = new AuditEvent("category2", "type");

		
		assertFalse(event1.equals(event2));
	}		
	
	@Test
	public void testEquals_DifferentType_AssertNotEqual() throws Exception
	{
		AuditEvent event1 = new AuditEvent("category", "type1");
		AuditEvent event2 = new AuditEvent("category", "type2");

		
		assertFalse(event1.equals(event2));
	}		
	
	@Test
	public void testEquals_DifferentObject_AssertNotEqual() throws Exception
	{
		AuditEvent event = new AuditEvent("category", "type1");

		
		assertFalse(event.equals(event.toString()));
	}	
}