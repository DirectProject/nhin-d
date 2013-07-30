package org.nhindirect.install;

import java.io.File;

public abstract class AbstractCertCreator 
{
	protected String cnField;
	protected String expField;
	protected String keyStr;
	protected String pass;	
	
	protected static File createNewFileName(String fieldName, boolean isKey)
	{
		String fileName;
		
		int index;
		final String field = fieldName;

		index = field.indexOf("@");
		if (index > -1)
			fileName = field.substring(0, index);
		else
			fileName = field;			
		
		if (isKey)
			fileName += "Key";
		
		fileName += ".der";
		
		return new File(fileName);
		
	}	
	
	protected abstract void createCert(final String[] args);
}
