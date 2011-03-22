package org.nhindirect.common.audit.provider;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.impl.LoggingAuditor;
import org.nhindirect.common.audit.impl.MultiProviderAuditor;
import org.nhindirect.common.audit.impl.NoOpAuditor;
import org.nhindirect.common.audit.provider.SPIAuditorProvider;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

public class SPIAuditorProviderTest 
{
	private static final char fileSep = File.separatorChar;  
	
	private static final String resourceDirLoc = "." + fileSep + "target" + fileSep + "test-classes" + fileSep + "META-INF" + fileSep + "services" + fileSep;
	private static final String cloverResourceDirLoc = "." + fileSep + "target" + fileSep + "clover" + fileSep + "test-classes" + 
		fileSep + "META-INF" + fileSep + "services" + fileSep;

	
	private static final String SPIFileName = "org.nhindirect.common.audit.Auditor";	
	
	@Before
	@After
	public void cleanUpSPIFiles()
	{
		File spiFile = new File(resourceDirLoc + SPIFileName);
		if (spiFile.exists())
			spiFile.delete();
		
		spiFile = new File(cloverResourceDirLoc + SPIFileName);
		if (spiFile.exists())
			spiFile.delete();		
	}
	
	private void setupSPIImplementation(Class<?> clazz) throws Exception
	{
		File spiFile = new File(resourceDirLoc + SPIFileName);		
		
		String dataToWrite = "";
		if (spiFile.exists())
			dataToWrite = FileUtils.readFileToString(spiFile);
		
		if (!dataToWrite.isEmpty())
			dataToWrite += "\r\n" + clazz.getName();
		else
			dataToWrite = clazz.getName();
		
		FileUtils.writeStringToFile(spiFile, dataToWrite);
		
		spiFile = new File(cloverResourceDirLoc + SPIFileName);		
		FileUtils.writeStringToFile(spiFile, dataToWrite);		
	}	
	
	@Test
	public void testCreateSingleAuditorFromSPI_AssertAuditorInstanceOf() throws Exception
	{
		setupSPIImplementation(LoggingAuditor.class);
		
		SPIAuditorProvider prov = new SPIAuditorProvider();
		
		assertTrue(prov.isImplementationAvailable());
		assertNotNull(prov.get());
		assertTrue(prov.get() instanceof LoggingAuditor);
	}
	
	@Test
	public void testCreateSingleAuditorFromSPI_AssertMutliProviderAuditorCreated() throws Exception
	{
		setupSPIImplementation(LoggingAuditor.class);
		setupSPIImplementation(NoOpAuditor.class);
		
		SPIAuditorProvider prov = new SPIAuditorProvider();
		
		assertTrue(prov.isImplementationAvailable());
		assertNotNull(prov.get());
		assertTrue(prov.get() instanceof MultiProviderAuditor);
	}
	
	@Test
	public void testCreateSingleAuditorFromSPI_NullConstructor_AssertMutliProviderAuditorCreated() throws Exception
	{
		setupSPIImplementation(LoggingAuditor.class);
		setupSPIImplementation(NoOpAuditor.class);
		
		SPIAuditorProvider prov = new SPIAuditorProvider(null);
		
		assertTrue(prov.isImplementationAvailable());
		assertNotNull(prov.get());
		assertTrue(prov.get() instanceof MultiProviderAuditor);
	}	
	
	@Test
	public void testCreateSingleAuditorFromSPI_NonNullConstructor_AssertMutliProviderAuditorCreated() throws Exception
	{
		setupSPIImplementation(LoggingAuditor.class);
		setupSPIImplementation(NoOpAuditor.class);
		
		SPIAuditorProvider prov = new SPIAuditorProvider(Auditor.class.getClassLoader());
		
		assertTrue(prov.isImplementationAvailable());
		assertNotNull(prov.get());
		assertTrue(prov.get() instanceof MultiProviderAuditor);
	}		
	
	@Test
	public void testNoSPIAvailable_AssertNullAuditor() throws Exception
	{		
		SPIAuditorProvider prov = new SPIAuditorProvider();
		
		assertFalse(prov.isImplementationAvailable());
		assertNull(prov.get());

	}		
}
