package org.nhindirect.config.manager.printers;


import java.net.URL;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.nhindirect.dns.DNSException;
import org.nhindirect.stagent.cert.Thumbprint;

public class CertRecordPrinter extends AbstractRecordPrinter<org.nhind.config.Certificate>
{
	
	protected static final SimpleDateFormat dateFormatter;
	
	protected static final String CERT_NAME_COL = "Subject Name/URL";
	protected static final String RECORD_TYPE_COL = "Record Type";	
	protected static final String PRIVATE_IND_COL = "Private Key";		
	protected static final String TP_NAME_COL = "Thumbprint";
	protected static final String EXPIRES_COL = "Expires";
	
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();
		
		REPORT_COLS.add(new ReportColumn(CERT_NAME_COL, 55, "getCertificate"));
		REPORT_COLS.add(new ReportColumn(RECORD_TYPE_COL, 11, "getCertificate"));
		REPORT_COLS.add(new ReportColumn(PRIVATE_IND_COL, 12, "getCertificate"));		
		REPORT_COLS.add(new ReportColumn(TP_NAME_COL, 55, "getCertificate"));	
		REPORT_COLS.add(new ReportColumn(EXPIRES_COL, 15, "getCertificate"));	


		dateFormatter = new SimpleDateFormat("MMM d yyyy" , Locale.getDefault());
	}

	
	public CertRecordPrinter()
	{
		super(150, REPORT_COLS);
	}
	
	@SuppressWarnings("unused")
	@Override
	protected String getColumnValue(ReportColumn column, org.nhind.config.Certificate retCert)
	{
		String tpOrURL = null;
		boolean isURL = false;
		
		X509Certificate cert = null;
		
		try
		{
			cert = CertUtils.toX509Certificate(retCert.getData());
			tpOrURL = Thumbprint.toThumbprint(cert).toString();
		}
		catch (DNSException e)
		{
			// probably not an X509 CERT... might be a URL
		}
		
		if (tpOrURL == null)
		{
			try
			{
				tpOrURL = new String(retCert.getData());
				URL url = new URL(tpOrURL);
				isURL = true;
			}
			catch (Exception e)
			{
				// invalid URL
				return "";
			}
		}
		
		
		
		try
		{
			if (column.header.equals(CERT_NAME_COL))			
				return retCert.getOwner();
			else if (column.header.equals(RECORD_TYPE_COL))
				return (isURL) ? "IPKIX" : "PKIX";			
			else if (column.header.equals(TP_NAME_COL))
				return isURL ? tpOrURL : Thumbprint.toThumbprint(cert).toString();			
			else if (column.header.equals(EXPIRES_COL))
				return isURL ? "" : dateFormatter.format(cert.getNotAfter());	
			else if (column.header.equals(PRIVATE_IND_COL))
				return retCert.isPrivateKey() ? "Y" : "N";
			else
				return super.getColumnValue(column, retCert);
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}
	
}
