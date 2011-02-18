package org.nhindirect.common.audit.impl;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import javax.management.openmbean.CompositeData;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.DefaultAuditContext;

public class FileAuditorTest 
{
	private static final String PRINCIPAL = "JUNITTEST";
	private static final AuditEvent UNIT_TEST_EVENT = new AuditEvent("name", "value");	
	
	private static final char fileSep = File.separatorChar;
	
	private static File auditFile;
	

	static
	{		
		auditFile = new File("target" + fileSep + "testAuditFile.txt");
		
		if (auditFile.exists())
			auditFile.delete();
	}

	@Test
	public void testCreateAuditor_NullFile_AssertException()
	{
		boolean exceptionOccured = false;
		try
		{
			FileAuditor auditor = new FileAuditor(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}

	@Test
	public void testCreateAuditor_IllegalFileName_AssertException()
	{
		boolean exceptionOccured = false;
		try
		{
			FileAuditor auditor = new FileAuditor(new File(".."));
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}

	@Test
	public void testCreateAuditor_IllegalFileName2_AssertException()
	{
		boolean exceptionOccured = false;
		try
		{
			FileAuditor auditor = new FileAuditor(new File("!-@$*?\n\r/\0"));
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testAudit()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);
	}
	
	@Test
	public void testAudit_EmptyPrincipal_AssertExeption()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		
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
	public void testAudit_NullPrincipal_AssertExeption()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		
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
	public void testAudit_NullEvent_AssertExeption()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		
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
	public void testAuditCategoryWithContext()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		Collection<? extends AuditContext> ctx = Arrays.asList(new DefaultAuditContext("name", "value"));
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT, ctx);
	}	
	
	@Test
	public void testAuditCategoryWithContextContext_EmptyContext()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT, new ArrayList<AuditContext>());	
		
	}	
	
	@Test
	public void testAuditCategoryAndContext_NullContext()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT, null);			
	}		
	
	
	@Test
	public void testMalformedAuditFile_AssertFileReset() throws Exception	
	{
		FileUtils.deleteQuietly(auditFile);
		FileOutputStream stream = FileUtils.openOutputStream(auditFile);
		for (int i = 0; i < 2048; ++i)
			stream.write(65);
		
		stream.close();
		
		FileAuditor auditor = new FileAuditor(auditFile);

		assertEquals(0, auditor.getEventCount().intValue());
		assertNull(auditor.getLastEvent());
		
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);

		
		assertEquals(2, auditor.getEventCount().intValue());
		assertNotNull(auditor.getLastEvent());
	}		
	
	
	@Test 
	public void testClear()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);
		
		auditor.clear();
		assertEquals(0, auditor.getEventCount().intValue());
		assertNull(auditor.getLastEvent());
	}

	@Test 
	public void testGetEventCount_AssertNoRecords()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.clear();
		assertEquals(0, auditor.getEventCount().intValue());
		assertNull(auditor.getLastEvent());
	}	

	@Test 
	public void testGetEventCount_AssertRecordsExist()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);
		auditor.audit(PRINCIPAL, UNIT_TEST_EVENT);

		
		assertEquals(2, auditor.getEventCount().intValue());
		assertNotNull(auditor.getLastEvent());
	}	

	@Test 
	public void testGetLastEvent_AssertNoRecords()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		
		auditor.clear();		
		assertNull(auditor.getLastEvent());
	}
	
	@Test 
	public void testGetLastEvent_AssertRecordContent()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		
		AuditEvent event1 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		AuditEvent event2 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		
		auditor.audit(PRINCIPAL, event1);
		auditor.audit(PRINCIPAL, event2);
		
		assertNotNull(auditor.getLastEvent());
		
		CompositeData lastMessage = auditor.getLastEvent();
		assertEquals(event2.getName(), lastMessage.get("Event Name"));
		assertEquals(event2.getType(), lastMessage.get("Event Type"));
		assertTrue(lastMessage.get("Event Id").toString().length() > 0);
		assertTrue(lastMessage.get("Event Time").toString().length() > 0);
		assertNotNull(lastMessage.get("Contexts"));
	}		
	
	@Test 
	public void testGetLastEvent_AssertRecordContentAndContext()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		
		AuditEvent event1 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		AuditEvent event2 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		
		DefaultAuditContext context1 = new DefaultAuditContext("name1", "value1");
		DefaultAuditContext context2 = new DefaultAuditContext("name2", "value2");
		
		auditor.audit(PRINCIPAL, event1);
		auditor.audit(PRINCIPAL, event2, Arrays.asList(context1, context2));
		
		
		assertNotNull(auditor.getLastEvent());
		
		CompositeData lastMessage = auditor.getLastEvent();
		assertEquals(event2.getName(), lastMessage.get("Event Name"));
		assertEquals(event2.getType(), lastMessage.get("Event Type"));
		assertTrue(lastMessage.get("Event Id").toString().length() > 0);
		assertTrue(lastMessage.get("Event Time").toString().length() > 0);
		assertNotNull(lastMessage.get("Contexts"));
		String[] contexts = (String[])lastMessage.get("Contexts");
		assertEquals(2, contexts.length);
		
		assertEquals("name1:value1", contexts[0]);
		assertEquals("name2:value2", contexts[1]);
	}		
	
	@Test 
	public void testGetEvents_AssertGotAllRecordsRequested()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		
		AuditEvent event1 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		AuditEvent event2 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		
		DefaultAuditContext context1 = new DefaultAuditContext("name1", "value1");
		DefaultAuditContext context2 = new DefaultAuditContext("name2", "value2");
		
		auditor.audit(PRINCIPAL, event1);
		auditor.audit(PRINCIPAL, event2, Arrays.asList(context1, context2));
		
		CompositeData[] events = auditor.getEvents(2);
		
		assertNotNull(events);
		assertEquals(2, events.length);
		
		CompositeData lastMessage = events[0];
		assertEquals(event2.getName(), lastMessage.get("Event Name"));
		assertEquals(event2.getType(), lastMessage.get("Event Type"));
		assertTrue(lastMessage.get("Event Id").toString().length() > 0);
		assertTrue(lastMessage.get("Event Time").toString().length() > 0);
		assertNotNull(lastMessage.get("Contexts"));
		String[] contexts = (String[])lastMessage.get("Contexts");
		assertEquals(2, contexts.length);
		
		assertEquals("name1:value1", contexts[0]);
		assertEquals("name2:value2", contexts[1]);
	}		
	
	@Test 
	public void testGetEvents_RequestMoreThanAvailable_AssertGotAllAvailableRecords()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.clear();
		
		AuditEvent event1 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		AuditEvent event2 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		
		DefaultAuditContext context1 = new DefaultAuditContext("name1", "value1");
		DefaultAuditContext context2 = new DefaultAuditContext("name2", "value2");
		
		auditor.audit(PRINCIPAL, event1);
		auditor.audit(PRINCIPAL, event2, Arrays.asList(context1, context2));
		
		CompositeData[] events = auditor.getEvents(5);
		
		assertNotNull(events);
		assertEquals(2, events.length);
		
		CompositeData lastMessage = events[0];
		assertEquals(event2.getName(), lastMessage.get("Event Name"));
		assertEquals(event2.getType(), lastMessage.get("Event Type"));
		assertTrue(lastMessage.get("Event Id").toString().length() > 0);
		assertTrue(lastMessage.get("Event Time").toString().length() > 0);
		assertNotNull(lastMessage.get("Contexts"));
		String[] contexts = (String[])lastMessage.get("Contexts");
		assertEquals(2, contexts.length);
		
		assertEquals("name1:value1", contexts[0]);
		assertEquals("name2:value2", contexts[1]);
	}		
	
	@Test 
	public void testGetEvents_RequestLessThanAvailable_AssertGotOnlyRecords()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.clear();
		
		AuditEvent event1 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		AuditEvent event2 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		
		DefaultAuditContext context1 = new DefaultAuditContext("name1", "value1");
		DefaultAuditContext context2 = new DefaultAuditContext("name2", "value2");
		
		auditor.audit(PRINCIPAL, event1);
		auditor.audit(PRINCIPAL, event2, Arrays.asList(context1, context2));
		
		CompositeData[] events = auditor.getEvents(1);
		
		assertNotNull(events);
		assertEquals(1, events.length);
		
		CompositeData lastMessage = events[0];
		assertEquals(event2.getName(), lastMessage.get("Event Name"));
		assertEquals(event2.getType(), lastMessage.get("Event Type"));
		assertTrue(lastMessage.get("Event Id").toString().length() > 0);
		assertTrue(lastMessage.get("Event Time").toString().length() > 0);
		assertNotNull(lastMessage.get("Contexts"));
		String[] contexts = (String[])lastMessage.get("Contexts");
		assertEquals(2, contexts.length);
		
		assertEquals("name1:value1", contexts[0]);
		assertEquals("name2:value2", contexts[1]);
	}		
	
	@Test 
	public void testGetEvents_NoRecordsAvailable_NoRecordsFound()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.clear();
		
		
		CompositeData[] events = auditor.getEvents(1);
		
		assertNull(events);
	}	
	
	@Test 
	public void testGetEvents_NoRecordsRequested_ReturnedRecords()
	{
		FileAuditor auditor = new FileAuditor(auditFile);
		auditor.clear();
		
		AuditEvent event1 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		AuditEvent event2 = new AuditEvent("Category" + UUID.randomUUID(), "type");
		
		DefaultAuditContext context1 = new DefaultAuditContext("name1", "value1");
		DefaultAuditContext context2 = new DefaultAuditContext("name2", "value2");
		
		auditor.audit(PRINCIPAL, event1);
		auditor.audit(PRINCIPAL, event2, Arrays.asList(context1, context2));
		
		CompositeData[] events = auditor.getEvents(0);
		
		assertNull(events);

	}		
}
