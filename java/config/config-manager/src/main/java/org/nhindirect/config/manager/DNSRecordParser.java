/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.config.manager;

import java.net.InetAddress;

import org.nhindirect.dns.tools.utils.StringArrayUtil;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.SOARecord;

/**
 * Parses an array of strings into DNS records.
 * @author Greg Meyer
 *
 * @since 1.0
 */
public class DNSRecordParser 
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
	
	/**
	 * Default empty constructor
	 * 
	 * @since 1.0
	 */
	public DNSRecordParser()
	{
	}
	
	/*
	 * converts a string to a dnsjava Name
	 */
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
	
	/*
	 * converts a string to a InetAddress object
	 */
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
	
	/**
	 * Converts A record configuration information to an ARecord 
	 * @param args The A record configuration parameters.
	 * @return A DNS ARecord.
	 * 
	 * @since 1.0
	 */
	public ARecord parseANAME(String[] args)
	{
		
	    String domainName = StringArrayUtil.getRequiredValue(args, 0);
	    String ipAddress = StringArrayUtil.getRequiredValue(args, 1);
	    int ttl = Integer.parseInt(StringArrayUtil.getRequiredValue(args, 2));
	    
	    return new ARecord(nameFromString(domainName), DClass.IN, ttl, inetFromString(ipAddress));

	}
	       
	/**
	 * Converts SAO record configuration information to an SOARecord 
	 * @param args The SOA record configuration parameters.
	 * @return A DNS SAORecord.
	 * 
	 * @since 1.0
	 */	
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
	  
	/**
	 * Converts MX record configuration information to an MXRecord 
	 * @param args The MX record configuration parameters.
	 * @return A DNS MXRecord.
	 * 
	 * @since 1.0
	 */		
	public MXRecord parseMX(String[] args)
	{        
		String domainName = StringArrayUtil.getRequiredValue(args, 0);
		String exchange = StringArrayUtil.getRequiredValue(args, 1);
	    int ttl = Integer.parseInt(StringArrayUtil.getRequiredValue(args, 2));
	    short pref = Short.parseShort(StringArrayUtil.getOptionalValue(args, 3, "0"));
	
	    return new MXRecord(nameFromString(domainName), DClass.IN, ttl, pref, nameFromString(exchange));
	}
}
