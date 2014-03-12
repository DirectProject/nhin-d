package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.mailet.MailetConfig;
import org.nhindirect.common.rest.BootstrapBasicAuthServiceSecurityManager;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.provider.OpenServiceSecurityManagerProvider;
import org.nhindirect.gateway.smtp.provider.ConfigBasicAuthServiceSecurityManagerProvider;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.options.OptionsManagerUtils;

import com.google.inject.Provider;

public class NHINDSecurityAndTrustMailet_getServiceSecurityManagerProviderTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			
			if (getSecurityManagerProviderName() != null && !getSecurityManagerProviderName().isEmpty())
				params.put(SecurityAndTrustMailetOptions.SERVICE_SECURITY_MANAGER_PROVIDER, getSecurityManagerProviderName());
				
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
			doAssertions(theMailet.getServiceSecurityManagerProvider());
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected String getSecurityManagerProviderName()
		{
			return "";
		}
		
		protected void doAssertions(Provider<ServiceSecurityManager> provider) throws Exception
		{
			
		}			
	}
	
	public void test_getServiceSecurityManager_nullProviderNameAndOptions_assertDefaultProvider() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(Provider<ServiceSecurityManager> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof OpenServiceSecurityManagerProvider);
			}				
		}.perform();
	}	
	
	public void test_getServiceSecurityManager_mailetParamOpenaSecurityProvider_assertOpenProvider() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getSecurityManagerProviderName()
			{
				return "org.nhindirect.common.rest.provider.OpenServiceSecurityManagerProvider";
			}
			
			@Override
			protected void doAssertions(Provider<ServiceSecurityManager> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof OpenServiceSecurityManagerProvider);
			}				
		}.perform();
	}	
	
	
	public void test_getServiceSecurityManager_jvmParamOpenSecurityProvider_assertOpenSecurityProvider() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void setupMocks() 
			{
				super.setupMocks();
				System.setProperty("org.nhindirect.gateway.smtp.james.mailet.ServiceSecurityManagerProvider", "org.nhindirect.common.rest.provider.OpenServiceSecurityManagerProvider");
			}
			
			@Override
			protected void tearDownMocks()
			{
				System.setProperty("org.nhindirect.gateway.smtp.james.mailet.ServiceSecurityManagerProvider", "");
			}
			
			@Override
			protected void doAssertions(Provider<ServiceSecurityManager> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof OpenServiceSecurityManagerProvider);
			}				
		}.perform();
	}	
	
	public void test_getServiceSecurityManager_invalidProviderClassName_assertDefaultProvider() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getSecurityManagerProviderName()
			{
				return "org.nhindirect.common.rest.provider.BogusServiceSecurityManagerProvider";
			}
			
			@Override
			protected void doAssertions(Provider<ServiceSecurityManager> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof OpenServiceSecurityManagerProvider);
			}				
		}.perform();
	}		
	
	public void test_getServiceSecurityManager_mailetParamBasicAuthSecurityProvider_assertBasicProviderWithUserPass() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected MailetConfig getMailetConfig() throws Exception
			{
				String configfile = TestUtils.getTestConfigFile(getConfigFileName());
				Map<String,String> params = new HashMap<String, String>();
				
				params.put("ConfigURL", "file://" + configfile);
				
				if (getSecurityManagerProviderName() != null && !getSecurityManagerProviderName().isEmpty())
					params.put(SecurityAndTrustMailetOptions.SERVICE_SECURITY_MANAGER_PROVIDER, getSecurityManagerProviderName());
				
				params.put(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SUBJECT, "gm2552");
				params.put(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SECRET, "password");
				
				return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
			}
			
			@Override
			protected String getSecurityManagerProviderName()
			{
				return "org.nhindirect.gateway.smtp.provider.ConfigBasicAuthServiceSecurityManagerProvider";
			}
			
			@Override
			protected void doAssertions(Provider<ServiceSecurityManager> provider) throws Exception
			{
				assertNotNull(provider);
				assertTrue(provider instanceof ConfigBasicAuthServiceSecurityManagerProvider);
				
				BootstrapBasicAuthServiceSecurityManager mgr = (BootstrapBasicAuthServiceSecurityManager)provider.get();
				assertNotNull(mgr);
			}				
		}.perform();
	}	
}
