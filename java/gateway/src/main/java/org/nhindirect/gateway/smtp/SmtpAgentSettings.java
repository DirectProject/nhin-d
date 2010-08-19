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

public class SmtpAgentSettings
{	
	private final Map<String, DomainPostmaster> domains;
	private final RawMessageSettings rawMessageSettings;
	private final ProcessOutgoingSettings outgoingSettings;
	private final ProcessIncomingSettings incomingSettings;
	private final ProcessBadMessageSettings badMessageSettings;
	
	public SmtpAgentSettings(Map<String, DomainPostmaster>  domains, RawMessageSettings rawMessageSettings, ProcessOutgoingSettings outgoingSettings,
			ProcessIncomingSettings incomingSettings, ProcessBadMessageSettings badMessageSettings)
	{
		this.domains = domains;
		this.rawMessageSettings = rawMessageSettings;
		this.outgoingSettings = outgoingSettings;
		this.incomingSettings = incomingSettings;
		this.badMessageSettings = badMessageSettings;
	}
	
	public Map<String, DomainPostmaster> getDomainPostmasters()
	{
		return Collections.unmodifiableMap(domains);
	}
	
	public RawMessageSettings getRawMessageSettings()
	{
		return rawMessageSettings;
	}
	
	public ProcessOutgoingSettings getOutgoingMessageSettings()
	{
		return outgoingSettings;
	}
	
	public ProcessIncomingSettings getIncomingMessageSettings()
	{
		return incomingSettings;
	}
	
	public ProcessBadMessageSettings getBadMessageSettings()
	{
		return badMessageSettings;
	}
}
