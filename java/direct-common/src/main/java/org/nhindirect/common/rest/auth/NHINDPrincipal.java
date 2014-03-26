package org.nhindirect.common.rest.auth;

import java.security.Principal;

public class NHINDPrincipal implements Principal
{
	protected final String name;
	
	protected final String role;
	
	public NHINDPrincipal(String name, String role)
	{
		this.name = name;
		this.role = role;
	}

	@Override
	public String getName() 
	{
		return name;
	}
	
	public String getRole()
	{
		return role;
	}
}
