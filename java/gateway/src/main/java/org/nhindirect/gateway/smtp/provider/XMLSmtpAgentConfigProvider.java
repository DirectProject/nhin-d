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

package org.nhindirect.gateway.smtp.provider;

import java.net.URL;

import org.nhindirect.gateway.smtp.annotation.SmptAgentConfigFile;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.config.XMLSmtpAgentConfig;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class XMLSmtpAgentConfigProvider implements Provider<SmtpAgentConfig>, URLAccessedConfigProvider, NHINDAgentConfigurableProvider
{
	private String configFile;
	private Provider<NHINDAgent> agentProvider;
	
	public XMLSmtpAgentConfigProvider()
	{
		
	}
	
	@Inject
	public XMLSmtpAgentConfigProvider(@SmptAgentConfigFile String configFile, Provider<NHINDAgent> agentProvider)
	{
		this.configFile = configFile;
		this.agentProvider = agentProvider;
	}
	
	
	@Override
	public void setConfigURL(URL url) 
	{
		this.configFile = url.toExternalForm();
		
	}
	
	@Override
	public void setNHINDAgentProvider(Provider<NHINDAgent> provider) 
	{
		this.agentProvider = provider;
	}
	
	public SmtpAgentConfig get()
	{
		return new XMLSmtpAgentConfig(configFile, agentProvider);
	}
}
