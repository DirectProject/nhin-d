package org.nhindirect.dns.tools;

import java.io.PrintWriter;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import org.nhind.config.DnsRecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.security.CERTConverter;


public class DnsRecordPrinter 
{
	private final PrintWriter writer;
    
    /// <summary>
    /// Initializes the printer with <paramref name="writer"/>
    /// </summary>
    /// <param name="writer">The <see cref="TextWriter"/> used for output</param>
    public DnsRecordPrinter()
    {        
        writer = new PrintWriter(System.out);
    }
    
    
    public void print(Collection<DnsRecord> records)
    {
        if (records == null || records.size() == 0)
        {
            writer.println("Empty record list");
            return;
        }

        for (DnsRecord record : records)
        {
            print(record);
        }
    }
    
    public void print(DnsRecord[] records)
    {
    	if (records == null || records.length == 0)
        {
            writer.println("Empty record array");
            return;
        }
    	
    	print(Arrays.asList(records));
    }

    private String typeToString(int type)
    {
    	switch (type)
    	{
    		case Type.A:
    			return "A";
    			
    		case Type.MX:
    			return "MX";
    			
    		case Type.SOA:
    			return "SOA";
    			
    		case Type.CERT:
    			return "CERT";
    			
    		default:
    			return "Unknown";    		
    	}
    }
    
    public void print(DnsRecord record)
    {
        if (record == null)
        {
            writer.println("Null Resource Record");
            return;
        }
        
        writer.println("-----------");
        print("Record Name", record.getName());
        print("Type", typeToString(record.getType()));
        print("TTL", String.valueOf(record.getTtl()));
        switch(record.getType())
        {
            default:
                break;
            
            case Type.A:
                print((ARecord)toRecord(record));                
                break;
                
            case Type.SOA:
            	print((SOARecord)toRecord(record));
                break;
                    
            case Type.MX:
            	print((MXRecord)toRecord(record));
                break;               
                                        
            case Type.CERT:
            	print((CERTRecord)toRecord(record));                                   
                break;
        }
        
        writer.flush();
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

    
    private Record toRecord(DnsRecord rec)
    {
    	return Record.newRecord(nameFromString(rec.getName()), rec.getType(), rec.getDclass(), rec.getTtl(), rec.getData());
    }
    
    public void print(ARecord body)
    {
        if (body == null)
        {
            print("Null A Record Body");
            return;
        }

        this.print("IPAddress", body.getAddress().getHostAddress());
    }

    public void print(MXRecord body)
    {
        if (body == null)
        {
            print("Null MX Record Body");
            return;
        }
        
        print("Access Exchage Server", body.getTarget().toString());
        print("Priority", String.valueOf(body.getPriority()));
    }
    
    public void print(SOARecord soa)
    {
        if (soa == null)
        {
            print("Null SOA Record Body");
            return;
        }

    	
    	print("DomainName", soa.getName().toString());
        print("PrimarySourceDomain", soa.getHost().toString());
        print("Refresh", String.valueOf(soa.getRefresh()));
        print("Retry", String.valueOf(soa.getRetry()));
        print("Expire", String.valueOf(soa.getExpire()));
        print("Minimum", String.valueOf(soa.getMinimum()));
    }
    
    public void print(CERTRecord certbody)
    {
        if (certbody == null)
        {
            print("Null CERT Record Body");
            return;
        }

    	
    	Certificate cert = CERTConverter.parseRecord(certbody);
		if (cert instanceof X509Certificate) // may not be an X509Cert
		{
			X509Certificate xcert = (X509Certificate)cert;
			print("Certificate Subject", xcert.getSubjectDN().getName());			
		}
    }
    


    void print(String name, String value)
    {
        writer.println(name + ": " + value);
    }


    void print(String message)
    {
    	writer.println(message);
    }

}
