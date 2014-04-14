package org.nhind.config.client;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.util.DNSRecordUtils;
import org.xbill.DNS.Type;

public class ClientProxyDNSTest 
{
	private static ConfigurationServiceProxy proxy;
	
	@BeforeClass
	public static void setupClass() throws Exception
	{
		ConfigServiceRunner.startConfigService();    	
    	proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}
	
	private void cleanDNSRecords() throws Exception
	{
		DnsRecord[] recs = proxy.getDNSByType(Type.ANY);
	
		if (recs != null)
			proxy.removeDNS(recs);
	}	
	
	@Test
	public void addDNSRecord() throws Exception
	{
		cleanDNSRecords();
		
		DNSRecord rec = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		
		DnsRecord recToAdd = new DnsRecord();
		recToAdd.setData(rec.getData());
		recToAdd.setDclass(rec.getDclass());
		recToAdd.setName(rec.getName());
		recToAdd.setTtl(rec.getTtl());
		recToAdd.setType(rec.getType());
		
		
		proxy.addDNS(new DnsRecord[] {recToAdd});
		
		assertEquals(1, proxy.getDNSCount());
		
		DnsRecord[] recs = proxy.getDNSByNameAndType(recToAdd.getName(), recToAdd.getType());
		assertNotNull(recs);
		assertEquals(1, recs.length);
		assertEquals(rec.getName(), recs[0].getName());
	}
}
