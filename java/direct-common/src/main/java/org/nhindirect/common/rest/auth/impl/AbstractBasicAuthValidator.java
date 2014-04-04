package org.nhindirect.common.rest.auth.impl;

import org.apache.commons.codec.binary.Base64;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;
import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.NHINDPrincipal;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;

public abstract class AbstractBasicAuthValidator implements BasicAuthValidator
{
	protected BasicAuthCredentialStore credStore;
	
	public AbstractBasicAuthValidator()
	{
		
	}
	
	public AbstractBasicAuthValidator(BasicAuthCredentialStore credStore)
	{
		setAuthStore(credStore);
	}
	
	public void setAuthStore(BasicAuthCredentialStore credStore)
	{
		this.credStore = credStore;
	}

	@Override
	public NHINDPrincipal authenticate(String rawAuth) throws BasicAuthException
	{
		
		
		// raw auth should start with "Basic" and contain a space
		// split the string by a space
		int idx = rawAuth.indexOf(" ");
		final String parsedRawAuth = (idx >= 0) ? rawAuth.substring(idx + 1) : rawAuth;
		
		// first decode
		final String authString = new String(Base64.decodeBase64(parsedRawAuth));
		
		// now parse into username and password 
		final String[] userPass = authString.split(":");
		
		return authenticate(userPass[0], userPass[1]);
	}	
}
