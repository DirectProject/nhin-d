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
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.module.SmtpAgentConfigModule;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * The SmtpAgentFactory is a bootstrapper for creating instances of the {@link SmtpAgent) based on configuration information.  Configurations
 * are loaded from a URL that may take the form of any addressable resource such as a file, HTTP resource, LDAP store, or database.  Based on the
 * URL protocol, an appropriate configuration loader and parser is instantiated which creates an injector used to provide instance of the SmptAgent.
 * Optionally specific configuration and security and trust agent providers can be passed for specific object creation.  This is generally useful
 * for creating mock implementations for testing.
 * @author Greg Meyer
 *
 */
public class SmtpAgentFactory 
{	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(SmtpAgentFactory.class);	
	
	/**
	 * Creates an instance of an {@link SmtpAgent} using the configuration information stored at the configuration location.
	 * @param configLocation The URL of the configuration information.  The URL may refer to any addressable resource.
	 * @return An initialized instance of an SmtpAgent.
	 * @throws SmtpAgentException Thrown if an error occurs while creating the SmtpAgent.
	 */
	public synchronized static SmtpAgent createAgent(URL configLocation) throws SmtpAgentException
	{
		return createAgent(configLocation, null, null, null);
	}
	
	/**
	 * Creates an instance of an {@link SmtpAgent} using the configuration information stored at the configuration location.  Optional 
	 * SmptAgentConfig and security and trust providers can be passed to create specific types of these components.
	 * @param configLocation The URL of the configuration information.  The URL may refer to any addressable resource.
	 * @param configProvider A provider used to create the SmtpAgentConfig component that parses and the configuration.
	 * @param agentProvider A provider used to create the security and trust agent component.
	 * @return An initialized instance of an SmtpAgent.
	 * @throws SmtpAgentException Thrown if an error occurs while creating the SmtpAgent.
	 */
	public synchronized static SmtpAgent createAgent(URL configLocation, Provider<SmtpAgentConfig> configProvider, 
			Provider<NHINDAgent> agentProvider) throws SmtpAgentException
	{
		return createAgent(configLocation, configProvider, agentProvider, null);
	}
		
	/**
	 * Creates an instance of an {@link SmtpAgent} using the configuration information stored at the configuration location.  Optional 
	 * SmptAgentConfig and security and trust providers can be passed to create specific types of these components.  Additional Guice 
	 * {@link Modules Modules} can be provided for additional dependency creation and injection.
	 * @param configLocation The URL of the configuration information.  The URL may refer to any addressable resource.
	 * @param configProvider A provider used to create the SmtpAgentConfig component that parses and the configuration.
	 * @param agentProvider A provider used to create the security and trust agent component.
	 * @param modules A collection of modules used for creating additional Guice bindings.
	 * @return An initialized instance of an SmtpAgent.
	 * @throws SmtpAgentException Thrown if an error occurs while creating the SmtpAgent.
	 */
	public synchronized static SmtpAgent createAgent(URL configLocation, Provider<SmtpAgentConfig> configProvider, 
			Provider<NHINDAgent> agentProvider, Collection<? extends Module> modules) throws SmtpAgentException
	{	
		SmtpAgent retVal = null;
		
		try
		{
			Injector agentInjector = buildAgentInjector(configLocation, configProvider, agentProvider);
			
			if (modules != null && modules.size() > 0)
				agentInjector = agentInjector.createChildInjector(modules);
						
			retVal = agentInjector.getInstance(SmtpAgent.class);
			
			agentInjector.injectMembers(retVal);
				
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
