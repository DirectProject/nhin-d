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

import javax.mail.internet.MimeMessage;

import com.google.inject.ImplementedBy;

/**
 * The NHINDAgent is the primary entity for applying cryptography and trust logic on incoming and outgoing messages.  The main messaging system (such as an SMTP server,
 * email client, or other message handling agent) instantiates an instance of the agent with configurable certificates storage implementations and trust anchor
 * stores.  The agent then applies S/MIME logic to the messages and asserts that the messages are being routed to and from trusted addresses.
 * <p>
 * The agent can support multiple local domains within one instance. 
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
@ImplementedBy(DefaultNHINDAgent.class)
public interface NHINDAgent 
{
    /**
     * Gets the list of domains that the agent is serving.
     * @return The domains that the agent is serving.
     */
    public Collection<String> getDomains();
	
	/**
	 * Processes an incoming message represented by a raw string.  The message will be decrypted and validated that it meets trust assertions.
	 * @param messageText The raw contents of the incoming message that will be processed.
	 * @return An incoming messaging object that contains the unwrapped and decrypted message. 
	 */    
    public IncomingMessage processIncoming(String messageText);
    
    /**
	 * Processes an incoming message represented by a raw string.  The message will be decrypted and validated that it meets trust assertions.
	 * @param messageText The raw contents of the incoming message that will be processed.
	 * @param recipients The recipients of the message.  This overrides the routing headers in the message.
	 * @param sender The sender of the message.  This overrides the to FROM routing header in the message.
	 * @return An incoming messaging object that contains the unwrapped and decrypted message. 
	 */       
    public IncomingMessage processIncoming(String messageText, NHINDAddressCollection recipients, NHINDAddress sender);
    
    /**
	 * Processes a pre-enveloped message.  The message will be decrypted and validated that it meets trust assertions.
	 * @param envelope A message envelope containing the incoming message.
	 * @return An incoming messaging object that contains the unwrapped and decrypted message. 
	 */      
    public IncomingMessage processIncoming(MessageEnvelope envelope);
    
    /**
	 * Processes an incoming mime message.  The message will be decrypted and validated that it meets trust assertions.
	 * @param msg The incoming mime message. 
	 * @return An incoming messaging object that contains the unwrapped and decrypted message. 
	 */ 
    public IncomingMessage processIncoming(MimeMessage msg);
    
    /**
	 * Processes a pre-enveloped message.  The message will be decrypted and validated that it meets trust assertions.
	 * @param envelope A message envelope containing the incoming message.
	 * @return An incoming messaging object that contains the unwrapped and decrypted message. 
	 */  
    public IncomingMessage processIncoming(IncomingMessage message);
    
    /**
	 * Processes an outgoing message represented by a raw string.  The message will be wrapped, encrypted, and signed.
	 * @param messageText The raw contents of the incoming message that will be processed.
	 * @return An outoing messaging object that contains the wrapped message that is and encrypted and signed. 
	 */      
    public OutgoingMessage processOutgoing(String messageText);
    
    /**
	 * Processes an outgoing message represented by a raw string.  The message will be wrapped, encrypted, and signed.
	 * @param messageText The raw contents of the incoming message that will be processed.
	 * @param recipients The recipients of the message.  This overrides the routing headers in the message.
	 * @param sender The sender of the message.  This overrides the to FROM routing header in the message.
	 * @return An outoing messaging object that contains the wrapped message that is and encrypted and signed. 
	 */        
    public OutgoingMessage processOutgoing(String messageText, NHINDAddressCollection recipients, NHINDAddress sender);
    
    /**
	 * Processes an outgoing pre-enveloped message.  The message will be wrapped, encrypted, and signed.
	 * @param envelope A message envelope containing the outgoing message.
	 * @return An outoing messaging object that contains the wrapped message that is and encrypted and signed. 
	 */    
    public OutgoingMessage processOutgoing(MessageEnvelope envelope);
    
    /**
	 * Processes an outgoing pre-enveloped message.  The message will be wrapped, encrypted, and signed.
	 * @param message A message envelope containing the incoming message.
	 * @return An outoing messaging object that contains the wrapped message that is and encrypted and signed. 
	 */ 
    public OutgoingMessage processOutgoing(OutgoingMessage message);
}
