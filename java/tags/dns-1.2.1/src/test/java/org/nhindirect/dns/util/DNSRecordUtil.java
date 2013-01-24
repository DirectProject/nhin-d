package org.nhindirect.dns.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.FileUtils;
import org.nhind.config.DnsRecord;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.util.DNSRecordUtils;
import org.xbill.DNS.DClass;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Name;

public class DNSRecordUtil 
{
	private static final String certBasePath = "src/test/resources/certs/"; 
	
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

	public static String getCertOwner(X509Certificate cert)
    {
		X500Principal prin = cert.getSubjectX500Principal();
		
    	// get the domain name
		Map<String, String> oidMap = new HashMap<String, String>();
		oidMap.put("1.2.840.113549.1.9.1", "EMAILADDRESS");  // OID for email address
		String prinName = prin.getName(X500Principal.RFC1779, oidMap);    
		
		// see if there is an email address first in the DN
		String searchString = "EMAILADDRESS=";
		int index = prinName.indexOf(searchString);
		if (index == -1)
		{
			searchString = "CN=";
			// no Email.. check the CN
			index = prinName.indexOf(searchString);
			if (index == -1)
				return ""; // no CN... nothing else that can be done from here
		}
		
		// look for a "," to find the end of this attribute
		int endIndex = prinName.indexOf(",", index);
		String address;
		if (endIndex > -1)
			address = prinName.substring(index + searchString.length(), endIndex);
		else 
			address= prinName.substring(index + searchString.length());
		
		return address;
    }	
	
	public static X509Certificate loadCertificate(String certFileName) throws Exception
	{
		File fl = new File(certBasePath + certFileName);
		
		InputStream str = new ByteArrayInputStream(FileUtils.readFileToByteArray(fl));
		
		X509Certificate retVal = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(str);
		
		str.close();
		
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
	
	public static DnsRecord createMXRecord(String name, String target, int priority) throws Exception
	{
		DNSRecord rec = DNSRecordUtils.createMXRecord(name, target, 86400L, priority);
		
		return toDnsRecord(rec);
	}	
	
	public static DnsRecord createSOARecord(String name, String nameServer, String hostmaster) throws Exception
	{
		DNSRecord rec = DNSRecordUtils.createSOARecord(name, 3600L, nameServer, hostmaster, 1, 3600L, 600L, 604800L, 3600L);
		
		return toDnsRecord(rec);
	}		
	
	public static DnsRecord createNSRecord(String name, String target) throws Exception
	{
		
		if (!name.endsWith("."))
			name = name + ".";
		
		if (!target.endsWith("."))
			target = target + ".";
		
		NSRecord rec = new NSRecord(Name.fromString(name), DClass.IN, 86400L, Name.fromString(target));
		
		return toDnsRecord(DNSRecord.fromWire(rec.toWireCanonical()));
		
	}	
	
	public static DnsRecord createCNAMERecord(String name, String target) throws Exception
	{
		
		if (!name.endsWith("."))
			name = name + ".";
		
		if (!target.endsWith("."))
			target = target + ".";
		
		CNAMERecord rec = new CNAMERecord(Name.fromString(name), DClass.IN, 86400L, Name.fromString(target));
		
		return toDnsRecord(DNSRecord.fromWire(rec.toWireCanonical()));
		
	}		
}
