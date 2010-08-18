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

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Incoming messages are specific types of MessageEnvelope that have been signed and encrypted.  
 * <p>
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class IncomingMessage extends DefaultMessageEnvelope
{
	
	private CMSSignedData signature;
	private Collection<DefaultMessageSignatureImpl> senderSignatures;
	
	/**
	 * Constructs an incoming envelope from a message. 
	 * @param message The incoming message.
	 */
    public IncomingMessage(Message message)
    {
    	super(message);
    }

	/**
	 * Constructs an incoming envelope from a message represented by a raw string.. 
	 * @param message The incoming message.
	 */    
    public IncomingMessage(String message)
    {
    	super(message);
    }    
    
	/**
	 * Constructs an incoming envelope from a message, a list of recipients, and a sender.  This is intended to override the standard to and from headers
	 * in the incoming message.
	 * @param message The incoming message.
	 * @param recipients A collection of addresses that denote the recipients of the message.
	 * @param sender The original sender of the message.
	 */     
    @Inject
    public IncomingMessage(@Named("Message") Message message, @Named("Recipients") NHINDAddressCollection recipients, @Named("Sender") NHINDAddress sender)
    {
    	super(message, recipients, sender);
    }       
    
	/**
	 * Constructs an incoming envelope from a message represented as a raw string, a list of recipients, and a sender.  
	 * This is intended to override the standard to and from headers in the incoming message.
	 * @param message The incoming message.
	 * @param recipients A collection of addresses that denote the recipients of the message.
	 * @param sender The original sender of the message.
	 */     
    public IncomingMessage(String message, NHINDAddressCollection recipients, NHINDAddress sender)
    {
    	super(message, recipients, sender);
    }       
    
	/**
	 * Constructs an incoming envelope from another envelope.
	 * @param message The incoming message.
	 */      
    protected IncomingMessage(MessageEnvelope envelope)
    {
    	super(envelope);
    }

    /**
     * Gets the message signature data.  This includes the all the attributes of the signature block and in the case of enveloped signatures it will
     * also include the signed content
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

    /**
     * Indicates if the message has signature.
     * @return True if the message has signatures.  False other wise.
     */
    public boolean hasSignatures()
    {
    	return signature != null;
    }
    
    /**
     * Gets the collection of individual signers of the message.  This is a subset of data of the signature, but includes
     * additional information such as the singers certificate and validation flags.
     * @return The collection of signers.
     */
    public Collection<DefaultMessageSignatureImpl> getSenderSignatures()
    {
    	return Collections.unmodifiableCollection(senderSignatures);
    }
    
    /**
     * Sets the collection of signers of a message.
     * @param senderSignatures The collection of signers of a message.
     */
    public void setSenderSignatures(Collection<DefaultMessageSignatureImpl> senderSignatures)
    {
    	this.senderSignatures = senderSignatures;
    }
    
    /**
     * Indicates if the message has signers.
     * @return True if the message has signers.  False otherwise.
     */
    public boolean hasSenderSignatures()
    {
    	return (senderSignatures != null && senderSignatures.size() > 0);
    }
    
    @Override
    /**
     * {@inheritDoc}
     */
	protected void categorizeRecipients(TrustEnforcementStatus minTrustStatus)
    {
        super.categorizeRecipients(minTrustStatus);
        this.getDomainRecipients().removeUntrusted(minTrustStatus);
    }    
}
