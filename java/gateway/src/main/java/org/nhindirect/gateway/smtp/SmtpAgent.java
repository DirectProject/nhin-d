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

import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.ImplementedBy;


/**
 * The SmtpAgent is a generic gateway to the security trust agent that handles message over the SMTP protocol.  It is intended to sit between an SMTP
 * server implementation coupled with an server specific bridge and the security trust bridge.  Because each SMPT server potentially 
 * handles custom message processing differently and creates server specific message/SMTP envelope wrappers, bridge implementations are specific
 * to the servers that they are coupled with.  The bridges are responsible for intercepting messages through the SMTP stack, gathers routing information
 * (either from the message routing headers or the SMTP envelope headers), calling the SmtpAgent, and moving processed messages forward through the SMTP
 * stack.  Bridges are also responsible for relaying any bounce messages created by the SmtpAgent.  NOTE: Bounce messages are also processed by the 
 * security and trust agent before being returned. 
 * 
 * @author Greg Meyer
 *
 */
@ImplementedBy(DefaultSmtpAgent.class)
public interface SmtpAgent 
{
	public MessageProcessResult processMessage(MimeMessage message, NHINDAddressCollection recipients, NHINDAddress sender);
	
	public NHINDAgent getAgent();
	
	public SmtpAgentSettings getSmtpAgentSettings();
	
	public BounceMessageCreator getIncomingBounceCreator();
	
	public BounceMessageCreator getOutgoingBounceCreator();	
}
