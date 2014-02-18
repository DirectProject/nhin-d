package org.nhindirect.common.audit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.nhindirect.common.audit.impl.LoggingAuditor;
import org.nhindirect.common.audit.impl.MultiProviderAuditor;
import org.nhindirect.common.audit.impl.NoOpAuditor;
import org.nhindirect.common.audit.module.ProviderAuditorModule;
import org.nhindirect.common.audit.provider.LoggingAuditorProvider;

import com.google.inject.Module;
import com.google.inject.Provider;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class AuditorFactoryTest 
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
	public void testCreateAuditor_SingleSPIImplementation_AssertCreatedSPIAuditor() throws Exception
	{
		setupSPIImplementation(NoOpAuditor.class);
		
		Auditor auditor = AuditorFactory.createAuditor();
		
		assertNotNull(auditor);
		assertTrue(auditor instanceof NoOpAuditor);		
	}		
	
	@Test
	public void testCreateAuditor_NoSPI_AssertCreatedDefaultAuditor()
	{
		Auditor auditor = AuditorFactory.createAuditor();
		
		assertNotNull(auditor);
		assertTrue(auditor instanceof LoggingAuditor);		
	}	
	

	
	@Test
	public void testCreateAuditor_MultiSPIImplementation_AssertCreatedSPIAuditor() throws Exception
	{
		setupSPIImplementation(NoOpAuditor.class);
		setupSPIImplementation(LoggingAuditor.class);		
		
		Auditor auditor = AuditorFactory.createAuditor();
		
		assertNotNull(auditor);
		assertTrue(auditor instanceof MultiProviderAuditor);		
	}		
	
	@Test
	public void testCreateAuditor_SingleProvider_AssertCreatedAuditor() throws Exception
	{
		LoggingAuditorProvider proviver = new LoggingAuditorProvider();
		
		Auditor auditor = AuditorFactory.createAuditor(proviver);
		
		assertNotNull(auditor);
		assertTrue(auditor instanceof LoggingAuditor);		
	}
	
	@Test
	public void testCreateAuditor_NullProvider_AssertException() throws Exception
	{		
		boolean exeptionOccured = false;
		
		try
		{
			AuditorFactory.createAuditor((Provider<Auditor>)null);
		}
		catch (IllegalArgumentException e)
		{
			exeptionOccured = true;
		}

		assertTrue(exeptionOccured);		
	}
	
	@Test
	public void testCreateAuditor_SingleModule_AssertCreatedAuditor() throws Exception
	{		
		LoggingAuditorProvider provider = new LoggingAuditorProvider();
		
		ProviderAuditorModule module = ProviderAuditorModule.create(provider);
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(module);
		
		Auditor auditor = AuditorFactory.createAuditor(modules);
		
		assertNotNull(auditor);
		assertTrue(auditor instanceof LoggingAuditor);			
	}	
	
	@Test
	public void testCreateAuditor_NullModule_AssertException() throws Exception
	{		
		boolean exeptionOccured = false;
		
		try
		{
			AuditorFactory.createAuditor((Collection<Module>)null);
		}
		catch (IllegalArgumentException e)
		{
			exeptionOccured = true;
		}

		assertTrue(exeptionOccured);			
	}	
	
	@Test
	public void testCreateAuditor_EmptyModule_AssertException() throws Exception
	{		
		boolean exeptionOccured = false;
		
		try
		{
			AuditorFactory.createAuditor(new ArrayList<Module>());
		}
		catch (IllegalArgumentException e)
		{
			exeptionOccured = true;
		}

		assertTrue(exeptionOccured);			
	}
}
