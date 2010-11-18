package org.nhindirect.dns.util;

import java.security.cert.X509Certificate;

import org.nhind.config.DnsRecord;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.util.DNSRecordUtils;

public class DNSRecordUtil 
{
	private static DnsRecord toDnsRecord(DNSRecord rec)
	{
		DnsRecord retVal = new DnsRecord();
		
		retVal.setData(rec.getData());
		retVal.setDclass(rec.getDclass());
		retVal.setName(rec.getName());
		retVal.setTtl(rec.getTtl());
		retVal.setType(rec.getType());
		
		return retVal;
	}
	
	public static DnsRecord createARecord(String name, String ip) throws Exception
	{
		DNSRecord rec = DNSRecordUtils.createARecord(name, 86400L, ip);
		
		return toDnsRecord(rec);
	}

	public static DnsRecord createCERTRecord(String name, X509Certificate cert) throws Exception
	{
		DNSRecord rec = DNSRecordUtils.createX509CERTRecord(name, 86400L, cert);
		
		return toDnsRecord(rec);
	}
}
