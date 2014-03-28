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

import org.nhindirect.stagent.mail.Message;

/**
 * Outgoing messages are specific types of NHINDMessage that need to been signed and encrypted.  
 * <p>
 * The domain(s) bound to the provided agent is used
 * to remove recipients that are not in the agent's domain(s).
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class OutgoingMessage extends DefaultMessageEnvelope
{
	/**
	 * Create an outgoing message from a mime message.
	 * @param message The message to be enveloped.
	 */
    public OutgoingMessage(Message message)
    {
    	super(message);
    }

    /**
     * Create an outgoing message from a mime message overriding the routing headers.
     * @param message The message to be enveloped.
     * @param recipients The message recipients.
     * @param sender The message sender.
     */
    public OutgoingMessage(Message message, NHINDAddressCollection recipients, NHINDAddress sender)
    {
    	super(message, recipients, sender);
    }		    
    
	/**
	 * Create an outgoing message from a raw string.
	 * @param message The raw string representation of the message to be enveloped.
	 */    
    public OutgoingMessage(String message)
    {
    	super(message);
    }

    /**
     * Create an outgoing message from a raw string. overriding the routing headers.
     * @param message The raw string representation of the message to be enveloped.
     * @param recipients The message recipients.
     * @param sender The message sender.
     */
    public OutgoingMessage(String message, NHINDAddressCollection recipients, NHINDAddress sender)
    {
    	super(message, recipients, sender);
    }	    
    
	/**
	 * Create an outgoing message from a pre-eveloped message.
	 * @param message The raw string representation of the message to be enveloped.
	 */   
    public OutgoingMessage(MessageEnvelope envelope)
    {
    	super(envelope);
    }
}
