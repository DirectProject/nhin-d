package org.nhindirect.install;

import java.util.HashMap;
import java.util.Map;


import org.nhindirect.stagent.cert.tools.certgen.CertCreateFields;
import org.nhindirect.stagent.cert.tools.certgen.CertGenerator;


public class CACreator extends AbstractCertCreator
{	
	public CACreator()
	{
		
	}
	
	public static void main(String[] args) 
	{
		final CACreator creator = new CACreator();
		
		creator.createCert(args);
	}
	
	@Override
	public void createCert(final String[] args)
	{
		cnField = args[0];
		expField = args[1]; 
		keyStr = args[2];
		pass = args[3];		
		
		Map<String, Object> attributes = new HashMap<String, Object>(); 
		attributes.put("CN",cnField);
		
		int exp = Integer.parseInt(expField);
		int keyStre =  Integer.parseInt(keyStr);
		
		CertCreateFields createFields = new CertCreateFields(attributes, createNewFileName(cnField, false), createNewFileName(cnField, true),
				pass.toCharArray(), exp, 
				keyStre, null, null);	
		
		try
		{
			CertGenerator.createCertificate(createFields, false);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}	
	}
	
}
