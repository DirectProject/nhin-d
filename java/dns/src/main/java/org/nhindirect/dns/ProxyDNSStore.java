package org.nhindirect.dns;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.ResolverConfig;

/**
 * Proxy DNS server that forwards all requests to another configured DNS server set. 
 * @author Greg Meyer
 *
 */
public class ProxyDNSStore implements DNSStore 
{
	private static final int DEFAULT_RESOLVER_PORT = 53;
	
	private final String[] servers;
	private final int port;

	public ProxyDNSStore()
	{
		this(DEFAULT_RESOLVER_PORT);
	}
	
	public ProxyDNSStore(int port)
	{
		this(null, DEFAULT_RESOLVER_PORT);
	}

	
	public ProxyDNSStore(Collection<String> servers)
	{
		this(servers, DEFAULT_RESOLVER_PORT);
	}
	
	public ProxyDNSStore(Collection<String> servers, int port)
	{
		if (servers == null || servers.size() == 0)
		{

			String[] configedServers = ResolverConfig.getCurrentConfig().servers();
			
			if (configedServers != null)
			{
				this.servers = configedServers;
			}	
			else 
				this.servers = null;
		}
		else
		{
			this.servers = new String[servers.size()];
			servers.toArray(this.servers);
		}
		
		this.port = port;
	}
	
	
	@Override
	public Message get(Message dnsMsg) throws DNSException
	{
		ExtendedResolver resolver = createExResolver(servers, port, 2, 2000);
		// try UPD first
		
		Message response = null;
		try
		{
			response = resolver.send(dnsMsg);			
		}
		catch (IOException e)
		{
			/* no-op */
		}
		
		if (response == null)
		{
			// try TCP
			resolver.setTCP(true);
			try
			{
				response = resolver.send(dnsMsg);			
			}
			catch (IOException e)
			{
				/* no-op */
			}			
		}
		
		return response;
	}
	
	private ExtendedResolver createExResolver(String[] servers, int port, int retries, int timeout)
	{
		ExtendedResolver retVal = null;
		try
		{
			retVal = new ExtendedResolver(servers);
			retVal.setRetries(retries);
			retVal.setTimeout(timeout);
		}
		catch (UnknownHostException e) {/* no-op */}
		return retVal;
	}
}
