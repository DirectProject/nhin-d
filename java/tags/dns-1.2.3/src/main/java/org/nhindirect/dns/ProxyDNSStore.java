/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.ResolverConfig;

/**
 * Proxy DNS store that delegates all requests to another set of DNS servers.
 * The store defaults to using port 53 and the machine's configured DNS servers.
 * @author Greg Meyer
 *
 * @since 1.0
 */
public class ProxyDNSStore implements DNSStore 
{
	private static final int DEFAULT_RESOLVER_PORT = 53;
	
	private final String[] servers;
	private final int port;

	/**
	 * Creates a default proxy store.
	 */
	public ProxyDNSStore()
	{
		this(DEFAULT_RESOLVER_PORT);
	}
	
	/**
	 * Creates a proxy store delegating requests to the provided port.
	 * @param port The IP port to use when calling the proxy DNS server.
	 */
	public ProxyDNSStore(int port)
	{
		this(null, DEFAULT_RESOLVER_PORT);
	}

	/**
	 * Creates a proxy using the the provided servers for delegating requests.
	 * @param servers A collections of IP4 addresses (as strings) that the proxy will delegate request to.
	 */
	public ProxyDNSStore(Collection<String> servers)
	{
		this(servers, DEFAULT_RESOLVER_PORT);
	}
	
	/**
	 * Creates a proxy using the provided servers and port for delegating requests.
	 * @param servers A collections of IP4 addresses (as strings) that the proxy will delegate request to.
	 * @param port The IP port to use when calling the proxy DNS server.
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
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
	
	/*
	 * Create the resolver that will do the DNS requests.
	 */
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
