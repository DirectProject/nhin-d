package org.nhindirect.config.manager.printers;

import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.nhindirect.stagent.cert.Thumbprint;

public class BundleAnchorRecordPrinter extends AbstractRecordPrinter<org.nhind.config.TrustBundleAnchor>
{
	protected static final String ANCHOR_NAME_COL = "Anchor Name";
	protected static final String TP_NAME_COL = "Thumbprint";
	protected static final String EXPIRES_COL = "Expires";
	
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	protected final DateFormat dtFormat;
	
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();

		REPORT_COLS.add(new ReportColumn(ANCHOR_NAME_COL, 50, "AnchorAsX509Certificate"));
		REPORT_COLS.add(new ReportColumn(TP_NAME_COL, 44, "AnchorAsX509Certificate"));	
		REPORT_COLS.add(new ReportColumn(EXPIRES_COL, 20, "Expires"));			
		
	}
	
	public BundleAnchorRecordPrinter()
	{
		super(170, REPORT_COLS);
		
		dtFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	}
	
	@Override
	protected String getColumnValue(ReportColumn column, org.nhind.config.TrustBundleAnchor record)
	{
		try
		{
			final X509Certificate anchor = CertUtils.toX509Certificate(record.getData());
			if (column.header.equals(ANCHOR_NAME_COL))
			{
				final X509Principal principal = PrincipalUtil.getSubjectX509Principal(anchor);
				final Vector<?> values = principal.getValues(X509Name.CN);
				final String cn = (String) values.get(0);
				
				return cn;
			}
			else if (column.header.equals(TP_NAME_COL))
				return Thumbprint.toThumbprint(anchor).toString();
			else if (column.header.equals(EXPIRES_COL))
			{
				return dtFormat.format(record.getValidEndDate().getTime());
			}
			else
				return super.getColumnValue(column, record);
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}	
}
