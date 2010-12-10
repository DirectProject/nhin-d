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

/**
 * Utility class for formatting and outputting the content of DNS records.
 * @author Greg Meyer
 * 
 * @since 1.0
 */
public class DefaultDNSRecordPrinter implements DNSRecordPrinter
{
	private final PrintWriter writer;
    
	/**
	 * Default constructor.  Create a writer that outputs to system console.
	 * 
	 * @since 1.0
	 */
    public DefaultDNSRecordPrinter()
    {        
        writer = new PrintWriter(System.out);
    }
    
    /**
     * {@inheritDoc}
     */
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
    
    /**
     * {@inheritDoc}
     */  
    public void print(DnsRecord[] records)
    {
    	if (records == null || records.length == 0)
        {
            writer.println("Empty record array");
            return;
        }
    	
    	print(Arrays.asList(records));
    }

    /*
     * Converts a DNS record type to a string representation
     */
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
    
    /**
     * {@inheritDoc}
     */
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
    
    /*
     * converts a String to a DNS name
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
     * converts a configuration service DnsRecord to a dnsjava Record
     */
    private Record toRecord(DnsRecord rec)
    {
    	return Record.newRecord(nameFromString(rec.getName()), rec.getType(), rec.getDclass(), rec.getTtl(), rec.getData());
    }
    
    /*
     * prints the A record specific fields
     */
    private void print(ARecord body)
    {
        if (body == null)
        {
            print("Null A Record Body");
            return;
        }

        this.print("IPAddress", body.getAddress().getHostAddress());
    }

    /*
     * prints the MX record specific fields
     */
    private void print(MXRecord body)
    {
        if (body == null)
        {
            print("Null MX Record Body");
            return;
        }
        
        print("Access Exchage Server", body.getTarget().toString());
        print("Priority", String.valueOf(body.getPriority()));
    }
    
    /*
     * prints the SOA record specific fields
     */
    private void print(SOARecord soa)
    {
        if (soa == null)
        {
            print("Null SOA Record Body");
            return;
        }

    	
    	print("DomainName", soa.getName().toString());
        print("Primary Name Server", soa.getHost().toString());
        print("Refresh", String.valueOf(soa.getRefresh()));
        print("Retry", String.valueOf(soa.getRetry()));
        print("Expire", String.valueOf(soa.getExpire()));
        print("Minimum", String.valueOf(soa.getMinimum()));
    }
    
    /*
     * prints the CERT record specific fields
     */    
    private void print(CERTRecord certbody)
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
    

    /*
     * prints a name value pair
     */
    private void print(String name, String value)
    {
        writer.println(name + ": " + value);
    }

    /*
     * prints a specific string message
     */
    private void print(String message)
    {
    	writer.println(message);
    }

}
