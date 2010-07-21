package org.nhindirect.stagent.trust.impl;


import java.security.cert.Certificate;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.bouncycastle.cms.CMSSignedData;

import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.OutgoingMessage;
import org.nhindirect.stagent.cert.SingnerCertPair;
import org.nhindirect.stagent.trust.ITrustModel;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;
import org.nhindirect.stagent.trust.TrustError;
import org.nhindirect.stagent.trust.TrustException;

/**
 * Default implementation of the trust model.
 * <p>
 * For outgoing messages each recipient is checked that it has a valid public certificate and that the certificate 
 * has a trusted anchor in the trust settings.
 * <p>
 * For incoming messages the sender's signature is validated and each recipient is checked to have a valid certificate.  The
 * sender is also validated to be trusted by the recipients.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class TrustModel implements ITrustModel 
{
    public static final TrustModel Default = new TrustModel();
    
    /**
     * Constructor
     */
    public TrustModel()
    {
    }

    private void checkSignature(IncomingMessage message)
    {
    	CMSSignedData signature = message.getSignature();
            	
        
    	SingnerCertPair signerCertPair = CryptoExtensions.findSignerByCert(signature, message.getSender().getCertificate());
        if (signerCertPair == null)
        {
        	signerCertPair = CryptoExtensions.findSignerByName(signature, message.getSender().getHost());
        }
        if (signerCertPair == null)
        {
            throw new TrustException(TrustError.MissingSenderSignature);
        }
        //
        // Verify the signature
        //

        try
        {
        	if (!signerCertPair.getSigner().verify(signerCertPair.getCertificate(), "BC"))
        		throw new TrustException(TrustError.SignatureValidation);        	
        }
        catch (Exception e)
        {       	
        	throw new TrustException(TrustError.SignatureValidation, e);
        }
                
        
        message.setValidatedSigner(signerCertPair.getSigner());
    }
       
    /**
     * {@inheritDoc}}
     */
    public void enforce(IncomingMessage message)
    {
        if (message == null)
        {
            throw new IllegalArgumentException();
        }
        
        if (!message.isSignatureVerified())
        {
            this.checkSignature(message);
        }
        
        NHINDAddress sender = message.getSender();
              
        X509Certificate senderCert = sender.hasCertificate() ? sender.getCertificate() : 
             CryptoExtensions.findSignerByName(message.getSignature(), sender.getAddress()).getCertificate();
        
        //bool thumbprintVerified = (sender.hasCertificate() && sender.Certificate.Thumbprint == signer.Certificate.Thumbprint);

        NHINDAddressCollection recipients = message.getDomainRecipients();
        for (int i = 0, count = recipients.size(); i < count; ++i)
        {
            NHINDAddress recipient = recipients.get(i);
            TrustEnforcementStatus trustStatus = TrustEnforcementStatus.Failed;
            //
            // Does this recipient trust the message sender? 
            //
            if (this.isTrusted(sender, senderCert, recipient.getTrustAnchors()))
            {
                if (sender.hasCertificate())
                {
                    //trustStatus = (thumbprintVerified) ? TrustEnforcementStatus.Success : TrustEnforcementStatus.Success_ThumbprintMismatch;
                	trustStatus = TrustEnforcementStatus.Success;
                }
                else
                {
                    trustStatus = TrustEnforcementStatus.Success_Offline;
                }
            }                    
            recipient.setStatus(trustStatus);
        }            
    }
    
    /**
     * {@inheritDoc}}
     */    
    public void enforce(OutgoingMessage message)
    {
        if (message == null)
        {
            throw new IllegalArgumentException();
        }
        
        NHINDAddress sender = message.getSender();
        NHINDAddressCollection recipients = message.getRecipients();
        
        for (int i = 0, count = recipients.size(); i < count; ++i)
        {
            NHINDAddress recipient = recipients.get(i);
            recipient.setStatus(TrustEnforcementStatus.Failed);                
            if (recipient.hasCertificate() && this.isTrusted(recipient, recipient.getCertificate(), sender.getTrustAnchors()))
            {
                recipient.setStatus(TrustEnforcementStatus.Success);
            }
        }
    }
                            
    private boolean isTrusted(InternetAddress address, X509Certificate certificate, Collection<X509Certificate> anchors)
    {    	
    	try
    	{
        	
    		CertPath certPath = null;
        	CertificateFactory factory = CertificateFactory.getInstance("X509");
        	
        	List<Certificate> certs = new ArrayList<Certificate>();
        	certs.add(certificate);
        	
        	Set<TrustAnchor> trustAnchorSet = new HashSet<TrustAnchor>();
        		
        	for (X509Certificate archor : anchors)
        		trustAnchorSet.add(new TrustAnchor(archor, null));
        	
            PKIXParameters params = new PKIXParameters(trustAnchorSet); 
            params.setRevocationEnabled(false); // NHIND Revocations are handled using ICertificateStore.getCertificate
        	certPath = factory.generateCertPath(certs);
        	CertPathValidator pathValidator = CertPathValidator.getInstance("PKIX");    		
    		

        	pathValidator.validate(certPath, params);
    		return true;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return false;    	
    }        

}
