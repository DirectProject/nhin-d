package org.nhindirect.common.audit.provider;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import org.nhindirect.common.audit.impl.FileAuditor;

public class FileAuditorProviderTest 
{
	private static final String testFileName = "./target/FileAuditProviderTest.log";
	
	@Test
	public void testCreateProviderFromString()
	{
		FileAuditorProvider provider = new FileAuditorProvider(testFileName);
		
		assertNotNull(provider.get());
		assertTrue(provider.get() instanceof FileAuditor);		
	}
	
	@Test
	public void testCreateProviderFromString_NullFileName_AssertException()
	{
		boolean exceptionOccured = false;
		try
		{
			new FileAuditorProvider((String)null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testCreateProviderFromString_EmptyFileName_AssertException()
	{
		boolean exceptionOccured = false;
		try
		{
			new FileAuditorProvider("");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
	
	@Test
	public void testCreateProviderFromFile()
	{
		FileAuditorProvider provider = new FileAuditorProvider(new File(testFileName));
		
		assertNotNull(provider.get());
		assertTrue(provider.get() instanceof FileAuditor);		
	}	

	@Test
	public void testCreateProviderFromFile_NullFile_AssertException()
	{
		boolean exceptionOccured = false;
		try
		{
			new FileAuditorProvider((File)null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
}
