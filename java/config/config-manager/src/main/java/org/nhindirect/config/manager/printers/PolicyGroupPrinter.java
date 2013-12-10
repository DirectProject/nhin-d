package org.nhindirect.config.manager.printers;

import java.util.ArrayList;
import java.util.Collection;

public class PolicyGroupPrinter extends AbstractRecordPrinter<org.nhind.config.CertPolicyGroup>
{
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	protected static final String POLICY_GROUP_NAME_COL = "Policy Group Name";
	protected static final String POLICY_NUMBER_COL = "Number of Policies";		
	
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();
		
		REPORT_COLS.add(new ReportColumn(POLICY_GROUP_NAME_COL, 40, "PolicyGroupName"));
		REPORT_COLS.add(new ReportColumn(POLICY_NUMBER_COL, 16, "Policies"));	
	}
	
	public PolicyGroupPrinter()
	{
		super(57, REPORT_COLS);
	}
	
	@Override
	protected String getColumnValue(ReportColumn column, org.nhind.config.CertPolicyGroup group)
	{

		try
		{
			if (column.header.equals(POLICY_NUMBER_COL))	
			{
				return Integer.toString((group.getCertPolicyGroupReltn() == null) ? 0 : group.getCertPolicyGroupReltn().length); 
			}
			else
				return super.getColumnValue(column, group);
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}
}
