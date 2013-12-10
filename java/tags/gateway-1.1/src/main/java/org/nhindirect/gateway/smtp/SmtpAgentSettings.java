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

import java.util.Collections;
import java.util.Map;

/**
 * {@link SmtpAgent} configuration settings.
 * @author Greg Meyer
 * @author Umesh Madan
 */
public class SmtpAgentSettings
{	
	private final Map<String, DomainPostmaster> domains;
	private final RawMessageSettings rawMessageSettings;
	private final ProcessOutgoingSettings outgoingSettings;
	private final ProcessIncomingSettings incomingSettings;
	private final ProcessBadMessageSettings badMessageSettings;
	private final NotificationProducer notificationProducer;
	
	/**
	 * Constructs the settings objects.
	 * @param domains A list of domains and postmasters handled by the SmtpAgent.
	 * @param rawMessageSettings Settings for processing raw messages.
	 * @param outgoingSettings Settings for processing outgoing messages.
	 * @param incomingSettings Settings for processing incoming messages.
	 * @param badMessageSettings Settings for processing bad messages.
	 */
	public SmtpAgentSettings(Map<String, DomainPostmaster>  domains, RawMessageSettings rawMessageSettings, ProcessOutgoingSettings outgoingSettings,
			ProcessIncomingSettings incomingSettings, ProcessBadMessageSettings badMessageSettings, NotificationProducer notificationProducer)
	{
		this.domains = domains;
		this.rawMessageSettings = rawMessageSettings;
		this.outgoingSettings = outgoingSettings;
		this.incomingSettings = incomingSettings;
		this.badMessageSettings = badMessageSettings;
		this.notificationProducer = notificationProducer;
	}
	
	public NotificationProducer getNotificationProducer() 
	{
		return notificationProducer;
	}

	/**
	 * Gets the domains managed by the SmtpAgent and postmaster email addresses for each domain.
	 * @return The domains managed by the SmtpAgent
	 */
	public Map<String, DomainPostmaster> getDomainPostmasters()
	{
		return Collections.unmodifiableMap(domains);
	}
	
	/**
	 * Gets the settings for processing raw messages.
	 * @return The settings for processing raw messages.
	 */
	public RawMessageSettings getRawMessageSettings()
	{
		return rawMessageSettings;
	}
	
	/**
	 * Gets the settings for processing outgoing messages.
	 * @return The settings for processing outgoing messages.
	 */	
	public ProcessOutgoingSettings getOutgoingMessageSettings()
	{
		return outgoingSettings;
	}
	
	/**
	 * Gets the settings for processing incoming messages.
	 * @return The settings for processing incoming messages.
	 */		
	public ProcessIncomingSettings getIncomingMessageSettings()
	{
		return incomingSettings;
	}
	
	/**
	 * Gets the settings for processing failed and bad messages.
	 * @return The settings for processing failed and bad messages.
	 */		
	public ProcessBadMessageSettings getBadMessageSettings()
	{
		return badMessageSettings;
	}
}
