package org.nhindirect.common.audit;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import javax.persistence.EntityManagerFactory;

import org.junit.Test;
import org.nhindirect.common.audit.provider.RDBMSAuditorProvider;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
			new RDBMSAuditorProvider("auditStoreBogus.xml");
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;	
		}
		
		assertTrue(exceptionOccured);
	}		
	
	@Test
	public void testCreateWithEntityManager_assertCreated() throws Exception
	{
		
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("auditStore.xml");
		
		
		final EntityManagerFactory factory = ctx.getBean(EntityManagerFactory.class);

		
		final RDBMSAuditorProvider provider = new RDBMSAuditorProvider(factory.createEntityManager());
		
		assertNotNull(provider.get());
	}	
}
