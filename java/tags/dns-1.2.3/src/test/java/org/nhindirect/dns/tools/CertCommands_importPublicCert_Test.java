package org.nhindirect.dns.tools;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

import junit.framework.TestCase;

public class CertCommands_importPublicCert_Test extends TestCase 
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
			List<String> certsToAdd = getCertFilesToAdd();
			
			
			if (certsToAdd.size() > 0)
			{
				for (String certToAdd : certsToAdd)
					certCommands.importPublicCert(new String[] {certToAdd});
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
		
		protected abstract List<String> getCertFilesToAdd() throws Exception;	
		
		protected abstract void doAssertions(Certificate[] importedCerts) throws Exception;		
	}
	
	public void testImportPublicCert_importFromFile_AssertRecordImported() throws Exception 
	{		
		new TestPlan()
		{
			
			@Override
			protected List<String> getCertFilesToAdd() throws Exception
			{
				 List<String> retCerts = new ArrayList<String>();
				 
				 retCerts.add("./src/test/resources/certs/gm2552.der");
				 
				 return retCerts;
			}

			@Override
			protected void doAssertions(Certificate[] importedCerts) throws Exception	
			{
				assertEquals(1, importedCerts.length);
				Certificate cert = importedCerts[0];
				assertEquals("gm2552@securehealthemail.com", cert.getOwner());
				assertFalse(cert.isPrivateKey());
			}
		}.perform();
	}
	
	public void testImportPublicCert_fileDoesNotExist_AssertRecordNotImported() throws Exception 
	{		
		new TestPlan()
		{
			
			@Override
			protected List<String> getCertFilesToAdd() throws Exception
			{
				 List<String> retCerts = new ArrayList<String>();
				 
				 retCerts.add("./src/test/resources/certs/gm2552doesnotexist.der");
				 
				 return retCerts;
			}

			@Override
			protected void doAssertions(Certificate[] importedCerts) throws Exception	
			{
				assertNull(importedCerts);
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
			protected List<String> getCertFilesToAdd() throws Exception
			{
				 List<String> retCerts = new ArrayList<String>();
				 
				 retCerts.add("./src/test/resources/certs/gm2552.der");
				 
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
