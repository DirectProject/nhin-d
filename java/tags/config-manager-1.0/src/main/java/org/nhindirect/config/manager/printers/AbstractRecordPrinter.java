package org.nhindirect.config.manager.printers;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;


public abstract class AbstractRecordPrinter<T> implements RecordPrinter<T>
{
	protected final Collection<ReportColumn> reportColumns;
	protected final int tableWidth;
	
	protected static class ReportColumn
	{
		protected final String header;
		protected final int width;
		protected final String fieldName;
		
		public ReportColumn(String header, int width, String fieldName)
		{
			this.header = header;
			this.width = width;
			this.fieldName = fieldName;
		}
	}
	
	public AbstractRecordPrinter(int tableWidth, Collection<ReportColumn> reportColumns)
	{
		this.tableWidth = tableWidth;
		this.reportColumns = reportColumns;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void printRecord(T record)
	{
		printRecords(Arrays.asList(record));
	}
	
	@Override
	public void printRecords(Collection<T> records)
	{
		printHeader();
		
		for (T record : records)
			printRecordInternal(record);
	}
	
	protected void printRecordInternal(T record)
	{
		StringBuilder builder = new StringBuilder();
		
		int cnt = 0;
		for (ReportColumn column : reportColumns)
		{
			

			builder.append("  ");
			String colValue = getColumnValue(column, record);
			builder.append(colValue);
			// pad the rest with spaces
			int padSize = (column.width - 2 ) - colValue.length();
			for (int i = 0; i < padSize; ++i)
				builder.append(' ');

			if (++cnt < reportColumns.size())
				builder.append("|");
		}
		
		builder.append("\r\n");
		for (int i = 0; i < tableWidth; ++i)
			builder.append('-');
		
		System.out.println(builder.toString());
	}
	
	protected String getColumnValue(ReportColumn column, T record)
	{
		// default is to get the field value by introspection using
		// the field name
		try
		{
			Method method = record.getClass().getDeclaredMethod("get" + column.fieldName);
			Object obj = method.invoke(record);
			return obj.toString();
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}
	
	protected void printHeader()
	{
		StringBuilder builder = new StringBuilder();
		
		// top of header
		for (int i = 0; i < tableWidth; ++i)
			builder.append('-');
		
		builder.append("\r\n|");
		
		int cnt = 0;
		int widthUsed = 0;
		for (ReportColumn column : reportColumns)
		{
			int currentWidth = 0;
			if (++cnt >= reportColumns.size())
				currentWidth = tableWidth - widthUsed;
			else 
				currentWidth = column.width;
			
			// center the header
			int padding = (currentWidth - column.header.length()) / 2;
			
			// add pre padding
			for (int i = 0; i < padding; ++i)
				builder.append(' ');
			
			// print header
			builder.append(column.header);
			
			// add post padding
			for (int i = 0; i < (padding -1); ++i)
				builder.append(' ');
			
			builder.append("|");
			
			widthUsed += currentWidth;
		}
		
		// end of header
		builder.append("\r\n");
		for (int i = 0; i < tableWidth; ++i)
			builder.append('-');
		
		System.out.println(builder.toString());
	}
}