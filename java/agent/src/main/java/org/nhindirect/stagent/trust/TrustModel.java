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

package org.nhindirect.stagent.trust;


import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyFilter;
import org.nhindirect.policy.PolicyFilterFactory;
import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.stagent.AgentError;
import org.nhindirect.stagent.AgentException;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.DefaultMessageSignatureImpl;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.OutgoingMessage;
import org.nhindirect.stagent.cert.SignerCertPair;
import org.nhindirect.stagent.policy.PolicyResolver;
import org.nhindirect.stagent.trust.annotation.TrustPolicyFilter;
import org.nhindirect.stagent.trust.annotation.TrustPolicyResolver;

import com.google.inject.Inject;

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
public class TrustModel 
{
    public static final TrustModel Default = new TrustModel();
    
    private final TrustChainValidator certChainValidator;

    private static final Log LOGGER = LogFactory.getFactory().getInstance(TrustModel.class);
    
	private PolicyResolver trustPolicyResolver;
    
	private PolicyFilter policyFilter;
	
    /**
     * Constructs a model with a default validator.
     */
    public TrustModel()
    {
    	certChainValidator = new TrustChainValidator();
        try
        {
        	this.policyFilter = PolicyFilterFactory.getInstance();
        }
        catch (PolicyParseException e)
        {
        	throw new AgentException(AgentError.Unexpected, "Failed to create policy filter object.", e);
        }
    }

    /**
     * Constructs a model with a provided chain validator.
     * @param validator The trust validator used to valid trust of a certificate with trust anchors.
     */
    @Inject
    public TrustModel(TrustChainValidator validator)
    {
    	certChainValidator = validator;
        try
        {
        	this.policyFilter = PolicyFilterFactory.getInstance();
        }
        catch (PolicyParseException e)
        {
        	throw new AgentException(AgentError.Unexpected, "Failed to create policy filter object.", e);
        }
    }    
    
    /**
     * Gets the chain validator associated with this model.
     * @return The chain validator associated with this model.
     */
    public TrustChainValidator getCertChainValidator()
    {
    	return certChainValidator;
    }
    
    /**
     * Sets the policy filter for trust validation.
     * @param policyFilter The filter used to check certificate for policy compliance.
     */
    @Inject(optional=true)
	public void setPolicyFilter(@TrustPolicyFilter PolicyFilter policyFilter)
	{
		this.policyFilter = policyFilter;
	}
	
    /**
    * Gets the policy filter for trust validation.
    * @return policyFilter The filter used to check certificate for policy compliance.    
    **/
	public PolicyFilter getPolicyFilter()
	{
		return this.policyFilter;
	}
    
	/**
	 * Sets the policy resolver for trust validation
	 * @param trustPolicyResolver The policy resolver used to finding certificate policies for trust validation.
	 */
    @Inject(optional=true)
	public void setTrustPolicyResolver(@TrustPolicyResolver PolicyResolver trustPolicyResolver)
	{
		this.trustPolicyResolver = trustPolicyResolver;
	}
    
	/**
	 * Gets the policy resolver for trust validation
	 * @return  The policy resolver used to finding certificate policies for trust validation.
	 */
    public PolicyResolver getTrustPolicyResolver()
	{
		return this.trustPolicyResolver;
	}
    
    /**
     * Enforces the trust policy an incoming message.  Each domain recipient's trust status is set according the models trust policy. 
     */
    public void enforce(IncomingMessage message)
    {
    	if (message == null)
    		throw new IllegalArgumentException();
    	
    	if (!message.hasSignatures())
    		throw new AgentException(AgentError.UntrustedMessage);
            	
    	findSenderSignatures(message);
        if (!message.hasSenderSignatures())
            throw new AgentException(AgentError.MissingSenderSignature);
                      
        // 
        // For each domain recipient, find at least one valid sender signature that the recipient trusts
        // the default value of the trust status is false, so only change the status if a trusted
        // certificate is found
        //        
        NHINDAddressCollection recipients = message.getDomainRecipients();
        for (NHINDAddress recipient : recipients)
        {
        	recipient.setStatus(TrustEnforcementStatus.Failed);
        	
        	// make sure the recipient has its own cert... otherwise this may
        	// be a bogus recipient
        	if (recipient.getCertificates() != null)
        	{
	        	
	        	// Find a trusted signature
	        	DefaultMessageSignatureImpl trustedSignature = findTrustedSignature(message, recipient, recipient.getTrustAnchors());
	        	
	        	// verify the signature
	        	if (trustedSignature != null)
	        	{
	
	                recipient.setStatus(trustedSignature.isThumbprintVerified() ? TrustEnforcementStatus.Success 
	                		: TrustEnforcementStatus.Success_ThumbprintMismatch);
	        	}
	        	else
	        	{
	        		LOGGER.warn("enforce(IncomingMessage message) - could not find a trusted certificate for recipient " + recipient.getAddress());
	        	}
        	}
        	else
        	{
        		LOGGER.warn("enforce(IncomingMessage message) - recipient " + recipient.getAddress() + " does not have a bound certificate");
        	}
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
        
        for (NHINDAddress recipient : recipients)
        {
            recipient.setStatus(TrustEnforcementStatus.Failed);                
            
            Collection<X509Certificate> certs = recipient.getCertificates();
            if (certs == null || certs.size() == 0)
            	LOGGER.warn("enforce(OutgoingMessage message) - recipient " + recipient.getAddress() + " has no bound certificates");

        	recipient.setCertificates(findTrustedCerts(certs, sender.getTrustAnchors()));
        	if (recipient.hasCertificates())
        		recipient.setStatus(TrustEnforcementStatus.Success);
        	else
        		LOGGER.warn("enforce(OutgoingMessage message) - could not trust any certificates for recipient " + recipient.getAddress());

        }
    }
           
    protected Collection<X509Certificate> findTrustedCerts(Collection<X509Certificate> certs, Collection<X509Certificate> anchors)
    {
        if (certs == null)
        {
            return null;
        }
        
        Collection<X509Certificate> trustedCerts = null;
        for (X509Certificate cert : certs)
        {
        	if (certChainValidator.isTrusted(cert, anchors))
        	{
                if (trustedCerts == null)
                {
                	trustedCerts = new ArrayList<X509Certificate>();
                }
                trustedCerts.add(cert);        		
        	}
        }
        
        return trustedCerts;
    }
    
    protected void findSenderSignatures(IncomingMessage message)
    {
    	message.setSenderSignatures(null);
    	
    	NHINDAddress sender = message.getSender();
    	 
    	Collection<DefaultMessageSignatureImpl> senderSignatures = new ArrayList<DefaultMessageSignatureImpl>();
    	
    	// check for signatures at an individual level    	
    	Collection<SignerCertPair> individualSenders = CryptoExtensions.findSignersByName(message.getSignature(), sender.getAddress(), null);
    	
    	// check for signatures at an org level
    	Collection<SignerCertPair> orgSenders = CryptoExtensions.findSignersByName(message.getSignature(), 
    			sender.getHost(), Arrays.asList(new String[] {sender.getAddress()}));
    	
    	for (SignerCertPair pair : individualSenders)
    		senderSignatures.add(new DefaultMessageSignatureImpl(pair.getSigner(), false, pair.getCertificate()));
    	
    	for (SignerCertPair pair : orgSenders)
    		senderSignatures.add(new DefaultMessageSignatureImpl(pair.getSigner(), true, pair.getCertificate()));
    	
    	message.setSenderSignatures(senderSignatures);
    }
    
    protected  DefaultMessageSignatureImpl findTrustedSignature(IncomingMessage message, Collection<X509Certificate> anchors)    
    {
    	// implemented for passivity reasons
    	return findTrustedSignature(message, null, anchors);
    }
    
    protected DefaultMessageSignatureImpl findTrustedSignature(IncomingMessage message, InternetAddress recipient, Collection<X509Certificate> anchors)    
    {
    	NHINDAddress sender = message.getSender();
    	
        Collection<DefaultMessageSignatureImpl> signatures = message.getSenderSignatures();
        DefaultMessageSignatureImpl lastTrustedSignature = null;    	
        
        for (DefaultMessageSignatureImpl signature : signatures)
        {
        	// The point of this loop is to find the most trusted signature
        	// to satisfy the most stringent enforcement policy.  Thumb print match policy is the best, so we will 
        	// return if we find a thumb print match... otherwise keep searching until we either find one
        	// of find the best possible match
        	
        	boolean certTrustedAndInPolicy = certChainValidator.isTrusted(signature.getSignerCert(), anchors) && signature.checkSignature();
        	if (certTrustedAndInPolicy && recipient != null)
        	{
        		certTrustedAndInPolicy = this.isCertPolicyCompliant(recipient, signature.getSignerCert());
        	}
        	
        	if (certTrustedAndInPolicy)
        	{
        		if (!sender.hasCertificates())
        			return signature; // Can't really check thumbprints etc. So, this is about as good as its going to get
        		
            	if (signature.checkThumbprint(sender))
            	{
            		return signature;
            	}
            	
                //
                // We'll save this guy, but keep looking for a signer whose thumbprint we can verify
                // If we can't find one, we'll use the last trusted signer we found.. and just mark the recipient's trust
                // enforcement status as Success_ThumbprintMismatch
                //    
            	lastTrustedSignature = signature;        		
        	}
        	
        }
        
        return lastTrustedSignature;
    }
    
    protected boolean isCertPolicyCompliant(InternetAddress recipient, X509Certificate cert)
    {
    	boolean isCompliant = true;
    	// apply the policy if it exists
    	if (this.trustPolicyResolver != null && policyFilter != null)
    	{	
    		// get the incoming public policy based on the sender
    		final Collection<PolicyExpression> expressions = trustPolicyResolver.getIncomingPolicy(recipient); 

    		for (PolicyExpression expression : expressions)
    		{
    			try
    			{
    				// check for compliance
	    			if (!policyFilter.isCompliant(cert, expression))
	    			{
	    				isCompliant = false;
	    				break;
	    			}
    			}
    			catch (PolicyRequiredException requiredException)
    			{
    				isCompliant = false;
    				break;
    			}
    			catch (PolicyProcessException processException)
    			{
    				throw new AgentException(AgentError.InvalidPolicy, processException);
    			}
    		}
    	}
    	
    	return isCompliant;
    }
}
