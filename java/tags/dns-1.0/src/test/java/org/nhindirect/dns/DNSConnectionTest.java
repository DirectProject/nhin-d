package org.nhindirect.dns;

import java.net.Inet4Address;

import org.apache.mina.util.AvailablePortFinder;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Options;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import junit.framework.TestCase;

public class DNSConnectionTest extends TestCase 
{
	
	
	public void testDNSSocketConnectionTCPWithProxyStore() throws Exception
	{
		
		
		DNSServerSettings settings = new DNSServerSettings();
		settings.setPort(AvailablePortFinder.getNextAvailable( 1024 ));
		
		DNSServer server = new DNSServer(new ProxyDNSStore(), settings);
		
		
		server.start();
		
		// give the server a couple seconds to start
		Thread.currentThread().sleep(2000);
		
		// turn on debug settings for the DNS client
		Options.set("verbose", "true");
		
		Lookup lu = new Lookup(new Name("google.com"), Type.A);
		Inet4Address.getLocalHost();
		ExtendedResolver resolver = new ExtendedResolver(new String[] {"127.0.0.1", Inet4Address.getLocalHost().getHostAddress()});
		resolver.setTCP(true);
		resolver.setPort(settings.getPort());
		lu.setResolver(resolver); // default retries is 3, limite to 2
	
		
		Record[] retRecords = lu.run();
		assertNotNull(retRecords);
		
		
		
		server.stop();
		
		Thread.currentThread().sleep(4000);
	}

	
	public void testDNSSocketConnectionUDPWithProxyStore() throws Exception
	{
		
		
		DNSServerSettings settings = new DNSServerSettings();
		settings.setPort(AvailablePortFinder.getNextAvailable( 1024 ));
		
		DNSServer server = new DNSServer(new ProxyDNSStore(), settings);
		
		
		server.start();
		
		// give the server a couple seconds to start
		Thread.currentThread().sleep(2000);
		
		// turn on debug settings for the DNS client
		Options.set("verbose", "true");
		
		Lookup lu = new Lookup(new Name("google.com"), Type.A);
		Inet4Address.getLocalHost();
		ExtendedResolver resolver = new ExtendedResolver(new String[] {"127.0.0.1", Inet4Address.getLocalHost().getHostAddress()});
		resolver.setTCP(false);
		resolver.setPort(settings.getPort());
		lu.setResolver(resolver); // default retries is 3, limite to 2
	
		
		Record[] retRecords = lu.run();
		assertNotNull(retRecords);
		
		
		server.stop();
		
		Thread.currentThread().sleep(4000);
	}
	
}
