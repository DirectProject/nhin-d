package org.nhindirect.common.rest.auth;

public interface BasicAuthCredential 
{
	public String getUser();
	
	public String getPassword();
	
	public String getRole();
}
