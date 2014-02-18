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

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.Arrays;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.tools.utils.Command;
import org.nhindirect.dns.tools.utils.StringArrayUtil;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

/**
 * Command definition and logic for managing DNS records.  Commands are case-insensitive.
 * @author Greg Meyer
 *
 * @since 1.0
 */
public class DNSRecordCommands 
{
    private static final String IMPORT_MX_USAGE = "Import a new MX dns record from a binary file." +
    	"\r\n\tfilepath " +
        "\r\n\t filePath: path to the MX record binary file. Can have any (or no extension)";

    private static final String IMPORT_SOA_USAGE = "Import a new SOA dns record from a binary file." +
        "\r\n\tfilepath " +
        "\r\n\t filePath: path to the SOA record binary file. Can have any (or no extension)";

    private static final String IMPORT_ADDRESS_USAGE = "Import a new A dns record from a binary file." +
        "\r\n\tfilepath " +
        "\r\n\t filePath: path to the A record binary file. Can have any (or no extension)";

    private static final String ADD_MX_USAGE = "Add a new MX dns record." +
    	"\r\n" + DNSRecordParser.PARSE_MX_USAGE;

    private static final String ENSURE_MX_USAGE = "Adds a new MX dns record if an identical one does't already exist. " +
        "\r\n" + DNSRecordParser.PARSE_MX_USAGE;

    private static final String ADD_SOA_USAGE = "Add a new SOA dns record." +
        "\r\n" + DNSRecordParser.PARSE_SOA_USAGE;

    private static final String ENSURE_SOA_USAGE = "Add a new SOA dns record if an identical one does not exist." +
        "\r\n" + DNSRecordParser.PARSE_SOA_USAGE;
      
    private static final String ADD_ANAME_USAGE  = "Add a new ANAME dns record." +
        "\r\n" + DNSRecordParser.PARSE_ANAME_USAGE;

    private static final String ENSURE_ANAME_USAGE = "Add a new ANAME dns record if an identical one does not exist." +
        "\r\n" + DNSRecordParser.PARSE_ANAME_USAGE;

    private static final String REMOVE_MX_USAGE = "Remove an existing MX record by ID." +
        "\r\n\trecordid" +
        "\r\n\t recordid: record id to be removed from the database";


    private static final String REMOVE_SOA_USAGE = "Remove an existing SOA record by ID." +
        "\r\n\trecordid" +
        "\r\nt\t recordid: record id to be removed from the database";


    private static final String REMOVE_ANAME_USAGE = "Remove an existing ANAME record by ID." +
        "\r\n\trecordid" +
        "\r\n\t recordid: record id to be removed from the database";


    private static final String GET_MX_USAGE = "Gets an existing MX record by ID." +
        "\r\n\trecordid" +
        "\r\n\t recordid: record id to be retrieved from the database";


    private static final String GET_SOA_USAGE = "Gets an existing SOA record by ID." +
    	"\r\n\trecordid" +
        "\r\n\t recordid: record id to be retrieved from the database";


    private static final String GET_ANAME_USAGE = "Gets an existing ANAME record by ID." +
        "\r\n\trecordid";

    private static final String GET_ALL_USAGE = "Gets all records in the DNS store.";
    private DNSRecordPrinter printer;
    private DNSRecordParser parser;
    private ConfigurationServiceProxy proxy;
    
    /**
     * Constructor that takes a reference to the configuration service proxy.
     * @param proxy Configuration service proxy for accessing the configuration service.
     * 
     * @since 1.0
     */
	public DNSRecordCommands(ConfigurationServiceProxy proxy)
	{
	    parser = new DNSRecordParser();
	    printer = new DefaultDNSRecordPrinter();
	    this.proxy = proxy;
	}
	
	/*
	 * Convert a dnsjava record to a DnsRecord for use with the proxy.
	 */
	private DnsRecord fromRecord(Record rec)
	{
	    DnsRecord retVal = new DnsRecord();
	    retVal.setData(rec.rdataToWireCanonical());
	    retVal.setDclass(rec.getDClass());
	    retVal.setName(rec.getName().toString());
	    retVal.setTtl(rec.getTTL());
	    retVal.setType(rec.getType());
	    
	    return retVal;
	}
	
	/*
	 * Loads a record from a file.  Records are stored in raw wire format.
	 */
	private DnsRecord loadAndVerifyDnsRecordFromBin(String path)
	{
	    File recFile = new File(path);
	    if (!recFile.exists())
	    	throw new IllegalArgumentException("Record file " + recFile.getAbsolutePath() + " not found");
	    
	    Record rec = null;
	    try
	    {
		    byte[] wire = FileUtils.readFileToByteArray(recFile);
	
		    rec = Record.fromWire(wire, Section.ANSWER);
	    }
	    catch (Exception e)
	    {
	    	throw new RuntimeException("Error reading file " + recFile.getAbsolutePath() + " : " + e.getMessage(), e);
	    }
	    
	    return (rec != null) ? fromRecord(rec) : null;
	}
	
	/*
	 * Adds a DNS record to the configuration service.
	 */
	private void addDNS(DnsRecord dnsRecord)
	{
		try
		{
			proxy.addDNS(new DnsRecord[] {dnsRecord});
			System.out.println("Record added successfully.");
		}		
		catch (RemoteException e)
		{
			throw new RuntimeException("Error adding DNS record: " + e.getMessage(), e);
		}
	
	}

	/*
	 * Removed a DNS record from the service
	 */
	private void removeDNS(long recordId)
	{
		try
		{
			proxy.removeDNSByRecordId(recordId);
			System.out.println("Record removed successfully.");
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service: " + e.getMessage(), e);
		}
	}
	
	/*
	 * Imports a specific DNS record type from a file.
	 */
	private void importRecord(String path, int type)
	{
		DnsRecord dnsRecord = loadAndVerifyDnsRecordFromBin(path);

		if (dnsRecord.getType() != type)
		{
			throw new IllegalArgumentException("File " + path + " does not contain the requested record type");
		}
		
		addDNS(dnsRecord);
	}
	
	/**
	 * Imports an MX record from a file.  The file contains the record in raw DNS wire format.
	 * @param args The first entry in the array contains the file path (required).
	 * 
     * @since 1.0
	 */
	@Command(name = "Dns_MX_Import", usage = IMPORT_MX_USAGE)
	public void mXImport(String[] args)
	{
	    String path = StringArrayUtil.getRequiredValue(args, 0);
	    importRecord(path, Type.MX);
	}
		
	/**
	 * Imports an SOA record from a file.  The file contains the record in raw DNS wire format.
	 * @param args The first entry in the array contains the file path (required).
	 * 
     * @since 1.0
	 */
	@Command(name = "Dns_SOA_Import", usage = IMPORT_SOA_USAGE)
	public void sOAImport(String[] args)
	{
	    String path = StringArrayUtil.getRequiredValue(args, 0);
	    importRecord(path, Type.SOA);
	}
	
	/**
	 * Imports an A record from a file.  The file contains the record in raw DNS wire format.
	 * @param args The first entry in the array contains the file path (required).
	 * 
     * @since 1.0
	 */
	@Command(name = "Dns_ANAME_Import", usage = IMPORT_ADDRESS_USAGE)
	public void importAddress(String[] args)
	{
	    String path = StringArrayUtil.getRequiredValue(args, 0);
	    importRecord(path, Type.A);
	}       
	
	/**
	 * Adds an MX records to the configuration service.
	 * @param args Contains the MX record attributes.
	 * 
	 * @since 1.0
	 */
	@Command(name = "Dns_MX_Add", usage = ADD_MX_USAGE)
	public void addMX(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseMX(args));
		
		addDNS(record);
	}
	
	/**
	 * Adds an MX records to the configuration service only if the record does not exist.
	 * @param args Contains the MX record attributes.
	 * 
	 * @since 1.0
	 */	
	@Command(name = "Dns_MX_Ensure", usage = ENSURE_MX_USAGE)
	public void ensureMX(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseMX(args));
	    if (!verifyIsUnique(record, false))
	    {
	        return;
	    }
	    
		
		addDNS(record);
	}
	
	/**
	 * Adds an SOA records to the configuration service.
	 * @param args Contains the SOA record attributes.
	 * 
	 * @since 1.0
	 */		
	@Command(name = "Dns_SOA_Add", usage = ADD_SOA_USAGE)
	public void addSOA(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseSOA(args));
		
		addDNS(record);
	}
	
	/**
	 * Adds an SOA records to the configuration service only if the record does not exist.
	 * @param args Contains the SOA record attributes.
	 * 
	 * @since 1.0
	 */	
	@Command(name = "Dns_SOA_Ensure", usage = ENSURE_SOA_USAGE)
	public void ensureSOA(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseSOA(args));
	    if (!verifyIsUnique(record, false))
	    {
	        return;
	    }
	    
		addDNS(record);
	}
	
	/**
	 * Adds an A records to the configuration service.
	 * @param args Contains the A record attributes.
	 * 
	 * @since 1.0
	 */
	@Command(name = "Dns_ANAME_Add", usage = ADD_ANAME_USAGE)
	public void addANAME(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseANAME(args));
		addDNS(record);
	}
	
	
	/**
	 * Adds an A records to the configuration service only if the record does not exist.
	 * @param args Contains the A record attributes.
	 * 
	 * @since 1.0
	 */		
	@Command(name = "Dns_ANAME_Ensure", usage = ENSURE_ANAME_USAGE)
	public void ensureANAME(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseANAME(args));
	    if (!verifyIsUnique(record, false))
	    {
	        return;
	    }
	    
		addDNS(record);
	}
	
	/**
	 * Removes an MX record from the configuration service by record id.
	 * @param args The first entry in the array contains the record id (required).
	 * 
	 * @since 1.0
	 */
	@Command(name = "Dns_MX_Remove", usage = REMOVE_MX_USAGE)
	public void removeMX(String[] args)
	{
	    long recordID = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
	    removeDNS(recordID);
	}
	
	/**
	 * Removes an SOA record from the configuration service by record id.
	 * @param args The first entry in the array contains the record id (required).
	 * 
	 * @since 1.0
	 */
	@Command(name = "Dns_SOA_Remove", usage = REMOVE_SOA_USAGE)
	public void removeSOA(String[] args)
	{
	    long recordID = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
	    removeDNS(recordID);
	}
	
	/**
	 * Removes an A record from the configuration service by record id.
	 * @param args The first entry in the array contains the record id (required).
	 * 
	 * @since 1.0
	 */	
	@Command(name = "Dns_ANAME_Remove", usage = REMOVE_ANAME_USAGE)
	public void removeANAME(String[] args)
	{
	    long recordID = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
	    removeDNS(recordID);
	}
	
	/**
	 * Looks up an MX record by record id.
	 * @param args The first entry in the array contains the record id (required).
	 * 
	 * @since 1.0
	 */		
	@Command(name = "Dns_MX_Get", usage = GET_MX_USAGE)
	public void getMX(String[] args)
	{
	    get(Long.parseLong(StringArrayUtil.getRequiredValue(args, 0)));
	}
	
	/**
	 * Looks up an SOA record by record id.
	 * @param args The first entry in the array contains the record id (required).
	 * 
	 * @since 1.0
	 */	
	@Command(name = "Dns_SOA_Get", usage = GET_SOA_USAGE)
	public void getSOA(String[] args)
	{
		get(Long.parseLong(StringArrayUtil.getRequiredValue(args, 0)));
	}
	
	/**
	 * Looks up an A record by record id.
	 * @param args The first entry in the array contains the record id (required).
	 * 
	 * @since 1.0
	 */	
	@Command(name = "Dns_ANAME_Get", usage = GET_ANAME_USAGE)
	public void getANAME(String[] args)
	{
		get(Long.parseLong(StringArrayUtil.getRequiredValue(args, 0)));
	}
	
	/**
	 * Retrieves and prints all records in the configuration store. 
	 * @param args Empty
	 * 
	 * @since 1.0
	 */		
	@Command(name= "Dns_Get_All", usage = GET_ALL_USAGE)
	public void getAll(String[] args)
	{
	    DnsRecord[] records = null;
	    try
	    {
	    	records = proxy.getDNSByType(Type.ANY);
	    }
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service: " + e.getMessage(), e);
		}
		
	    if (records == null || records.length == 0)
	    {
	    	System.out.println("No records found");
	    }
	    else
	    	print(records);
	}
	
	/*
	 * Gets and prints a record by record is
	 */
	private void get(long recordID)
	{
	    DnsRecord record = getRecord(recordID);
	    if (record != null)
	    	printer.print(record);
	}
	
	/**
	 * Looks up all records for a given domain and any sub domains.
	 * @param args The first entry in the array contains the domain name (required).
	 * 
	 * @since 1.0
	 */			
	@Command(name = "Dns_Match", usage = "Resolve all records for the given domain")
	public void match(String[] args)
	{
	    String domain = StringArrayUtil.getRequiredValue(args, 0);
	    DnsRecord[] records = null;
	    Pattern pattern = Pattern.compile(domain);
	    ArrayList<DnsRecord> matchedRecords = new ArrayList<DnsRecord>(); 
	    try
	    {
	    	records = proxy.getDNSByType(Type.ANY);
	    }
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service: " + e.getMessage(), e);
		}
		
	    if (records == null || records.length == 0)
	    {
	        System.out.println("No records found");
	        return;
	    }
	    else
	    {
	    	for (DnsRecord record : records)
	    	{
	    		Matcher matcher = pattern.matcher(record.getName());
	    		if (matcher.find())
	    		{
	    			matchedRecords.add(record);
	    		}
	    	}
	    }
	    
	    if (matchedRecords.size() == 0)
	    {
	        System.out.println("No records found");
	        return;
	    }	    
	    
	    print(matchedRecords.toArray(new DnsRecord[matchedRecords.size()]));
	}
	
	/**
	 * Looks up SOA records for a given domain.
	 * @param args The first entry in the array contains the domain name (required).
	 * 
	 * @since 1.0
	 */	
	@Command(name = "Dns_SOA_Match", usage = "Resolve SOA records for the given domain")
	public void matchSOA(String[] args)
	{
	    match(StringArrayUtil.getRequiredValue(args, 0), Type.SOA);
	}
	
	/**
	 * Looks up A records for a given host name.
	 * @param args The first entry in the array contains the domain name (required).
	 * 
	 * @since 1.0
	 */		
	@Command(name = "Dns_ANAME_Match", usage = "Resolve Address records for the given domain")
	public void matchAName(String[] args)
	{
	    match(StringArrayUtil.getRequiredValue(args, 0), Type.A);
	}
	
	/**
	 * Looks up MX records for a given domain.
	 * @param args The first entry in the array contains the domain name (required).
	 * 
	 * @since 1.0
	 */		
	@Command(name = "Dns_MX_Match", usage = "Resolve MX records for the given domain")
	public void matchMX(String[] args)
	{
	    match(StringArrayUtil.getRequiredValue(args, 0), Type.MX);
	}
	
	/*
	 * gets records for a domain name and sub domains for a specific type of record
	 */
	private void match(String domain, int type)
	{
	    DnsRecord[] records = getRecords(domain, type);
	    if (records != null && records.length > 0)
	    	print(records);
	}
	
	/*
	 * gets a record by record id
	 */
	private DnsRecord getRecord(long recordID)
	{
		DnsRecord dr = null;
		try
		{
			dr = proxy.getDNSByRecordId(recordID);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service: " + e.getMessage(), e);
		}
		
	    if (dr == null)
	    {
	    	System.out.println("No record found matching id.");
	    }
	    
	    return dr;
	}
	
	/*
	 * gets records by name and type
	 */
	private DnsRecord[] getRecords(String domain, int type)
	{
		if (!domain.endsWith("."))
			domain += ".";
		
	    DnsRecord[] records = null;
	    try
	    {
	    	records = proxy.getDNSByNameAndType(domain, type);
	    }
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service: " + e.getMessage(), e);
		}
		
	    if (records == null || records.length == 0)
	    {
	    	System.out.println("No records found");
	    }
	    return records;
	}
	
	/*
	 * ensures that a record is unique in the configuration service
	 */
	private boolean verifyIsUnique(DnsRecord record, boolean details)
	{
	    DnsRecord existing = find(record);
	    if (existing != null)
	    {
	        System.out.println("Record already exists");

            print(existing);

	        return false;
	    }
	    
	    return true;
	}
	        
	/*
	 * finds a specific record by name and type
	 */
	private DnsRecord find(DnsRecord record)
	{
	    DnsRecord[] existingRecords = null;
	    try
	    {
	    	existingRecords = proxy.getDNSByNameAndType(record.getName(), record.getType());
	    }
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service: " + e.getMessage(), e);
		}
		
	    if (existingRecords == null || existingRecords.length == 0)
	    {
	        return null;
	    }
	    	    
	    for (DnsRecord existingRecord : existingRecords)
	    	if (Arrays.areEqual(record.getData(), existingRecord.getData()))
	    		return existingRecord;
	    
	    return null;
	}
	
	/*
	 * prints the contents of an array of records
	 */
	private void print(DnsRecord[] records)
	{
		if (records != null)
		{
		    for(DnsRecord record : records)
		    {
		        print(record);
		        System.out.println("\r\n-------------------------------------------");
		    }
		}
	}
	
	/*
	 * prints the contents of a specific record
	 */
	private void print(DnsRecord dnsRecord)
	{
	    System.out.println("RecordID: " + dnsRecord.getId());                        

	
	    printer.print(dnsRecord);

	}
    
	/**
	 * Sets the printer that will be used to print record query responses.
	 * @param printer The printer that will be used to print record query responses.
	 */
	public void setRecordPrinter(DNSRecordPrinter printer)
	{
		this.printer = printer; 
	}
	
	/**
	 * Sets the printer that will be used to print record query responses.
	 * @param printer The printer that will be used to print record query responses.
	 */
	public void setConfigurationProxy(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy; 
	}	
}
