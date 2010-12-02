package org.nhindirect.dns.tools;

import java.io.File;
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
    	"\r\n" + DnsRecordParser.PARSE_MX_USAGE;

    private static final String ENSURE_MX_USAGE = "Adds a new MX dns record if an identical one does't already exist. " +
        "\r\n" + DnsRecordParser.PARSE_MX_USAGE;

    private static final String ADD_SOA_USAGE = "Add a new SOA dns record." +
        "\r\n" + DnsRecordParser.PARSE_SOA_USAGE;

    private static final String ENSURE_SOA_USAGE = "Add a new SOA dns record if an identical one does not exist." +
        "\r\n" + DnsRecordParser.PARSE_SOA_USAGE;
      
    private static final String ADD_ANAME_USAGE  = "Add a new ANAME dns record." +
        "\r\n" + DnsRecordParser.PARSE_ANAME_USAGE;

    private static final String ENSURE_ANAME_USAGE = "Add a new ANAME dns record if an identical one does not exist." +
        "\r\n" + DnsRecordParser.PARSE_ANAME_USAGE;

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

    DnsRecordPrinter printer;
    private DnsRecordParser parser;
    private final ConfigurationServiceProxy proxy;
    
	public DNSRecordCommands(ConfigurationServiceProxy proxy)
	{
	    parser = new DnsRecordParser();
	    printer = new DnsRecordPrinter();
	    this.proxy = proxy;
	}
	
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
	
	protected DnsRecord loadAndVerifyDnsRecordFromBin(String path)
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
	    	System.out.println("Error reading file " + recFile.getAbsolutePath());
	    }
	    
	    return (rec != null) ? fromRecord(rec) : null;
	}
	
	private void addDNS(DnsRecord dnsRecord)
	{
		try
		{
			proxy.addDNS(new DnsRecord[] {dnsRecord});
			System.out.println("Record added successfully.");
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service.");
		}
	}

	private void removeDNS(long recordId)
	{
		try
		{
			proxy.removeDNSByRecordId(recordId);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service.");
		}
	}
	
	protected void importRecord(String path, int type)
	{
		DnsRecord dnsRecord = loadAndVerifyDnsRecordFromBin(path);

		if (dnsRecord.getType() != type)
		{
			throw new IllegalArgumentException("File " + path + " does not contain the requested record type");
		}
		
		addDNS(dnsRecord);
	}
	
	@Command(name = "Dns_MX_Import", usage = IMPORT_MX_USAGE)
	public void mXImport(String[] args)
	{
	    String path = StringArrayUtil.getRequiredValue(args, 0);
	    importRecord(path, Type.MX);
	}
		
	@Command(name = "Dns_SOA_Import", usage = IMPORT_SOA_USAGE)
	public void sOAImport(String[] args)
	{
	    String path = StringArrayUtil.getRequiredValue(args, 0);
	    importRecord(path, Type.SOA);
	}
	
	@Command(name = "Dns_ANAME_Import", usage = IMPORT_ADDRESS_USAGE)
	public void importAddress(String[] args)
	{
	    String path = StringArrayUtil.getRequiredValue(args, 0);
	    importRecord(path, Type.A);
	}       
	
	@Command(name = "Dns_MX_Add", usage = ADD_MX_USAGE)
	public void addMX(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseMX(args));
		
		addDNS(record);
	}
	
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
	
	@Command(name = "Dns_SOA_Add", usage = ADD_SOA_USAGE)
	public void addSOA(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseSOA(args));
		
		addDNS(record);
	}
	
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
	
	@Command(name = "Dns_ANAME_Add", usage = ADD_ANAME_USAGE)
	public void addANAME(String[] args)
	{
	    DnsRecord record = fromRecord(parser.parseANAME(args));
		addDNS(record);
	}
	
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
	
	@Command(name = "Dns_MX_Remove", usage = REMOVE_MX_USAGE)
	public void removeMX(String[] args)
	{
	    long recordID = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
	    removeDNS(recordID);
	}
	

	@Command(name = "Dns_SOA_Remove", usage = REMOVE_SOA_USAGE)
	public void removeSOA(String[] args)
	{
	    long recordID = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
	    removeDNS(recordID);
	}
	
	@Command(name = "Dns_ANAME_Remove", usage = REMOVE_ANAME_USAGE)
	public void removeANAME(String[] args)
	{
	    long recordID = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
	    removeDNS(recordID);
	}
	
	@Command(name = "Dns_MX_Get", usage = GET_MX_USAGE)
	public void getMX(String[] args)
	{
	    get(Long.parseLong(StringArrayUtil.getRequiredValue(args, 0)));
	}
	
	@Command(name = "Dns_SOA_Get", usage = GET_SOA_USAGE)
	public void getSOA(String[] args)
	{
		get(Long.parseLong(StringArrayUtil.getRequiredValue(args, 0)));
	}
	
	@Command(name = "Dns_ANAME_Get", usage = GET_ANAME_USAGE)
	public void getANAME(String[] args)
	{
		get(Long.parseLong(StringArrayUtil.getRequiredValue(args, 0)));
	}
	
	private void get(long recordID)
	{
	    DnsRecord record = getRecord(recordID);
	    printer.print(record);
	}
	
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
			throw new RuntimeException("Error accessing configuration service.");
		}
		
	    if (records == null || records.length == 0)
	    {
	        throw new IllegalArgumentException("No records found");
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
	        throw new IllegalArgumentException("No matches found");
	    }	    
	    
	    print(matchedRecords.toArray(new DnsRecord[matchedRecords.size()]));
	}
	

	@Command(name = "Dns_SOA_Match", usage = "Resolve SOA records for the given domain")
	public void matchSOA(String[] args)
	{
	    match(StringArrayUtil.getRequiredValue(args, 0), Type.SOA);
	}
	
	@Command(name = "Dns_ANAME_Match", usage = "Resolve Address records for the given domain")
	public void matchAName(String[] args)
	{
	    match(StringArrayUtil.getRequiredValue(args, 0), Type.A);
	}
	
	@Command(name = "Dns_MX_Match", usage = "Resolve MX records for the given domain")
	public void MatchMX(String[] args)
	{
	    match(StringArrayUtil.getRequiredValue(args, 0), Type.MX);
	}
	
	private void match(String domain, int type)
	{
	    DnsRecord[] records = getRecords(domain, type);
	    if (records == null || records.length == 0)
	    {
	        throw new IllegalArgumentException("No matches");
	    }
	    print(records);
	}
	
	private DnsRecord getRecord(long recordID)
	{
		DnsRecord dr = null;
		try
		{
			dr = proxy.getDNSByRecordId(recordID);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service.");
		}
		
	    if (dr == null)
	    {
	        throw new IllegalArgumentException("No record found matching id");
	    }
	    
	    return dr;
	}
	
	private DnsRecord[] getRecords(String domain, int type)
	{
	    DnsRecord[] records = null;
	    try
	    {
	    	records = proxy.getDNSByNameAndType(domain, type);
	    }
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service.");
		}
		
	    if (records == null || records.length == 0)
	    {
	        throw new IllegalArgumentException("No matches");
	    }
	    return records;
	}
	
	private boolean verifyIsUnique(DnsRecord record, boolean details)
	{
	    DnsRecord existing = find(record);
	    if (existing != null)
	    {
	        System.out.println("Record already exists");
	        if (details)
	        {
	            print(existing);
	        }
	        else
	        {
	        	System.out.println("RecordID: " + existing.getId());
	        }
	        return false;
	    }
	    
	    return true;
	}
	        
	private DnsRecord find(DnsRecord record)
	{
	    DnsRecord[] existingRecords = null;
	    try
	    {
	    	existingRecords = proxy.getDNSByNameAndType(record.getName(), record.getType());
	    }
		catch (Exception e)
		{
			throw new RuntimeException("Error accessing configuration service.");
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
	
	void print(DnsRecord[] records)
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
	
	void print(DnsRecord dnsRecord)
	{
	    System.out.println("RecordID: " + dnsRecord.getId());                        

	
	    printer.print(dnsRecord);
	
	    //CommandUI.Print("CreateDate", dnsRecord.CreateDate);
	    //CommandUI.Print("UpdateDate", dnsRecord.UpdateDate);
	}
    
}
