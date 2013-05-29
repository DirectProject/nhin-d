package org.nhindirect.gateway.smtp.config;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhind.config.ConfigurationService;
import org.nhindirect.config.service.impl.ConfigurationServiceImplServiceSoapBindingStub;
import org.nhindirect.gateway.smtp.config.cert.impl.ConfigServiceCertificateStore;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.options.OptionsManagerUtils;

import junit.framework.TestCase;

public class WSSmtpAgentConfig_setTimeoutTest extends TestCase
{
	private static final String CONFIG_URL = "http://test/configservice";
	
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
		WSSmtpAgentConfig store = new WSSmtpAgentConfig(new URL(CONFIG_URL), null);

		ConfigurationService internalService = store.cfService.getConfigurationService();
		
		assertEquals(ConfigServiceCertificateStore.DEFAULT_WS_CONNECTION_TIMEOUT, ((ConfigurationServiceImplServiceSoapBindingStub) internalService).getTimeout());
	}
	
	public void testGetDefaultCachePolicyTest_useSettingsFromJVMParams_assertSettings() throws Exception
	{
		System.setProperty("org.nhindirect.stagent.cert.wsresolver.ConnectionTimeout", "60000");
		
		try
		{
			WSSmtpAgentConfig store = new WSSmtpAgentConfig(new URL(CONFIG_URL), null);
			ConfigurationService internalService = store.cfService.getConfigurationService();
			
			assertEquals(60000, ((ConfigurationServiceImplServiceSoapBindingStub) internalService).getTimeout());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cert.wsresolver.ConnectionTimeout", "");
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
			outStream.write("org.nhindirect.stagent.cert.wsresolver.ConnectionTimeout=12000\r\n".getBytes());
			outStream.flush();
			
		}
		finally
		{
			IOUtils.closeQuietly(outStream);
		}
		
		try
		{
			WSSmtpAgentConfig store = new WSSmtpAgentConfig(new URL(CONFIG_URL), null);
			ConfigurationService internalService = store.cfService.getConfigurationService();
			
			assertEquals(12000, ((ConfigurationServiceImplServiceSoapBindingStub) internalService).getTimeout());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cert.wsresolver.ConnectionTimeout", "");
			System.setProperty("org.nhindirect.stagent.PropertiesFile", "");
			propFile.delete();
		}

	}	
}
