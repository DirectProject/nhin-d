package org.nhindirect.stagent;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.stagent.ProtocolException.ProtocolError;
import org.nhindirect.stagent.cert.ICertificateService;
import org.nhindirect.stagent.cert.ICertificateStore;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.parser.Protocol;
import org.nhindirect.stagent.trust.ITrustSettingsStore;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;
import org.nhindirect.stagent.trust.TrustError;
import org.nhindirect.stagent.trust.TrustException;
import org.nhindirect.stagent.trust.impl.TrustModel;

/**
 * The NHINDAgent is the primary entity for applying cryptography and trust logic on incoming and outgoing messages.  The main messaging system (such as an SMTP server,
 * email client, or other message handling agent) instantiates an instance of the agent with configurable certificates storage implementations and trust anchor
 * stores.  The agent then applies S/MIME logic to the messages and asserts that the messages are being routed to and from trusted addresses.
 * <p>
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
@SuppressWarnings("unchecked")
public class NHINDAgent 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDAgent.class);
	
	static MimeMultipart lastMMPart = null;
	
	private Cryptographer m_cryptographer;
    private ICertificateService m_internalCertService;
    private ICertificateStore m_externalCertStore;
    private ITrustSettingsStore m_trustSettings;
    private TrustModel m_trustModel;
    private boolean m_encryptionEnabled = true;
    private TrustEnforcementStatus m_minTrustRequirement;
    private String m_domain;
    private NHINDAgentEventListener m_listener = null;
    
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
    public NHINDAgent(String domain, ICertificateService internalCerts, ICertificateStore externalCerts, ITrustSettingsStore trustSettings)
    {

    	
    	this(domain, internalCerts, externalCerts, trustSettings, TrustModel.Default, Cryptographer.Default);
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
    public NHINDAgent(String domain, ICertificateService internalCerts, ICertificateStore externalCerts, ITrustSettingsStore trustSettings, TrustModel trustModel, Cryptographer cryptographer)
    {            	
    	if (domain == null || domain.length() == 0 || internalCerts == null || externalCerts == null || trustSettings == null || trustModel == null || cryptographer == null)
        {
            throw new IllegalArgumentException();
        }

    	LOGGER.info("Initializing NHINDAgent\r\n\tDomain: " + domain + "\r\n");
    	
        this.m_domain = domain;
        this.m_internalCertService = internalCerts;
        this.m_externalCertStore = externalCerts;
        this.m_cryptographer = cryptographer;
        this.m_trustSettings = trustSettings;
        this.m_trustModel = trustModel;
        this.m_minTrustRequirement = TrustEnforcementStatus.Success_Offline;
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
    public Cryptographer getCryptographer()
    {
        return this.m_cryptographer;
    }

    /**
     * Indicates if messages are required to be encrypted in the agent.
     * @return True if messages are required to be encrypted in the agent.  False otherwise.
     */
    public boolean isEncryptMessages()
    {

        return this.m_encryptionEnabled;
    }
     
    /**
     * Sets if messages are required to be encrypted in the agen
     * @param value True if messages are required to be encrypted in the agent.  False otherwise.
     */
    public void setEncryptMessages(boolean value)
    {
        this.m_encryptionEnabled = value;
    }

    /**
     * Gets the certificate store used to encrypt messages and validate signatures.  This store generally contains only public certificates
     * @return The certificate store used to encrypt messages and validate signatures.
     */
    public ICertificateStore getExternalCertStore()
    {
        return this.m_externalCertStore;
    }

    /**
     * Gets the certificate store used to decrypt and sign messages.  Certificates in this store must have access to the certifcate's private key.
     * @return The certificate store used to decrypt and sign messages.
     */
    public ICertificateService getInternalCertService()
    {
        return this.m_internalCertService;
    }

    /**
     * Gets the certificate store that contains the certificate anchors that validate if certificates are trusted.
     * @return The certificate store that contains the certificate anchors that validate if certificates are trusted.
     */
    public ITrustSettingsStore getTrustSettings()
    {
        return this.m_trustSettings;
    }

    /**
     * Gets the minimum trust status applied to messages by the agent.
     * @return The minimum trust status applied to messages by the agent.
     */
    public TrustEnforcementStatus getMinTrustRequirement()
    {
    	return this.m_minTrustRequirement;
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
        this.m_minTrustRequirement = value;
    }

    /**
     * Sets the event listener that will receive notifications at different stages of message processing. 
     * @param listener A concrete implementation of an NHINDAgentEventListener.
     */
    public void setEventListener(NHINDAgentEventListener listener)
    {
    	m_listener = listener;
    }


	/**
	 * Processes an incoming MimeMessage.  The message will be decrypted and validated that it meets trust assertions.
	 * @param msg The message to be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */
    public String processIncoming(MimeMessage msg)
    {
    	IncomingMessage inMsg = null;
    	try
    	{
    		inMsg = new IncomingMessage(msg);
    		
    	}
    	catch (MessagingException e)
    	{
    		throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
    	}
    	
    	return processIncoming(inMsg);
    }    
    
	/**
	 * Processes an incoming message.  The message will be decrypted and validated that it meets trust assertions.
	 * @param messageText The raw contents of the incoming message that will be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */    
    public String processIncoming(String messageText)
    {
        if (messageText == null || messageText.length() == 0)
        {
            throw new IllegalArgumentException();
        }

    	IncomingMessage inMsg = null;
    	try
    	{
            ByteArrayInputStream inStream = new ByteArrayInputStream(messageText.getBytes()); 
    		inMsg = new IncomingMessage(inStream);	
    	}
    	catch (MessagingException e)
    	{
    		throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
    	}                

        return processIncoming(inMsg);
    }
    
	/**
	 * Processes an incoming message.  The message will be decrypted and validated that it meets trust assertions.
	 * @param message The message to be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */
    public String processIncoming(IncomingMessage message)
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

            message = processMessage(message);

            if (m_listener != null)
            	m_listener.postProcessIncoming(message);                
            
        	if (LOGGER.isDebugEnabled())
        		LOGGER.debug("Completed processing incoming message.  Result message:\r\n" + EntitySerializer.Default.serialize(message) + "\r\n");              
            
            return EntitySerializer.Default.serialize(message);
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

    private IncomingMessage processMessage(IncomingMessage message)
    {
    	IncomingMessage retVal = null;
    	
        if (message.getSender() == null)
        {
            throw new TrustException(TrustError.UntrustedSender);
        }

        message.categorizeRecipients(this.getDomain());
        if (!message.hasDomainRecipients())
        {
        	throw new TrustException(TrustError.NoTrustedRecipients);
        }
        //
        // Map each address to its certificates/trust settings
        //
        this.bindAddresses(message);
        //
        // Extract signed content from the message
        //
        SignedEntity signedEntity = this.decryptSignedContent(message);
        
        
        //
        // Extract the signature. 
        //
        message.setSignature(this.m_cryptographer.deserializeSignatureEnvelope(signedEntity));
        //
        // Alter body to contain actual content. Also clean up mime headers on the message that were there to support
        // signatures etc
        //
        
        // Enforce trust requirements, including checking signatures
        //
        this.m_trustModel.enforce(message);
        //
        // Remove any untrusted recipients...
        //
        if (message.hasDomainRecipients())
        {
            message.categorizeRecipients(this.m_minTrustRequirement);
        }
        if (!message.hasDomainRecipients())
        {
            throw new TrustException(TrustError.NoTrustedRecipients);
        }        
        
        message.updateTo();
        
        try
        {
        	//message.setText(signedEntity.getContent().toString());
        	
        	InternetHeaders headers = new InternetHeaders();
        	
        	// remove all mime headers
        	Enumeration eHeaders = message.getAllHeaders();
        	while (eHeaders.hasMoreElements())
        	{
        		Header hdr = (Header)eHeaders.nextElement();
        		if (!Protocol.startsWith(hdr.getName(), Protocol.MimeHeaderPrefix))
        			//message.removeHeader(hdr.getName());
        			headers.setHeader(hdr.getName(), hdr.getValue());
        	}
        	
        	// add back in headers from original message
        	eHeaders = signedEntity.getContent().getAllHeaders();
        	while (eHeaders.hasMoreElements())
        	{
        		Header hdr = (Header)eHeaders.nextElement();
        		headers.setHeader(hdr.getName(), hdr.getValue());
        	}   

        	retVal = new IncomingMessage(headers, signedEntity.getContent().getContentAsBytes());
        	
        }
        catch (MessagingException e)
        {
        	throw new ProtocolException(ProtocolError.InvalidBody, e);
        }
        
        return retVal;
    }

    private void bindAddresses(IncomingMessage message)
    {
        //
        // Retrieving the sender's certificate is optional
        //
        message.getSender().setCertificate(this.resolvePublicCert(message.getSender(), false));
        //
        // Bind each recpient's certs and trust settings
        //

        for (NHINDAddress recipient : message.getDomainRecipients())
        {
            recipient.setCertificate(this.resolvePrivateCert(recipient, false));
            recipient.setTrustAnchors(this.m_trustSettings.getTrustAnchorsIncoming(recipient));
        }
    }

    private SignedEntity decryptSignedContent(IncomingMessage message)
    {
        SignedEntity signedEntity;
        try
        {
	        if (this.m_encryptionEnabled)
	        {
	            //            
	            // Decrypt and parse message body into a signature entity - the envelope that contains our data + signature
	            // We can use the cert of any ONE of the recipients to decrypt
	            //
	            X509Certificate decryptingCert = message.getDomainRecipients().getFirstCertificate();
	            if (decryptingCert == null)
	            {
	                throw new TrustException(TrustError.MissingRecipientCertificate);
	            }
	            MimeEntity decryptedEntity = this.m_cryptographer.decrypt(message, (X509CertificateEx)decryptingCert);
	            //
	            // Extract the signature envelope. That contains both the signature and the actual message content
	            //
				ByteArrayDataSource dataSource = new ByteArrayDataSource(decryptedEntity.getRawInputStream(), decryptedEntity.getContentType());
				
				MimeMultipart verifyMM = new MimeMultipart(dataSource);	    	
	            
	            signedEntity = SignedEntity.load(verifyMM);
	        }
	        else
	        {
				ByteArrayDataSource dataSource = new ByteArrayDataSource(message.getRawInputStream(), message.getContentType());
				
				MimeMultipart verifyMM = new MimeMultipart(dataSource);	    	
	            
	            signedEntity = SignedEntity.load(verifyMM);
	        }
        }
        catch (Exception e)
        {
        	throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
        }
        
        return signedEntity;
    }

	/**
	 * Processes an outgoing message.  The message will be singed, encrypted, and validated that it meets trust assertions.
	 * @param messageText The raw contents of the incoming message that will be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */
    public String processOutgoing(String messageText)
    {
        if (messageText == null || messageText.length() == 0)
        {
            throw new IllegalArgumentException();
        }

    	LOGGER.debug("Processing outgoing message:\r\n" + messageText + "\r\n");    	        
        
        MimeMessage entity = EntitySerializer.Default.deserialize(messageText);
        
        OutgoingMessage message = null;
        try
        {
        	message = new OutgoingMessage(entity);
        }
        catch (MessagingException e)
        {
        	throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
        }
        
        message.validate();

        try
        {
            message.setAgent(this);


            if (m_listener != null)
            	m_listener.preProcessOutgoing(message);                

            message = processMessage(message);

            if (m_listener != null)
            	m_listener.postProcessOutgoing(message);      
            
        	if (LOGGER.isDebugEnabled())
        		LOGGER.debug("Completed processing outing message.  Result message:\r\n" + EntitySerializer.Default.serialize(message) + "\r\n");             
            
            return EntitySerializer.Default.serialize(message);
        }
        catch (Exception error)
        {        	        	        
        	LOGGER.error("Error processing outgoing message: " + error.getMessage(), error);
        	
        	NHINDException throwError = new NHINDException(error);
        	
            if (m_listener != null)
            	m_listener.errorOutgoing(message, error);  
            throw throwError;  // rethrow error
        }
    }

	/**
	 * Processes an outgoing message.  The message will be singed, encrypted, and validated that it meets trust assertions.
	 * @param message The message to be processed.
	 * @return A string that contains the raw contents of the processed message.
	 */    
    private OutgoingMessage processMessage(OutgoingMessage message)
    {
        if (message.getSender() == null)
        {
            throw new ProtocolException(ProtocolError.MissingFrom);
        }

        this.bindAddresses(message);
        if (!message.hasRecipients())
        {
            throw new ProtocolException(ProtocolError.MissingTo);
        }
        //
        // Enforce the trust model.
        //
        this.m_trustModel.enforce(message);
        //
        // Remove any non-trusted recipients
        //
        message.categorizeRecipients(this.m_minTrustRequirement);
        if (!message.hasRecipients())
        {
            throw new TrustException(TrustError.NoTrustedRecipients);
        }
        //
        // Finally, sign and encrypt the message
        //
        return signAndEncryptMessage(message);
    }

    private void bindAddresses(OutgoingMessage message)
    {
        //
        // Retrieving the sender's private certificate is requied for encryption
        //
        message.getSender().setCertificate(this.resolvePrivateCert(message.getSender(), true));
        message.getSender().setTrustAnchors(this.m_trustSettings.getTrustAnchorsOutgoing(message.getSender()));
        //
        // Bind each recipient's certs
        //
        NHINDAddressCollection recipients = message.getRecipients();
        for (int i = 0, count = recipients.size(); i < count; ++i)
        {
            NHINDAddress recipient = recipients.get(i);
            recipient.setCertificate(this.resolvePublicCert(recipient, false));
        }
    }

    //
    // First sign, THEN encrypt the message
    //
    private OutgoingMessage signAndEncryptMessage(OutgoingMessage message)
    {
    	OutgoingMessage retVal = null;
        SignedEntity signedEntity = this.m_cryptographer.sign(message, (X509CertificateEx)message.getSender().getCertificate());
               
        try
        {
	        if (this.m_encryptionEnabled)
	        {	        	
	            MimeEntity encryptedEntity = this.m_cryptographer.encrypt(signedEntity.getMimeMultipart(), message.getRecipients().getCertificates());
	            //
	            // Alter message content to contain encrypted data
	            //
	            
	            message.updateTo();
	            
	            InternetHeaders headers = new InternetHeaders();
	            Enumeration eHeaders = message.getAllHeaders();
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

	            retVal = new OutgoingMessage(headers, encryptedEntity.getContentAsBytes());	            
	        }
	        else
	        {      	
	            message.updateTo();
	            
	            InternetHeaders headers = new InternetHeaders();
	            Enumeration eHeaders = message.getAllHeaders();
	            while (eHeaders.hasMoreElements())
	            {
	            	Header hdr = (Header)eHeaders.nextElement();
	            	headers.setHeader(hdr.getName(), hdr.getValue());
	            }    
	              
	            headers.setHeader(Protocol.ContentTypeHeader, signedEntity.getMimeMultipart().getContentType());
	            retVal = new OutgoingMessage(headers, signedEntity.getEntityBodyAsBytes());	        
	         }
        }
        catch (Exception e)
        {
        	throw new ProtocolException(ProtocolError.InvalidMimeEntity, e);
        }
        
        return retVal;
    }

    private X509CertificateEx resolvePrivateCert(InternetAddress address, boolean required)
    {
        X509CertificateEx cert = null;
        try
        {
            cert = this.m_internalCertService.getPrivateCertificate(address);
            if (cert == null && required)
            {
                throw new TrustException(TrustError.UnknownRecipient);
            }
        }
        catch (Exception ex)
        {
            if (required)
            {
                if (this.m_listener != null)
                	m_listener.error(ex);
                
                throw new NHINDException(ex);
            }
        }

        return cert;
    }

    private X509Certificate resolvePublicCert(InternetAddress address, boolean required) throws NHINDException
    {
        X509Certificate cert = null;
        try
        {
            if (address.getAddress().contains(this.m_domain))
            {
                //
                // Internal address
                //
                cert = this.m_internalCertService.getCertificate(address);
            }
            else
            {
                cert = this.m_externalCertStore.getCertificate(address);
            }

            if (cert == null && required)
            {
                throw new TrustException(TrustError.UnknownRecipient);
            }
        }
        catch (Exception ex)
        {
            if (required)
            {
                if (this.m_listener != null)
                	m_listener.error(ex);
                
                throw new NHINDException(ex);
            }

        }

        return cert;
    }

}
