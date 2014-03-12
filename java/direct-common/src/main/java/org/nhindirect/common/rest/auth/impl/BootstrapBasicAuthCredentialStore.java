package org.nhindirect.common.rest.auth.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.nhindirect.common.rest.auth.BasicAuthCredential;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;

public class BootstrapBasicAuthCredentialStore implements BasicAuthCredentialStore
{
	protected Map<String, BasicAuthCredential> credentialMap;
	
	public BootstrapBasicAuthCredentialStore()
	{
		credentialMap =  new HashMap<String, BasicAuthCredential>();
	}
	
	public BootstrapBasicAuthCredentialStore(List<BasicAuthCredential> credentials)
	{
		this();
		
		setCredentails(credentials);
	}

	public void setCredentails(List<BasicAuthCredential> credentials)
	{
		for (BasicAuthCredential cred : credentials)
			credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
	}

	public void setCredentialsAsDelimetedString(List<String> credentials)
	{
		for (String str : credentials)
		{
			final String parsedStr[] = str.split(",");
			final BasicAuthCredential cred = new DefaultBasicAuthCredential(parsedStr[0], parsedStr[1], parsedStr[2]);
			credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
		}
	}
	
	public void setCredentialsAsProperties(Properties credentials)
	{
		for (Entry<Object, Object> entry : credentials.entrySet())
		{
			final String parsedStr[] = entry.getValue().toString().split(",");
			final BasicAuthCredential cred = new DefaultBasicAuthCredential(parsedStr[0], parsedStr[1], parsedStr[2]);
			credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
		}
	}
	
	@Override
	public BasicAuthCredential getCredential(String name) 
	{
		return credentialMap.get(name.toUpperCase(Locale.getDefault()));
	}
}
