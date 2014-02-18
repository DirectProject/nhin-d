package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsManagerUtils;
import org.nhindirect.stagent.options.OptionsParameter;

import junit.framework.TestCase;

public class NotificationSupressor_isConusmingProcessedMDNTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put(SecurityAndTrustMailetOptions.CONFIG_URL_PARAM, "file://" + configfile);
			params.put(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM, getConsumeMDNSetting());
			
			return new MockMailetConfig(params, "NotificationSuppressor");	
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
			NotificationSuppressor theMailet = new NotificationSuppressor();

			MailetConfig config = getMailetConfig();
			
			theMailet.init(config);
			doAssertions(theMailet.consumeMDNProcessed);
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}

		protected String getConsumeMDNSetting()
		{
			return "";
		}
		
		protected void doAssertions(boolean consumeMDN) throws Exception
		{
			
		}			
	}
	
	public void testIsConusmingProcessedMDNTest_nullConfigParamAndOptions_assertfalse() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getConsumeMDNSetting()
			{
				return null;
			}
			
			@Override
			protected void doAssertions(boolean consumeMDN) throws Exception
			{
				assertFalse(consumeMDN);
			}				
		}.perform();
	}	
	
	public void testIsConusmingProcessedMDNTest_emtpyConfigParamAndOptions_assertfalse() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(boolean consumeMDN) throws Exception
			{
				assertFalse(consumeMDN);
			}				
		}.perform();
	}	
	
	public void testIsConusmingProcessedMDNTest_trueConfigParam_assertTrue() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConsumeMDNSetting()
			{
				return "true";
			}
			
			@Override
			protected void doAssertions(boolean consumeMDN) throws Exception
			{
				assertTrue(consumeMDN);
			}				
		}.perform();
	}	
	
	public void testIsConusmingProcessedMDNTest_TRUEConfigParam_assertTrue() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConsumeMDNSetting()
			{
				return "TRUE";
			}
			
			@Override
			protected void doAssertions(boolean consumeMDN) throws Exception
			{
				assertTrue(consumeMDN);
			}				
		}.perform();
	}	
	
	public void testIsConusmingProcessedMDNTest_falseConfigParam_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConsumeMDNSetting()
			{
				return "false";
			}
			
			@Override
			protected void doAssertions(boolean consumeMDN) throws Exception
			{
				assertFalse(consumeMDN);
			}				
		}.perform();
	}	
	
	public void testIsConusmingProcessedMDNTest_emptyConfigParam_trueOptionSettings_assertTrue() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM, "true"));
			}
			
			@Override
			protected void doAssertions(boolean consumeMDN) throws Exception
			{
				assertTrue(consumeMDN);
			}				
		}.perform();
	}	
	
	public void testIsConusmingProcessedMDNTest_emptyConfigParam_falseOptionSettings_assertFalse() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM, "false"));
			}
			
			@Override
			protected void doAssertions(boolean consumeMDN) throws Exception
			{
				assertFalse(consumeMDN);
			}				
		}.perform();
	}	
}
