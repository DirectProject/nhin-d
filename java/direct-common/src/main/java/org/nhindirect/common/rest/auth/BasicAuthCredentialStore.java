package org.nhindirect.common.rest.auth;

public interface BasicAuthCredentialStore 
{
	public BasicAuthCredential getCredential(String name);
}
