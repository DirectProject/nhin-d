package org.nhindirect.gateway.smtp;

import static org.mockito.Mockito.mock;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;

import junit.framework.TestCase;

public class GatewayState_startSettingsManagerTest extends TestCase
{
	public void testStartSettingsManager_nullAgent_assertException() throws Exception
	{
		
		GatewayState instance = GatewayState.getInstance();
		
		if (instance.isAgentSettingManagerRunning())
			instance.stopAgentSettingsManager();
		
		instance.setSmptAgentConfig(mock(SmtpAgentConfig.class));
		instance.setSmtpAgent(null);
		
		boolean exceptionOccured = false;
		
		try
		{
			instance.startAgentSettingsManager();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testStartSettingsManager_nullAgentConfig_assertException() throws Exception
	{
		
		GatewayState instance = GatewayState.getInstance();
		
		if (instance.isAgentSettingManagerRunning())
			instance.stopAgentSettingsManager();
		
		instance.setSmptAgentConfig(null);
		instance.setSmtpAgent(mock(SmtpAgent.class));
		
		boolean exceptionOccured = false;
		
		try
		{
			instance.startAgentSettingsManager();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testStartSettingsManager_managerAlreadyRunning_assertException() throws Exception
	{
		GatewayState instance = GatewayState.getInstance();
		
		if (instance.isAgentSettingManagerRunning())
			instance.stopAgentSettingsManager();
		
		instance.setSmptAgentConfig(mock(SmtpAgentConfig.class));
		instance.setSmtpAgent(mock(SmtpAgent.class));
		
		instance.startAgentSettingsManager();
		
		boolean exceptionOccured = false;
		try
		{
			instance.startAgentSettingsManager();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testStartSettingsManager_assertSuccessfulStart() throws Exception
	{
		GatewayState instance = GatewayState.getInstance();
		
		if (instance.isAgentSettingManagerRunning())
			instance.stopAgentSettingsManager();
		
		instance.setSmptAgentConfig(mock(SmtpAgentConfig.class));
		instance.setSmtpAgent(mock(SmtpAgent.class));
		
		instance.startAgentSettingsManager();
		
		assertTrue(instance.isAgentSettingManagerRunning());
	}
}
