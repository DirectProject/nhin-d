package org.nhindirect.config.manager.printers;

import java.util.ArrayList;
import java.util.Collection;

public class SettingRecordPrinter extends AbstractRecordPrinter<org.nhind.config.Setting>
{
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	protected static final String SETTING_NAME_COL = "Name";
	protected static final String SETTING_VALUE_COL = "Value";		
	
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();
		
		REPORT_COLS.add(new ReportColumn(SETTING_NAME_COL, 15, "Name"));
		REPORT_COLS.add(new ReportColumn(SETTING_NAME_COL, 85, "Value"));	
	}
	
	public SettingRecordPrinter()
	{
		super(100, REPORT_COLS);
	}
	
}
