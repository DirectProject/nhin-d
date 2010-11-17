/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/


package org.nhindirect.dns;

import com.google.inject.Inject;

/**
 * The DNS server creates the UDP and TCP responders and manages their life cycles.  DNS queries are delegated
 * the responders which use the {@link DNSStore} to lookup entries.
 * <p>
 * To run a server, an instance of a server is created followed by calling the {@link #start()} method.
 * @author Greg Meyer
 * @since 1.0
 */
public class DNSServer 
{	
	private DNSResponder tcpResponder;
	private DNSResponder updResponder;
	
	/**
	 * Create a new DNSServer
	 * @param store The storage medium of the DNS records.
	 * @param settings DNS server specific settings such as UDP/TCP ports, IP bindings, and thread tuning parameters.
	 */
	@Inject
	public DNSServer(DNSStore store, DNSServerSettings settings)
	{		
		try
		{
			tcpResponder = new DNSResponderTCP(settings, store);
		}
		catch (DNSException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			updResponder = new DNSResponderUDP(settings, store);
		}
		catch (DNSException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the DNS server by initializing and launching the TCP and UDP listeners.
	 * @throws DNSException Thrown if the internal listeners could not be started.
	 */
	public void start() throws DNSException
	{
		tcpResponder.start();
		updResponder.start();
	}
	
	/**
	 * Stops the server and shuts down the TCP and UPD listeners.
	 * @throws DNSException Thrown if the internal listeners could not be stopped.
	 */
	public void stop() throws DNSException
	{
		tcpResponder.stop();
		updResponder.stop();
	}
}
