package org.nhindirect.gateway.smtp;

import static org.mockito.Mockito.mock;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;

import junit.framework.TestCase;

public class GatewayState_stopSettingManagerTest extends TestCase
{
	public void testStopSettingsManager_managerNull_assertExepction() throws Exception
	{
		GatewayState instance = GatewayState.getInstance();
		
		if (instance.isAgentSettingManagerRunning())
			instance.stopAgentSettingsManager();
		
		boolean exceptionOccured = false;
		
		try
		{
			instance.stopAgentSettingsManager();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testStopSettingsManager_assertStoppedSuccessful() throws Exception
	{
		GatewayState instance = GatewayState.getInstance();
		
		if (instance.isAgentSettingManagerRunning())
			instance.stopAgentSettingsManager();
		
		instance.setSmptAgentConfig(mock(SmtpAgentConfig.class));
		instance.setSmtpAgent(mock(SmtpAgent.class));
		
		instance.startAgentSettingsManager();
		
		assertTrue(instance.isAgentSettingManagerRunning());
		
		instance.stopAgentSettingsManager();
		assertFalse(instance.isAgentSettingManagerRunning());
	}
}
