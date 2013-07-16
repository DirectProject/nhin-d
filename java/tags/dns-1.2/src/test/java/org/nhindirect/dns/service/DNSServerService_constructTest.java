package org.nhindirect.dns.service;

import java.net.URL;

import junit.framework.TestCase;

import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;

public class DNSServerService_constructTest extends TestCase 
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected int port;
		protected DNSServerService server = null;
		protected ConfigurationServiceProxy proxy;
				
		protected void setupMocks() throws Exception
		{
			if (!ConfigServiceRunner.isServiceRunning())
				ConfigServiceRunner.startConfigService();

			proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
			
		}
		
		@Override
		protected void tearDownMocks() throws Exception
		{
			if (server != null)
				server.stopService();
		}
		
		@Override
		protected void performInner() throws Exception
		{
			port = AvailablePortFinder.getNextAvailable(1024);
			DNSServerSettings settings = new DNSServerSettings();
			settings.setPort(port);
			server = new DNSServerService(new URL(ConfigServiceRunner.getConfigServiceURL()), settings);
			
			doAssertions();
		}	
		
		protected void doAssertions()
		{
			
		}
	}
	
	public void testConstructServer_assertDefaultDNSStore() throws Exception 
	{
		new TestPlan()
		{
			protected void doAssertions()
			{
				assertEquals("org.nhindirect.dns.ConfigServiceDNSStore", server.server.getDNSStoreImplName());
			}
			
		}.perform();
		
	}
	
	public void testConstructServer_nonExistantProviderClass_assertDefaultDNSStore() throws Exception 
	{
		new TestPlan()
		{
			@Override
			public void setupMocks() throws Exception
			{
				super.setupMocks();
				
				System.setProperty(DNSServerService.DNS_STORE_PROVIDER_VAR, "com.cern.bogus.WhoCares");
			}
			
			@Override
			public void tearDownMocks() throws Exception
			{
				System.setProperty(DNSServerService.DNS_STORE_PROVIDER_VAR, "");
				
				super.tearDownMocks();
			}
			
			protected void doAssertions()
			{
				assertEquals("org.nhindirect.dns.ConfigServiceDNSStore", server.server.getDNSStoreImplName());
			}
			
		}.perform();
		
	}	
	
	public void testConstructServer_overriddenProviderClass_assertDNSStoreClass() throws Exception 
	{
		new TestPlan()
		{
			@Override
			public void setupMocks() throws Exception
			{
				super.setupMocks();
				
				System.setProperty(DNSServerService.DNS_STORE_PROVIDER_VAR, "org.nhindirect.dns.provider.MockDNSStoreProvider");
			}
			
			@Override
			public void tearDownMocks() throws Exception
			{
				System.setProperty(DNSServerService.DNS_STORE_PROVIDER_VAR, "");
				
				super.tearDownMocks();
			}
			
			protected void doAssertions()
			{
				assertEquals("org.nhindirect.dns.MockDNSStore", server.server.getDNSStoreImplName());
			}
			
		}.perform();
		
	}	
	

	public void testConstructServer_overriddenConfigDNSProviderClass_assertDNSStoreClass() throws Exception 
	{
		new TestPlan()
		{
			@Override
			public void setupMocks() throws Exception
			{
				super.setupMocks();
				
				System.setProperty(DNSServerService.DNS_STORE_PROVIDER_VAR, "org.nhindirect.dns.provider.MockConfigDNSStoreProvider");
			}
			
			@Override
			public void tearDownMocks() throws Exception
			{
				System.setProperty(DNSServerService.DNS_STORE_PROVIDER_VAR, "");
				
				super.tearDownMocks();
			}
			
			protected void doAssertions()
			{
				assertEquals("org.nhindirect.dns.MockDNSStore", server.server.getDNSStoreImplName());
			}
			
		}.perform();
		
	}	
}
