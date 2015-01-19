package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.provider.RESTSmtpAgentConfigProvider;
import org.nhindirect.gateway.smtp.provider.WSSmtpAgentConfigProvider;
import org.nhindirect.gateway.smtp.provider.XMLSmtpAgentConfigProvider;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.options.OptionsManagerUtils;

import com.google.inject.Provider;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_getConfigProviderTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			
			if (getSmtpAgentConfigProviderName() != null && !getSmtpAgentConfigProviderName().isEmpty())
				params.put(SecurityAndTrustMailetOptions.SMTP_AGENT_CONFIG_PROVIDER, getSmtpAgentConfigProviderName());
				
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
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet()
			{

			};

			MailetConfig config = getMailetConfig();
			
			try
			{
				theMailet.init(config);
			}
			catch (Exception e)
			{
				/* don't care if an exception occured during init */
			}
			doAssertions(theMailet.getConfigProvider());
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected String getSmtpAgentConfigProviderName()
		{
			return "";
		}
		
		protected void doAssertions(Provider<SmtpAgentConfig> provider) throws Exception
		{
			
		}			
	}
	
	public void test_getServiceSecurityManager_nullProviderNameAndOptions_assertDefaultProvider() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(Provider<SmtpAgentConfig> provider) throws Exception
			{
				assertNull(provider);
			}				
		}.perform();
	}	
	
	public void test_getServiceSecurityManager_mailetParamXMLConfigProvider_assertXMLProvider() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getSmtpAgentConfigProviderName()
			{
				return "org.nhindirect.gateway.smtp.provider.XMLSmtpAgentConfigProvider";
			}
			
			@Override
			protected void doAssertions(Provider<SmtpAgentConfig> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof XMLSmtpAgentConfigProvider);
			}				
		}.perform();
	}	
	
	public void test_getServiceSecurityManager_mailetParamRESTConfigProvider_assertRESProvider() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getSmtpAgentConfigProviderName()
			{
				return "org.nhindirect.gateway.smtp.provider.RESTSmtpAgentConfigProvider";
			}
			
			@Override
			protected void doAssertions(Provider<SmtpAgentConfig> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof RESTSmtpAgentConfigProvider);
			}				
		}.perform();
	}	
	
	public void test_getServiceSecurityManager_jvmParamRESTConfigProvider_assertRESTsProvider() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				System.setProperty("org.nhindirect.gateway.smtp.james.mailet.SmptAgentConfigProvider", "org.nhindirect.gateway.smtp.provider.RESTSmtpAgentConfigProvider");
			}
			
			@Override
			protected void tearDownMocks()
			{
				System.setProperty("org.nhindirect.gateway.smtp.james.mailet.SmptAgentConfigProvider", "");
			}
			
			@Override
			protected void doAssertions(Provider<SmtpAgentConfig> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof RESTSmtpAgentConfigProvider);
			}				
		}.perform();
	}	
	
	public void test_getServiceSecurityManager_invalidProviderClassName_assertDefaultProvider() throws Exception 
	{
		new TestPlan() 
		{
			protected String getSmtpAgentConfigProviderName()
			{
				return "org.nhindirect.gateway.smtp.provider.BogusSmtpAgentConfigProvider";
			}
			
			@Override
			protected void doAssertions(Provider<SmtpAgentConfig> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof WSSmtpAgentConfigProvider);
			}				
		}.perform();
	}		
}
