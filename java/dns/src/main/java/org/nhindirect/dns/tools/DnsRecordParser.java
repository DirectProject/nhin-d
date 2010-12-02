package org.nhindirect.dns.tools;

import java.net.InetAddress;

import org.nhindirect.dns.tools.utils.StringArrayUtil;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.SOARecord;

public class DnsRecordParser 
{
	public static final String PARSE_ANAME_USAGE = "  hostname ipaddress ttl [notes]" +
	      "\r\n\t hostname: host name for the record" + 
	      "\r\n\t ipaddress: IP address in dot notation" +
	      "\r\n\t ttl: time to live in seconds, 32bit int";
	
	public static final String PARSE_SOA_USAGE = "  domainname primarysourcedomain responsibleemail serialnumber ttl [refresh] [retry] [expire] [minimum] [notes]" +
	      "\r\n\t domainname: The domain name of the name server that was the primary source for this zone" +
	      "\r\n\t responsibleemail: Email mailbox of the hostmaster" +
	      "\r\n\t serialnumber: Version number of the original copy of the zone." +
	      "\r\n\t ttl: time to live in seconds, 32bit int" +
	      "\r\n\t [refresh]: Number of seconds before the zone should be refreshed." + 
	      "\r\n\t [retry]: Number of seconds before failed refresh should be retried." + 
	      "\r\n\t [expire]: Number of seconds before records should be expired if not refreshed" +
	      "\r\n\t [minimum]: Minimum TTL for this zone.";
	
	public static final String PARSE_MX_USAGE = "  domainname exchange ttl [preference] [notes]" + 
	      "\r\n\t domainname: email domain name for the record" +
	      "\r\n\t exchange: smtp server host name for the domain" + 
	      "\r\n\t ttl: time to live in seconds" +
	      "\r\n\t [preference]: short value indicating preference of the record";
	
	public DnsRecordParser()
	{
	}
	
	private Name nameFromString(String str)
	{
		if (!str.endsWith("."))
			str += ".";
	
		try
		{
			return Name.fromString(str);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Invalid DNS name");
		}
	}
	
	private InetAddress inetFromString(String str)
	{
		try
		{
			return InetAddress.getByName(str);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Invalid ip address");
		}
	}
	
	public ARecord parseANAME(String[] args)
	{
		
	    String domainName = StringArrayUtil.getRequiredValue(args, 0);
	    String ipAddress = StringArrayUtil.getRequiredValue(args, 1);
	    int ttl = Integer.parseInt(StringArrayUtil.getRequiredValue(args, 2));
	    
	    return new ARecord(nameFromString(domainName), DClass.IN, ttl, inetFromString(ipAddress));

	}
	        
	public SOARecord parseSOA(String[] args)
	{
	    String domainName = StringArrayUtil.getRequiredValue(args, 0);
	    String primarySourceDomain = StringArrayUtil.getRequiredValue(args, 1);
	    String responsibleEmail = StringArrayUtil.getRequiredValue(args, 2);
	    int serialNumber = Integer.parseInt(StringArrayUtil.getRequiredValue(args, 3));
	    int ttl = Integer.parseInt(StringArrayUtil.getRequiredValue(args, 4));
	
	    int refresh = Integer.parseInt(StringArrayUtil.getOptionalValue(args, 5, "0"));
	    int retry = Integer.parseInt(StringArrayUtil.getOptionalValue(args, 6, "0"));
	    int expire = Integer.parseInt(StringArrayUtil.getOptionalValue(args, 7, "0"));
	    int minimum = Integer.parseInt(StringArrayUtil.getOptionalValue(args, 8, "0"));
	
	    return new SOARecord(nameFromString(domainName), DClass.IN, ttl, nameFromString(primarySourceDomain), 
	    		nameFromString(responsibleEmail), serialNumber, refresh, retry, expire, minimum);

	}
	        
	public MXRecord parseMX(String[] args)
	{        
		String domainName = StringArrayUtil.getRequiredValue(args, 0);
		String exchange = StringArrayUtil.getRequiredValue(args, 1);
	    int ttl = Integer.parseInt(StringArrayUtil.getRequiredValue(args, 2));
	    short pref = Short.parseShort(StringArrayUtil.getOptionalValue(args, 3, "0"));
	
	    return new MXRecord(nameFromString(domainName), DClass.IN, ttl, pref, nameFromString(exchange));
	}
}
