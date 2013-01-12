package org.nhindirect.gateway.smtp;

import java.io.File;

import junit.framework.TestCase;

public class MessageProcessingSettingsTest extends TestCase 
{

	private static final File validSaveFolder = new File("./target/SaveMessageFolder");
	static final char[] invalidFileName;
	
	static
	{
		invalidFileName = new char[Character.MAX_VALUE];
		
		for (char i = 1; i < Character.MAX_VALUE; ++i)
		{
			invalidFileName[i - 1] = i;
		}
	}
	
	private static class ConcreteMessageProcessingSettings extends MessageProcessingSettings
	{
		
	}
	
	public void testConstructor()
	{
		ConcreteMessageProcessingSettings settings = new ConcreteMessageProcessingSettings();
		
		assertNull(settings.getSaveMessageFolder());
	}
	
	public void testSetSaveMessageFolder()
	{
		ConcreteMessageProcessingSettings settings = new ConcreteMessageProcessingSettings();
		settings.setSaveMessageFolder(validSaveFolder);
		
		assertEquals(validSaveFolder.getAbsolutePath(), settings.getSaveMessageFolder().getAbsolutePath());
		assertTrue(settings.hasSaveMessageFolder());
	}
	
	public void testSetSaveMessageFolder_InvalidFolderName_AssertException()
	{
		
		File invalidFile = new File(new String(invalidFileName));
		
		ConcreteMessageProcessingSettings settings = new ConcreteMessageProcessingSettings();
		
		boolean exceptionOccured = false;
		try
		{
			settings.setSaveMessageFolder(invalidFile);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
}
