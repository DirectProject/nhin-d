package org.nhindirect.config.manager.printers;

import java.util.ArrayList;
import java.util.Collection;

public class PolicyUsagePrinter extends AbstractRecordPrinter<org.nhind.config.CertPolicyGroupReltn>
{
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	protected static final String POLICY_NAME_COL = "Policy Name";
	protected static final String POLICY_LEXICON_COL = "Lexicon";	
	protected static final String POLICY_USAGE_COL = "Usage";		
	protected static final String INCOMING_COL = "Incoming";
	protected static final String OUTGOING_COL = "Outgoing";
			
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();
		
		REPORT_COLS.add(new ReportColumn(POLICY_NAME_COL, 40, "PolicyName"));
		REPORT_COLS.add(new ReportColumn(POLICY_LEXICON_COL, 20, "Lexicon"));
		REPORT_COLS.add(new ReportColumn(POLICY_USAGE_COL, 20, "PolicyUse"));		
		REPORT_COLS.add(new ReportColumn(INCOMING_COL, 12, "Incoming"));
		REPORT_COLS.add(new ReportColumn(OUTGOING_COL, 12, "Outgoing"));
	}
	
	public PolicyUsagePrinter()
	{
		super(110, REPORT_COLS);
	}
	
	@Override
	protected String getColumnValue(ReportColumn column, org.nhind.config.CertPolicyGroupReltn reltn)
	{
		try
		{
			if (column.header.equals(POLICY_NAME_COL))	
			{
				return reltn.getCertPolicy().getPolicyName();
			}
			else if (column.header.equals(POLICY_LEXICON_COL))	
			{
				return reltn.getCertPolicy().getLexicon().toString();
			}	
			else if (column.header.equals(INCOMING_COL))	
			{
				return Boolean.toString(reltn.isIncoming());
			}	
			else if (column.header.equals(OUTGOING_COL))	
			{
				return Boolean.toString(reltn.isOutgoing());
			}				
			else
				return super.getColumnValue(column, reltn);
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}
}
