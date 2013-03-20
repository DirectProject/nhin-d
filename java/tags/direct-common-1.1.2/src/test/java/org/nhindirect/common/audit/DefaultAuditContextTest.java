package org.nhindirect.common.audit;

import org.junit.Test;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class DefaultAuditContextTest 
{
	@Test
	public void testConstructAuditContext() throws Exception
	{
		DefaultAuditContext context = new DefaultAuditContext("test", "");
		
		assertNotNull(context);
	}
	
	@Test
	public void testGetName() throws Exception
	{
		DefaultAuditContext context = new DefaultAuditContext("name", "value");
		
		assertEquals("name", context.getContextName());
	}	
	
	@Test
	public void testGetValue() throws Exception
	{
		DefaultAuditContext context = new DefaultAuditContext("name", "value");
		
		assertEquals("value", context.getContextValue());
	}		
	
	@Test
	public void testGetEmptyValue() throws Exception
	{
		DefaultAuditContext context = new DefaultAuditContext("name", "");
		
		assertEquals("", context.getContextValue());
	}		
	
	@Test
	public void testConstructAuditContext_EmptyName_AssertIllgalArgumentException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new DefaultAuditContext("", "value");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testConstructAuditContext_NullName_AssertIllgalArgumentException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new DefaultAuditContext(null, "value");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testConstructAuditContext_NullValue_AssertIllgalArgumentException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new DefaultAuditContext("name", null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
	
	@Test
	public void testAuditContextToString() throws Exception
	{
		DefaultAuditContext context = new DefaultAuditContext("name", "value");
		assertEquals("name:value", context.toString());
	}	
}
