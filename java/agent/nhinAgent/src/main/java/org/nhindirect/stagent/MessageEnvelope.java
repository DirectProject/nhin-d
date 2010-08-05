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

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeError;
import org.nhindirect.stagent.mail.MimeException;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;

public class MessageEnvelope 
{
	protected NHINDAgent agent;
	protected Message message;
	protected NHINDAddress sender;
	protected NHINDAddressCollection to;
	protected NHINDAddressCollection cc;
	protected NHINDAddressCollection bcc;
	protected NHINDAddressCollection recipients;
	protected NHINDAddressCollection rejectedRecipients;
	protected NHINDAddressCollection domainRecipients;
	protected Collection<NHINDAddress> otherRecipients;     
	
	public MessageEnvelope(Message message)
	{
		if (message == null)
			throw new IllegalArgumentException();
			
		this.message = message;
		this.setRecipients(this.collectRecipients());
		
		try
		{
			if (message.getFrom() == null || message.getFrom().length == 0)
				throw new AgentException(AgentError.MissingFrom);
			
			this.setSender(new NHINDAddress((InternetAddress)message.getFrom()[0], AddressSource.From));			
		}
		catch (MessagingException e)
		{
			throw new AgentException(AgentError.MissingFrom, e);
		}		
	}
	
	public MessageEnvelope(String rawMessage)
	{
		this(fromStringToMessage(rawMessage));
	}
	
	public MessageEnvelope(Message message, NHINDAddressCollection recipients, NHINDAddress sender)
	{
		if (message == null)
			throw new IllegalArgumentException();	
		
		this.message = message;
		this.setRecipients(recipients);
		this.setSender(sender);		
	}		
	
	public MessageEnvelope(String rawMessage, NHINDAddressCollection recipients, NHINDAddress sender)
	{
		this(fromStringToMessage(rawMessage), recipients, sender);
	}	
	
	protected MessageEnvelope(MessageEnvelope envelope)
	{
		agent = envelope.agent;
		message = envelope.message;
		recipients = envelope.recipients;
		sender = envelope.sender;
	}	
	
	private static Message fromStringToMessage(String rawMessage)
	{
		try
		{
			return new Message(EntitySerializer.Default.deserialize(rawMessage));
		}
		catch (MessagingException e) {/* no-op */}
		
		return null;
	}
	
	public NHINDAgent getAgent()
	{
		return this.agent;
	}
	
	protected void setAgent(NHINDAgent agent)
	{
		this.agent = agent;
	}
	
	public Message getMessage()
	{
		return this.message;
	}
	
	protected void setMessage(Message message)
	{
        if (message == null)
            throw new AgentException(AgentError.MissingMessage);
        
        this.message = message;
	}
	
	public NHINDAddress getSender()
	{
		return this.sender;
	}
	
	protected void setSender(NHINDAddress sender)
	{
        if (sender == null)
        {
            throw new AgentException(AgentError.NoSender);
        }
        this.sender = sender;		
	}	
	
	public NHINDAddressCollection getRecipients()
	{
		if (this.recipients == null)
		{
			this.collectRecipients();
		}
		
		return recipients;
	}
	
	protected void setRecipients(NHINDAddressCollection recipients)
	{
		if (recipients == null || recipients.size() == 0)
			throw new AgentException(AgentError.NoRecipients);
		
		this.recipients = recipients;
	}
	
	public boolean hasRecipients()
	{
		return (recipients != null && recipients.size() > 0);
	}
	
    /**
     * Gets a list of recipients in the message that are not trusted by the address.
     * @return A list of recipients in the message that are not trusted by the address.
     */   
	public NHINDAddressCollection getRejectedRecipients()
	{
        if (this.rejectedRecipients == null)
        {
            this.rejectedRecipients = new NHINDAddressCollection();
        }

        return rejectedRecipients;		
	}
	
    /**
     * Indicates if the message has recipients that are not trusted by the address.
     * @return True if the message has recipients that are not trusted by the address.  False otherwise. 
     */   	
	public boolean hasRejectedRecipients()
	{
		NHINDAddressCollection rejRecipients = this.getRejectedRecipients();
		return (rejRecipients != null && rejRecipients.size() > 0);
	}
	
    /**
     * Gets a list of recipients in the message that are part of the agent's domain.
     * @return A list of recipients in the agent's domain.
     */	
	public NHINDAddressCollection getDomainRecipients()
	{
        if (this.domainRecipients == null)
        {
            categorizeRecipients(getAgent().getDomains());
        }

        return domainRecipients;		
	}	
	
	 /**
     * Indicates if the message has recipients that are in the agent's domain.
     * @return True if the message has recipients that are in the agent's domain.  False otherwise. 
     */	
	public boolean hasDomainRecipients()
	{
        NHINDAddressCollection dRecipients = this.getDomainRecipients();
        return (dRecipients != null && dRecipients.size() > 0);
	}	
	
    /**
     * Gets a list of recipients in the message that are not part of the agent's domain.
     * @return A list of recipients that are not in the agent's domain.
     */	
	public Collection<NHINDAddress> getOtherRecipients()
	{
        if (this.otherRecipients == null)
        {
            categorizeRecipients(getAgent().getDomains());
        }

        return this.otherRecipients;		
	}	
	
    /**
     * Indicates if the message has recipients that are not in the agent's domain.
     * @return True if the message has recipients that are not in the agent's domain.  False otherwise. 
     */    	
	public boolean hasOtherRecipients()
	{
		Collection<NHINDAddress> oRecipients = this.getOtherRecipients();
        return (oRecipients != null && oRecipients.size() > 0);
	}	
	
	protected NHINDAddressCollection getTo()
	{
		if (to == null)
		{
			to = NHINDAddressCollection.parse(message.getToHeader(), AddressSource.To);
		}
		
		return to;
	}
	
	protected NHINDAddressCollection getCC()
	{
		if (cc == null)
		{
			cc = NHINDAddressCollection.parse(message.getCCHeader(), AddressSource.CC);
		}
		
		return cc;
	}
	
	protected NHINDAddressCollection getBCC()
	{
		if (bcc == null)
		{
			bcc = NHINDAddressCollection.parse(message.getBCCHeader(), AddressSource.BCC);
		}
		
		return bcc;
	}	
	
    public String serializeMessage()
    {
        return EntitySerializer.Default.serialize(this.getMessage());
    }

    protected void clear()
    {
    	message = null;
    	sender = null;
    	to = null;
    	cc = null;
    	bcc = null;
    	recipients = null;
    	rejectedRecipients = null;
    	domainRecipients = null;
    	otherRecipients = null;    
    }
	
	protected NHINDAddressCollection collectRecipients()
	{
        NHINDAddressCollection addresses = new NHINDAddressCollection();
        if (this.getTo() != null)
        {
            addresses.addAll(this.getTo());
        }                
        if (this.getCC() != null)
        {
            addresses.addAll(this.getCC());
        }
        if (this.getBCC() != null)
        {
            addresses.addAll(this.getBCC());
        }
        return addresses;
	}

    /**
     * Updates the valid domain recipients & other recipients.
     */	
	protected void updateRoutingHeaders(NHINDAddressCollection rejectedRecipients)
	{
        if (rejectedRecipients == null || rejectedRecipients.size() == 0)
        {
            return;
        }	
        
        try
        {
	        if (this.getTo() != null)
	        {
	        	this.getTo().removeAll(rejectedRecipients);
	        	this.getMessage().setHeader(MailStandard.ToHeader, this.getTo().toString());
	        }

	        if (this.getCC() != null)
	        {
	        	this.getCC().removeAll(rejectedRecipients);
	        	this.getMessage().setHeader(MailStandard.CCHeader, this.getCC().toString());
	        }

	        if (this.getBCC() != null)
	        {
	        	this.getBCC().removeAll(rejectedRecipients);
	        	this.getMessage().setHeader(MailStandard.BCCHeader, this.getBCC().toString());
	        }

        }
        catch (MessagingException e) 
        {
        	throw new MimeException(MimeError.InvalidHeader);
        }
    }
	
	protected void updateRoutingHeaders()
	{
		if (hasRejectedRecipients())
			this.updateRoutingHeaders(this.getRejectedRecipients());
	}
	
	protected void validate()
	{
		
	}
	
    /**
     * Splits recipients into domain recipients and external recipients.  The agent's domain is used to determine a recipients category.
     */
	protected void categorizeRecipients(Collection<String> domains)
	{
        if (domains == null || domains.size() == 0)
        {
            throw new IllegalArgumentException();
        }

        NHINDAddressCollection recipients = this.getRecipients();
        this.domainRecipients = new NHINDAddressCollection();
        this.otherRecipients = new ArrayList<NHINDAddress>();

        for (NHINDAddress address : recipients)
        {
            if (address.isInDomain(domains))
            {
                this.domainRecipients.add(address);
            }
            else
            {
                this.otherRecipients.add(address);
            }
        }	
	}
	
    /**
     * Categorizes recipients as either trusted or untrusted (rejected).
     * @param minTrustStatus The minimum level of trust a recipient must have in order to be considered trusted.
     */		
	protected void categorizeRecipients(TrustEnforcementStatus minTrustStatus)
	{
		rejectedRecipients = NHINDAddressCollection.create(getRecipients().getUntrusted(minTrustStatus));
		getRecipients().removeUntrusted(minTrustStatus);
	}
}
