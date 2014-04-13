package org.nhindirect.dns.tools;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhind.config.EntityStatus;
import org.nhindirect.dns.ConfigServiceDNSStore;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.xbill.DNS.Type;

public class CertCommands_removeCert_Test extends TestCase
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
			List<Certificate> certsToAdd = getCertsToAdd();
			
			try
			{
				if (certsToAdd.size() > 0)
				{
					proxy.addCertificates(certsToAdd.toArray(new Certificate[certsToAdd.size()]));
				}					
			}
			catch (Exception e)
			{
				
			}
				
			List<String> certsToRemove = getCertOwnersToRemove();
			for (String certToRemove : certsToRemove)
			{
				certCommands.removeCert(new String[] {certToRemove});
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
		
		protected abstract List<Certificate> getCertsToAdd() throws Exception;	

		protected abstract List<String> getCertOwnersToRemove() throws Exception;	
		
		protected abstract void doAssertions(Certificate[] importedCerts) throws Exception;		
	}
	
	public void testRemoveCert_ownerExists_AssertRecordRemoved() throws Exception 
	{		
		new TestPlan()
		{
			
			@Override
			protected  List<Certificate> getCertsToAdd() throws Exception
			{
				 List<Certificate> retCerts = new ArrayList<Certificate>();
				 
				 Certificate cert = new Certificate();
				 cert.setOwner("test.com");
				 cert.setData("http://localhost/test.der".getBytes());
				 cert.setStatus(EntityStatus.ENABLED);
				 
				 retCerts.add(cert);
				 
				 return retCerts;
			}

			protected List<String> getCertOwnersToRemove() throws Exception
			{
				List<String> certsToRemove = new ArrayList<String>();
				certsToRemove.add("test.com");
				
				return certsToRemove;
			}
			
			@Override
			protected void doAssertions(Certificate[] importedCerts) throws Exception	
			{
				assertNull(importedCerts);
			}
		}.perform();
	}	
	
	public void testRemoveCert_ownerDoesNotExists_AssertRecordNotRemoved() throws Exception 
	{		
		new TestPlan()
		{
			
			@Override
			protected  List<Certificate> getCertsToAdd() throws Exception
			{
				 List<Certificate> retCerts = new ArrayList<Certificate>();
				 
				 Certificate cert = new Certificate();
				 cert.setOwner("test.com");
				 cert.setData("http://localhost/test.der".getBytes());
				 cert.setStatus(EntityStatus.ENABLED);
				 
				 retCerts.add(cert);
				 
				 return retCerts;
			}

			protected List<String> getCertOwnersToRemove() throws Exception
			{
				List<String> certsToRemove = new ArrayList<String>();
				certsToRemove.add("test2.com");
				
				return certsToRemove;
			}
			
			@Override
			protected void doAssertions(Certificate[] importedCerts) throws Exception	
			{
				assertEquals(1, importedCerts.length);
				
				Certificate cert = importedCerts[0];
				assertEquals("test.com", cert.getOwner());
			}
		}.perform();
	}	
	
	public void testRemoveCert_invalidProxy_AssertRecordNotRemoved() throws Exception 
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
			protected  List<Certificate> getCertsToAdd() throws Exception
			{
				 List<Certificate> retCerts = new ArrayList<Certificate>();
				 
				 Certificate cert = new Certificate();
				 cert.setOwner("test.com");
				 cert.setData("http://localhost/test.der".getBytes());
				 cert.setStatus(EntityStatus.ENABLED);
				 
				 retCerts.add(cert);
				 
				 return retCerts;
			}

			protected List<String> getCertOwnersToRemove() throws Exception
			{
				List<String> certsToRemove = new ArrayList<String>();
				certsToRemove.add("test2.com");
				
				return certsToRemove;
			}
			
			@Override
			protected void doAssertions(Certificate[] importedCerts) throws Exception	
			{
				assertNull(importedCerts);
			}
		}.perform();
	}	
}
