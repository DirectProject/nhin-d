/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
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

import java.net.URL;

import org.nhindirect.dns.config.DNSServerConfig;
import org.nhindirect.dns.module.DNSServerConfigModule;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;


/**
 * The DNSServerFactory is a bootstrapper for creating instances of the {@link DNSServer) based on configuration information.  Configurations
 * are loaded from a URL that may take the form of any addressable resource such as a file, HTTP resource, LDAP store, or database.  Based on the
 * URL protocol, an appropriate configuration loader and parser is instantiated which creates an injector used to provide instance of the DNSServer.
 * Optionally specific configuration and {@link DNSStore} providers can be passed for specific object creation.  This is generally useful
 * for creating mock implementations for testing.
 * @author Greg Meyer
 *
 * @since 1.0
 */
public class DNSServerFactory 
{
	/**
	 * Creates an instance of a {@link DNSServer} using the configuration information stored at the configuration location.
	 * @param configLocation The URL of the configuration information.  The URL may refer to any addressable resource.
	 * @return An initialized instance of a DNSServer.
	 * @throws DNSException Thrown if an error occurs while creating the DNSServer.
	 */
	public synchronized static DNSServer createDNSServer(URL configLocation) throws DNSException
	{
		return createDNSServer(configLocation, null, null);
	}
	
	/**
	 * Creates an instance of a {@link DNSServer} using the configuration information stored at the configuration location.  An
	 * optional {@link DNSStore> provider can be passed for initializing the server with a specific record store.
	 * @param configLocation The URL of the configuration information.  The URL may refer to any addressable resource.
	 * @param dnsStore Optional provider that will create an instance of a specific {@link DNSStore} type.  If this is null, the
	 * system will create a default store.
	 * @param settings Optional DNS server settings.  Overridden by settings from the configuration service.
	 * @return An initialized instance of a DNSServer.
	 * @throws DNSException Thrown if an error occurs while creating the DNSServer.
	 */	
	public synchronized static DNSServer createDNSServer(URL configLocation, Provider<DNSStore> dnsStore, Provider<DNSServerSettings> settings) throws DNSException
	{
		DNSServer retVal = null;
		
		try
		{
			Injector agentInjector = buildServerInjector(configLocation, dnsStore, settings);
			retVal = agentInjector.getInstance(DNSServer.class);
				
		}
		catch (Exception t)
		{
			// catch all
			throw new DNSException(DNSError.newError(-1), "DNSServer creation failed: " + t.getMessage(), t);
		}		
		
		return retVal;		
	}
	
	/*
	 * Creates an injector for getting SmtpAgent instances
	 */
	private static Injector buildServerInjector(URL configLocation, Provider<DNSStore> storeProvider, Provider<DNSServerSettings> settings)
	{
		Injector configInjector = Guice.createInjector(DNSServerConfigModule.create(configLocation, storeProvider, settings));
		
		DNSServerConfig config = configInjector.getInstance(DNSServerConfig.class);
		
		return config.getServerInjector();
	}
}
