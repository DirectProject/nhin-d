package org.nhind.config.client;


import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;


public class ClientProxyEndToEndTest
{
	private static ConfigurationServiceProxy proxy;
	
	@BeforeClass
	public static void setupClass() throws Exception
	{
		ConfigServiceRunner.startConfigService();    	
    	proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception
	{
		
		ConfigServiceRunner.shutDownConfigService();
	}
	
	private void cleanDomains() throws Exception
	{
		Domain[] doms = proxy.listDomains(null, 100);
	
		if (doms != null)
			for (Domain domain : doms)
				proxy.removeDomain(domain.getDomainName());
	}
	
	@Test
	public void addDomain() throws Exception
	{
		cleanDomains();
		
		Domain domain = new Domain();
		domain.setDomainName("health.testdomain.com");
		domain.setPostMasterEmail("postmaster@health.testdomain.com");
		
		proxy.addDomain(domain);
		
		int count = proxy.getDomainCount();
		
		TestCase.assertEquals(1,count);
	}

	
	
	@Test
	public void getDomainCount() throws Exception
	{
		
		int count = proxy.getDomainCount();
		
		System.out.println("Domain Count: " + count);
	}
}