package org.nhindirect.dns.tools;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.axis.AxisFault;
import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.ConfigServiceDNSStore;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

public class DNSRecordCommands_ensureARecords_Test extends TestCase
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
		
		protected List<Record> getARecordsInStore() throws Exception
		{
			DnsRecord[] records = proxy.getDNSByType(Type.A);
			
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
				ARecord rec = (ARecord)recordToAdd;
				String[] command = {rec.getName().toString(), rec.getAddress().getHostAddress(), Long.toString(rec.getTTL())};
				recordCommands.ensureANAME(command);
			}
			
			
			doAssertions(getARecordsInStore());
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
	
	public void testEnsureAName_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				List<Record> addRecords = new ArrayList<Record>();
				addRecords.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
								
				return addRecords;				
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(1, records.size());
				
				Record rec = records.iterator().next();
				assertEquals("example.domain.com.", rec.getName().toString());
				assertEquals(3600, rec.getTTL());
				assertEquals(Type.A, rec.getType());
				assertEquals(DClass.IN, rec.getDClass());
				
				ARecord aRec = (ARecord)rec;
				assertEquals(aRec.getAddress().getHostAddress(), "127.0.0.1");
				
			}

		}.perform();
	}
	
	public void testEnsureAName_AssertOneEntry() throws Exception 
	{
		new TestPlan()
		{
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				List<Record> addRecords = new ArrayList<Record>();
				addRecords.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				addRecords.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));		
				
				return addRecords;				
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
			
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{
				// make sure the first command worked

				assertNotNull(records);
				assertEquals(1, records.size());
				
				Record rec = records.iterator().next();
				assertEquals("example.domain.com.", rec.getName().toString());
				assertEquals(3600, rec.getTTL());
				assertEquals(Type.A, rec.getType());
				assertEquals(DClass.IN, rec.getDClass());
				
				ARecord aRec = (ARecord)rec;
				assertEquals(aRec.getAddress().getHostAddress(), "127.0.0.1");	
			}

		}.perform();
	}	
	
	public void testEnsureMultipleRecords_AssertRecordsAdded() throws Exception 
	{
		new TestPlan()
		{
			private List<Record> addRecords;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				addRecords = new ArrayList<Record>();
				addRecords.add(new ARecord(Name.fromString("example1.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				addRecords.add(new ARecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.2")));		
				addRecords.add(new ARecord(Name.fromString("example3.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.3")));
				
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
	
	public void testEnsureMultipleRecords_SameNameDiffIP_AssertRecordsAdded() throws Exception 
	{
		new TestPlan()
		{
			private List<Record> addRecords;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				addRecords = new ArrayList<Record>();
				addRecords.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				addRecords.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.2")));		
				addRecords.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.3")));
				
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
	
	public void testFailToEnsure_invalidProxy_AssertException() throws Exception 
	{
		new TestPlan()
		{
			private List<Record> addRecords;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				addRecords = new ArrayList<Record>();
				addRecords.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				
				this.recordCommands.setConfigurationProxy(new ConfigurationServiceProxy("http://localhost:7777/bogusendpoint"));
				return addRecords;				
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertNotNull(exception);
				assertTrue(exception instanceof RuntimeException);
				assertTrue(exception.getCause() instanceof AxisFault);
			}
			
			@Override			
			protected void doAssertions(List<Record> recordsMatched) throws Exception		
			{	
			}
		}.perform();
	}			
}
