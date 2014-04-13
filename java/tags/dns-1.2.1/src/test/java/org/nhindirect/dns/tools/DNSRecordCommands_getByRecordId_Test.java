package org.nhindirect.dns.tools;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.Type;

public class DNSRecordCommands_getByRecordId_Test extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{
        protected MockDNSRecordPrinter recordPrinter;
		
		protected int port;
		protected DNSServer server = null;
		protected ConfigurationServiceProxy proxy;
		protected DNSRecordCommands recordCommands;
		
		protected Record toRecord(DnsRecord rec) throws Exception
		{			
			return Record.newRecord(Name.fromString(rec.getName()), rec.getType(), rec.getDclass(), rec.getTtl(), rec.getData());
		}

		protected DnsRecord fromRecord(Record rec) throws Exception
		{			
			DnsRecord newRec = new DnsRecord();
			newRec.setData(rec.rdataToWireCanonical());
			newRec.setDclass(rec.getDClass());
			newRec.setName(rec.getName().toString());
			newRec.setTtl(rec.getTTL());
			newRec.setType(rec.getType());
			
			return newRec;
		}
				
		protected List<Record> getRecordsInStore(int type) throws Exception
		{
			DnsRecord[] records = proxy.getDNSByType(type);
			
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
			
			recordPrinter = new MockDNSRecordPrinter();
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
			recordCommands.setRecordPrinter(recordPrinter);
			
			List<Record> recordsToAdd = getRecordsToAdd();
			
			
			DnsRecord[] addRecs = new DnsRecord[recordsToAdd.size()];
			int cnt = 0;
			for (Record recordToAdd : recordsToAdd)
			{				
				addRecs[cnt++] = fromRecord(recordToAdd);
			}
			
			proxy.addDNS(addRecs);
			
			addRecs = proxy.getDNSByType(Type.ANY);					
			
			List<DnsRecord> recordsToFetch = getRecordsToFetch(Arrays.asList(addRecs));
			for (DnsRecord recordToRemove : recordsToFetch)
			{
				switch (recordToRemove.getType())
				{
					case Type.A:
						recordCommands.getANAME(new String[] {Long.toString(recordToRemove.getId())});
						break;
					case Type.MX:
						recordCommands.getMX(new String[] {Long.toString(recordToRemove.getId())});
						break;
					case Type.SOA:
						recordCommands.getSOA(new String[] {Long.toString(recordToRemove.getId())});
						break;

				}						
			}
						
			doAssertions();
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
		
		protected abstract List<DnsRecord> getRecordsToFetch(List<DnsRecord> addedRecords) throws Exception;
		
		protected abstract void doAssertions() throws Exception;
	}

	public void testGetAllRecords_AssertAllRecordFetched() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				List<Record> recordsToAdd = new ArrayList<Record>();
				
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.2")));
				recordsToAdd.add(new MXRecord(Name.fromString("example.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail1.example.domain.com.")));
				recordsToAdd.add(new MXRecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail2.exampl2.domain.com.")));

				recordsToAdd.add(new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600));						
				
				return recordsToAdd;						
			}

			@Override
			protected List<DnsRecord> getRecordsToFetch(List<DnsRecord> addedRecords) throws Exception
			{
				return addedRecords;
			}

			@Override
			protected void doAssertions() throws Exception
			{
				assertEquals(5, recordPrinter.printRecordCalled);
			}
		}.perform();
	}		
	
	public void testGetOnlyARecords_AssertOnlyARecordsFetched() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				List<Record> recordsToAdd = new ArrayList<Record>();
				
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.2")));
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.3")));
				recordsToAdd.add(new MXRecord(Name.fromString("example.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail1.example.domain.com.")));
				recordsToAdd.add(new MXRecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail2.exampl2.domain.com.")));

				recordsToAdd.add(new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600));						
				
				return recordsToAdd;						
			}

			@Override
			protected List<DnsRecord> getRecordsToFetch(List<DnsRecord> addedRecords) throws Exception
			{
				List<DnsRecord> retVal = new ArrayList<DnsRecord>();
			
				for (DnsRecord addedRecord : addedRecords)
				{
					if (addedRecord.getType() == Type.A)
						retVal.add(addedRecord);
				}
				
				return retVal;
			}

			@Override
			protected void doAssertions() throws Exception
			{
				assertEquals(3, recordPrinter.printRecordCalled);
			}
		}.perform();
	}		

	public void testGetAllExceptARecords_AssertNoARecordsFetched() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				List<Record> recordsToAdd = new ArrayList<Record>();
				
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));

				recordsToAdd.add(new MXRecord(Name.fromString("example.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail1.example.domain.com.")));
				recordsToAdd.add(new MXRecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail2.exampl2.domain.com.")));

				
				recordsToAdd.add(new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600));						

				recordsToAdd.add(new SOARecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example2.domain.com."),
						Name.fromString("gm2552.example2.domain.com."), 1, 3600, 60, 60, 3600));						

				return recordsToAdd;						
			}

			@Override
			protected List<DnsRecord> getRecordsToFetch(List<DnsRecord> addedRecords) throws Exception
			{
				List<DnsRecord> retVal = new ArrayList<DnsRecord>();
			
				for (DnsRecord addedRecord : addedRecords)
				{
					if (addedRecord.getType() != Type.A)
						retVal.add(addedRecord);
				}
				
				return retVal;
			}

			@Override
			protected void doAssertions() throws Exception
			{
				assertEquals(4, recordPrinter.printRecordCalled);
			}
		}.perform();
	}			
	
	public void testReturnNoRecords_noIdsProvided_AssertNoRecordsFetched() throws Exception 
	{		
		new TestPlan()
		{
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				List<Record> recordsToAdd = new ArrayList<Record>();
				
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));

				recordsToAdd.add(new MXRecord(Name.fromString("example.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail1.example.domain.com.")));
				recordsToAdd.add(new MXRecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail2.exampl2.domain.com.")));

				
				recordsToAdd.add(new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600));						

				recordsToAdd.add(new SOARecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example2.domain.com."),
						Name.fromString("gm2552.example2.domain.com."), 1, 3600, 60, 60, 3600));						

				return recordsToAdd;						
			}

			@Override
			protected List<DnsRecord> getRecordsToFetch(List<DnsRecord> addedRecords) throws Exception
			{
				List<DnsRecord> retVal = Collections.emptyList();
				return retVal;
			}

			@Override
			protected void doAssertions() throws Exception
			{
				assertEquals(0, recordPrinter.printRecordCalled);
			}
		}.perform();
	}		
	
	public void testReturnNoRecords_noMatchingIds_AssertNoRecordsFetched() throws Exception 
	{
		new TestPlan()
		{
			private List<Record> recordsToAdd;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				recordsToAdd = new ArrayList<Record>();
				
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				
				return recordsToAdd;						
			}
			
			@Override
			protected List<DnsRecord> getRecordsToFetch(List<DnsRecord> addedRecords) throws Exception
			{	
				DnsRecord largestRecord = null;
				// find the record with the highest id and add 1 to it
				for (DnsRecord record : addedRecords)
				{
					if (largestRecord == null || largestRecord.getId() < record.getId())
						largestRecord = record;						
				}
				
				largestRecord.setId(largestRecord.getId() + 1);
				
				return new ArrayList<DnsRecord>(Arrays.asList(largestRecord));
			}

			
			@Override
			protected void doAssertions() throws Exception
			{
				assertEquals(0, recordPrinter.printRecordCalled);
			}

		}.perform();
	}
	
	public void testFailureToGetRecords_invalidProxy_AssertException() throws Exception 
	{
		new TestPlan()
		{
			private List<Record> recordsToAdd;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				recordsToAdd = new ArrayList<Record>();
				
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				
				return recordsToAdd;						
			}
			
			@Override
			protected List<DnsRecord> getRecordsToFetch(List<DnsRecord> addedRecords) throws Exception
			{				
				this.recordCommands.setConfigurationProxy(new ConfigurationServiceProxy("http://localhost:7777/bogusendpoint"));
				return addedRecords;
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertNotNull(exception);
				assertTrue(exception instanceof RuntimeException);
				assertTrue(exception.getCause() instanceof AxisFault);
				
				
			}
			
			@Override
			protected void doAssertions() throws Exception
			{

			}

		}.perform();
	}		
}
