package org.nhindirect.gateway.smtp.james.mailet;


import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.mailet.MailetConfig;
import org.nhindirect.common.tx.module.DefaultTxDetailParserModule;
import org.nhindirect.common.tx.module.ProviderTxServiceModule;
import org.nhindirect.common.tx.provider.NoOpTxServiceClientProvider;
import org.nhindirect.common.tx.provider.RESTTxServiceClientProvider;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;

import com.google.inject.Module;

public class NHINDSecurityAndTrustMailet_createDefaultTxServiceModulesTest extends TestCase
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
		protected void performInner() throws Exception
		{
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet();

			MailetConfig config = getMailetConfig();
			
			theMailet.init(config);
			doAssertions(theMailet.createDefaultTxServiceModules());
		}
		
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}

		protected String getMessageMonitoringServiceURL()
		{
			return "";
		}
		
		protected void doAssertions(Collection<Module> modules) throws Exception
		{
		}			
	}
	
	public void testCreateDefaultServiceModules_nullServiceURL_assertNoOpMonitoringService() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageMonitoringServiceURL()
			{
				return null;
			}
			
			@Override
			protected void doAssertions(Collection<Module> modules) throws Exception
			{
				assertNotNull(modules);
				assertEquals(2, modules.size());
				
				
				Iterator<Module> iter = modules.iterator();
				assertTrue(iter.next() instanceof DefaultTxDetailParserModule);
				Module serviceModule = iter.next();
				assertTrue(serviceModule instanceof ProviderTxServiceModule);
				
				Field field = ProviderTxServiceModule.class.getDeclaredField("txServiceProv");
				field.setAccessible(true);
				field.get(serviceModule);
				assertTrue(field.get(serviceModule) instanceof NoOpTxServiceClientProvider);
			}				
		}.perform();
	}
	
	public void testCreateDefaultServiceModules_emptyServiceURL_assertNoOpMonitoringService() throws Exception 
	{
		new TestPlan() 
		{
			
			@Override
			protected void doAssertions(Collection<Module> modules) throws Exception
			{
				assertNotNull(modules);
				assertEquals(2, modules.size());		
				
				Iterator<Module> iter = modules.iterator();
				assertTrue(iter.next() instanceof DefaultTxDetailParserModule);
				Module serviceModule = iter.next();
				assertTrue(serviceModule instanceof ProviderTxServiceModule);

				Field field = ProviderTxServiceModule.class.getDeclaredField("txServiceProv");
				field.setAccessible(true);
				field.get(serviceModule);
				assertTrue(field.get(serviceModule) instanceof NoOpTxServiceClientProvider);
			}				
		}.perform();
	}
	
	public void testCreateDefaultServiceModules_serviceURLAvailable_assertDefaultTxService() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected String getMessageMonitoringServiceURL()
			{
				return "http://localhost";
			}
			
			@Override
			protected void doAssertions(Collection<Module> modules) throws Exception
			{
				assertNotNull(modules);
				assertEquals(2, modules.size());		
				
				Iterator<Module> iter = modules.iterator();
				assertTrue(iter.next() instanceof DefaultTxDetailParserModule);
				Module serviceModule = iter.next();
				assertTrue(serviceModule instanceof ProviderTxServiceModule);

				Field field = ProviderTxServiceModule.class.getDeclaredField("txServiceProv");
				field.setAccessible(true);
				field.get(serviceModule);
				assertTrue(field.get(serviceModule) instanceof RESTTxServiceClientProvider);
			}				
		}.perform();
	}
}
