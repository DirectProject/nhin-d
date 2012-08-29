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
 * server implementation coupled with an server specific bridge and the security trust bridge.  Because each SMTP server potentially 
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
	/**
	 * Processes an message from an SMTP stack.  The bridge component between the SMTP stack and the SMTP agent is responsible for
	 * extracting the message, the recipient list, and the sender.  In some cases, the routing headers may have different information than
	 * what is populated in the SMTP MAIL FROM and RCTP TO headers.  In these cases, the SMTP headers should be favored over the routing
	 * headers in the message and passed as the recipient collection and sender to this method.
	 * @param message The message in the SMTP envelope.
	 * @param recipients The recipients of the message.  The RCTP TO headers should be used over the message routing headers.
	 * @param sender The send of the message. The MAIL FROM header should be used over the From: routing header in the message.
	 */
	public MessageProcessResult processMessage(MimeMessage message, NHINDAddressCollection recipients, NHINDAddress sender);
	
	/**
	 * Gets a references to the security and trust agent used by the SmtpAgent.
	 * @return A references to the security and trust agent used by the SmtpAgent
	 */
	public NHINDAgent getAgent();
	
	/**
	 * Gets the configuration settings of the SmtpAgent.
	 * @return The configuration settings of the SmtpAgent.
	 */
	public SmtpAgentSettings getSmtpAgentSettings();
}
