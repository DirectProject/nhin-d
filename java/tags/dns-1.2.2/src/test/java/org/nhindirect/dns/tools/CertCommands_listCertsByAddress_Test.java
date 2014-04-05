package org.nhindirect.dns.tools;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import junit.framework.TestCase;

public class CertCommands_listCertsByAddress_Test extends TestCase 
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

			
			List<Certificate> certsToAdd = getCertRecsToAdd();
			
			
			if (certsToAdd.size() > 0)
			{
				
				proxy.addCertificates(certsToAdd.toArray(new Certificate[certsToAdd.size()]));
			}					

			List<String> adds = getAddressesToSearch();
			if (adds != null && adds.size() != 0)
				for (String add : adds)
					certCommands.listCertsByAddress(new String[] {add});
						
			doAssertions();
		}
		
		protected abstract List<Certificate> getCertRecsToAdd() throws Exception;	
		
		protected abstract List<String> getAddressesToSearch() throws Exception;	
		
		protected abstract void doAssertions() throws Exception;		
	}
	
	public void testListCertsByAddress_noCertsInStore_AssertNoRecordsFetched() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected List<String> getAddressesToSearch() throws Exception
			{
				List<String> retVal = new ArrayList<String>();
				
				retVal.add("test.com");
				
				return retVal;
			}
			
			@Override
			protected List<Certificate> getCertRecsToAdd() throws Exception
			{
				 List<Certificate> retCerts = new ArrayList<Certificate>();
				 
				 return retCerts;
			}

			@Override
			protected void doAssertions() throws Exception		
			{
				assertEquals(0, recordPrinter.getRecordCount());
			}
		}.perform();
	}	
	
	
	public void testListCertsByAddress_singleCertInStore_AssertRecordsFetched() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected List<String> getAddressesToSearch() throws Exception
			{
				List<String> retVal = new ArrayList<String>();
				
				retVal.add("test.com");
				
				return retVal;
			}
			
			@Override
			protected List<Certificate> getCertRecsToAdd() throws Exception
			{
				 List<Certificate> retCerts = new ArrayList<Certificate>();
				 
				 Certificate cert = new Certificate();
				 cert.setOwner("test.com");
				 cert.setData("http://localhost/test.der".getBytes());
				 cert.setStatus(EntityStatus.ENABLED);
				 
				 retCerts.add(cert);
				 
				 return retCerts;
			}

			@Override
			protected void doAssertions() throws Exception		
			{
				assertEquals(1, recordPrinter.getRecordCount());
				
				final Collection<Certificate> printedRecords  = recordPrinter.getPrintedRecords();
				Certificate cert = printedRecords.iterator().next();
				assertEquals("test.com", cert.getOwner());
			}
		}.perform();
	}
	
	public void testListCertsByAddress_singleCertInStore_multipleSearches_AssertSingleRecordsFetched() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected List<String> getAddressesToSearch() throws Exception
			{
				List<String> retVal = new ArrayList<String>();
				
				retVal.add("test.com");
				retVal.add("test2.com");
				
				return retVal;
			}
			
			@Override
			protected List<Certificate> getCertRecsToAdd() throws Exception
			{
				 List<Certificate> retCerts = new ArrayList<Certificate>();
				 
				 Certificate cert = new Certificate();
				 cert.setOwner("test.com");
				 cert.setData("http://localhost/test.der".getBytes());
				 cert.setStatus(EntityStatus.ENABLED);
				 
				 retCerts.add(cert);
				 
				 return retCerts;
			}

			@Override
			protected void doAssertions() throws Exception		
			{
				assertEquals(1, recordPrinter.getRecordCount());
				
				final Collection<Certificate> printedRecords  = recordPrinter.getPrintedRecords();
				Certificate cert = printedRecords.iterator().next();
				assertEquals("test.com", cert.getOwner());
			}
		}.perform();
	}
	
	public void testListCertsByAddress_mutipleCertsInStore_multipleSearches_AssertRecordsFetched() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected List<String> getAddressesToSearch() throws Exception
			{
				List<String> retVal = new ArrayList<String>();
				
				retVal.add("test1.com");
				retVal.add("test2.com");
				
				return retVal;
			}
			
			@Override
			protected List<Certificate> getCertRecsToAdd() throws Exception
			{
				 List<Certificate> retCerts = new ArrayList<Certificate>();
				 
				 Certificate cert = new Certificate();
				 cert.setOwner("test1.com");
				 cert.setData("http://localhost/test1.der".getBytes());
				 cert.setStatus(EntityStatus.ENABLED);
				 
				 retCerts.add(cert);
				 
				 cert = new Certificate();
				 cert.setOwner("test2.com");
				 cert.setData("http://localhost/test2.der".getBytes());
				 cert.setStatus(EntityStatus.ENABLED);
				 
				 retCerts.add(cert);
				 
				 return retCerts;
			}

			@Override
			protected void doAssertions() throws Exception		
			{
				assertEquals(2, recordPrinter.getRecordCount());
				
				boolean test1Found = false;
				boolean test2Found = false;
				for (Certificate cert : recordPrinter.getPrintedRecords())
				{
					if (cert.getOwner().equals("test1.com"))
						test1Found = true;
					else if (cert.getOwner().equals("test2.com"))
						test2Found = true;
				}
				
				assertTrue(test1Found);
				assertTrue(test2Found);
			}
		}.perform();
	}	
	
	public void testListCertsByAddress_invalidProxy_AssertNoRecordsFetched() throws Exception 
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
			protected List<String> getAddressesToSearch() throws Exception
			{
				List<String> retVal = new ArrayList<String>();
				
				retVal.add("test.com");
				
				return retVal;
			}
			
			@Override
			protected List<Certificate> getCertRecsToAdd() throws Exception
			{
				 List<Certificate> retCerts = new ArrayList<Certificate>();
				 

				 
				 return retCerts;
			}

			@Override
			protected void doAssertions() throws Exception		
			{
				assertEquals(0, recordPrinter.getRecordCount());
			}
		}.perform();
	}	
}
