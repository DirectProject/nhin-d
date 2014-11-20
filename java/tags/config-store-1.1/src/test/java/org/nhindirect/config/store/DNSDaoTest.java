package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.InetAddress;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.config.store.dao.DNSDao;
import org.nhindirect.config.store.util.DNSRecordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Type;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/configStore-test.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class DNSDaoTest
{
	private static final String derbyHomeLoc = "/target/data";	
	
	private static final String certBasePath = "src/test/resources/certs/"; 
	
	@Autowired
	private DNSDao dnsDao;	
	
	static
	{
		try
		{
			
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			File baseLocation = new File("dummy.txt");
			String fullDerbyHome = baseLocation.getAbsolutePath().substring(0, baseLocation.getAbsolutePath().lastIndexOf(File.separator)) + derbyHomeLoc;
			System.setProperty("derby.system.home", fullDerbyHome);

		}
		catch (Exception e)
		{
			
		}
	}	
	
	private byte[] loadCertificateData(String certFileName) throws Exception
	{
		File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	
	@Test
	public void testCleanDatabase() throws Exception 
	{
		Collection<DNSRecord> records = dnsDao.get(Type.ANY);	
		
		if (records != null && records.size() > 0)
		{
			for (DNSRecord record : records)
				dnsDao.remove(record.getId());
			
		}
		records = dnsDao.get(Type.ANY);	
		
		assertEquals(0, records.size());
	}

	@Test
	public void testAddCertRecord() throws Exception
	{
		testCleanDatabase();

		byte[] certData = loadCertificateData("gm2552.der");
		assertNotNull(certData);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(certData);

    	X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
    	
    	DNSRecord record1 = DNSRecordUtils.createX509CERTRecord("gm2552@securehealthemail.com", 86400L, cert);
    	
    	dnsDao.add(Arrays.asList(record1));
    	Collection<DNSRecord> records = dnsDao.get(Type.CERT);
    	
    	assertEquals(1, records.size());
    	assertEquals(record1, records.iterator().next());
    	

	}
	
	@Test
	public void testAddSingleARecords() throws Exception 
	{
		
		testCleanDatabase();
		
		// Add 1 record
		DNSRecord record = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		dnsDao.add(Arrays.asList(record));
		
		Collection<DNSRecord> records = dnsDao.get(record.getName());
		
		assertEquals(1, records.size());
		
		DNSRecord compareRec = records.iterator().next();
		assertEquals(record.getName(), compareRec.getName());
		assertEquals(Type.A, compareRec.getType());
	}	
	
	@Test
	public void testMultipleARecords() throws Exception 
	{
		
		testCleanDatabase();
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		DNSRecord record2 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "74.22.43.123"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("sample.domain.com", 86400L, "81.142.48.20"); 
		
		dnsDao.add(Arrays.asList(record1, record2, record3));
		
		/*
		 * Get by name
		 */
		Collection<DNSRecord> records = dnsDao.get(record1.getName());
		
		assertEquals(1, records.size());
		
		DNSRecord compareRec = records.iterator().next();
		assertEquals(record1.getName(), compareRec.getName());
		assertEquals(Type.A, compareRec.getType());
		
		/*
		 * Get all types
		 */
		records = dnsDao.get(Type.ANY);
		assertEquals(3, records.size());
		
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
		
		/*
		 * Get A only
		 */
		records = dnsDao.get(Type.A);
		assertEquals(3, records.size());
		
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
		
		/*
		 * Get SRV only
		 */
		records = dnsDao.get(Type.SRV);
		assertEquals(0, records.size());		
	}	
	
	@Test
	public void testAddRecord_invalidType() throws Exception 
	{
		
		testCleanDatabase();
		
		DNSRecord record = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		record.setType(Type.ANY);
		
		boolean exceptionOccured = false;
		try
		{	
			dnsDao.add(Arrays.asList(record));
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
	
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testAddRecord_recordAlreadyExistsWithRdata() throws Exception 
	{
		
		testCleanDatabase();
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		dnsDao.add(Arrays.asList(record1));
		
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		boolean exceptionOccured = false;
		try
		{	
			dnsDao.add(Arrays.asList(record2));
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
	
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testMultipleARecords_differentRdata() throws Exception 
	{
		
		testCleanDatabase();
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 

		
		dnsDao.add(Arrays.asList(record1));
		
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		dnsDao.add(Arrays.asList(record2));
		/*
		 * Get by name
		 */
		Collection<DNSRecord> records = dnsDao.get(record1.getName());
		
		assertEquals(2, records.size());
		
		records.contains(record1);
		records.contains(record2);
			
	}	
	
	@Test
	public void testGetByType() throws Exception 
	{
		
		testCleanDatabase();
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		DNSRecord record2 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.1"); 
		DNSRecord record3 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		dnsDao.add(Arrays.asList(record1, record2, record3));

		/*
		 * By A
		 */
		Collection<DNSRecord> records = dnsDao.get(Type.A);
		
		assertEquals(2, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));

		
		/*
		 * By SRV
		 */
		records = dnsDao.get(Type.SRV);
		
		assertEquals(1, records.size());
		assertTrue(records.contains(record3));
		
		/*
		 * By ANY
		 */
		records = dnsDao.get(Type.ANY);		
		
		assertEquals(3, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));		
		assertTrue(records.contains(record3));		
	}		
	
	@Test
	public void testGetByName() throws Exception 
	{
		
		testCleanDatabase();
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsDao.add(Arrays.asList(record1, record2, record3, record4, record5));

		Collection<DNSRecord> records = dnsDao.get(record1.getName());
		
		assertEquals(2, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));


		records = dnsDao.get(record3.getName());
		
		assertEquals(1, records.size());
		assertTrue(records.contains(record3));
		

		records = dnsDao.get(record4.getName());		
		
		assertEquals(2, records.size());
		assertTrue(records.contains(record4));
		assertTrue(records.contains(record5));		
		
		
		records = dnsDao.get("bogus.com.");		
		
		assertEquals(0, records.size());
	}		
	
	@Test
	public void testGetByRecord() throws Exception 
	{
		
		testCleanDatabase();
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsDao.add(Arrays.asList(record1, record2, record3, record4, record5));

		Collection<DNSRecord> records = dnsDao.get(record3.getName());
		
		assertEquals(1, records.size());
		DNSRecord checkRec = dnsDao.get(records.iterator().next().getId());
		assertNotNull(checkRec);
		assertEquals(checkRec, record3);


		records = dnsDao.get(Type.ANY);
		assertEquals(5, records.size());
		long[] ids = new long[records.size()];
		int cnt = 0;
		for (DNSRecord record : records)
			ids[cnt++] = record.getId();
		
		records = dnsDao.get(ids);
		assertEquals(5, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
		assertTrue(records.contains(record4));
		assertTrue(records.contains(record5));
	}	
	
	@Test
	public void testGetCount() throws Exception 
	{
		
		testCleanDatabase();
		
		assertEquals(0, dnsDao.count());
		
		// Add 5 record
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsDao.add(Arrays.asList(record1, record2, record3, record4, record5));

		assertEquals(5, dnsDao.count());
		
	}	
	
	
	@Test
	public void testRemoveByRecords() throws Exception 
	{
		
		testCleanDatabase();
		
		assertEquals(0, dnsDao.count());
		
		// Add 5 record
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsDao.add(Arrays.asList(record1, record2, record3, record4, record5));

		assertEquals(5, dnsDao.count());
		
		// remove the first three records
		dnsDao.remove(Arrays.asList(record1, record2, record3));
		
		Collection<DNSRecord> records = dnsDao.get(Type.ANY);
		assertEquals(2, records.size());
		assertTrue(records.contains(record4));
		assertTrue(records.contains(record5));
		
		// remove the last two records
		dnsDao.remove(Arrays.asList(record4, record5));
		
		records = dnsDao.get(Type.ANY);
		assertEquals(0, records.size());

	}		
	
	@Test
	public void testRemoveByIds() throws Exception 
	{
		
		testCleanDatabase();
		
		assertEquals(0, dnsDao.count());
		
		// Add 5 record
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsDao.add(Arrays.asList(record1, record2, record3, record4, record5));

		assertEquals(5, dnsDao.count());
		
		// remove record 3
		Collection<DNSRecord> records = dnsDao.get(record3.getName());
		assertEquals(1, records.size());
		dnsDao.remove(records.iterator().next().getId());
		records = dnsDao.get(Type.ANY);
		assertEquals(4, dnsDao.count());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record4));
		assertTrue(records.contains(record5));
		


		// remove the rest
		long[] ids = new long[records.size()];
		int cnt = 0;
		for (DNSRecord record : records)
			ids[cnt++] = record.getId();
		
		dnsDao.remove(ids);
		records = dnsDao.get(Type.ANY);
		assertEquals(0, records.size());

	}		
	
	@Test
	public void testRemoveByIds_noqualifying() throws Exception 
	{
		
		testCleanDatabase();
		
		assertEquals(0, dnsDao.count());
		
		dnsDao.remove(876343);
		
		// should result in a functional no-op
		assertEquals(0, dnsDao.count());
	}		
	
	@Test
	public void testRemoveByRecords_noqualifying() throws Exception 
	{
		
		testCleanDatabase();
		
		assertEquals(0, dnsDao.count());
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		
		// should result in a functional no-op
		dnsDao.remove(Arrays.asList(record1));
		
		assertEquals(0, dnsDao.count());

	}			
	
	@Test
	public void testUpdateRecord() throws Exception 
	{
		
		testCleanDatabase();
		
		assertEquals(0, dnsDao.count());
		

		DNSRecord record1 = DNSRecordUtils.createMXRecord("example.domain.com", "127.0.0.1", 86400L, 1);
		dnsDao.add(Arrays.asList(record1));
		
		Collection<DNSRecord> records = dnsDao.get(Type.ANY);
		assertEquals(1, records.size());
		DNSRecord checkRecord = records.iterator().next();
		assertEquals(record1, checkRecord);
		
		checkRecord.setName("example2.domain.com.");
		dnsDao.update(checkRecord.getId(), checkRecord);
		
		records = dnsDao.get(Type.ANY);
		assertEquals(1, records.size());
		DNSRecord modRecord = records.iterator().next();
		assertEquals(checkRecord, modRecord);
		
	}
	
	@Test
	public void testUpdateRecord_recordDoesNotExist() throws Exception 
	{
		
		testCleanDatabase();
		
		assertEquals(0, dnsDao.count());
		

		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		boolean exceptionOccured = false;
		try
		{
			dnsDao.update(123432, record1);
		}
		catch(ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
	}	
	
	@Test
	public void testUpdateRecord_illegalAnyType() throws Exception 
	{
		
		testCleanDatabase();
		
		assertEquals(0, dnsDao.count());
		

		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		dnsDao.add(Arrays.asList(record1));
		
		Collection<DNSRecord> records = dnsDao.get(Type.ANY);
		assertEquals(1, records.size());
		DNSRecord checkRecord = records.iterator().next();
		assertEquals(record1, checkRecord);
		
		checkRecord.setType(Type.ANY);
		
		boolean exceptionOccured = false;
		try
		{
			dnsDao.update(checkRecord.getId(), checkRecord);
		}
		catch(ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
	}
}
