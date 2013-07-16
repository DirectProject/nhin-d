package org.nhindirect.dns.tools;

import java.util.ArrayList;
import java.util.Collection;

import org.nhind.config.Certificate;
import org.nhindirect.dns.tools.printers.CertRecordPrinter;

public class CertRecordCounterPrinter extends CertRecordPrinter
{
	public Collection<Certificate> printedRecords = new ArrayList<Certificate>();
	
	protected int recordCount;
	
	public CertRecordCounterPrinter()
	{
		super();
	}
	
	protected void printRecordInternal(Certificate record)
	{
		++recordCount;
		printedRecords.add(record);
		super.printRecordInternal(record);
	}
	
	public int getRecordCount()
	{
		return recordCount;
	}
	
	public Collection<Certificate> getPrintedRecords()
	{
		return printedRecords;
	}
}
