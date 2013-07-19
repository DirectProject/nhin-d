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
import org.nhindirect.common.audit.impl.MultiProviderAuditor;
import org.nhindirect.common.audit.impl.NoOpAuditor;

public class MultiProviderAuditorTest 
{
	private static final String PRINCIPAL = "JUNITTEST";
	private static final AuditEvent UNIT_TEST_EVENT = new AuditEvent("name", "value");	
	
	@Test
	public void testCreateAuditor_NullAuditors_AssertException()
	{
		
		boolean exceptionOccured = false;
		try
		{
			new MultiProviderAuditor(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testCreateAuditor_EmptyAuditors_AssertException()
	{
		
		boolean exceptionOccured = false;
		try
		{
			new MultiProviderAuditor(new ArrayList<Auditor>());
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);		
	}	
	
	@Test
	public void testAuditEvent()
	{
		Auditor auditor = new MultiProviderAuditor(Arrays.asList(new LoggingAuditor(), new NoOpAuditor()));
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);
	}
	
	@Test
	public void testAudit_NullPrincipal_AssertExeption()
	{
		Auditor auditor = new MultiProviderAuditor(Arrays.asList(new LoggingAuditor(), new NoOpAuditor()));
		
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
		Auditor auditor = new MultiProviderAuditor(Arrays.asList(new LoggingAuditor(), new NoOpAuditor()));
		
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
		Auditor auditor = new MultiProviderAuditor(Arrays.asList(new LoggingAuditor(), new NoOpAuditor()));
		
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
		Auditor auditor = new MultiProviderAuditor(Arrays.asList(new LoggingAuditor(), new NoOpAuditor()));
		Collection<? extends AuditContext> ctx = Arrays.asList(new DefaultAuditContext("name", "value"));
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT, ctx);
	}	
	
	@Test
	public void testAuditCategoryAndMessage_OneAuditorFails()
	{
		Auditor auditor = new MultiProviderAuditor(Arrays.asList(new LoggingAuditor(), new ExceptionAuditor()));
	
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);
	}		
}
