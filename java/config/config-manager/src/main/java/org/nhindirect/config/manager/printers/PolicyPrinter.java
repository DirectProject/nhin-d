package org.nhindirect.config.manager.printers;

import java.util.ArrayList;
import java.util.Collection;


public class PolicyPrinter extends AbstractRecordPrinter<org.nhind.config.CertPolicy>
{
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	protected static final String POLICY_NAME_COL = "Policy Name";
	protected static final String POLICY_TYPE_COL = "Lexicon";	
	protected static final String POLICY_DEF_COL = "Defintion";		
	
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();
		
		REPORT_COLS.add(new ReportColumn(POLICY_NAME_COL, 40, "PolicyName"));
		REPORT_COLS.add(new ReportColumn(POLICY_TYPE_COL, 20, "Lexicon"));
		REPORT_COLS.add(new ReportColumn(POLICY_DEF_COL, 90, "PolicyData"));		
	}
	
	public PolicyPrinter()
	{
		super(150, REPORT_COLS);
	}
	
	@Override
	protected String getColumnValue(ReportColumn column, org.nhind.config.CertPolicy policy)
	{

		try
		{
			if (column.header.equals(POLICY_DEF_COL))	
			{
				return new String(policy.getPolicyData());
			}
			else
				return super.getColumnValue(column, policy);
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}	
}
