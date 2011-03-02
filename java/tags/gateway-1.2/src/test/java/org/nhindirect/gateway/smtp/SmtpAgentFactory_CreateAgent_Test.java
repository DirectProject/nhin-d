package org.nhindirect.gateway.smtp;

import java.net.URL;
import java.util.Arrays;

import junit.framework.TestCase;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.nhindirect.common.audit.impl.FileAuditor;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.provider.XMLSmtpAgentConfigProvider;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.MockNHINDAgent;
import org.nhindirect.stagent.module.FileAuditorModule;
import org.nhindirect.stagent.provider.MockNHINDAgentProvider;

import com.google.inject.Provider;


public class SmtpAgentFactory_CreateAgent_Test extends TestCase
{

	public void testCreateDefaultAgent_XMLConfiguration() throws Exception
	{
		SmtpAgent agent = SmtpAgentFactory.createAgent(new URL("file://" + TestUtils.getTestConfigFile("ValidConfig.xml")));
		
		assertNotNull(agent);
		assertNotNull(agent.getAgent());
	}	
	
	public void testCreateDefaultAgent_InvalidXMLConfiguration_AssertException() throws Exception
	{
		
		boolean exceptionOccured = false;
		try
		{
			SmtpAgentFactory.createAgent(new URL("file://" + TestUtils.getTestConfigFile("DoesNotExistConfig.xml")));
		}
		catch (SmtpAgentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}	
	
	public void testCreateDefaultAgent_XMLConfigurationProvider() throws Exception
	{
			Provider<SmtpAgentConfig> configProvider = new XMLSmtpAgentConfigProvider(TestUtils.getTestConfigFile("ValidConfig.xml"), null);
			
			SmtpAgent agent = SmtpAgentFactory.createAgent(null, configProvider, null);
			assertNotNull(agent);
			assertNotNull(agent.getAgent());
	}	
	
	public void testCreateDefaultAgent_XMLConfigurationProvider_MockAgent() throws Exception
	{
			Provider<SmtpAgentConfig> configProvider = new XMLSmtpAgentConfigProvider(TestUtils.getTestConfigFile("ValidConfig.xml"), 
					new MockNHINDAgentProvider(Arrays.asList("mydomain")));
			
			SmtpAgent agent = SmtpAgentFactory.createAgent(null, configProvider, 
					new MockNHINDAgentProvider(Arrays.asList("mydomain")));
			
			assertNotNull(agent);
			assertNotNull(agent.getAgent());
			assertTrue(agent.getAgent() instanceof MockNHINDAgent);
	}		
	
	public void testCreateDefaultAgent_XMLConfigurationProvider_AuditorModule() throws Exception
	{
			Provider<SmtpAgentConfig> configProvider = new XMLSmtpAgentConfigProvider(TestUtils.getTestConfigFile("ValidConfig.xml"), 
					new MockNHINDAgentProvider(Arrays.asList("mydomain")));
			
			SmtpAgent agent = SmtpAgentFactory.createAgent(null, configProvider, 
					new MockNHINDAgentProvider(Arrays.asList("mydomain")), Arrays.asList(FileAuditorModule.create("target/AuditFile.txt")));
			
			assertNotNull(agent);
			assertNotNull(agent.getAgent());
			assertTrue(agent.getAgent() instanceof MockNHINDAgent);
			
			DefaultSmtpAgent smtpAgent = (DefaultSmtpAgent)agent;
			assertNotNull(smtpAgent.getAuditor());
			assertTrue(smtpAgent.getAuditor() instanceof FileAuditor);
	}		
}
