package org.nhindirect.dns.tools;

import java.util.ArrayList;
import java.util.Collection;

import org.nhind.config.DnsRecord;

public class MockDNSRecordPrinter implements DNSRecordPrinter 
{
	
	public Collection<DnsRecord> printedRecords = new ArrayList<DnsRecord>();
	public int printRecordCalled = 0;
	public int printCollectionCalled = 0;
	public int printArrayCalled = 0;

	@Override
	public void print(Collection<DnsRecord> records) 
	{	
		++printCollectionCalled;
		for (DnsRecord rec : records)
			print(rec);

	}

	@Override
	public void print(DnsRecord record) 
	{
		++printRecordCalled;
		printedRecords.add(record);
	}

	@Override
	public void print(DnsRecord[] records) 
	{
		++printArrayCalled;
		for (DnsRecord rec : records)
			print(rec);		
	}

}
