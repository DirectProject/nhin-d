package org.nhindirect.common.rest;

public class BootstrapBasicAuthServiceSecurityManager extends AbstractBasicAuthServiceSecurityManager
{
	
	public BootstrapBasicAuthServiceSecurityManager()
	{
		super();
	}
	
	public BootstrapBasicAuthServiceSecurityManager(String user, String pass)
	{
		super();
		setCredentials(user, pass);
	}
	
	public void setCredentials(String user, String pass)
	{
		this.username = user;
		this.password = pass;
	}
}
