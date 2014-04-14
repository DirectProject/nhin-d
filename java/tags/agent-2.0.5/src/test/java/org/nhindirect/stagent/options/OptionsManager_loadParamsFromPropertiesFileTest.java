package org.nhindirect.stagent.options;

import java.io.File;
import java.io.OutputStream;
import java.util.UUID;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class OptionsManager_loadParamsFromPropertiesFileTest extends TestCase
{
	@Override
	public void setUp()
	{
		OptionsManager.destroyInstance();
	}
	
	@Override
	public void tearDown()
	{
		OptionsManager.getInstance().options.clear();

	}
	
	public void testloadParamsFromPropertiesFile_defaultPropertiesFile() throws Exception
	{
		File propFile = new File(OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		OutputStream outStream = null;
		
		
		try
		{
			outStream = FileUtils.openOutputStream(propFile);
			outStream.write("org.nhindirect.stagent.cryptography.JCEProviderName=SC".getBytes());
			
		}
		finally
		{
			IOUtils.closeQuietly(outStream);
		}
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);
		
		try
		{
			assertEquals("SC", param.getParamValue());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
			propFile.delete();
		}
	}
	
	public void testloadParamsFromPropertiesFile_defaultPropertiesFile_fileDoesNotExist() throws Exception
	{
		File propFile = new File(OptionsManager.DEFAULT_PROPERTIES_FILE);
		assertFalse(propFile.exists());
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);

		assertNull(param);
	
	}
	
	public void testloadParamsFromPropertiesFile_customPropertiesFile_fileDoesNotExist() throws Exception
	{
		File propFile = new File("./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);

		assertNull(param);
	
	}
	
	public void testloadParamsFromPropertiesFile_customPropertiesFile() throws Exception
	{
		File propFile = new File("./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		System.setProperty("org.nhindirect.stagent.PropertiesFile", "./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		
		OutputStream outStream = null;
		final String jvmPropValue = UUID.randomUUID().toString();
		
		try
		{
			
			outStream = FileUtils.openOutputStream(propFile);
			final String value = "org.nhindirect.stagent.cryptography.JCEProviderName=" + jvmPropValue;
			outStream.write(value.getBytes());
			
		}
		finally
		{
			IOUtils.closeQuietly(outStream);
		}
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);
		
		try
		{
			assertEquals(jvmPropValue, param.getParamValue());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.PropertiesFile", "");
			System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
			propFile.delete();
		}
	}
	
	public void testloadParamsFromPropertiesFile_customPropertiesFile_paramIsNotAKnownJVMProp() throws Exception
	{
		File propFile = new File("./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		System.setProperty("org.nhindirect.stagent.PropertiesFile", "./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		
		OutputStream outStream = null;
		final String jvmPropValue = UUID.randomUUID().toString();
		
		try
		{
			
			outStream = FileUtils.openOutputStream(propFile);
			final String value = "testProperty=" + jvmPropValue;
			outStream.write(value.getBytes());
			
		}
		finally
		{
			IOUtils.closeQuietly(outStream);
		}
		
		OptionsParameter param = OptionsManager.getInstance().getParameter("testProperty");
		
		try
		{
			assertEquals(jvmPropValue, param.getParamValue());
		}
		finally
		{
			System.setProperty("testProperty", "");
			System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
			propFile.delete();
		}
	}
	
	
	public void testloadParamsFromPropertiesFile_defaultPropertiesFile_JVMOverridesProperty() throws Exception
	{
		File propFile = new File(OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		OutputStream outStream = null;
		
		
		try
		{
			outStream = FileUtils.openOutputStream(propFile);
			outStream.write("org.nhindirect.stagent.cryptography.JCEProviderName=SC".getBytes());
			
		}
		finally
		{
			IOUtils.closeQuietly(outStream);
		}
		
		final String jvmPropValue = UUID.randomUUID().toString();
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", jvmPropValue);
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);
		
		try
		{
			assertEquals(jvmPropValue, param.getParamValue());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
			propFile.delete();
		}
	}
	
}
