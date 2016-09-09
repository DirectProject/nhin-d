package org.nhindirect.config.manager.printers;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

public class DomainPrinter extends AbstractRecordPrinter<org.nhind.config.Domain>
{
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	protected static final String DOMAIN_ID_COL = "ID";
	protected static final String DOMAIN_NAME_COL = "Domain Name";
	protected static final String POSTMASTER_TYPE_COL = "Postmaster Email";	
	protected static final String STATUS_COL = "Status";		
	
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();

		REPORT_COLS.add(new ReportColumn(DOMAIN_ID_COL, 20, "Id"));
		REPORT_COLS.add(new ReportColumn(DOMAIN_NAME_COL, 40, "DomainName"));
		REPORT_COLS.add(new ReportColumn(POSTMASTER_TYPE_COL, 50, "PostmasterEmail"));
		REPORT_COLS.add(new ReportColumn(STATUS_COL, 20, "Status"));		
	}
	
	public DomainPrinter()
	{
		super(130, REPORT_COLS);
	}
	
	@Override
	protected String getColumnValue(ReportColumn column, org.nhind.config.Domain domain)
	{

		try
		{
			if (column.header.equals(POSTMASTER_TYPE_COL))	
			{
				if (StringUtils.isEmpty(domain.getPostMasterEmail()))
					return "<Not Configured>";
				else
					return domain.getPostMasterEmail();
			}
			else
				return super.getColumnValue(column, domain);
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}	
}
