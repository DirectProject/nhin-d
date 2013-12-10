package org.nhindirect.dns.tools;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.ConfigServiceDNSStore;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.Type;

import junit.framework.TestCase;

public class DNSRecordCommands_addSAORecords_Test extends TestCase 
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected int port;
		protected DNSServer server = null;
		protected ConfigurationServiceProxy proxy;
		protected DNSRecordCommands recordCommands;
		
		protected Record toRecord(DnsRecord rec) throws Exception
		{			
			return Record.newRecord(Name.fromString(rec.getName()), rec.getType(), rec.getDclass(), rec.getTtl(), rec.getData());
		}
		
		protected List<Record> getSOARecordsInStore() throws Exception
		{
			DnsRecord[] records = proxy.getDNSByType(Type.SOA);
			
			List<Record> retVal;
			
			if (records == null || records.length == 0)
				retVal = Collections.emptyList();
			else
			{
				retVal = new ArrayList<Record>();
				for (DnsRecord record : records)
					retVal.add(toRecord(record));
			}
			
			return retVal;
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
			
		}
		
		@Override
		protected void tearDownMocks() throws Exception
		{
			if (server != null)
				server.stop();
		}	
		
		@Override
		protected void performInner() throws Exception
		{
			recordCommands = new DNSRecordCommands(proxy);
			
			List<Record> recordsToAdd = getRecordsToAdd();
			
			
			for (Record recordToAdd : recordsToAdd)
			{				
				SOARecord rec = (SOARecord)recordToAdd;
				String[] command = {rec.getName().toString(), rec.getHost().toString(), rec.getAdmin().toString(),
						Long.toString(rec.getSerial()), Long.toString(rec.getTTL()), Long.toString(rec.getRefresh()),
						Long.toString(rec.getRetry()), Long.toString(rec.getExpire()), Long.toString(rec.getMinimum())};
				recordCommands.addSOA(command);
			}
			
			
			doAssertions(getSOARecordsInStore());
		}		
		
		private void cleanRecords() throws Exception
		{
			DnsRecord[] rec = proxy.getDNSByType(Type.ANY);
			
			if (rec != null && rec.length > 0)
				proxy.removeDNS(rec);
			
			rec = proxy.getDNSByType(Type.ANY);
			
			assertNull(rec);
			
		}		
				
		
		protected abstract List<Record> getRecordsToAdd() throws Exception;
		
		protected abstract void doAssertions(List<Record> records) throws Exception;
	}
	
	public void testAddSOA_AssertRecordAdded() throws Exception 
	{
		
		
		new TestPlan()
		{
			private Record addRecord;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				List<Record> addRecords = new ArrayList<Record>();
				addRecord = new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600);
				
				addRecords.add(addRecord);
								
				return addRecords;				
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(1, records.size());
				
				Record rec = records.iterator().next();
				assertEquals(addRecord, rec);
				
			}

		}.perform();
	}
	
	public void testAddDupSOA_AssertOneEntry() throws Exception 
	{
		new TestPlan()
		{
			private Record addRecord;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				List<Record> addRecords = new ArrayList<Record>();

				addRecords.add(addRecord = new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600));	
				
				addRecords.add(new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600));	
				
				return addRecords;				
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof RuntimeException);
				assertNotNull(exception.getCause());
				assertTrue(exception.getCause() instanceof RemoteException);

				// make sure the first command worked
				Collection<Record> records = getSOARecordsInStore();
				
				assertNotNull(records);
				assertEquals(1, records.size());
				Record rec = records.iterator().next();
				assertEquals(addRecord, rec);
				
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{
	
			}

		}.perform();
	}	
	
	public void testAddMultipleSOARecords_AssertRecordsAdded() throws Exception 
	{
		new TestPlan()
		{
			private List<Record> addRecords;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				addRecords = new ArrayList<Record>();
				addRecords.add(new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600));			
				
				addRecords.add(new SOARecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example2.domain.com."),
						Name.fromString("gm2552.example2.domain.com."), 1, 3600, 60, 60, 3600));	
				
				addRecords.add(new SOARecord(Name.fromString("example3.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example3.domain.com."),
						Name.fromString("gm2552.example3.domain.com."), 1, 3600, 60, 60, 3600));					
				
				return addRecords;				
			}
			
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(3, records.size());
				
				for (Record record : addRecords)
				{
					int index = records.indexOf(record);
					assertTrue(index > -1);
					Record checkRecord = records.get(index);
					assertEquals(record, checkRecord);
				}
			}

		}.perform();
	}	
	
		
}
