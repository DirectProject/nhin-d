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
import java.util.Collections;

import org.bouncycastle.cms.CMSSignedData;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;

/**
 * Incoming messages are specific types of NHINDMessage that have been signed and encrypted.  
 * <p>
 * The domain(s) bound to the provided agent is used
 * to remove recipients that are not in the agent's domain(s).
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class IncomingMessage extends MessageEnvelope
{
	
	private CMSSignedData signature;
	private Collection<MessageSignature> senderSignatures;
	
    public IncomingMessage(Message message)
    {
    	super(message);
    }

    public IncomingMessage(String message)
    {
    	super(message);
    }    
    
    public IncomingMessage(Message message, NHINDAddressCollection recipients, NHINDAddress sender)
    {
    	super(message, recipients, sender);
    }       
    
    public IncomingMessage(String message, NHINDAddressCollection recipients, NHINDAddress sender)
    {
    	super(message, recipients, sender);
    }       
    
    protected IncomingMessage(MessageEnvelope envelope)
    {
    	super(envelope);
    }

    /**
     * Gets the message signature data.
     * @return The message signature data.
     */
    public CMSSignedData getSignature()
    {
        return signature;
    }
    
    /**
     * Sets the message signature data.
     * @param value The message signature data.
     */    
    public void setSignature(CMSSignedData sig)
    {
        signature = sig;
    }

    public boolean hasSignatures()
    {
    	return signature != null;
    }
    
    public Collection<MessageSignature> getSenderSignatures()
    {
    	return Collections.unmodifiableCollection(senderSignatures);
    }
    
    public void setSenderSignatures(Collection<MessageSignature> senderSignatures)
    {
    	this.senderSignatures = senderSignatures;
    }
    
    public boolean hasSenderSignatures()
    {
    	return (senderSignatures != null && senderSignatures.size() > 0);
    }
    
    @Override
	protected void categorizeRecipients(TrustEnforcementStatus minTrustStatus)
    {
        super.categorizeRecipients(minTrustStatus);
        this.getDomainRecipients().removeUntrusted(minTrustStatus);
    }    
}
