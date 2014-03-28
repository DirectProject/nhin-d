package org.nhindirect.common.audit.impl;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.DefaultAuditContext;
import org.nhindirect.common.audit.impl.LoggingAuditor;

public class LoggingAuditorTest 
{
	private static final String PRINCIPAL = "JUNITTEST";
	private static final AuditEvent UNIT_TEST_EVENT = new AuditEvent("name", "value");
	
	@Test
	public void testAuditEvent()
	{
		LoggingAuditor auditor = new LoggingAuditor();
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);
	}
	
	@Test
	public void testAudit_NullPrincipal_AssertExeption()
	{
		LoggingAuditor auditor = new LoggingAuditor();
		
		boolean exceptionOccured = false;
		try
		{
			auditor.audit(null, UNIT_TEST_EVENT);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testAuditCategoryAndMessage_EmptyPrincipal_AssertExeption()
	{
		LoggingAuditor auditor = new LoggingAuditor();
		
		boolean exceptionOccured = false;
		try
		{
			auditor.audit("", UNIT_TEST_EVENT);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testAudit_NullEvent_AssertExeption()
	{
		LoggingAuditor auditor = new LoggingAuditor();
		
		boolean exceptionOccured = false;
		try
		{
			auditor.audit(PRINCIPAL, null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
		
	
	@Test
	public void testAuditWithContext()
	{
		LoggingAuditor auditor = new LoggingAuditor();
		Collection<? extends AuditContext> ctx = Arrays.asList(new DefaultAuditContext("name", "value"));
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT, ctx);
	}	
}
