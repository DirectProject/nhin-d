package org.nhindirect.gateway.smtp.config.cert.impl;

import java.io.File;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.options.OptionsManagerUtils;

import junit.framework.TestCase;

public class ConfigServiceCertificateStore_getDefaultCachePolicyTest extends TestCase
{
	@Override
	public void setUp()
	{
		OptionsManagerUtils.clearOptionsManagerInstance();
		CertCacheFactory.getInstance().flushAll();
	}
	
	@Override
	public void tearDown()
	{
		OptionsManagerUtils.clearOptionsManagerOptions();
		CertCacheFactory.getInstance().flushAll();
	}
	
	
	public void testGetDefaultCachePolicyTest_useDefaultSettings_assertSettings() throws Exception
	{
		ConfigServiceCertificateStore store = new ConfigServiceCertificateStore(null);

		assertNotNull(store.cachePolicy);
		assertEquals(ConfigServiceCertificateStore.DEFAULT_WS_MAX_CAHCE_ITEMS, store.cachePolicy.getMaxItems());
		assertEquals(ConfigServiceCertificateStore.DEFAULT_WS_TTL, store.cachePolicy.getSubjectTTL());
	}
	
	public void testGetDefaultCachePolicyTest_useSettingsFromJVMParams_assertSettings() throws Exception
	{
		System.setProperty("org.nhindirect.stagent.cert.wsresolver.MaxCacheSize", "500");
		System.setProperty("org.nhindirect.stagent.cert.wsresolver.CacheTTL", "1800");
		
		try
		{
			ConfigServiceCertificateStore store = new ConfigServiceCertificateStore(null);
			assertNotNull(store.cachePolicy);
			assertEquals(500, store.cachePolicy.getMaxItems());
			assertEquals(1800, store.cachePolicy.getSubjectTTL());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cert.wsresolver.MaxCacheSize", "");
			System.setProperty("org.nhindirect.stagent.cert.wsresolver.CacheTTL", "");
		}

	}	
	
	public void testGetDefaultCachePolicyTest_useSettingsFromPropertiesFile_assertSettings() throws Exception
	{	
		File propFile = new File("./target/props/agentSettings.properties");
		if (propFile.exists())
			propFile.delete();
	
		System.setProperty("org.nhindirect.stagent.PropertiesFile", "./target/props/agentSettings.properties");
		
		OutputStream outStream = null;
	
		try
		{
			outStream = FileUtils.openOutputStream(propFile);
			outStream.write("org.nhindirect.stagent.cert.wsresolver.MaxCacheSize=1200\r\n".getBytes());
			outStream.write("org.nhindirect.stagent.cert.wsresolver.CacheTTL=900".getBytes());
			outStream.flush();
			
		}
		finally
		{
			IOUtils.closeQuietly(outStream);
		}
		
		try
		{
			ConfigServiceCertificateStore store = new ConfigServiceCertificateStore(null);
			assertNotNull(store.cachePolicy);
			assertEquals(1200, store.cachePolicy.getMaxItems());
			assertEquals(900, store.cachePolicy.getSubjectTTL());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cert.wsresolver.MaxCacheSize", "");
			System.setProperty("org.nhindirect.stagent.cert.wsresolver.CacheTTL", "");
			System.setProperty("org.nhindirect.stagent.PropertiesFile", "");
			propFile.delete();
		}

	}	
}
