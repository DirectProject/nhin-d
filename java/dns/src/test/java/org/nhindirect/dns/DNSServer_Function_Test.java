package org.nhindirect.dns;


import java.net.Inet4Address;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.mina.util.AvailablePortFinder;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhind.config.EntityStatus;
import org.nhindirect.dns.util.BaseTestPlan;
import org.nhindirect.dns.util.ConfigServiceRunner;
import org.nhindirect.dns.util.DNSRecordUtil;
import org.nhindirect.dns.util.IPUtils;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;
import org.xbill.DNS.security.CERTConverter;

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

	private Certificate xCertToCert(X509Certificate cert) throws Exception
	{
		Certificate retVal = new Certificate();
		retVal.setOwner(DNSRecordUtil.getCertOwner(cert));
		retVal.setData(cert.getEncoded());

		return retVal;
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
			ExtendedResolver resolver = new ExtendedResolver(IPUtils.getDNSLocalIps());
			resolver.setTimeout(300);

			resolver.setTCP(true);
			resolver.setPort(port);

			Collection<Record> retrievedRecord = new ArrayList<Record>();

			Collection<Query> queries = getTestQueries();
			for (Query query : queries)
			{
				Lookup lu = new Lookup(new Name(query.name), query.type);
				Cache ch = Lookup.getDefaultCache(DClass.IN);
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

		protected abstract void addRecords() throws Exception;

		protected abstract Collection<Query> getTestQueries() throws Exception;

		protected abstract void doAssertions(Collection<Record> records) throws Exception;

	}

	public void testQueryARecord_AssertRecordsRetrieved_NoSOA() throws Exception
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

	public void testQueryARecord_AssertRecordsRetrieved_SOARecord() throws Exception
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

				rec = DNSRecordUtil.createARecord("sub2.example2.domain.com", "127.0.0.1");
				recs.add(rec);

				rec = DNSRecordUtil.createSOARecord("domain.com", "nsserver.domain.com","master.domain.com");
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("sub2.example2.domain.com", Type.A));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(1, records.size());
				assertEquals("sub2.example2.domain.com.", records.iterator().next().getName().toString());
			}
		}.perform();
	}

/*
	public void testQueryARecord_noRecordInDNSServer_assertDelegation() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createARecord("example.domain.com", "127.0.0.1");
				recs.add(rec);

				rec = DNSRecordUtil.createNSRecord("sub.example.domain.com", "127.0.0.3");
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("sub.example.domain.com", Type.A));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(1, records.size());
				Record rec = records.iterator().next();
				assertEquals("sub.example.domain.com.", rec.getName().toString());
				assertEquals(Type.NS, rec.getType());
			}
		}.perform();
	}
*/
	
	public void testQueryARecordByAny_AssertRecordsRetrieved() throws Exception
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
				queries.add(new Query("example2.domain.com", Type.ANY));

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

				rec = DNSRecordUtil.createSOARecord("domain.com", "nsserver.domain.com","master.domain.com");
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

	public void testAnyQueryType_multipleTypesInRecord_AssertRecordsRetrieved() throws Exception
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

				rec = DNSRecordUtil.createMXRecord("example.domain.com", "domain.com", 1);
				recs.add(rec);
				
				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("example.domain.com", Type.ANY));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(3, records.size());
				assertEquals("example.domain.com.", records.iterator().next().getName().toString());
			}
		}.perform();		
	}
	
	public void testQueryCERTRecords_AssertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				// add some CERT records
				ArrayList<Certificate> recs = new ArrayList<Certificate>();

				X509Certificate cert = DNSRecordUtil.loadCertificate("bob.der");
				Certificate addCert = xCertToCert(cert);
				recs.add(addCert);

				cert = DNSRecordUtil.loadCertificate("gm2552.der");
				addCert = xCertToCert(cert);
				recs.add(addCert);

				cert = DNSRecordUtil.loadCertificate("ryan.der");
				addCert = xCertToCert(cert);
				recs.add(addCert);

				proxy.addCertificates(recs.toArray(new Certificate[recs.size()]));


				ArrayList<DnsRecord> soaRecs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createSOARecord("securehealthemail.com", "nsserver.securehealthemail.com","master.securehealthemail.com");
				soaRecs.add(rec);

				proxy.addDNS(soaRecs.toArray(new DnsRecord[soaRecs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("gm2552.securehealthemail.com", Type.CERT));
				queries.add(new Query("ryan.securehealthemail.com", Type.ANY));
				queries.add(new Query("bob.somewhere.com", Type.A));

				return queries;

			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(2, records.size());

				boolean foundGreg = false;
				boolean foundRyan = false;
				for (Record record : records)
				{
					assertTrue(record instanceof CERTRecord);

					X509Certificate cert = (X509Certificate)CERTConverter.parseRecord((CERTRecord)record);
					assertNotNull(cert);

					if (DNSRecordUtil.getCertOwner(cert).equals("gm2552@securehealthemail.com"))
						foundGreg = true;
					else if (DNSRecordUtil.getCertOwner(cert).equals("ryan@securehealthemail.com"))
						foundRyan = true;
				}

				assertTrue(foundGreg);
				assertTrue(foundRyan);
			}
		}.perform();
	}

	public void testQueryCERTRecords_AssertNoRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("gm2552.securehealthemail.com", Type.CERT));
				queries.add(new Query("ryan.securehealthemail.com", Type.ANY));
				queries.add(new Query("bob.somewhere.com", Type.A));

				return queries;

			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(0, records.size());

			}
		}.perform();
	}

	public void testQueryIPKIXCERTRecords_AssertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				// add some CERT records
				ArrayList<Certificate> recs = new ArrayList<Certificate>();

				X509Certificate cert = DNSRecordUtil.loadCertificate("ryan.der");
				Certificate addCert = xCertToCert(cert);
				recs.add(addCert);

				cert = DNSRecordUtil.loadCertificate("gm2552.der");
				addCert = xCertToCert(cert);
				recs.add(addCert);

				Certificate ipkixCert = new Certificate();
				ipkixCert.setOwner("somewhere.com");
				ipkixCert.setData("http://localhost/somewhere.der".getBytes());
				ipkixCert.setPrivateKey(false);
				ipkixCert.setStatus(EntityStatus.ENABLED);
				recs.add(ipkixCert);
				
				proxy.addCertificates(recs.toArray(new Certificate[recs.size()]));


				ArrayList<DnsRecord> soaRecs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createSOARecord("securehealthemail.com", "nsserver.securehealthemail.com","master.securehealthemail.com");
				soaRecs.add(rec);

				rec = DNSRecordUtil.createSOARecord("somewhere.com", "nsserver.somewhere.com","master.somewhere.com");
				soaRecs.add(rec);
				
				proxy.addDNS(soaRecs.toArray(new DnsRecord[soaRecs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("somewhere.com", Type.CERT));
				queries.add(new Query("ryan.securehealthemail.com", Type.ANY));
				queries.add(new Query("bob.somewhere.com", Type.A));
				
				return queries;

			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(2, records.size());

				boolean foundSomewhere = false;
				boolean foundRyan = false;
				for (Record record : records)
				{
					assertTrue(record instanceof CERTRecord);

					CERTRecord certRect = (CERTRecord)record;
					
					if (certRect.getCertType() == CERTRecord.URI)
					{
					
						assertEquals("http://localhost/somewhere.der", new String(certRect.getCert()));
						foundSomewhere = true;
					}
					else
					{
						X509Certificate cert = (X509Certificate)CERTConverter.parseRecord((CERTRecord)record);
						assertNotNull(cert);
						assertTrue(DNSRecordUtil.getCertOwner(cert).equals("ryan@securehealthemail.com"));
						foundRyan = true;
					}
				}

				assertTrue(foundSomewhere);
				assertTrue(foundRyan);
			}
		}.perform();
	}
	
	public void testQueryMXRecord_AssertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createMXRecord("domain.com", "example.domain.com", 1);
				recs.add(rec);

				rec = DNSRecordUtil.createMXRecord("domain.com", "example2.domain.com", 2);
				recs.add(rec);

				rec = DNSRecordUtil.createMXRecord("domain2.com", "example.domain2.com", 1);
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("domain.com", Type.MX));
				queries.add(new Query("domain.com", Type.A));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(2, records.size());
				assertEquals("domain.com.", records.iterator().next().getName().toString());
			}
		}.perform();
	}

	public void testQueryMXRecordByA_AssertNoRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createMXRecord("domain.com", "example.domain.com", 1);
				recs.add(rec);

				rec = DNSRecordUtil.createMXRecord("domain.com", "example2.domain.com", 2);
				recs.add(rec);

				rec = DNSRecordUtil.createMXRecord("domain2.com", "example.domain2.com", 1);
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("domain.com", Type.A));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(0, records.size());
			}
		}.perform();
	}


	public void testQueryNSRecord_AssertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createNSRecord("domain.com", "ns.domain.com");
				recs.add(rec);

				rec = DNSRecordUtil.createNSRecord("domain.com", "ns2.domain.com");
				recs.add(rec);

				rec = DNSRecordUtil.createNSRecord("domain2.com", "ns.domain2.com");
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("domain.com", Type.NS));
				queries.add(new Query("domain.com", Type.A));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(2, records.size());
				assertEquals("domain.com.", records.iterator().next().getName().toString());

				for (Record rec : records)
				{
					assertEquals(Type.NS, rec.getType());
				}
			}
		}.perform();
	}

	public void testQueryNSRecordByA_AssertNoRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createNSRecord("domain.com", "ns.domain.com");
				recs.add(rec);

				rec = DNSRecordUtil.createNSRecord("domain.com", "ns2.domain.com");
				recs.add(rec);

				rec = DNSRecordUtil.createNSRecord("domain2.com", "ns.domain2.com");
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("domain.com", Type.A));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(0, records.size());
			}
		}.perform();
	}

	public void testQueryCNAMERecord_AssertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createCNAMERecord("domainserver.com", "domain.com");
				recs.add(rec);

				rec = DNSRecordUtil.createCNAMERecord("domainserver2.com", "domain.com");
				recs.add(rec);

				rec = DNSRecordUtil.createCNAMERecord("domain2server.com", "domain2.com");
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("domainserver.com", Type.CNAME));
				queries.add(new Query("domainserver2.com", Type.CNAME));
				queries.add(new Query("domain.com", Type.A));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(2, records.size());
				assertEquals("domainserver.com.", records.iterator().next().getName().toString());

				for (Record rec : records)
				{
					assertEquals(Type.CNAME, rec.getType());
				}
			}
		}.perform();
	}

	public void testQueryCNAMERecordByA_AssertNoRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createCNAMERecord("domainserver.com", "domain.com");
				recs.add(rec);

				rec = DNSRecordUtil.createCNAMERecord("domainserver2.com", "domain.com");
				recs.add(rec);

				rec = DNSRecordUtil.createCNAMERecord("domain2server.com", "domain2.com");
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));

			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("domain.com", Type.A));

				return queries;
			}

			protected void doAssertions(Collection<Record> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(0, records.size());
			}
		}.perform();
	}

	public void testQueryUnsupportedQueryType() throws Exception
	{
		new TestPlan()
		{
			protected void addRecords() throws Exception
			{
				ArrayList<DnsRecord> recs = new ArrayList<DnsRecord>();
				DnsRecord rec = DNSRecordUtil.createCNAMERecord("domainserver.com", "domain.com");
				recs.add(rec);

				proxy.addDNS(recs.toArray(new DnsRecord[recs.size()]));
			}

			protected Collection<Query> getTestQueries() throws Exception
			{
				Collection<Query> queries = new ArrayList<Query>();
				queries.add(new Query("domain.com", Type.AAAA));

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
