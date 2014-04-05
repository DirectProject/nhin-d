package org.nhindirect.common.rest;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpUriRequest;

public abstract class AbstractBasicAuthServiceSecurityManager implements ServiceSecurityManager
{
	protected final String AUTH_TYPE = "BASIC ";
	protected final String AUTH_HEADER = "Authorization";
	
	protected String username;
	protected String password;
	
	public AbstractBasicAuthServiceSecurityManager()
	{
		
	}
	
	@Override
	public void init() 
	{
		// do nothing
	}

	@Override
	public void authenticateSession() 
	{
		// do nothing
	}

	@Override
	public HttpUriRequest createAuthenticatedRequest(HttpUriRequest request) 
	{
		final String basicAuthCredFormat = username + ":" + password;
		
		final String encodedFormat = AUTH_TYPE + new String(Base64.encodeBase64(basicAuthCredFormat.getBytes()));
		
		request.addHeader(AUTH_HEADER, encodedFormat);
		
		return request;
	}
	
}
