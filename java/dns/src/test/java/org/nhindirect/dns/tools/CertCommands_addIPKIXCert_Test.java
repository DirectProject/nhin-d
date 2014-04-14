package org.nhindirect.dns.tools;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.ConfigServiceDNSStore;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.xbill.DNS.Type;

public class CertCommands_addIPKIXCert_Test extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{
        protected CertRecordCounterPrinter recordPrinter;
		
		protected int port;
		protected DNSServer server = null;
		protected ConfigurationServiceProxy proxy;
		protected CertCommands certCommands;
		
		@Override
		protected void setupMocks() throws Exception
		{
			if (!ConfigServiceRunner.isServiceRunning())
				ConfigServiceRunner.startConfigService();

			proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
			
			cleanRecords();
			
			port = AvailablePortFinder.getNextAvailable(1024);
			DNSServerSettings settings = new DNSServerSettings();
			settings.setPort(port);
						
			server = new DNSServer(new ConfigServiceDNSStore(new URL(ConfigServiceRunner.getConfigServiceURL())), settings);
			
			server.start();		
			
			recordPrinter = new CertRecordCounterPrinter();
			
			certCommands = new CertCommands(proxy);
			certCommands.setRecordPrinter(recordPrinter);
		}
		
		@Override
		protected void tearDownMocks() throws Exception
		{
			if (server != null)
				server.stop();
		}	
		
		private void cleanRecords() throws Exception
		{
			DnsRecord[] rec = proxy.getDNSByType(Type.ANY);
			
			if (rec != null && rec.length > 0)
				proxy.removeDNS(rec);
			
			rec = proxy.getDNSByType(Type.ANY);
			
			assertNull(rec);
			
			Certificate[] certs = proxy.listCertificates(0,  1000, null);
			
			if (certs != null && certs.length > 0)
			{
				long[] ids = new long[certs.length];
				int idx = 0;
				for (Certificate cert : certs)
					ids[idx++] = cert.getId();
				
				proxy.removeCertificates(ids);
			}
			
		}	
		
		@Override
		protected void performInner() throws Exception
		{			
			Map<String, String> certsToAdd = getCertsToAdd();
			
			
			if (certsToAdd.size() > 0)
			{
				for (Entry<String, String> certToAdd : certsToAdd.entrySet())
					certCommands.addIPKIXCert(new String[] {certToAdd.getKey(), certToAdd.getValue()});
			}					

			Certificate[] importedCerts = null;
			try
			{
				importedCerts = proxy.listCertificates(0,  100, null);
			}
			catch (Exception e)
			{
				
			}
			doAssertions(importedCerts);
		}
		
		protected abstract Map<String, String> getCertsToAdd() throws Exception;	
		
		protected abstract void doAssertions(Certificate[] importedCerts) throws Exception;		
	}
	
	public void testImportPublicCert_addIPKIX_AssertRecordImported() throws Exception 
	{		
		new TestPlan()
		{
			
			@Override
			protected Map<String, String> getCertsToAdd() throws Exception
			{
				 Map<String, String> retCerts = new HashMap<String, String>();
				 
				 retCerts.put("test.com", "http://test.com/test.der");
				 
				 return retCerts;
			}

			@Override
			protected void doAssertions(Certificate[] importedCerts) throws Exception	
			{
				assertEquals(1, importedCerts.length);
				Certificate cert = importedCerts[0];
				assertEquals("test.com", cert.getOwner());
				assertFalse(cert.isPrivateKey());
				assertEquals("http://test.com/test.der", new String(cert.getData()));
			}
		}.perform();
	}	
	
	public void testImportPublicCert_invalidProxy_AssertRecordNotImported() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected void setupMocks() throws Exception
			{
				super.setupMocks();
				proxy = new ConfigurationServiceProxy("http://boGussite.cdm");
				certCommands.setConfigurationProxy(proxy);
			}
			
			@Override
			protected Map<String, String> getCertsToAdd() throws Exception
			{
				 Map<String, String> retCerts = new HashMap<String, String>();
				 
				 retCerts.put("test.com", "http://test.com/test.der");
				 
				 return retCerts;
			}

			@Override
			protected void doAssertions(Certificate[] importedCerts) throws Exception	
			{
				assertNull(importedCerts);
			}
		}.perform();
	}		
}
