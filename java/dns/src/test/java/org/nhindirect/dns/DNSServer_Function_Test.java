package org.nhindirect.dns;

import java.net.Inet4Address;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.nhindirect.dns.util.DNSRecordUtil;
import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

public class DNSServer_Function_Test extends TestCase
{
	private static class Query
	{
		public String name;
		public int type;
		
		public Query(String name, int type)
		{
			this.name = name;
			this.type = type;
		}
	}
	
	abstract class TestPlan extends BaseTestPlan 
	{
		protected int port;
		protected DNSServer server = null;
		protected ConfigurationServiceProxy proxy;
		
		
		@Override
		protected void setupMocks() throws Exception
		{
			if (!ConfigServiceRunner.isServiceRunning())
				ConfigServiceRunner.startConfigService();

			proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
			
			cleanRecords();
			
			addRecords();
			
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
			Inet4Address.getLocalHost();
			ExtendedResolver resolver = new ExtendedResolver(new String[] {"127.0.0.1", Inet4Address.getLocalHost().getHostAddress()});

			resolver.setTCP(false);
			resolver.setPort(port);
			
			Collection<Record> retrievedRecord = new ArrayList<Record>();
			
			Collection<Query> queries = getTestQueries();
			for (Query query : queries)
			{
				Lookup lu = new Lookup(new Name(query.name), query.type);
				Cache ch = lu.getDefaultCache(DClass.IN);
				ch.clearCache();
				lu.setResolver(resolver);
								
				Record[] retRecords = lu.run();
				if (retRecords != null && retRecords.length > 0)
					retrievedRecord.addAll(Arrays.asList(retRecords));
			}
			
			doAssertions(retrievedRecord);
		}
		
		private void cleanRecords() throws Exception
		{
			DnsRecord[] rec = proxy.getDNSByType(Type.ANY);
			
			if (rec != null && rec.length > 0)
				proxy.removeDNS(rec);
			
			rec = proxy.getDNSByType(Type.ANY);
			
			assertNull(rec);
		}
		
		protected abstract void addRecords() throws Exception;
		
		protected abstract Collection<Query> getTestQueries() throws Exception;
		
		protected abstract void doAssertions(Collection<Record> records) throws Exception;

	}
	
	public void testQueryARecord_AssertRecordsRetrieved() throws Exception 
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createARecord("example.domain.com", "127.0.0.1");
				recs.add(rec);
				
				rec = DNSRecordUtil.createARecord("example2.domain.com", "127.0.0.1");
				recs.add(rec);
								
				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));
								
			}
			
			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("example2.domain.com", Type.A));
				
				return queries;
			}
			
			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(1, records.size());
				assertEquals("example2.domain.com.", records.iterator().next().getName().toString());
			}
		}.perform();
	}
	
	public void testQueryMutliARecords_AssertRecordsRetrieved() throws Exception 
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createARecord("example.domain.com", "127.0.0.1");
				recs.add(rec);
				
				rec = DNSRecordUtil.createARecord("example.domain.com", "127.0.0.2");
				recs.add(rec);
								
				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));
								
			}
			
			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("example.domain.com", Type.A));
				
				return queries;
			}
			
			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(2, records.size());
				assertEquals("example.domain.com.", records.iterator().next().getName().toString());
			}
		}.perform();	
	}
	
	public void testQueryARecords_AssertNoRecordsRetrieved() throws Exception 
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				// add no records
								
			}
			
			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("example.domain.com", Type.A));
				
				return queries;
			}
			
			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(0, records.size());
			}
		}.perform();	
	}	
}
