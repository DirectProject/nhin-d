package org.nhindirect.dns.tools;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.ConfigServiceDNSStore;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.tools.DNSManager;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.Type;

import junit.framework.TestCase;

public class DNSManager_functional_Test extends TestCase 
{	
	abstract class TestPlan extends BaseTestPlan 
	{
		protected static final int ERROR_FUNCTION_FAILED = 0x7877;  // bogus error code for our test purposes 
		
		protected int port;
		protected DNSServer server = null;
		protected ConfigurationServiceProxy proxy;
		
		protected Record toRecord(DnsRecord rec) throws Exception
		{
			
			return Record.newRecord(Name.fromString(rec.getName()), rec.getType(), rec.getDclass(), rec.getTtl(), rec.getData());
		}
		
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
			DNSManager.setExitOnEndCommands(false);
		}
		
		@Override
		protected void performInner() throws Exception
		{
			Collection<String[]> commands = getCommands();
			
			
			for (String[] commandSet : commands)
			{

				String[] args = new String[commandSet.length + 2];
				args[0] =  "configurl";  
				args[1] =  ConfigServiceRunner.getConfigServiceURL();
				System.arraycopy(commandSet, 0, args, 2, commandSet.length);
				DNSManager.main(args);
			}
			
			doAssertions();
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
			
			
			Certificate[] certs = proxy.getCertificatesForOwner(null, null);
			if (certs != null && certs.length > 0)
			{
				long[] ids = new long[certs.length];
				int cnt = 0;
				for (Certificate cert : certs)
					ids[cnt++] = cert.getId();
				
				proxy.removeCertificates(ids);
			}
			
			certs = proxy.getCertificatesForOwner("", null);
			
			assertNull(certs);
		}
		
		protected abstract Collection<String[]> getCommands() throws Exception;
		
		protected abstract void doAssertions() throws Exception;
	}
	
	public void testAddAName_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_aname_add", "example.domain.com", "127.0.0.1", "3600"};
				commands.add(command);
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.A);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("example.domain.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.A, rec.getType());
				assertEquals(DClass.IN, rec.getDclass());
				
				ARecord aRec = (ARecord)toRecord(rec);
				assertEquals(aRec.getAddress().getHostAddress(), "127.0.0.1");
				
			}

		}.perform();
	}
	
	public void testAddDupAName_AssertOneEntry() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_aname_add", "example.domain.com", "127.0.0.1", "3600"};
				commands.add(command);
				command = new String[] {"dns_aname_add", "example.domain.com", "127.0.0.1", "3600"};				
				commands.add(command);
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				// make sure the first command worked
				DnsRecord[] records = proxy.getDNSByType(Type.A);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("example.domain.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.A, rec.getType());
				assertEquals(DClass.IN, rec.getDclass());
				
				ARecord aRec = (ARecord)toRecord(rec);
				assertEquals(aRec.getAddress().getHostAddress(), "127.0.0.1");				
			}

		}.perform();
	}
	
	public void testAddANameEnsure_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_aname_ensure", "example.domain.com", "127.0.0.1", "3600"};
				commands.add(command);
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.A);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("example.domain.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.A, rec.getType());
				assertEquals(DClass.IN, rec.getDclass());
				
				ARecord aRec = (ARecord)toRecord(rec);
				assertEquals(aRec.getAddress().getHostAddress(), "127.0.0.1");
				
			}

		}.perform();
	}
	
	public void testDupANameEnsure_AssertOneEntry() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_aname_ensure", "example.domain.com", "127.0.0.1", "3600"};
				commands.add(command);
				command = new String[] {"dns_aname_ensure", "example.domain.com", "127.0.0.1", "3600"};				
				commands.add(command);
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				// make sure  first command worked
				DnsRecord[] records = proxy.getDNSByType(Type.A);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("example.domain.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.A, rec.getType());
				assertEquals(DClass.IN, rec.getDclass());
				
				ARecord aRec = (ARecord)toRecord(rec);
				assertEquals(aRec.getAddress().getHostAddress(), "127.0.0.1");				
			}

		}.perform();
	}
	
	
	public void testAddSOA_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_soa_add", "example.com", "ns1.example.com", "gm2552@example.com", "1", "3600"};
				commands.add(command);
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.SOA);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("example.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.SOA, rec.getType());
				
				SOARecord soaRec = (SOARecord)toRecord(rec);
				assertEquals(soaRec.getAdmin().toString(),  "gm2552\\@example.com.");
				
				assertEquals(soaRec.getDClass(),  DClass.IN);
				assertEquals(soaRec.getHost().toString(), "ns1.example.com.");
				assertEquals(soaRec.getName().toString(), "example.com.");
				assertEquals(soaRec.getTTL(), 3600);
				assertEquals(soaRec.getSerial(), 1);
			}

		}.perform();
	}
	
	public void testAddDupSOA_AssertOneEntry() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_soa_add", "example.com", "ns1.example.com", "gm2552@example.com", "1", "3600"};
				commands.add(command);
				command = new String[] {"dns_soa_add", "example.com", "ns1.example.com", "gm2552@example.com", "1", "3600"};
				commands.add(command);
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.SOA);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("example.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.SOA, rec.getType());
				
				SOARecord soaRec = (SOARecord)toRecord(rec);
				assertEquals(soaRec.getAdmin().toString(),  "gm2552\\@example.com.");
				
				assertEquals(soaRec.getDClass(),  DClass.IN);
				assertEquals(soaRec.getHost().toString(), "ns1.example.com.");
				assertEquals(soaRec.getName().toString(), "example.com.");
				assertEquals(soaRec.getTTL(), 3600);
				assertEquals(soaRec.getSerial(), 1);
			}

		}.perform();
	}
	
	public void testAddSOAEnsure_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_soa_ensure", "example.com", "ns1.example.com", "gm2552@example.com", "1", "3600"};
				commands.add(command);
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.SOA);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("example.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.SOA, rec.getType());
				
				SOARecord soaRec = (SOARecord)toRecord(rec);
				assertEquals(soaRec.getAdmin().toString(),  "gm2552\\@example.com.");
				
				assertEquals(soaRec.getDClass(),  DClass.IN);
				assertEquals(soaRec.getHost().toString(), "ns1.example.com.");
				assertEquals(soaRec.getName().toString(), "example.com.");
				assertEquals(soaRec.getTTL(), 3600);
				assertEquals(soaRec.getSerial(), 1);
				
			}

		}.perform();
	}
	
	public void testDupSOAEnsure_AssertOneEntry() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_soa_ensure", "example.com", "ns1.example.com", "gm2552@example.com", "1", "3600"};
				commands.add(command);
				command = new String[] {"dns_soa_ensure", "example.com", "ns1.example.com", "gm2552@example.com", "1", "3600"};
				commands.add(command);
				
				return commands;			
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.SOA);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("example.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.SOA, rec.getType());
				
				SOARecord soaRec = (SOARecord)toRecord(rec);
				assertEquals(soaRec.getAdmin().toString(),  "gm2552\\@example.com.");
				
				assertEquals(soaRec.getDClass(),  DClass.IN);
				assertEquals(soaRec.getHost().toString(), "ns1.example.com.");
				assertEquals(soaRec.getName().toString(), "example.com.");
				assertEquals(soaRec.getTTL(), 3600);
				assertEquals(soaRec.getSerial(), 1);		
			}

		}.perform();
	}	
	
	public void testAddMX_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_mx_add", "domain.com", "mail1.domain.com", "3600", "1"};
				commands.add(command);
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.MX);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("domain.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.MX, rec.getType());
				assertEquals(DClass.IN, rec.getDclass());
				
				MXRecord mxRec = (MXRecord)toRecord(rec);
				assertEquals(mxRec.getTarget().toString(), "mail1.domain.com.");
				
			}

		}.perform();
	}
	
	public void testAddDupMX_AssertOneEntry() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_mx_add", "domain.com", "mail1.domain.com", "3600", "1"};
				commands.add(command);
				command = new String[] {"dns_mx_add", "domain.com", "mail1.domain.com", "3600", "1"};
				commands.add(command);				
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.MX);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("domain.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.MX, rec.getType());
				assertEquals(DClass.IN, rec.getDclass());
				
				MXRecord mxRec = (MXRecord)toRecord(rec);
				assertEquals(mxRec.getTarget().toString(), "mail1.domain.com.");			
			}

		}.perform();
	}
	
	public void testAddMXEnsure_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_mx_ensure", "domain.com", "mail1.domain.com", "3600", "1"};
				commands.add(command);				
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.MX);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("domain.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.MX, rec.getType());
				assertEquals(DClass.IN, rec.getDclass());
				
				MXRecord mxRec = (MXRecord)toRecord(rec);
				assertEquals(mxRec.getTarget().toString(), "mail1.domain.com.");
				
			}

		}.perform();
	}
	
	public void testDupMXEnsure_AssertOneEntry() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_mx_ensure", "domain.com", "mail1.domain.com", "3600", "1"};
				commands.add(command);				
				command = new String[] {"dns_mx_ensure", "domain.com", "mail1.domain.com", "3600", "1"};
				commands.add(command);				
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.MX);
				assertNotNull(records);
				assertEquals(1, records.length);
				
				DnsRecord rec = records[0];
				assertEquals("domain.com.", rec.getName());
				assertEquals(3600, rec.getTtl());
				assertEquals(Type.MX, rec.getType());
				assertEquals(DClass.IN, rec.getDclass());
				
				MXRecord mxRec = (MXRecord)toRecord(rec);
				assertEquals(mxRec.getTarget().toString(), "mail1.domain.com.");			
			}

		}.perform();
	}
		
	
	public void testGetAll_AssertAllRecords() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected Collection<String[]> getCommands() throws Exception
			{
				Collection<String[]> commands = new ArrayList<String[]>();
				
				String[] command = new String[] {"dns_mx_ensure", "domain.com", "mail1.domain.com", "3600", "1"};
				commands.add(command);				
				command = new String[] {"dns_soa_ensure", "domain.com", "ns1.domain.com", "gm2552@domain.com", "1", "3600"};
				commands.add(command);				
				command = new String[] {"dns_aname_ensure", "ns1.domain.com", "10.45.110.23", "3600"};
				commands.add(command);					
				command = new String[] {"dns_get_all"};
				commands.add(command);					
				
				return commands;				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				DnsRecord[] records = proxy.getDNSByType(Type.ANY);
				assertNotNull(records);
				assertEquals(3, records.length);		
			}

		}.perform();
	}	
}

