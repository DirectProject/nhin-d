package org.nhindirect.common.rest.auth.impl;

import org.nhindirect.common.rest.auth.BasicAuthCredential;

public class DefaultBasicAuthCredential implements BasicAuthCredential
{
	protected final String name;
	
	protected final String password;
	
	protected final String role;
	
	public DefaultBasicAuthCredential(String name, String password, String role)
	{
		this.name = name;
		this.password = password;
		this.role = role;
	}

	@Override
	public String getUser() 
	{
		return name;
	}

	@Override
	public String getPassword() 
	{
		return password;
	}

	@Override
	public String getRole() 
	{
		return role;
	}
	
	
}
