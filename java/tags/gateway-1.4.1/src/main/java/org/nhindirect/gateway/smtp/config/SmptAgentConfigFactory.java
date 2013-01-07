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

package org.nhindirect.gateway.smtp.config;

import java.net.URL;

import org.nhindirect.gateway.smtp.module.SmtpAgentConfigModule;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Factory for creating an {@link SmtpAgentConfig} instance based on the config location and custom providers and modules.
 * @author Greg Meyer
 * @Since 1.4
 */
public class SmptAgentConfigFactory 
{
	/**
	 * Creates an instance of an {@link SmtpAgentConfig} object.
	 * @param configLocation The URL of the configuration information.
	 * @param configProvider Guice provider for custom SmtpAgentConfig instances.
	 * @param agentProvider Guice provider for custom agent instances.
	 * @return An instantiated instance of an {@link SmtpAgentConfig} object.
	 */
	public static SmtpAgentConfig createSmtpAgentConfig(URL configLocation, Provider<SmtpAgentConfig> configProvider, Provider<NHINDAgent> agentProvider)
	{
		final Injector configInjector = Guice.createInjector(SmtpAgentConfigModule.create(configLocation, configProvider, agentProvider));
		
		return configInjector.getInstance(SmtpAgentConfig.class);
	}
}
