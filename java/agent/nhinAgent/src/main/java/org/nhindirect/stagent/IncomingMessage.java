package org.nhindirect.stagent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.nhindirect.stagent.ProtocolException.ProtocolError;
import org.nhindirect.stagent.parser.Protocol;
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
public class IncomingMessage extends NHINDMessage
{
	
	private CMSSignedData signature;
    private SignerInformation signer;
    private NHINDAddressCollection domainRecipients;
    private Collection<InternetAddress> otherRecipients;        
    private NHINDAddressCollection rejectedRecipients;
    
    /**
     * Constructs an empty message.
     */
    public IncomingMessage()
    {
    	super();
    }

    /**
     * {@inheritDoc}}
     */
    public IncomingMessage(InternetHeaders headers, byte[] content) throws MessagingException 
    {
    	super(headers, content);
    }       
    
    /**
     * {@inheritDoc}}
     */
    public IncomingMessage(MimeMessage message) throws MessagingException
    {
    	super(message);
    }        
    
    /**
     * {@inheritDoc}}
     */    
    public IncomingMessage(InputStream inStream) throws MessagingException 
    {
    	super(inStream);    	    	
    }
    
    /**
     * Gets a list of recipients in the message that are part of the agent's domain.
     * @return A list of recipients in the agent's domain.
     */
    public NHINDAddressCollection getDomainRecipients()
    {

        if (domainRecipients == null)
        {
            categorizeRecipients(getAgent().getDomain());
        }

        return domainRecipients;
    }
    
    /**
     * Indicates if the message has recipients that are in the agent's domain.
     * @return True if the message has recipients that are in the agent's domain.  False otherwise. 
     */
    public boolean hasDomainRecipients()
    {

        NHINDAddressCollection recipients = this.getDomainRecipients();
        return (recipients != null && recipients.size() > 0);
    }

    /**
     * Gets a list of recipients in the message that are not part of the agent's domain.
     * @return A list of recipients that are not in the agent's domain.
     */
    public Collection<InternetAddress> getOtherRecipients()
    {
        if (otherRecipients == null)
        {
            categorizeRecipients(this.getAgent().getDomain());
        }
        
        return otherRecipients;        
    }

    /**
     * Indicates if the message has recipients that are not in the agent's domain.
     * @return True if the message has recipients that are not in the agent's domain.  False otherwise. 
     */    
    public boolean hasOtherRecipients()
    {

    	Collection<InternetAddress> recipients = getOtherRecipients();
            
    	return (recipients != null && recipients.size() > 0);
    }

    /**
     * Gets a list of recipients in the message that are not trusted by the address.
     * @return A list of recipients in the message that are not trusted by the address.
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
    public void setSignature(CMSSignedData value)
    {
        signature = value;
    }

    /**
     * Gets the validated signer that signed the message.
     * @return The validated signer that signed the message.
     */
    public SignerInformation getValidatedSigner()
    {
        return signer;
    }
    
    /**
     * Sets the validated signer that signed the message.
     * @param value The validated signer that signed the message.
     */
    public void setValidatedSigner(SignerInformation value)
    {
        signer = value;
    }

    /**
     * Indicates if the signature has been verified to be authentic.
     * @return  True if the signature has been verified.  False otherwise.
     */
    public boolean isSignatureVerified()
    {
        return (signer != null);
    }

    /// <summary>
    /// Split "To" into recipients who are in this domain vs external
    /// </summary>
    /// <param name="domain"></param>
    /**
     * Splits recipients into domain recipients and external recipients.  The agent's domain is used to determine a recipients category.
     */
    public void categorizeRecipients(String domain)
    {
        if (domain == null || domain.length() == 0)
        {
            throw new IllegalArgumentException();
        }
        
        Address[] to = null;
        try
        {
        	to = this.getAllRecipients();
        }
        catch (MessagingException e)
        {
        	throw new ProtocolException(ProtocolError.MissingTo, e);
        }
        domainRecipients = new NHINDAddressCollection();
        otherRecipients = new ArrayList<InternetAddress>();
        
        for (Address addr : to)
        {
        	InternetAddress address = (InternetAddress)addr;
        	
            if (address.getAddress().contains(domain))
            {
                domainRecipients.add(new NHINDAddress(address));
            }
            else
            {
                otherRecipients.add(address);
            }
        }
    }

    /**
     * Categorizes recipients as either trusted or untrusted (rejected).
     * @param minTrustStatus The minimum level of trust a recipient must have in order to be considered trusted.
     */
    public void categorizeRecipients(TrustEnforcementStatus minTrustStatus)
    {
        rejectedRecipients = NHINDAddressCollection.create(getDomainRecipients().getUntrusted(minTrustStatus));
        getDomainRecipients().removeUntrusted(minTrustStatus);
        
    }

    /**
     * Updates the valid domain recipients & other recipients into a "To".
     */
    public void updateTo()
    {
        Collection<InternetAddress> recipients = getDomainRecipients().toInternetAddressCollection();
        recipients.addAll(getOtherRecipients());
        
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
