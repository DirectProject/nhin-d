package org.nhindirect.common.crypto.tools.commands.printers;

import java.security.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.codec.binary.Hex;
import org.nhindirect.common.crypto.tools.commands.KeyModel;
import org.nhindirect.common.tooling.printer.AbstractRecordPrinter;

public class KeyPrinter extends AbstractRecordPrinter<KeyModel>
{
	protected static final Collection<ReportColumn> REPORT_COLS;
	
	protected static final String KEY_NAME_COL = "Key Name";
	protected static final String KEY_TYPE_COL = "Key Type";
	protected static final String KEY_TEXT_COL = "Key Text";
	protected static final String KEY_TB_COL = "Key Thumbprint";	
	
	static
	{
		REPORT_COLS = new ArrayList<ReportColumn>();
		
		REPORT_COLS.add(new ReportColumn(KEY_NAME_COL, 40, "KeyName"));
		REPORT_COLS.add(new ReportColumn(KEY_TYPE_COL, 25, "KeyType"));	
		REPORT_COLS.add(new ReportColumn(KEY_TEXT_COL, 16, "KeyText"));		
		REPORT_COLS.add(new ReportColumn(KEY_TB_COL, 30, "KeyThumbprints"));		
	}
	
	public KeyPrinter()
	{
		super(111, REPORT_COLS);
	}
	
	@Override
	protected String getColumnValue(ReportColumn column, KeyModel model)
	{

		try
		{
			if (column.getHeader().equals(KEY_TYPE_COL))	
			{
				String type = "";
				final Key key = model.getKey();
				if (key instanceof javax.crypto.SecretKey)
					type = "Secret Key: " + key.getAlgorithm();
				else if (key instanceof java.security.PublicKey)
					type = "Public Key: " + key.getAlgorithm();
				else if (key instanceof java.security.PrivateKey)
					type = "Key Pair: " + key.getAlgorithm();
				else
					type = key.getClass().toString();
				
				return type;
			}
			else if (column.getHeader().equals(KEY_TEXT_COL))
			{
				return new String(model.getKeyText());
			}
			else if (column.getHeader().equals(KEY_TB_COL))
			{
				byte[] data = null;
				final Key key = model.getKey();
				if (key instanceof javax.crypto.SecretKey)
					data = ((javax.crypto.SecretKey)key).getEncoded();
				else if (key instanceof java.security.PublicKey)
					data = ((java.security.PublicKey)key).getEncoded();
				else if (key instanceof java.security.PrivateKey)
					data = ((java.security.PrivateKey)key).getEncoded();

				if (data == null)
					return "NA";
				
				final MessageDigest sha = MessageDigest.getInstance("SHA-1");
				byte[] hash = sha.digest(data);
				
				return Hex.encodeHexString(hash);
				
			}			
			else
				return super.getColumnValue(column, model);
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}
}
