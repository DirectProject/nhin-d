package org.nhindirect.common.rest.auth;

import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;

public interface BasicAuthValidator 
{
	public NHINDPrincipal authenticate(String rawAuth) throws BasicAuthException;
	
	public NHINDPrincipal authenticate(String subject, String password) throws BasicAuthException;
}
