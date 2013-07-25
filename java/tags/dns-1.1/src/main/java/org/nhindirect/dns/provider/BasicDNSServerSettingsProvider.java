/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns.provider;


import org.nhindirect.dns.DNSServerSettings;

import com.google.inject.Provider;

/** Guice provider for configuring a minimal set of DNSServer settings.
 *  @author Greg Meyer
 *
 *  @since 1.0
 */
public class BasicDNSServerSettingsProvider implements Provider<DNSServerSettings>
{
	
	private final String bindings;
	private final int port;
	
	/**
	 * Creates a provider using the default DNS server settings.
	 */
	public BasicDNSServerSettingsProvider()
	{
		this(null, 0);
	}
	
	/**
	 * Creates a provider allowing the IP binding addresses and port to be overridden.
	 * @param bindings A comma delimited list of IP binding addresses.
	 * @param port The IP port that the server will use to listen for DNS requests.
	 */
	public BasicDNSServerSettingsProvider(String bindings, int port)
	{
		this.bindings = bindings;
		this.port = port;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DNSServerSettings get()
	{ 
		DNSServerSettings settings = new DNSServerSettings();
		if (port > 0)
			settings.setPort(port);
		
		if (bindings != null && !bindings.isEmpty())
			settings.setBindAddress(bindings);
		
		return settings;
	}
}
