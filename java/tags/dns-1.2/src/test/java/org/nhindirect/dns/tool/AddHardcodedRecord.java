package org.nhindirect.dns.tool;

import java.util.ArrayList;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.util.DNSRecordUtil;
import org.xbill.DNS.Type;

import junit.framework.TestCase;

public class AddHardcodedRecord extends TestCase 
{
	public void testDummy()
	{
		
	}
	
	public static void main(String args[])
	{
		try
		{
			ConfigurationServiceProxy proxy = new ConfigurationServiceProxy("http://securehealthemail.com:8080/config-service/ConfigurationService");
		
			// clean everything
			
			DnsRecord[] recs = proxy.getDNSByType(Type.ANY);
			
			if (recs != null && recs.length > 0)
				proxy.removeDNS(recs);
			
			recs = proxy.getDNSByType(Type.ANY);
			
			assertNull(recs);
			
			
			// now add
			ArrayList<DnsRecord> recsAdd = new ArrayList<DnsRecord>();
			DnsRecord rec = DNSRecordUtil.createARecord("direct.securehealthemail.com", "184.73.173.57");
			recsAdd.add(rec);
			
			rec = DNSRecordUtil.createARecord("ns1.direct.securehealthemail.com", "184.73.173.57");
			recsAdd.add(rec);		
			
			rec = DNSRecordUtil.createARecord("mail1.direct.securehealthemail.com", "184.73.173.57");
			recsAdd.add(rec);			
			
			rec = DNSRecordUtil.createSOARecord("direct.securehealthemail.com", "ns1.direct.securehealthemail.com", "greg.meyer@direct.securehealthemail.com");
			recsAdd.add(rec);
			
			
			rec = DNSRecordUtil.createMXRecord("direct.securehealthemail.com", "mail1.direct.securehealthemail.com", 0);
			recsAdd.add(rec);
			
			proxy.addDNS(recsAdd.toArray(new DnsRecord[recsAdd.size()]));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
