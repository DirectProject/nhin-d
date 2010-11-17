package org.nhindirect.dns.util;

import org.nhind.config.DnsRecord;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.util.DNSRecordUtils;

public class DNSRecordUtil 
{
	public static DnsRecord createARecord(String name, String ip) throws Exception
	{
		DNSRecord rec = DNSRecordUtils.createARecord(name, 86400L, ip);
		
		DnsRecord retVal = new DnsRecord();
		retVal.setData(rec.getData());
		retVal.setDclass(rec.getDclass());
		retVal.setName(rec.getName());
		retVal.setTtl(rec.getTtl());
		retVal.setType(rec.getType());
		
		return retVal;
	}

}
