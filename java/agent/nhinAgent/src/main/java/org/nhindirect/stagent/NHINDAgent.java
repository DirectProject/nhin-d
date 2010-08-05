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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSSignedData;
import org.nhindirect.stagent.cert.ICertificateResolver;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cryptography.SMIMECryptographer;
import org.nhindirect.stagent.cryptography.SMIMEStandard;
import org.nhindirect.stagent.cryptography.SignedEntity;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeError;
import org.nhindirect.stagent.mail.MimeException;
import org.nhindirect.stagent.mail.MimeStandard;
import org.nhindirect.stagent.mail.WrappedMessage;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.trust.ITrustAnchorResolver;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;
import org.nhindirect.stagent.trust.TrustError;
import org.nhindirect.stagent.trust.TrustException;
import org.nhindirect.stagent.trust.TrustModel;

/**
 * The NHINDAgent is the primary entity for applying cryptography and trust logic on incoming and outgoing messages.  The main messaging system (such as an SMTP server,
 * email client, or other message handling agent) instantiates an instance of the agent with configurable certificates storage implementations and trust anchor
 * stores.  The agent then applies S/MIME logic to the messages and asserts that the messages are being routed to and from trusted addresses.
 * <p>
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class NHINDAgent 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDAgent.class);
	
	static MimeMultipart lastMMPart = null;
	
	private SMIMECryptographer cryptographer;
    private ICertificateResolver privateCertResolver;
    private ICertificateResolver publicCertResolver;
    private ITrustAnchorResolver trustAnchors;
    private TrustModel trustModel;
    private TrustEnforcementStatus minTrustRequirement;
    private String m_domain;
    private NHINDAgentEventListener m_listener = null;
    
    private boolean encryptionEnabled = true;
    private boolean wrappingEnabled = true;
    private boolean allowNonWrappedIncoming = true;
    private boolean fetchIncomingSenderCerts = false;    
    
    static
    {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    /**
     * Constructs an agent with domain, certificate services, and trust anchor store.
     * @param domain The domain that this agent will be serving.
     * @param internalCerts A certificate store for messages originating internally.  The store contains certificates that have access to private keys for decryption and 
     * signing messages.
     * @param externalCerts A certificate store for incoming messages.  The store contains public certificates for message signature validation and encyprtion. 
     * @param trustSettings A certificate store for certificate anchors.  Certificate anchors are certificates that can validate the authenticity of
     * a certificate.  They are used by the agent to determine if a certificate is trusted by the system.
     */
    public NHINDAgent(String domain, ICertificateResolver privateCerts, ICertificateResolver publicCerts, ITrustAnchorResolver anchors)
    {

    	
    	this(domain, privateCerts, publicCerts, anchors, TrustModel.Default, SMIMECryptographer.Default);
    }

    /**
     * Constructs an agent with domain, certificate services, and trust anchor store.
     * @param domain The domain that this agent will be serving.
     * @param internalCerts A certificate store for messages originating internally.  The store contains certificates that have access to private keys for decryption and 
     * signing messages.
     * @param externalCerts A certificate store for incoming messages.  The store contains public certificates for message signature validation and encyprtion. 
     * @param trustSettings A certificate store for certificate anchors.  Certificate anchors are certificates that can validate the authenticity of
     * a certificate.  They are used by the agent to determine if a certificate is trusted by the system.
     * @param A trust model implementation that asserts the if a message is trusted.
     * @param A cryptography implementation used to sign, encrypt, and decrypt messages.
     */    
    public NHINDAgent(String domain, ICertificateResolver privateCerts, ICertificateResolver publicCerts, ITrustAnchorResolver anchors, TrustModel trustModel, SMIMECryptographer cryptographer)
    {            	
    	if (domain == null || domain.length() == 0 || privateCerts == null || publicCerts == null || anchors == null || trustModel == null || cryptographer == null)
        {
            throw new IllegalArgumentException();
        }

    	LOGGER.info("Initializing NHINDAgent\r\n\tDomain: " + domain + "\r\n");
    	
        this.m_domain = domain;
        this.privateCertResolver = privateCerts;
        this.publicCertResolver = publicCerts;
        this.cryptographer = cryptographer;
        this.trustAnchors = anchors;
        this.trustModel = trustModel;
        this.minTrustRequirement = TrustEnforcementStatus.Success_Offline;
    }

    /**
     * Gets the domain that the agent is serving.
     * @return The domain that the agent is serving.
     */
    public String getDomain()
    {
        return this.m_domain;
    }

    /**
     * Gets the Cryptographer used by the agent to perform cryptography operations.
     * @return The Cryptographer used by the agent to perform cryptography operations.
     */
    public SMIMECryptographer getCryptographer()
    {
        return this.cryptographer;
    }

    /**
     * Indicates if messages are required to be encrypted in the agent.
     * @return True if messages are required to be encrypted in the agent.  False otherwise.
     */
    public boolean isEncryptMessages()
    {

        return this.encryptionEnabled;
    } 
    
    /**
     * Sets if messages are required to be encrypted in the agen
     * @param value True if messages are required to be encrypted in the agent.  False otherwise.
     */
    public void setEncryptMessages(boolean value)
    {
        this.encryptionEnabled = value;
    }

    public boolean isWrappingEnabled() 
    {
		return wrappingEnabled;
	}

	public void setWrappingEnabled(boolean wrappingEnabled) 
	{
		this.wrappingEnabled = wrappingEnabled;
	}

	public boolean isAllowNonWrappedIncoming() 
	{
		return allowNonWrappedIncoming;
	}

	public void setAllowNonWrappedIncoming(boolean allowNonWrappedIncoming) 
	{
		this.allowNonWrappedIncoming = allowNonWrappedIncoming;
	}

	public boolean isFetchIncomingSenderCerts() 
	{
		return fetchIncomingSenderCerts;
	}

	public void setFetchIncomingSenderCerts(boolean fetchIncomingSenderCerts)
	{
		this.fetchIncomingSenderCerts = fetchIncomingSenderCerts;
	}

	/**
     * Gets the certificate store used to encrypt messages and validate signatures.  This store generally contains only public certificates
     * @return The certificate store used to encrypt messages and validate signatures.
     */
    public ICertificateResolver getPublicCertResolver()
    {
        return this.publicCertResolver;
    }

    /**
     * Gets the certificate store used to decrypt and sign messages.  Certificates in this store must have access to the certifcate's private key.
     * @return The certificate store used to decrypt and sign messages.
     */
    public ICertificateResolver getPrivateCertResolver()
    {
        return this.privateCertResolver;
    }

    /**
     * Gets the certificate store that contains the certificate anchors that validate if certificates are trusted.
     * @return The certificate store that contains the certificate anchors that validate if certificates are trusted.
     */
    public ITrustAnchorResolver getTrustAnchors()
    {
        return this.trustAnchors;
    }

    /**
     * Gets the minimum trust status applied to messages by the agent.
     * @return The minimum trust status applied to messages by the agent.
     */
    public TrustEnforcementStatus getMinTrustRequirement()
    {
    	return this.minTrustRequirement;
    }
        
    /**
     * Sets the minimum trust status applied to messages by the agent.
     * @param value The minimum trust status applied to messages by the agent.
     */
    public void setMinTrustRequirement(TrustEnforcementStatus value)
    {
        if (value.compareTo(TrustEnforcementStatus.Success_Offline) < 0)
        {
            throw new IllegalArgumentException();
        }
        this.minTrustRequirement = value;
    }

    /**
     * Sets the event listener that will receive notifications at different stages of message processing. 
     * @param listener A concrete implementation of an NHINDAgentEventListener.
     */
    public void setEventListener(NHINDAgentEventListener listener)
    {
    	m_listener = listener;
    }

    
    public MessageEnvelope process(String messageText, boolean isIncoming)
    {
        return this.process(new MessageEnvelope(messageText), isIncoming);
    }
    
    public MessageEnvelope process(String messageText, NHINDAddressCollection recipients, NHINDAddress sender, boolean isIncoming)
    {
        return this.process(new MessageEnvelope(messageText, recipients, sender), isIncoming);
    }

    public MessageEnvelope process(MessageEnvelope envelope, boolean isIncoming)
    {
        if (envelope == null)
        {
            throw new IllegalArgumentException();
        }
        
        this.checkEnvelopeAddresses(envelope);

        if (SMIMEStandard.isEncrypted(envelope.getMessage()))
        {
            isIncoming = true;
            IncomingMessage incoming = new IncomingMessage(envelope);
            envelope.clear();
            
            this.processIncoming(incoming);
            return incoming;
        }

        isIncoming = false;
        OutgoingMessage outgoing = new OutgoingMessage(envelope);
        envelope.clear();
        
        this.processOutgoing(outgoing);
        return outgoing;
    }    

	/**
	 * Processes an incoming message.  The message will be decrypted and validated that it meets trust assertions.
	 * @param messageText The raw contents of the incoming message that will be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */    
    public IncomingMessage processIncoming(String messageText)
    {
        if (messageText == null || messageText.length() == 0)
        {
            throw new IllegalArgumentException();
        }

        return processIncoming(new IncomingMessage(messageText));
    }    
    
    public IncomingMessage processIncoming(String messageText, NHINDAddressCollection recipients, NHINDAddress sender)
    {
        this.checkEnvelopeAddresses(recipients, sender);
        
        IncomingMessage message = new IncomingMessage(messageText, recipients, sender);
        return this.processIncoming(message);                    
    }    
    
    public IncomingMessage processIncoming(MessageEnvelope envelope)
    {
        if (envelope == null)
        {
            throw new IllegalArgumentException();
        }
        
        this.checkEnvelopeAddresses(envelope);
        return this.processIncoming(new IncomingMessage(envelope));
    }    
    
    
	/**
	 * Processes an incoming MimeMessage.  The message will be decrypted and validated that it meets trust assertions.
	 * @param msg The message to be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */
    public IncomingMessage processIncoming(MimeMessage msg)
    {
    	IncomingMessage inMsg = null;
    	try
    	{
    		inMsg = new IncomingMessage(new Message(msg));
    		
    	}
    	catch (MessagingException e)
    	{
    		throw new MimeException(MimeError.InvalidMimeEntity, e);
    	}
    	
    	return processIncoming(inMsg);
    }    
    

    
	/**
	 * Processes an incoming message.  The message will be decrypted and validated that it meets trust assertions.
	 * @param message The message to be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */
    public IncomingMessage processIncoming(IncomingMessage message)
    {          
    	if (message == null)
        {
            throw new IllegalArgumentException();
        }    	

    	if (LOGGER.isDebugEnabled())
    		LOGGER.debug("Processing incoming message:\r\n" + message.toString() + "\r\n");    	
    	    	
        try
        {
            message.setAgent(this);
            message.validate();
            
            if (m_listener != null)
            	m_listener.preProcessIncoming(message);            

            processMessage(message);

            if (m_listener != null)
            	m_listener.postProcessIncoming(message);                
            
        	if (LOGGER.isDebugEnabled())
        		LOGGER.debug("Completed processing incoming message.  Result message:\r\n" + EntitySerializer.Default.serialize(message.getMessage()) + "\r\n");              
            
            return message;
        }
        catch (Exception error)
        {        	
        	LOGGER.error("Error processing incoming message: " + error.getMessage(), error);        	
        	
        	NHINDException throwError = new NHINDException(error);
        	
            if (m_listener != null)
            	m_listener.errorIncoming(message, error);  
            throw throwError;  // rethrow error
        }
    }

    private void processMessage(IncomingMessage message)
    {    	
        if (message.getSender() == null)
        {
            throw new TrustException(TrustError.UntrustedSender);
        }

        message.categorizeRecipients(this.getDomain());
        if (!message.hasDomainRecipients())
        {
        	throw new AgentException(AgentError.NoTrustedRecipients);
        }
        //
        // Map each address to its certificates/trust settings
        //
        this.bindAddresses(message);
        //
        // Extract signed content from the message
        //
        this.decryptSignedContent(message);
        
        //
        // The standard requires that the original message be wrapped to protect headers
        //
        message.setMessage(this.unwrapMessage(message.getMessage()));

        // Enforce trust requirements, including checking signatures
        //
        this.trustModel.enforce(message);        
        
        //
        // Remove any untrusted recipients...
        //
        if (message.hasDomainRecipients())
        {
            message.categorizeRecipients(this.minTrustRequirement);
        }
        if (!message.hasDomainRecipients())
        {
            throw new TrustException(TrustError.NoTrustedRecipients);
        }        
        
        message.updateRoutingHeaders();        
    }

    private void bindAddresses(IncomingMessage message)
    {
    	if (fetchIncomingSenderCerts)
    	{
    		message.getSender().setCertificates(this.resolvePublicCerts(message.getSender(), false));
    	}
    	
        //
        // Bind each recpient's certs and trust settings
        //

        for (NHINDAddress recipient : message.getDomainRecipients())
        {
            recipient.setCertificates(this.resolvePrivateCerts(recipient, false));
            recipient.setTrustAnchors(this.trustAnchors.getIncomingAnchors().getCertificates(recipient));
        }
    }

    @SuppressWarnings("unchecked")
    private void decryptSignedContent(IncomingMessage message)
    {
        
        MimeEntity decryptedEntity = this.decryptMessage(message);
        CMSSignedData signatures;
        MimeEntity payload;
        try
        {
	        if (SMIMEStandard.isContentEnvelopedSignature(new ContentType(decryptedEntity.getContentType())))
	        {
	            signatures = cryptographer.deserializeEnvelopedSignature(decryptedEntity);                
	            payload = new MimeEntity(new ByteArrayInputStream(signatures.getContentInfo().getEncoded()));
	        }                        
	        else if (SMIMEStandard.isContentMultipartSignature(new ContentType(decryptedEntity.getContentType())))
	        {
	            //
	            // Extract the signature envelope. That contains both the signature and the actual message content
	            //
				ByteArrayDataSource dataSource = new ByteArrayDataSource(decryptedEntity.getRawInputStream(), decryptedEntity.getContentType());
				
				MimeMultipart verifyMM = new MimeMultipart(dataSource);	    	                 	
	        	
	            SignedEntity signedEntity = SignedEntity.load(verifyMM);                  
	            signatures = cryptographer.deserializeSignatureEnvelope(signedEntity);
	            payload = signedEntity.getContent(); 
	        }
	        else
	        {
	            throw new AgentException(AgentError.UnsignedMessage);
	        }        
	        
	        message.setSignature(signatures);
	        
	        //
	        // Alter body to contain actual content. Also clean up mime headers on the message that were there to support
	        // signatures etc
	        //         	
        	InternetHeaders headers = new InternetHeaders();
        	
        	// remove all mime headers
        	Enumeration eHeaders = message.getMessage().getAllHeaders();
        	while (eHeaders.hasMoreElements())
        	{
        		Header hdr = (Header)eHeaders.nextElement();
        		if (!MimeStandard.startsWith(hdr.getName(), MimeStandard.HeaderPrefix))
        			headers.setHeader(hdr.getName(), hdr.getValue());
        	}
        	
        	
        	// add back in headers from original message
        	eHeaders = payload.getAllHeaders();
        	while (eHeaders.hasMoreElements())
        	{
        		Header hdr = (Header)eHeaders.nextElement();
        		headers.setHeader(hdr.getName(), hdr.getValue());
        	}   
			
        	Message msg = new Message(headers, payload.getContentAsBytes());
        	message.setMessage(msg);
        }
        catch (MessagingException e)
        {
        	throw new MimeException(MimeError.InvalidBody, e);
        }
        catch (IOException e)
        {
        	throw new MimeException(MimeError.InvalidBody, e);
        }        
    }

    private MimeEntity decryptMessage(IncomingMessage message)
    {
        MimeEntity decryptedEntity = null;
        if (this.encryptionEnabled)
        {
            //
            // Yes, this can be optimized heavily for multiple certs. 
            // But we will start with the easy to understand simple version
            //            
            // Decrypt and parse message body into a signature entity - the envelope that contains our data + signature
            // We can use the cert of any ONE of the recipients to decrypts
            // So basically, we'll try until we find one, or we just run out...
            //
            for (X509Certificate cert : message.getDomainRecipients().getCertificates())
            {
                try
                {
                	if (cert instanceof X509CertificateEx)
                	{
                		X509CertificateEx privCert = (X509CertificateEx)cert;
                		decryptedEntity = this.cryptographer.decrypt(message.getMessage(), privCert);
                		break;
                	}
                }
                catch (Exception e)
                {
                }
            }
        }
        else
        {
        	try
        	{
        		decryptedEntity = new MimeEntity(message.getMessage().getRawInputStream());
        	}
        	catch (MessagingException ex)
        	{
        		throw new AgentException(AgentError.MissingMessage);
        	}
        }
        
        if (decryptedEntity == null)
        {
            throw new AgentException(AgentError.UntrustedMessage);
        }

        return decryptedEntity;
    }    
    
    public OutgoingMessage processOutgoing(String messageText)
    {
    	if (messageText == null || messageText.length() == 0)
    		throw new IllegalArgumentException();
    	
        OutgoingMessage message = new OutgoingMessage(this.wrapMessage(messageText));
        
        return this.processOutgoing(message);
    }
    
    public OutgoingMessage processOutgoing(String messageText, NHINDAddressCollection recipients, NHINDAddress sender)
    {
        this.checkEnvelopeAddresses(recipients, sender);

        OutgoingMessage message = new OutgoingMessage(this.wrapMessage(messageText), recipients, sender);            
        return this.processOutgoing(message);            
    }    
    
    public OutgoingMessage processOutgoing(MessageEnvelope envelope)
    {
        if (envelope == null)
            throw new IllegalArgumentException();
        
        this.checkEnvelopeAddresses(envelope);

        OutgoingMessage message = new OutgoingMessage(envelope);
        return this.processOutgoing(message);
    }    
    
	/**
	 * Processes an outgoing message.  The message will be singed, encrypted, and validated that it meets trust assertions.
	 * @param messageText The raw contents of the incoming message that will be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */
    public OutgoingMessage processOutgoing(OutgoingMessage message)
    {
        if (message == null)
            throw new IllegalArgumentException();
     
    	//LOGGER.debug("Processing outgoing message:\r\n" + message.getMessage().g + "\r\n");    	        
        
        message.setAgent(this);    
                
        message.validate();

        try
        {

            if (m_listener != null)
            	m_listener.preProcessOutgoing(message);                

            processMessage(message);

            if (m_listener != null)
            	m_listener.postProcessOutgoing(message);      
            
        	//if (LOGGER.isDebugEnabled())
        	//	LOGGER.debug("Completed processing outing message.  Result message:\r\n" + EntitySerializer.Default.serialize(message) + "\r\n");             
        }
        catch (Exception error)
        {        	        	        
        	LOGGER.error("Error processing outgoing message: " + error.getMessage(), error);
        	
        	NHINDException throwError = new NHINDException(error);
        	
            if (m_listener != null)
            	m_listener.errorOutgoing(message, error);  
            throw throwError;  // rethrow error
        }
        
        return message;
    }

	/**
	 * Processes an outgoing message.  The message will be singed, encrypted, and validated that it meets trust assertions.
	 * @param message The message to be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */    
    private void processMessage(OutgoingMessage message)
    {
    	try
    	{
	        if (!WrappedMessage.isWrapped(message.getMessage()))
	        {
	            message.setMessage(this.wrapMessage(message.getMessage()));
	        }
    	}
    	catch (MessagingException e)
    	{
    		throw new MimeException(MimeError.InvalidMimeEntity);
    	}
    	
    	
        if (message.getSender() == null)
        {
            throw new AgentException(AgentError.MissingFrom);
        }

        this.bindAddresses(message);
        
        if (!message.hasRecipients())
        {
            throw new AgentException(AgentError.MissingFrom);
        }
        

        message.categorizeRecipients(this.m_domain);
        
        //
        // Enforce the trust model.
        //
        this.trustModel.enforce(message);        
        
        
        message.categorizeRecipients(this.minTrustRequirement);
        if (!message.hasRecipients())
        {
            throw new AgentException(AgentError.NoTrustedRecipients);
        }        

        //
        // Finally, sign and encrypt the message
        //
        this.signAndEncryptMessage(message);
        
        //
        // Not all recipients may be trusted. Remove them from Routing headers
        //
        message.updateRoutingHeaders();        
    }

    private void bindAddresses(OutgoingMessage message)
    {
        //
        // Retrieving the sender's private certificate is requied for encryption
        //
        message.getSender().setCertificates(this.resolvePrivateCerts(message.getSender(), true));
        message.getSender().setTrustAnchors(this.trustAnchors.getOutgoingAnchors().getCertificates(message.getSender()));

        //
        // Bind each recipient's certs
        //
        for(NHINDAddress recipient : message.getRecipients())
            recipient.setCertificates(this.resolvePublicCerts(recipient, false));
    }

    private Message wrapMessage(String messageText)
    {
    	Message retVal = null;
    	try
    	{
    		if (!wrappingEnabled)
    		{
    			return new Message(EntitySerializer.Default.deserialize(messageText));
    		}
        
    		retVal  = WrappedMessage.create(messageText, NHINDStandard.MailHeadersUsed);
    	}
    	catch (MessagingException e)
    	{
    		throw new MimeException(MimeError.InvalidMimeEntity, e);
    	}
    	
    	return retVal;
    }

    private Message wrapMessage(Message message)
    {
    	
    	Message retVal = null;
    	try
    	{
	        if (!wrappingEnabled)
	        {
	            return message;
	        }
	        
	        if (WrappedMessage.isWrapped(message))
	        {
	            return message;
	        }
	        
	        retVal =  WrappedMessage.create(message, NHINDStandard.MailHeadersUsed);
    	}
    	catch (MessagingException e)
    	{
    		throw new MimeException(MimeError.InvalidMimeEntity, e);
    	}
    	
    	return retVal;
    }
    
    private Message unwrapMessage(Message message)
    {
        if (!wrappingEnabled)
        {
            return message;
        }
        
        Message retMessage = null;
                
        try
        {

            if (allowNonWrappedIncoming && !WrappedMessage.isWrapped(message))
            {
                return message;
            }
        	retMessage = WrappedMessage.extract(message);
        }
        catch (MessagingException e)
        {
        	throw new MimeException(MimeError.InvalidMimeEntity, e);
        }
        
        return retMessage;
    }
    
    //
    // First sign, THEN encrypt the message
    //
    @SuppressWarnings("unchecked")
    private void signAndEncryptMessage(OutgoingMessage message)
    {
        SignedEntity signedEntity = cryptographer.sign(message.getMessage(), message.getSender().getCertificates());
               
        try
        {
	        if (encryptionEnabled)
	        {	        	
	            MimeEntity encryptedEntity = cryptographer.encrypt(signedEntity.getMimeMultipart(), message.getRecipients().getCertificates());
	            //
	            // Alter message content to contain encrypted data
	            //
	            
	            InternetHeaders headers = new InternetHeaders();
	            Enumeration eHeaders = message.getMessage().getAllHeaders();
	            while (eHeaders.hasMoreElements())
	            {
	            	Header hdr = (Header)eHeaders.nextElement();
	            	headers.setHeader(hdr.getName(), hdr.getValue());
	            }    
	              
	            eHeaders = encryptedEntity.getAllHeaders();
	            while (eHeaders.hasMoreElements())
	            {
	            	Header hdr = (Header)eHeaders.nextElement();
	            	headers.setHeader(hdr.getName(), hdr.getValue());
	            }    	            

	            Message msg = new Message(headers, encryptedEntity.getContentAsBytes());
	            
	            message.setMessage(msg);
	        }
	        else
	        {      	
	            
	            InternetHeaders headers = new InternetHeaders();
	            Enumeration eHeaders = message.getMessage().getAllHeaders();
	            while (eHeaders.hasMoreElements())
	            {
	            	Header hdr = (Header)eHeaders.nextElement();
	            	headers.setHeader(hdr.getName(), hdr.getValue());
	            }    
	              
	            headers.setHeader(MimeStandard.ContentTypeHeader, signedEntity.getMimeMultipart().getContentType());
	            Message msg = new Message(headers, signedEntity.getEntityBodyAsBytes());	  
	            
	            message.setMessage(msg);
	         }
        }
        catch (Exception e)
        {
        	throw new MimeException(MimeError.InvalidMimeEntity, e);
        }
    }

    private Collection<X509Certificate> resolvePrivateCerts(InternetAddress address, boolean required)
    {
    	Collection<X509Certificate> certs = null;
        try
        {
            certs = this.privateCertResolver.getCertificates(address);
            if (certs == null && required)
            {
                throw new AgentException(AgentError.UnknownRecipient);
            }
        }
        catch (Exception ex)
        {
            if (required)
            {
            	// for logging, tracking etc...
            	throw new NHINDException(ex);
            }
        }

        return certs;
    }

    private Collection<X509Certificate> resolvePublicCerts(InternetAddress address, boolean required) throws NHINDException
    {
    	Collection<X509Certificate> certs = null;
        try
        {
            certs = this.publicCertResolver.getCertificates(address);
            if (certs == null && required)
            {
                throw new AgentException(AgentError.UnknownRecipient);
            }
        }
        catch (Exception ex)
        {
            if (required)
            {
            	// for logging, tracking etc...
            	throw new NHINDException(ex);
            }                
        }

        return certs;
    }

    
    void checkEnvelopeAddresses(MessageEnvelope envelope)
    {
        this.checkEnvelopeAddresses(envelope.getRecipients(), envelope.getSender());
    }
    
    void checkEnvelopeAddresses(NHINDAddressCollection recipients, NHINDAddress sender)
    {
        if (recipients == null || recipients.size() == 0)
        {
            throw new AgentException(AgentError.NoRecipients);
        }
        if (sender == null)
        {
            throw new AgentException(AgentError.NoSender);
        }
        
        recipients.setSource(AddressSource.RcptTo);
        sender.setSource(AddressSource.MailFrom);
    }    
}
