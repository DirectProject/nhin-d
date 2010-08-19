package org.nhindirect.stagent;

import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.mail.Message;

public class MockNHINDAgent implements NHINDAgent
{
	private Collection<String> domains;
	
	public MockNHINDAgent(Collection<String> domains)
	{
		this.domains = domains; 
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
    	return message;
    }
}
