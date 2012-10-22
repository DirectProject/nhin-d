package org.nhindirect.gateway.smtp.james.mailet;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.mailet.MailetConfig;
import org.nhindirect.common.tx.impl.DefaultTxDetailParser;
import org.nhindirect.common.tx.impl.NoOpTxServiceClient;
import org.nhindirect.common.tx.module.DefaultTxDetailParserModule;
import org.nhindirect.common.tx.module.ProviderTxServiceModule;
import org.nhindirect.common.tx.provider.NoOpTxServiceClientProvider;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;

import com.google.inject.Module;

public class NHINDSecurityAndTrustMailet_monitorServiceInitialization_Test extends TestCase
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
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet()
			{
				@Override
				protected Collection<Module> getInitModules()
				{
					return getTestInitModules();
				}
			};

			MailetConfig config = getMailetConfig();
			
			NHINDSecurityAndTrustMailet spyMailet = spy(theMailet);
			
			spyMailet.init(config);
			doAssertions(spyMailet);
		}
		
		protected Collection<Module> getTestInitModules()
		{
			return null;
		}
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}

		protected String getMessageMonitoringServiceURL()
		{
			return "";
		}
		
		protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
		{
		}			
	}
	
	public void testMonitorServiceInitialization_nullInitModuels_assertNoOpMonitoringService() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				verify(agent, times(1)).createDefaultTxServiceModules();
				assertNotNull(agent.txParser != null);
				assertNotNull(agent.txService != null);
				assertTrue(agent.txParser instanceof DefaultTxDetailParser);
				assertTrue(agent.txService instanceof NoOpTxServiceClient);
			}				
		}.perform();
	}
	
	public void testMonitorServiceInitialization_nonNullInitModuels_assertNoOpMonitoringService() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected Collection<Module> getTestInitModules()
			{
				Collection<Module> modules = new ArrayList<Module>();
				modules.add(DefaultTxDetailParserModule.create());
				modules.add(ProviderTxServiceModule.create(new NoOpTxServiceClientProvider()));
				return modules;
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				verify(agent, never()).createDefaultTxServiceModules();
				assertNotNull(agent.txParser != null);
				assertNotNull(agent.txService != null);
				assertTrue(agent.txParser instanceof DefaultTxDetailParser);
				assertTrue(agent.txService instanceof NoOpTxServiceClient);
			}				
		}.perform();
	}
	
	public void testMonitorServiceInitialization_exceptionInFirstCreation_nullParserModule_assertNoOpMonitoringService() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected Collection<Module> getTestInitModules()
			{	
				Collection<Module> modules = new ArrayList<Module>();
				modules.add(ProviderTxServiceModule.create(new NoOpTxServiceClientProvider()));
				return modules;
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				verify(agent, times(1)).createDefaultTxServiceModules();
				assertNotNull(agent.txParser != null);
				assertNotNull(agent.txService != null);
				assertTrue(agent.txParser instanceof DefaultTxDetailParser);
				assertTrue(agent.txService instanceof NoOpTxServiceClient);
			}				
		}.perform();
	}
	
	public void testMonitorServiceInitialization_exceptionInFirstCreation_nullServiceModule_assertNoOpMonitoringService() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected Collection<Module> getTestInitModules()
			{	
				Collection<Module> modules = new ArrayList<Module>();
				modules.add(DefaultTxDetailParserModule.create());
				return modules;
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
			{
				verify(agent, times(1)).createDefaultTxServiceModules();
				assertNotNull(agent.txParser != null);
				assertNotNull(agent.txService != null);
				assertTrue(agent.txParser instanceof DefaultTxDetailParser);
				assertTrue(agent.txService instanceof NoOpTxServiceClient);
			}				
		}.perform();
	}
}
