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

package org.nhindirect.stagent;

import java.util.Collection;

import org.nhindirect.stagent.mail.Message;

import com.google.inject.ImplementedBy;

/**
 * A wrapper around a MimeMessage that categorizes routing headers such as trusted and non trusted recipients.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
@ImplementedBy(DefaultMessageEnvelope.class)
public interface MessageEnvelope {
	
	/**
	 * Serializes the wrapped message to a raw string representation.
	 * @return The wrapped message to as a raw string representation.
	 */
	public String serializeMessage();
	
	/**
	 * Gets the agent associated with the message.
	 * @return The security and trust agent.
	 */
	public NHINDAgent getAgent();
		
	/**
	 * Gets the mime message wrapped in the envelope.
	 * @return the mime message wrapped in the envelope.
	 */
	public Message getMessage();
	
	/**
	 * Gets the sender of the message.
	 * @return The sender of the message.
	 */
	public NHINDAddress getSender();	
	
	/**
	 * The collection of message recipients.
	 * @return Collection of message recipients.
	 */
	public NHINDAddressCollection getRecipients();	
	
	/**
	 * Indicates if the message has any recipients.
	 * @return True if the message has recipients.  False otherwise.
	 */
	public boolean hasRecipients();	
	
    /**
     * Gets a list of recipients in the message that are not trusted by the address.
     * @return A list of recipients in the message that are not trusted by the address.
     */   
	public NHINDAddressCollection getRejectedRecipients();	
	
    /**
     * Indicates if the message has recipients that are not trusted by the address.
     * @return True if the message has recipients that are not trusted by the address.  False otherwise. 
     */   	
	public boolean hasRejectedRecipients();
	
    /**
     * Gets a list of recipients in the message that are part of the agent's domain.
     * @return A list of recipients in the agent's domain.
     */	
	public NHINDAddressCollection getDomainRecipients();	
	
	 /**
     * Indicates if the message has recipients that are in the agent's domain.
     * @return True if the message has recipients that are in the agent's domain.  False otherwise. 
     */	
	public boolean hasDomainRecipients();	
	
    /**
     * Gets a list of recipients in the message that are not part of the agent's domain.
     * @return A list of recipients that are not in the agent's domain.
     */	
	public Collection<NHINDAddress> getOtherRecipients();
	
    /**
     * Indicates if the message has recipients that are not in the agent's domain.
     * @return True if the message has recipients that are not in the agent's domain.  False otherwise. 
     */    	
	public boolean hasOtherRecipients();
	
	
	public void ensureRecipientsCategorizedByDomain(Collection<String> domains);

}
