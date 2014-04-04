package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.options.OptionsManagerUtils;

import com.google.inject.Module;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_getAuditModuleTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			for (Entry<Object, Object> entry : System.getProperties().entrySet())
			{
				System.out.println("Name: " + entry.getKey());
				System.out.println("Value: " + entry.getValue());
				System.out.println("\r\n");
			}			
			
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put("ConfigURL", "file://" + configfile);
			
			if (getAuditorProviderName() != null && !getAuditorProviderName().isEmpty())
				params.put(SecurityAndTrustMailetOptions.SMTP_AGENT_AUDITOR_PROVIDER, getAuditorProviderName());
	
			if (getAuditorConfigLoc() != null && !getAuditorConfigLoc().isEmpty())
				params.put(SecurityAndTrustMailetOptions.SMTP_AGENT_AUDITOR_CONFIG_LOC, getAuditorConfigLoc());
			
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
			doAssertions(theMailet.getAuditModule());
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected String getAuditorProviderName()
		{
			return "";
		}
		
		protected String getAuditorConfigLoc()
		{
			return "";
		}
		
		protected void doAssertions(Module module) throws Exception
		{
			
		}			
	}
	
	public void testGetAuditModule_nullProviderName_assertNullModule() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(Module module) throws Exception
			{
				assertNull(module);
			}				
		}.perform();
	}	
	
	public void testGetAuditModule_rdbmsProviderName_noConfig_assertModuleCreated() throws Exception 
	{
		new TestPlan() 
		{
			protected String getAuditorProviderName()
			{
				return "org.nhindirect.common.audit.provider.RDBMSAuditorProvider";
			}
			
			@Override
			protected void doAssertions(Module module) throws Exception
			{
				assertNotNull(module);
			}				
		}.perform();
	}
	
	
	public void testGetAuditModule_rdbmsProviderName_withConfig_assertModuleCreated() throws Exception 
	{
		new TestPlan() 
		{
			protected String getAuditorProviderName()
			{
				return "org.nhindirect.common.audit.provider.RDBMSAuditorProvider";
			}
			
			protected String getAuditorConfigLoc()
			{
				return "auditStore.xml";
			}
			
			@Override
			protected void doAssertions(Module module) throws Exception
			{
				assertNotNull(module);
			}				
		}.perform();
	}
	
	public void testGetAuditModule_rdbmsProviderName_invalidConfigLocation_assertModlueNotCreated() throws Exception 
	{
		new TestPlan() 
		{
			protected String getAuditorProviderName()
			{
				return "org.nhindirect.common.audit.provider.RDBMSAuditorProvider";
			}
			
			protected String getAuditorConfigLoc()
			{
				return "auditStore.sml";
			}
			
			@Override
			protected void doAssertions(Module module) throws Exception
			{
				assertNull(module);
			}				
		}.perform();
	}	
	
	public void testGetAuditModule_loggingAuditor_configLocationProvided_assertModuleCreated() throws Exception 
	{
		new TestPlan() 
		{
			protected String getAuditorProviderName()
			{
				return "org.nhindirect.common.audit.provider.LoggingAuditorProvider";
			}
			
			protected String getAuditorConfigLoc()
			{
				return "auditStore.sml";
			}
			
			@Override
			protected void doAssertions(Module module) throws Exception
			{
				assertNotNull(module);
			}				
		}.perform();
	}	
	
	public void testGetAuditModule_invalidAuditor_assertModuleNotCreated() throws Exception 
	{
		new TestPlan() 
		{
			protected String getAuditorProviderName()
			{
				return "org.nhindirect.common.audit.provider.BogusAuditorProvider";
			}
			
			protected String getAuditorConfigLoc()
			{
				return "auditStore.sml";
			}
			
			@Override
			protected void doAssertions(Module module) throws Exception
			{
				assertNull(module);
			}				
		}.perform();
	}		
}
