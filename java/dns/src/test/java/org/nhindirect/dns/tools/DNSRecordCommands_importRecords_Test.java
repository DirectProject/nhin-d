package org.nhindirect.dns.tools;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.ConfigServiceDNSStore;
import org.nhindirect.dns.DNSServer;
import org.nhindirect.dns.DNSServerSettings;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;
import org.xbill.DNS.WireParseException;

import junit.framework.TestCase;

public class DNSRecordCommands_importRecords_Test extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected int port;
		protected DNSServer server = null;
		protected ConfigurationServiceProxy proxy;
		protected DNSRecordCommands recordCommands;
		
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
		
		protected Record toRecord(DnsRecord rec) throws Exception
		{			
			return Record.newRecord(Name.fromString(rec.getName()), rec.getType(), rec.getDclass(), rec.getTtl(), rec.getData());
		}
		
		protected Record fromFile(File file) throws Exception
		{
			return Record.fromWire(FileUtils.readFileToByteArray(file), Section.ANSWER);
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
			
			int type = getRecordTypeToImport();
			
			String[] fileToImport = {getRecordFileToImport().getAbsolutePath()};
			switch (type)
			{
				case Type.A:
					recordCommands.importAddress(fileToImport);
					break;
				case Type.MX:
					recordCommands.mXImport(fileToImport);
					break;					
				case Type.SOA:
					recordCommands.sOAImport(fileToImport);
					break;							
			}
					
			
			doAssertions(getRecordsInStore(type));
		}		
		
		private void cleanRecords() throws Exception
		{
			DnsRecord[] rec = proxy.getDNSByType(Type.ANY);
			
			if (rec != null && rec.length > 0)
				proxy.removeDNS(rec);
			
			rec = proxy.getDNSByType(Type.ANY);
			
			assertNull(rec);
			
		}		
				
		
		protected abstract File getRecordFileToImport() throws Exception;
		
		protected abstract int getRecordTypeToImport() throws Exception;
		
		protected abstract void doAssertions(List<Record> records) throws Exception;
	}
	
	public void testImportARecord_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			private String fileName = "src/test/resources/dnsrecords/example.domain.com.a";
			
			@Override
			protected File getRecordFileToImport() throws Exception
			{								
				return new File(fileName);				
			}
			
			@Override 
			protected int getRecordTypeToImport() throws Exception
			{
				return Type.A;
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{				
				assertNotNull(records);
				assertEquals(1, records.size());
				
				Record rec = fromFile(new File(fileName));
				Record checkRec = records.get(0);
				assertEquals(checkRec, rec);
				
			}

		}.perform();
	}		
	
	public void testImportMXRecord_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			private String fileName = "src/test/resources/dnsrecords/example.domain.com.mx";
			
			@Override
			protected File getRecordFileToImport() throws Exception
			{								
				return new File(fileName);				
			}
			
			@Override 
			protected int getRecordTypeToImport() throws Exception
			{
				return Type.MX;
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{				
				assertNotNull(records);
				assertEquals(1, records.size());
				
				Record rec = fromFile(new File(fileName));
				Record checkRec = records.get(0);
				assertEquals(checkRec, rec);
				
			}

		}.perform();
	}		
	
	public void testImportSOARecord_AssertRecordAdded() throws Exception 
	{
		new TestPlan()
		{
			private String fileName = "src/test/resources/dnsrecords/example.domain.com.soa";
			
			@Override
			protected File getRecordFileToImport() throws Exception
			{								
				return new File(fileName);				
			}
			
			@Override 
			protected int getRecordTypeToImport() throws Exception
			{
				return Type.SOA;
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{				
				assertNotNull(records);
				assertEquals(1, records.size());
				
				Record rec = fromFile(new File(fileName));
				Record checkRec = records.get(0);
				assertEquals(checkRec, rec);
				
			}

		}.perform();
	}	
	
	public void testImportRecord_fileNotFound_AssertException() throws Exception 
	{
		new TestPlan()
		{
			private String fileName = "src/test/resources/dnsrecords/example.domain.com.bogus";
			
			@Override
			protected File getRecordFileToImport() throws Exception
			{								
				return new File(fileName);				
			}
			
			@Override 
			protected int getRecordTypeToImport() throws Exception
			{
				return Type.SOA;
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertNotNull(exception);
				assertTrue(exception instanceof IllegalArgumentException);
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{				
				
			}

		}.perform();
	}		
	
	public void testImportRecord_corruptRecordFile_AssertException() throws Exception 
	{
		new TestPlan()
		{
			private String fileName = "src/test/resources/dnsrecords/example.domain.com.corrupt";
			
			@Override
			protected File getRecordFileToImport() throws Exception
			{								
				return new File(fileName);				
			}
			
			@Override 
			protected int getRecordTypeToImport() throws Exception
			{
				return Type.SOA;
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertNotNull(exception);
				assertTrue(exception instanceof RuntimeException);
				assertNotNull(exception.getCause());
				assertNotNull(exception.getCause() instanceof WireParseException);
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{				
				
			}

		}.perform();
	}			
	
	public void testImportRecord_incorrectType_AssertException() throws Exception 
	{
		new TestPlan()
		{
			private String fileName = "src/test/resources/dnsrecords/example.domain.com.a";
			
			@Override
			protected File getRecordFileToImport() throws Exception
			{								
				return new File(fileName);				
			}
			
			@Override 
			protected int getRecordTypeToImport() throws Exception
			{
				return Type.MX;
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertNotNull(exception);
				assertTrue(exception instanceof IllegalArgumentException);
			}
			
			@Override
			protected void doAssertions(List<Record> records) throws Exception
			{				
				
			}

		}.perform();
	}		
}
