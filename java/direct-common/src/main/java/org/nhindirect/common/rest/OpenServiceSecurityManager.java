package org.nhindirect.common.rest;

import org.apache.http.client.methods.HttpUriRequest;

public class OpenServiceSecurityManager implements ServiceSecurityManager
{

	public OpenServiceSecurityManager()
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
		// just return back the request
		return request;
	}

}
