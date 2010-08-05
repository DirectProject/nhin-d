package org.nhindirect.stagent;

import java.io.InputStream;
import java.util.Collection;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.ProtocolException.ProtocolError;
import org.nhindirect.stagent.parser.Protocol;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;

/**
 * Outgoing messages are specific types of NHINDMessage that need to been signed and encrypted.  
 * <p>
 * The domain(s) bound to the provided agent is used
 * to remove recipients that are not in the agent's domain(s).
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class OutgoingMessage extends NHINDMessage 
{
    NHINDAddressCollection recipients;
    NHINDAddressCollection rejectedRecipients;

    /**
     * Constructs an empty message.
     */
    public OutgoingMessage()
    {
    	super();
    }

    /**
     * {@inheritDoc}}
     */    
    public OutgoingMessage(InternetHeaders headers, byte[] content) throws MessagingException 
    {
    	super(headers, content);
    }      
    
    /**
     * {@inheritDoc}}
     */ 
    public OutgoingMessage(MimeMessage msg) throws MessagingException
    {
    	super(msg);
    }    
    
    /**
     * {@inheritDoc}}
     */    
    public OutgoingMessage(InputStream inStream) throws MessagingException 
    {
    	super(inStream);    	    	
    }    
    
    
    /**
     * Gets a collection of all recipients as NHINDAddresses.
     * @return A collection of all recipients as NHINDAddresses.
     */
    public NHINDAddressCollection getRecipients()
    { 	
        if (recipients == null)
        {
        	recipients = new NHINDAddressCollection();
        	
        	try
        	{
	        	for (Address addr : getAllRecipients())
	        	{
	        		
	        		recipients.add(new NHINDAddress((InternetAddress)addr));
	        	}
        	}
        	catch (MessagingException e)
        	{
        		throw new ProtocolException(ProtocolError.InvalidHeader, e);
        	}
        }
        
        return recipients;
    }
    
    /**
     * Indicates if the message has valid recipients.
     * @return True if the message has valid recipeints.  False otherwise.
     */
    public boolean hasRecipients()
    {

    	Collection<NHINDAddress>recipients = getRecipients();
            
    	return (recipients != null && recipients.size() > 0);
    }
    
    /**
     * Gets a collection of recipients in the message that are not trusted by the address.
     * @return A list of collection in the message that are not trusted by the address.
     */    
    public NHINDAddressCollection getRejectedRecipients()
    {
        if (rejectedRecipients == null)
        {
            rejectedRecipients = new NHINDAddressCollection();
        }
        
        return rejectedRecipients;
    }
    
    /**
     * Indicates if the message has recipients that are not trusted by the address.
     * @return True if the message has recipients that are not trusted by the address.  False otherwise. 
     */  
    public boolean hasRejectedRecipients()
    {

        return (rejectedRecipients != null && rejectedRecipients.size() > 0);
    }

    /**
     * Categorizes recipients as either trusted or untrusted (rejected).
     * @param minTrustStatus The minimum level of trust a recipient must have in order to be considered trusted.
     */
    public void categorizeRecipients(TrustEnforcementStatus minTrustStatus)
    {
        rejectedRecipients = NHINDAddressCollection.create(getRecipients().getUntrusted(minTrustStatus));
        getRecipients().removeUntrusted(minTrustStatus);            
    }
    
    /**
     * Updates the valid trusted recipients.
     */
    public void updateTo()
    {

        Collection<InternetAddress> recipients = getRecipients().toInternetAddressCollection();
        
        String header = InternetAddress.toString(recipients.toArray(new InternetAddress[recipients.size()]));

        try
        {
        	setHeader(Protocol.ToHeader, header);
        }
        catch (MessagingException e)
        {
        	throw new ProtocolException(ProtocolError.InvalidHeader, e);
        }
    }

}
