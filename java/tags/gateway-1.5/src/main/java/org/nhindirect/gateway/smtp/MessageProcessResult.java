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
*/package org.nhindirect.gateway.smtp;

import java.util.Collection;

import org.nhindirect.stagent.MessageEnvelope;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;

import java.util.Collections;

/**
 * Result structure for messages processed by the SmtpAgent.  Contains the result of processing the message and 
 * an acknowledgment message that should be sent back according the security and trust policy.
 * @author Greg Meyer
 */
public class MessageProcessResult 
{
	private final MessageEnvelope processedMessage;
	
	private Collection<NotificationMessage> notificationMessages;
	
	/**
	 * Construct a result.
	 * @param processedMessage The resulting message of processing a message through the SmtpAgent.
	 * @param ackMessage An acknowledgment message that should be sent to the sender.
	 */
	public MessageProcessResult(MessageEnvelope processedMessage, Collection<NotificationMessage> notificationMessages)
	{
		this.processedMessage = processedMessage;
		this.notificationMessages = notificationMessages;
	}

	/**
	 * Gets the resulting message of processing a message through the SmtpAgent.  A null message indicates that an error occurred while processing
	 * the message.
	 * @return The resulting message of processing a message through the SmtpAgen
	 */
	public MessageEnvelope getProcessedMessage() 
	{
		return processedMessage;
	}

	/**
	 * Get the notification messages that should be sent back to the sender according the security and trust policy.
	 * @return The notification messages that should be sent back to the sender.
	 */
	public Collection<NotificationMessage> getNotificationMessages() 
	{
		if (notificationMessages != null)
			return Collections.unmodifiableCollection(notificationMessages);
		else
			return Collections.emptyList();
	}

	public void setNotificationMessages(Collection<NotificationMessage> notificationMessages)
	{
		this.notificationMessages = notificationMessages;
	}
	
}
