package org.nhindirect.stagent;

import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class MockNHINDAgent implements NHINDAgent
{
	private Collection<String> domains;
	private AgentException incomingException;
	private AgentException outgoingException;
	private Collection<String> anchorDomains;
	
	public MockNHINDAgent(Collection<String> domains)
	{
		this.domains = domains; 
	}
	
	@Inject(optional = true)
	public void setIncomingException(@Named("MockAgentIncomingException") AgentException incomingException)
	{
		System.out.println("Setting mock incoming exception: " + incomingException.toString());
		this.incomingException = incomingException;
	}
	
	@Inject(optional = true)
	public void setOutgoingException(@Named("MockAgentOutgoingException") AgentException outgoingException)
	{
		this.outgoingException = outgoingException;
	}
	
	@Inject(optional = true)
	public void setAnchorDomains(@Named("MockAgentAnchorDomains") Collection<String> anchorDomains)
	{
		this.anchorDomains = anchorDomains;
	}	
	
    public Collection<String> getDomains()
    {
    	return Collections.unmodifiableCollection(domains);
    }

    public IncomingMessage processIncoming(String messageText)
    {
    	return this.processIncoming(new IncomingMessage(messageText));
    }
    
    
    public IncomingMessage processIncoming(String messageText, NHINDAddressCollection recipients, NHINDAddress sender)
    {
    	return this.processIncoming(new IncomingMessage(messageText, recipients, sender));
    }
    
    public IncomingMessage processIncoming(MessageEnvelope envelope)
    {
    	return this.processIncoming(new IncomingMessage(envelope.getMessage(), envelope.getRecipients(), envelope.getSender()));
    }

    public IncomingMessage processIncoming(MimeMessage msg)
    {
    	try
    	{
    		return this.processIncoming(new IncomingMessage(new Message(msg)));
    	}
    	catch (Exception e)
    	{
    		throw new RuntimeException (e);
    	}
    }
    
    public IncomingMessage processIncoming(IncomingMessage message)
    {
    	if (message.getAgent() == null)
    		message.setAgent(this);
    	
    	for (NHINDAddress addr : message.getRecipients())
    	{
    		if (!addr.isInDomain(this.domains))
    			addr.setStatus(TrustEnforcementStatus.Failed);
    		else
    			addr.setStatus(TrustEnforcementStatus.Success);
    	}
    	message.categorizeRecipients(TrustEnforcementStatus.Success);
    	message.updateRoutingHeaders();
    	
    	if (incomingException != null)
    		throw incomingException;
    	return message;
    }
    
    public OutgoingMessage processOutgoing(String messageText)
    {
    	return this.processOutgoing(new OutgoingMessage(messageText));
    }

    public OutgoingMessage processOutgoing(String messageText, NHINDAddressCollection recipients, NHINDAddress sender)
    {
    	return this.processOutgoing(new OutgoingMessage(messageText, recipients, sender));
    }
    
    public OutgoingMessage processOutgoing(MessageEnvelope envelope)
    {
    	return this.processOutgoing(new OutgoingMessage(envelope.getMessage(), envelope.getRecipients(), envelope.getSender()));
    }
    
    public OutgoingMessage processOutgoing(OutgoingMessage message)
    {
    	if (message.getAgent() == null)
    		message.setAgent(this);
    	
    	if (outgoingException != null)
    		throw outgoingException;
    	
    	if (this.anchorDomains != null)
    	{
	    	for (NHINDAddress addr : message.getRecipients())
	    	{
	    		if (!addr.isInDomain(this.anchorDomains))
	    			addr.setStatus(TrustEnforcementStatus.Failed);
	    		else
	    			addr.setStatus(TrustEnforcementStatus.Success);
	    	}
	    	message.categorizeRecipients(TrustEnforcementStatus.Success);
	    	message.updateRoutingHeaders();
    	}    	
    	
    	return message;
    }
}
