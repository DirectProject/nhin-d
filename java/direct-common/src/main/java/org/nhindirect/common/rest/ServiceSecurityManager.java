package org.nhindirect.common.rest;

import org.apache.http.client.methods.HttpUriRequest;

public interface ServiceSecurityManager 
{
	public void init();
	
	public void authenticateSession();
	
	public HttpUriRequest createAuthenticatedRequest(HttpUriRequest request);
}
