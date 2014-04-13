package org.nhindirect.config.manager.printers;

import java.util.Collection;

public interface RecordPrinter<T> 
{
	public void printRecord(T rec);
	
	public void printRecords(Collection<T> recs);
}
