package org.nhindirect.dns.tools;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import junit.framework.TestCase;

public class DNSRecordCommands_getAll_Test extends TestCase 
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
			
			
			if (recordsToAdd.size() > 0)
			{
				DnsRecord[] addRecs = new DnsRecord[recordsToAdd.size()];
				int cnt = 0;
				for (Record recordToAdd : recordsToAdd)
				{				
					addRecs[cnt++] = fromRecord(recordToAdd);
				}
				
				proxy.addDNS(addRecs);
			}					

			recordCommands.getAll(new String[] {});

			List<Record> matchedRecords = new ArrayList<Record>();
			for (DnsRecord matchedRecord : recordPrinter.printedRecords)
				matchedRecords.add(toRecord(matchedRecord));
						
			doAssertions(matchedRecords);
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
		
		protected abstract void doAssertions(List<Record> recordsMatched) throws Exception;		
	}

	
	public void testGetRecords_AssertAllRecordFetched() throws Exception 
	{		
		new TestPlan()
		{
			private List<Record> recordsToAdd;
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				recordsToAdd = new ArrayList<Record>();
				
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.1")));
				recordsToAdd.add(new ARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.2")));
				
				recordsToAdd.add(new MXRecord(Name.fromString("example.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail1.example.domain.com.")));
				recordsToAdd.add(new MXRecord(Name.fromString("example.domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail2.exampl2.domain.com.")));

				recordsToAdd.add(new SOARecord(Name.fromString("example.domain.com."), DClass.IN, 3600, Name.fromString("ns1.example.domain.com."),
						Name.fromString("gm2552.example.domain.com."), 1, 3600, 60, 60, 3600));						
		
				
				recordsToAdd.add(new ARecord(Name.fromString("domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.5")));				
				
				recordsToAdd.add(new MXRecord(Name.fromString("domain.com."), DClass.IN, 3600, 
						1, Name.fromString("mail1.domain.com.")));
				
				recordsToAdd.add(new SOARecord(Name.fromString("domain.com."), DClass.IN, 3600, Name.fromString("ns1.domain.com."),
						Name.fromString("gm2552.domain.com."), 1, 3600, 60, 60, 3600));						

				recordsToAdd.add(new ARecord(Name.fromString("example2.domain.com."), DClass.IN, 3600, InetAddress.getByName("127.0.0.5")));						
				
				return recordsToAdd;										
			}

			@Override
			protected void doAssertions(List<Record> recordsMatched) throws Exception		
			{
				assertNotNull(recordsMatched);
				assertEquals(9, recordsMatched.size());
				
				for (Record record : recordsToAdd)
				{
					int index = recordsToAdd.indexOf(record);
					assertTrue(index > -1);
					Record checkRecord = recordsToAdd.get(index);
					assertEquals(record, checkRecord);
				}				
			}
		}.perform();
	}		
	
	public void testGetRecords_emptyStore_AssertNoRecordsFetched() throws Exception 
	{		
		new TestPlan()
		{
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{

				return Collections.emptyList();									
			}


			@Override
			protected void doAssertions(List<Record> recordsMatched) throws Exception		
			{
				assertNotNull(recordsMatched);
				assertEquals(0, recordsMatched.size());				
			}
		}.perform();
	}		
	
	public void testFailureToFetchRecords_invalidProxy_AssertException() throws Exception 
	{		
		new TestPlan()
		{
			
			@Override
			protected List<Record> getRecordsToAdd() throws Exception
			{
				this.recordCommands.setConfigurationProxy(new ConfigurationServiceProxy("http://localhost:7777/bogusendpoint"));
				return Collections.emptyList();									
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
