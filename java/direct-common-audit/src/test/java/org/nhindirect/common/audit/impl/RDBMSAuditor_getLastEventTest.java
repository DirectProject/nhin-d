package org.nhindirect.common.audit.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;

import javax.management.openmbean.CompositeData;

import org.junit.Test;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.DefaultAuditContext;

public class RDBMSAuditor_getLastEventTest extends RDBMSAuditorBaseTest
{
	   @Test
		public void testGetLastEventTest_noContexts_lastEventRetrieved() throws Exception
		{
	    	
	    	final AuditEvent auditEvent = new AuditEvent("name1", "value1");

	    	this.auditor.audit("testPin", auditEvent, null);
	    	
	    	final CompositeData lastMessage = auditor.getLastEvent();
	    	
			assertNotNull(lastMessage);
			
			assertEquals(auditEvent.getName(), lastMessage.get("Event Name"));
			assertEquals(auditEvent.getType(), lastMessage.get("Event Type"));
			assertTrue(lastMessage.get("Event Id").toString().length() > 0);
			assertTrue(lastMessage.get("Event Time").toString().length() > 0);
			assertNotNull(lastMessage.get("Contexts"));
			String[] contexts = (String[])lastMessage.get("Contexts");
			assertEquals(1, contexts.length);

		}
	   
	   @Test
		public void testGetLastEventTest_withContexts_lastEventRetrieved() throws Exception
		{
	    	
	    	final AuditEvent auditEvent = new AuditEvent("name1", "value1");

			final DefaultAuditContext context1 = new DefaultAuditContext("name1", "value1");
			final DefaultAuditContext context2 = new DefaultAuditContext("name2", "value2");
	    	
	    	this.auditor.audit("testPin", auditEvent, Arrays.asList(context1, context2));
	    	
	    	final CompositeData lastMessage = auditor.getLastEvent();
	    	
			assertNotNull(lastMessage);
			
			assertEquals(auditEvent.getName(), lastMessage.get("Event Name"));
			assertEquals(auditEvent.getType(), lastMessage.get("Event Type"));
			assertTrue(lastMessage.get("Event Id").toString().length() > 0);
			assertTrue(lastMessage.get("Event Time").toString().length() > 0);
			assertNotNull(lastMessage.get("Contexts"));
			String[] contexts = (String[])lastMessage.get("Contexts");
			assertEquals(2, contexts.length);
			
			assertEquals("name1:value1", contexts[0]);
			assertEquals("name2:value2", contexts[1]);

		}	
	   
	   @Test
		public void testGetLastEventTest_noEntries_assertNull() throws Exception
		{
	    	
	    	final CompositeData lastMessage = auditor.getLastEvent();
	    	
			assertNull(lastMessage);

		}		   
}
