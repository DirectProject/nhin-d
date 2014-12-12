package org.nhindirect.common.audit;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.nhindirect.common.audit.provider.RDBMSAuditorProvider;

public class RDBMSAuditorProviderTest 
{
	@Test
	public void testCreateWithDefaultConfigFile_assertCreated() throws Exception
	{
		final RDBMSAuditorProvider provider = new RDBMSAuditorProvider();
		
		assertNotNull(provider.get());
	}
	
	@Test
	public void testCreateWithSpecificFile_assertCreated() throws Exception
	{
		final RDBMSAuditorProvider provider = new RDBMSAuditorProvider("auditStore.xml");
		
		assertNotNull(provider.get());
	}	
	
	@Test
	public void testCreateWithUnknownFile_assertException() throws Exception
	{
		boolean exceptionOccured = false;
		try
		{
			final RDBMSAuditorProvider provider = new RDBMSAuditorProvider("auditStoreBogus.xml");
			provider.get();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;	
		}
		
		assertTrue(exceptionOccured);
	}		
	
	
	@Test
	public void testCreateWithSpecificFile_assertCreatedAndWriteEvent() throws Exception
	{
		final RDBMSAuditorProvider provider = new RDBMSAuditorProvider("auditStore.xml");
		
		final Auditor auditor = provider.get();
		assertNotNull(auditor);
    	final AuditEvent auditEvent = new AuditEvent("name1", "value1");
    	
    	auditor.audit("testPin", auditEvent, null);
    	
	}	
}
