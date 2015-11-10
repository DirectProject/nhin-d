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

package org.nhindirect.gateway.smtp.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.Mailet;
import org.nhindirect.common.rest.BootstrapBasicAuthServiceSecurityManager;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.gateway.GatewayConfiguration;
import org.nhindirect.gateway.smtp.james.mailet.SecurityAndTrustMailetOptions;
import org.nhindirect.stagent.options.OptionsManager;

import com.google.inject.Provider;

/**
 * Guice provider for an HTTP basic auth security manager.  The basic auth use name and password attributes are read from the mailet configuration
 * information.
 * @author Greg Meyer
 *
 */
public class ConfigBasicAuthServiceSecurityManagerProvider implements Provider<ServiceSecurityManager>, MailetAwareProvider
{
	static
	{		
		initJVMParams();
	}
	
	private synchronized static void initJVMParams()
	{
		/*
		 * Mailet configuration parameters
		 */
		final Map<String, String> JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SUBJECT, "org.nhindirect.gateway.smtp.james.mailet.ServiceSecurityAuthSubject");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SECRET, "org.nhindirect.gateway.smtp.james.mailet.ServiceSecurityAuthSecret");
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	protected Mailet mailet;

	/**
	 * Constructor
	 */
	public ConfigBasicAuthServiceSecurityManagerProvider()
	{
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMailet(Mailet mailet) 
	{
		this.mailet = mailet;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceSecurityManager get()
	{
		// load information via the options manager
		
		final String user = GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SUBJECT, mailet, "");
		final String pass = GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.SERVICE_SECURITY_AUTH_SECRET, mailet, "");
		
		return new BootstrapBasicAuthServiceSecurityManager(user, pass);
	}
}