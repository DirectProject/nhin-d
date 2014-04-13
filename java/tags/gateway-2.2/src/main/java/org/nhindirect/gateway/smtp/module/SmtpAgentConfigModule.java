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

package org.nhindirect.gateway.smtp.module;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.provider.WSSmtpAgentConfigProvider;
import org.nhindirect.gateway.smtp.provider.XMLSmtpAgentConfigProvider;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class SmtpAgentConfigModule extends AbstractModule 
{
	private final URL configLocation;
	private final Provider<SmtpAgentConfig> smtpAgentConfigProvider;
	private final Provider<NHINDAgent> agentProvider;
	
	public static SmtpAgentConfigModule create(URL configLocation,  Provider<SmtpAgentConfig> smtpAgentConfigProvider, Provider<NHINDAgent> agentProvider)
	{
		return new SmtpAgentConfigModule(configLocation, smtpAgentConfigProvider, agentProvider);
	}
	
	private SmtpAgentConfigModule(URL configLocation, Provider<SmtpAgentConfig> smtpAgentConfigProvider, Provider<NHINDAgent> agentProvider)
	{
		this.configLocation = configLocation;
		this.smtpAgentConfigProvider = smtpAgentConfigProvider;
		this.agentProvider = agentProvider;
	}
	
	protected void configure()
	{	
		Provider<SmtpAgentConfig> provider = smtpAgentConfigProvider;
		
		if (provider == null)  
		{
			if (configLocation.getProtocol().equalsIgnoreCase("HTTP") || configLocation.getProtocol().equalsIgnoreCase("HTTPS"))
			{
				// web services based
				provider = new WSSmtpAgentConfigProvider(configLocation, agentProvider); 
			}
			else 
			{
				// use the default XML configuration
				// convert URL to file location
				File fl = FileUtils.toFile(configLocation);
				provider = new XMLSmtpAgentConfigProvider(fl.getAbsolutePath(), agentProvider);
			}
		}
		bind(SmtpAgentConfig.class).toProvider(provider);
	}
}
