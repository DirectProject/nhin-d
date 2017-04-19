package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.GatewayConfiguration;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsManagerUtils;
import org.nhindirect.stagent.options.OptionsParameter;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_getMonitoringServiceURLTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			params.put("MessageMonitoringServiceURL", getMessageMonitoringServiceURL());
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		@Override
		protected void setupMocks() 
		{
			OptionsManagerUtils.clearOptionsManagerInstance();
		}
		
		@Override
		protected void tearDownMocks()
		{
			OptionsManagerUtils.clearOptionsManagerOptions();
			OptionsManagerUtils.clearOptionsManagerInstance();
		}
		
		@Override
		protected void performInner() throws Exception
		{
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet();

			MailetConfig config = getMailetConfig();
			
			theMailet.init(config);
			doAssertions(GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.MONITORING_SERVICE_URL_PARAM, theMailet, null));
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}

		protected String getMessageMonitoringServiceURL()
		{
			return "";
		}
		
		protected void doAssertions(String serviceURL) throws Exception
		{
			
		}			
	}
	
	public void testGetMonitoringServiceURL_nullServiceURLAndOptions_assertNullServiceURL() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageMonitoringServiceURL()
			{
				return null;
			}
			
			@Override
			protected void doAssertions(String serviceURL) throws Exception
			{
				assertNull(serviceURL);
			}				
		}.perform();
	}	
	
	public void testGetMonitoringServiceURL_emptyServiceURLAndOptions_assertEmtpyServiceURL() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageMonitoringServiceURL()
			{
				return "";
			}
			
			@Override
			protected void doAssertions(String serviceURL) throws Exception
			{
				assertEquals("", serviceURL);
			}				
		}.perform();
	}	
	
	public void testGetMonitoringServiceURL_valueServiceURLAnd_assertServiceURL() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageMonitoringServiceURL()
			{
				return "http://localhost";
			}
			
			@Override
			protected void doAssertions(String serviceURL) throws Exception
			{
				assertEquals("http://localhost", serviceURL);
			}				
		}.perform();
	}	
	
	public void testGetMonitoringServiceURL_emptyServiceURLEmptyOptions_assertEmtpyServiceURL() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(SecurityAndTrustMailetOptions.MONITORING_SERVICE_URL_PARAM, "http://localhost"));
			}
			
			@Override
			protected void doAssertions(String serviceURL) throws Exception
			{
				assertEquals("http://localhost", serviceURL);
			}				
		}.perform();
	}	
}
