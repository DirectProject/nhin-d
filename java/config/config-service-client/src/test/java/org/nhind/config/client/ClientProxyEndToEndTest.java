package org.nhind.config.client;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;


public class ClientProxyEndToEndTest
{
	private Server server;
	private ConfigurationServiceProxy proxy;
	
	@Before
	public void setup() throws Exception
	{
		server = new Server();
		SocketConnector connector = new SocketConnector();
		connector.setPort(8090);
		
		WebAppContext context = new WebAppContext();
	    
    	context.setContextPath("/config");	 
    	context.setServer(server);
    	context.setWar("war/config-service.war");
    	    	
    	server.setSendServerVersion(false);
    	server.addConnector(connector);
    	server.addHandler(context);
    	
    	server.start();
    	
    	proxy = new ConfigurationServiceProxy("http://localhost:8090/config/ConfigurationService");
	}
	
	@After
	public void tearDown() throws Exception
	{
		if (server != null)
			server.stop();
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
