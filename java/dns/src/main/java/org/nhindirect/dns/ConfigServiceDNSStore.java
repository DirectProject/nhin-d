package org.nhindirect.dns;

import java.net.URL;
import java.util.Iterator;


import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

public class ConfigServiceDNSStore implements DNSStore 
{

	final ConfigurationServiceProxy proxy;
	
	public ConfigServiceDNSStore(URL serviceURL)
	{
		proxy = new ConfigurationServiceProxy(serviceURL.toString());
	}
	
	@Override
	public Message get(Message request) throws DNSException
	{
		if (request == null)
			throw new DNSException(DNSError.newError(Rcode.FORMERR));
		
		Header header = request.getHeader();
		if (header.getFlag(Flags.QR) || header.getRcode() != Rcode.NOERROR)
			throw new DNSException(DNSError.newError(Rcode.FORMERR));

		if (header.getOpcode() != Opcode.QUERY)
			throw new DNSException(DNSError.newError(Rcode.NOTIMP));	
		
        Record question = request.getQuestion();
        
        if (question == null || question.getDClass() != DClass.IN)
        {
        	throw new DNSException(DNSError.newError(Rcode.NOTIMP));
        }

        Record queryRecord = request.getQuestion();
        Name name = queryRecord.getName();
    	int type = queryRecord.getType();
        
    	RRset lookupRecords = null;
        switch (question.getType())
        {
        	case Type.A:
        	case Type.MX:
        	case Type.SOA:
        	case Type.SRV:
        	{
        		try
        		{
        			lookupRecords = processGenericRecordRequest(name.toString(), type);
        		}
        		catch (Exception e)
        		{
        			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call failed: " + e.getMessage(), e);
        		}
        		break;
        	}
        	case Type.CERT:
        	{
        		lookupRecords = processCERTRecordRequest(name.toString());
        		break;
        	}
        	case Type.ANY:
        	{
        		RRset genRecs = processGenericRecordRequest(name.toString(), Type.ANY);
        		RRset certRecs = processCERTRecordRequest(name.toString());

        		if (genRecs == null || certRecs == null)
        		{
        			if (genRecs != null)
        				lookupRecords = genRecs; // certRecs is null, just return genRecs
        			else if (certRecs != null)
        				lookupRecords = certRecs; // genRecs is null, just return certRecs
        			// default case is just leave lookupRecords null
        		}
        		else
        		{
        			// we know both are non null, merge
        			lookupRecords = genRecs;
        			Iterator<Record> iter = certRecs.rrs();
        			while (iter.hasNext())
        				lookupRecords.addRR(iter.next());
        		}        			
        	}
        	default:
        	{
        		throw new DNSException(DNSError.newError(Rcode.NOTIMP)); 
        	}        	
        }
        
        if (lookupRecords == null || lookupRecords.size() == 0)
        	return null;
        
        Message response = new Message(request.getHeader().getID());
        response.getHeader().setFlag(Flags.QR);
    	if (request.getHeader().getFlag(Flags.RD))
    		response.getHeader().setFlag(Flags.RD);
    	response.addRecord(queryRecord, Section.QUESTION);
    	
    	// we are authoritative only
    	response.getHeader().setFlag(Flags.AA);
    	
		Iterator<Record> iter = lookupRecords.rrs();
		while (iter.hasNext())
			response.addRecord(iter.next(), Section.ANSWER);
    	
    	return response;
	}

	protected RRset processGenericRecordRequest(String name, int type) throws DNSException
	{		
		DnsRecord records[];
		
		try
		{
			records = proxy.getDNSByNameAndType(name, type);
		}
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for DNS records failed: " + e.getMessage(), e);
		}
		
		if (records == null || records.length == 0)
			return null;
		
		RRset retVal = new RRset();	
		try
		{
			for (DnsRecord record : records)						
			{
				Record rec = Record.newRecord(Name.fromString(record.getName()), record.getType(), 
						record.getDclass(), record.getTtl(), record.getData());
				
				retVal.addRR(rec);
			}
		}		
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "Failure while parsing generic record data: " + e.getMessage(), e);
		}
		
		return retVal;
	}
	
	protected RRset processCERTRecordRequest(String name) throws DNSException
	{
		if (name.endsWith("."))
			name = name.substring(0, name.length() - 1);
				
		Certificate[] certs;
		
		// use the certificate configuration service
		try
		{
			certs = proxy.getCertificatesForOwner(name, null);
		}
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for certificates failed: " + e.getMessage(), e);
		}
		
		if (certs == null || certs.length == 0)
			return null;	
				
		RRset retVal = new RRset();		
		try
		{
			for (Certificate cert : certs)
				retVal.addRR(new CERTRecord(Name.fromString(name), DClass.IN, /*one day*/ 86400L, CERTRecord.PKIX, 0,
						   0, cert.getData()));
		}		
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "Failure while parsing CERT record data: " + e.getMessage(), e);
		}
		
		return retVal;
	}
}
