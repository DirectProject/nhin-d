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

package org.nhindirect.gateway.smtp;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.module.SmtpAgentConfigModule;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class SmtpAgentFactory 
{
	private static Map<URL, SmtpAgent> agents = new HashMap<URL, SmtpAgent>();
	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(SmtpAgentFactory.class);	
	
	public synchronized static SmtpAgent createAgent(URL configLocation)
	{
		return createAgent(configLocation, null, null);
	}
	
	public synchronized static SmtpAgent createAgent(URL configLocation, Provider<SmtpAgentConfig> configProvider, 
			Provider<NHINDAgent> agentProvider) throws SmtpAgentException
	{
		SmtpAgent retVal = null;
		
		try
		{
			agents.get(configLocation);
			
			if (retVal == null)
			{
				Injector agentInjector = buildAgentInjector(configLocation, configProvider, agentProvider);
				retVal = agentInjector.getInstance(SmtpAgent.class);
				
				agents.put(configLocation, retVal);
			}
		}
		catch (SmtpAgentException e)
		{
			// rethrow
			throw e;
		}
		catch (Exception t)
		{
			// catch all
			LOGGER.error("SmtpAgent creation failed: " + t.getMessage(), t);
			throw new SmtpAgentException(SmtpAgentError.Unknown, "SmtpAgent creation failed: " + t.getMessage(), t);
		}		
		catch (Throwable t)
		{
			// catch all
			LOGGER.error("SmtpAgent creation failed: " + t.getMessage(), t);
			throw new SmtpAgentException(SmtpAgentError.Unknown, "SmtpAgent creation failed: " + t.getMessage());
		}
		
		return retVal;
	}
		
	/*
	 * Creates an injector for getting SmtpAgent instances
	 */
	private static Injector buildAgentInjector(URL configLocation, Provider<SmtpAgentConfig> configProvider, Provider<NHINDAgent> agentProvider)
	{
		Injector configInjector = Guice.createInjector(SmtpAgentConfigModule.create(configLocation, configProvider, agentProvider));
		
		SmtpAgentConfig config = configInjector.getInstance(SmtpAgentConfig.class);
		
		return config.getAgentInjector();
	}
}
