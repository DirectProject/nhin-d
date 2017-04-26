package org.nhindirect.gateway.smtp;

import javax.mail.internet.InternetAddress;

import junit.framework.TestCase;

public class DomainPostmasterTest extends TestCase 
{
	public void testConstructDefaultPostmaster()
	{
		DomainPostmaster postmaster = new DomainPostmaster();
		
		assertNotNull(postmaster.getPostmaster());
		assertNull(postmaster.getPostmaster().toString());
		assertEquals("", postmaster.getDomain());
	}
	
	public void testConstructPostmasterWithDomain()
	{
		DomainPostmaster postmaster = new DomainPostmaster("domain1", null);
		
		assertNotNull(postmaster.getPostmaster());
		assertNotNull(postmaster.getPostmaster().toString());
		assertEquals("postmaster@domain1", postmaster.getPostmaster().getAddress());
		assertEquals("domain1", postmaster.getDomain());
	}	
	
	public void testConstructPostmasterWithDomainAndPostmaster() throws Exception
	{
		DomainPostmaster postmaster = new DomainPostmaster("domain1", new InternetAddress("me@domain1"));
		
		assertNotNull(postmaster.getPostmaster());
		assertEquals("me@domain1", postmaster.getPostmaster().toString());
		assertEquals("domain1", postmaster.getDomain());
	}	
	
	public void testConstructPostmaster_NullDomain_AssertException() throws Exception
	{
		boolean exceptionOccured = false;
		try
		{
			new DomainPostmaster(null, null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	public void testSetDomain_DefaultConstructor() throws Exception
	{
		DomainPostmaster postmaster = new DomainPostmaster();
		
		postmaster.setDomain("domain2");
		
		assertEquals("domain2", postmaster.getDomain());
	}	
	
	public void testSetDomain_ParamConstructor() throws Exception
	{
		DomainPostmaster postmaster = new DomainPostmaster("domain1", new InternetAddress("me@domain1"));
		
		postmaster.setDomain("domain2");
		assertEquals("domain2", postmaster.getDomain());
	}
	
	public void testSetPostmaster_DefaultConstructor() throws Exception
	{
		DomainPostmaster postmaster = new DomainPostmaster();
		
		postmaster.setPostmasters(new InternetAddress("me@domain1"));
		assertEquals("me@domain1", postmaster.getPostmaster().toString());
	}	
	
	public void testSetPostmaster_ParamConstructor() throws Exception
	{
		DomainPostmaster postmaster = new DomainPostmaster("domain1", new InternetAddress("me@domain1"));
			
		postmaster.setPostmasters(new InternetAddress("me@domain2"));
		assertEquals("me@domain2", postmaster.getPostmaster().toString());
	}		
}

