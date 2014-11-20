package org.nhindirect.gateway.smtp;

import static org.mockito.Mockito.mock;

import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;

import junit.framework.TestCase;

public class GatewayState_getSetAttributesTest extends TestCase
{

	public void testGetSetAttributes_getSetAgent() throws Exception
	{
		GatewayState instance = GatewayState.getInstance();
		
		instance.setSmtpAgent(null);
		assertNull(instance.getSmtpAgent());
		
		SmtpAgent smtpAgent = mock(SmtpAgent.class);
		instance.setSmtpAgent(smtpAgent);
		assertNotNull(instance.getSmtpAgent());
		assertEquals(smtpAgent, instance.getSmtpAgent());
	}
	
	public void testGetSetAttributes_getSetAgentConfig() throws Exception
	{
		GatewayState instance = GatewayState.getInstance();
		
		instance.setSmptAgentConfig(null);
		assertNull(instance.getSmtpAgentConfig());
		
		SmtpAgentConfig smtpAgentConfig = mock(SmtpAgentConfig.class);
		instance.setSmptAgentConfig(smtpAgentConfig);
		assertNotNull(instance.getSmtpAgentConfig());
		assertEquals(smtpAgentConfig, instance.getSmtpAgentConfig());
	}
	
	public void testGetSetAttributes_getSetManagerInterval() throws Exception
	{
		GatewayState instance = GatewayState.getInstance();
		
		
		instance.setSettingsUpdateInterval(387278394);

		assertEquals(387278394, instance.getSettingsUpdateInterval());
	}
}
