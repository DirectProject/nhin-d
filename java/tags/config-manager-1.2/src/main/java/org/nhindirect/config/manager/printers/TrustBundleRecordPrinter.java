package org.nhindirect.config.manager.printers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

public class TrustBundleRecordPrinter extends AbstractRecordPrinter<org.nhind.config.TrustBundle>
{
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	protected static final String BUNDLE_ID_COL = "ID";
	protected static final String BUNDLE_NAME_COL = "Bundle Name";
	protected static final String BUNDLE_URL_COL = "Bundle URL";	
	protected static final String BUNDLE_REFRESH_COL = "Refresh Interval";		
	protected static final String BUNDLE_LAST_CHECKED_COL = "Last Checked";	
	protected static final String BUNDLE_LAST_UPDATED_COL = "Last Updated";		
	
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();

		REPORT_COLS.add(new ReportColumn(BUNDLE_ID_COL, 10, "Id"));
		REPORT_COLS.add(new ReportColumn(BUNDLE_NAME_COL, 40, "BundleName"));
		REPORT_COLS.add(new ReportColumn(BUNDLE_URL_COL, 80, "BundleURL"));
		REPORT_COLS.add(new ReportColumn(BUNDLE_REFRESH_COL, 20, "RefreshInterval"));		
		REPORT_COLS.add(new ReportColumn(BUNDLE_LAST_CHECKED_COL, 30, "LastChecked"));	
		REPORT_COLS.add(new ReportColumn(BUNDLE_LAST_UPDATED_COL, 30, "Last Updated"));		
	}
	
	protected final DateFormat dtFormat;
	
	public TrustBundleRecordPrinter()
	{
		super(200, REPORT_COLS);
		
		dtFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	}
	
	@Override
	protected String getColumnValue(ReportColumn column, org.nhind.config.TrustBundle bundle)
	{

		try
		{
			if (column.header.equals(BUNDLE_LAST_CHECKED_COL))
			{
				return (bundle.getLastRefreshAttempt() == null) ? "N/A" : dtFormat.format(bundle.getLastRefreshAttempt().getTime());
						
			}
			else if (column.header.equals(BUNDLE_LAST_UPDATED_COL))
			{
				return (bundle.getLastSuccessfulRefresh() == null) ? "N/A" : dtFormat.format(bundle.getLastSuccessfulRefresh().getTime());
			}	
			else if (column.header.equals(BUNDLE_REFRESH_COL))
			{
				return bundle.getRefreshInterval() + " sec";
			}				
			else
				return super.getColumnValue(column, bundle);
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}	
}
