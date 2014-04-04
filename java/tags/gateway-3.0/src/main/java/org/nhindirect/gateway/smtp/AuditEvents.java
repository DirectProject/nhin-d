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

package org.nhindirect.gateway.smtp;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.notifications.MDNStandard;

/**
 * Definitions of audit names, types, and contexts
 * @author Greg Meyer
 * @since 1.2.1
 */
public class AuditEvents 
{
	/**
	 * Event type for processing direct messages
	 */
	public static final String EVENT_TYPE = "SMTP Direct Message Processing";
	
	/**
	 * Event name for processing incoming messages
	 */
	public static final String INCOMING_MESSAGE_NAME = "Incoming Direct Message";
	
	/**
	 * Event name for processing outgoing messages
	 */
	public static final String OUTGOING_MESSAGE_NAME = "Outgoing Direct Message";
	
	/**
	 * Event name for producing MDN messages
	 */
	public static final String PRODUCE_MDN_NAME = "Produce Direct MDN Message";
	
	/**
	 * Event name for receiving an MDN message
	 */
	public static final String MDN_RECEIVED_NAME = "Received Direct MDN Message";
	
	/**
	 * Event name for rejected message recipients
	 */
	public static final String REJECTED_RECIP_NAME = "Rejected Direct Message Recipients";
	
	/**
	 * Event name for rejected messages
	 */
	public static final String REJECTED_MESSAGE_NAME = "Rejected Direct Message";
	
	
	/**
	 * Default set of headers that are audited
	 */
	public static final String[] DEFAULT_HEADER_CONTEXT = {MailStandard.Headers.MessageID, MailStandard.Headers.From, MailStandard.Headers.To};
	
	/**
	 * Set of headers audited with outgoing MDN message
	 */
	public static final String[] MDN_HEADER_CONTEXT = {MailStandard.Headers.MessageID, MailStandard.Headers.From, 
		MailStandard.Headers.To, MDNStandard.Headers.Disposition, MDNStandard.Headers.OriginalMessageID, MDNStandard.Headers.FinalRecipient};
	
	/**
	 * Set of headers audited with incoming MDN messages
	 */
	public static final String[] MDN_RECEIVED_CONTEXT = {MailStandard.Headers.MessageID, MailStandard.Headers.From, 
		MailStandard.Headers.To, MDNStandard.Headers.OriginalMessageID, MDNStandard.Headers.Disposition, MDNStandard.Headers.FinalRecipient,};
	
	public static final String REJECTED_RECIPIENTS_CONTEXT = "Rejected Recipients";
	
	public static final String REJECTED_MESSAGE_REASON_CONTEXT = "Rejected Message Reason";
}
