package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.Mailet;
import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.smtp.GatewayState;
import org.nhindirect.gateway.smtp.config.ConfigServiceRunner;
import org.nhindirect.gateway.testutils.TestUtils;

import junit.framework.TestCase;

public class RefreshSecurityAndTrustStateMailet_refreshStateTest extends TestCase
{
	
	protected Mailet getMailet(String configurationFileName)  throws Exception
	{
		Mailet retVal = null;
		String configfile = TestUtils.getTestConfigFile(configurationFileName);
		Map<String,String> params = new HashMap<String, String>();
		
		if (configurationFileName.startsWith("http"))
			params.put("ConfigURL", ConfigServiceRunner.getConfigServiceURL());
		else
			params.put("ConfigURL", "file://" + configfile);
		
		retVal = new NHINDSecurityAndTrustMailet();
		
		MailetConfig mailetConfig = new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");
		
		retVal.init(mailetConfig);
		
		return retVal;
	}
	
	public void testRefreshWithUpdateManagerAlreadyRunning() throws Exception
	{
		getMailet("ValidConfig.xml");
		
		assertTrue(GatewayState.getInstance().isAgentSettingManagerRunning());
		
		RefreshSecurityAndTrustStateMailet refreshMailet = new RefreshSecurityAndTrustStateMailet();
		
		refreshMailet.service(new MockMail(null));
		
		assertTrue(GatewayState.getInstance().isAgentSettingManagerRunning());
	}
	
	public void testRefreshWithUpdateManagerNotRunning() throws Exception
	{
		getMailet("ValidConfig.xml");
		
		if (GatewayState.getInstance().isAgentSettingManagerRunning())
			GatewayState.getInstance().stopAgentSettingsManager();
		
		assertFalse(GatewayState.getInstance().isAgentSettingManagerRunning());
		
		RefreshSecurityAndTrustStateMailet refreshMailet = new RefreshSecurityAndTrustStateMailet();
		
		refreshMailet.service(new MockMail(null));
		
		assertTrue(GatewayState.getInstance().isAgentSettingManagerRunning());
	}
}
