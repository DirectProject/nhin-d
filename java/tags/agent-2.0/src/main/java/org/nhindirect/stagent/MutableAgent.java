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

import org.nhindirect.policy.PolicyFilter;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cryptography.Cryptographer;
import org.nhindirect.stagent.policy.PolicyResolver;
import org.nhindirect.stagent.trust.TrustAnchorResolver;
import org.nhindirect.stagent.trust.TrustModel;

/**
 * Defines an interface for modifying agent properties.  Care should be taken when implementing this interface to ensure thread safe operation of agent modification.
 * @author Greg Meyer
 * @since 1.3
 */
public interface MutableAgent 
{
	
    /**
     * Gets the list of domains that the agent is serving.
     * @return The domains that the agent is serving.
     */	
	public Collection<String> getDomains();
	
	/**
	 * Sets the list of domain that the agent is serving.
	 * @param domains The list of domain that the agent is serving.
	 */
	public void setDomains(Collection<String> domains);
	
    /**
     * Gets the Cryptographer used by the agent to perform cryptography operations.
     * @return The Cryptographer used by the agent to perform cryptography operations.
     */
	public Cryptographer getCryptographer();
	
	/**
	 * Sets the Cryptographer used by the agent to perform cryptography operations.
	 * @param cryptographer The Cryptographer used by the agent to perform cryptography operations.
	 */
	public void setCryptographer(Cryptographer cryptographer);
	
	/**
     * Gets the certificate stores used to encrypt messages and validate signatures.  This store generally contains only public certificates
     * @return The certificate stores used to encrypt messages and validate signatures.
     */
	public Collection<CertificateResolver> getPublicCertResolvers();
	
	/**
	 * Sets the certificate stores used to encrypt messages and validate signatures.  This store generally contains only public certificates
	 * @param resolvers The certificate stores used to encrypt messages and validate signatures.
	 */
	public void setPublicCertResolvers(Collection<CertificateResolver> resolvers);
	
    /**
     * Gets the certificate store used to decrypt and sign messages.  Certificates in this store must have access to the certifcate's private key.
     * @return The certificate store used to decrypt and sign messages.
     */
	public CertificateResolver getPrivateCertResolver();
	
	/**
	 * Sets the certificate store used to decrypt and sign messages.  Certificates in this store must have access to the certifcate's private key.
	 * @param resolver The certificate store used to decrypt and sign messages.
	 */
	public void setPrivateCertResolver(CertificateResolver resolver);
	
	
    /**
     * Gets the certificate store that contains the certificate anchors that validate if certificates are trusted.
     * @return The certificate store that contains the certificate anchors that validate if certificates are trusted.
     */
    public TrustAnchorResolver getTrustAnchors();
    
    
    /**
     * Sets the certificate store that contains the certificate anchors that validate if certificates are trusted.
     * @param resolver The certificate store that contains the certificate anchors that validate if certificates are trusted.
     */
    public void setTrustAnchorResolver(TrustAnchorResolver resolver);
    
    /**
     * Sets the event listener that will receive notifications at different stages of message processing. 
     * @param listener A concrete implementation of an NHINDAgentEventListener.
     */
    public void setEventListener(NHINDAgentEventListener listener);
    
    /**
     * Sets the event listener that will receive notifications at different stages of message processing. 
     * @return A concrete implementation of an NHINDAgentEventListener.
     */
    public NHINDAgentEventListener getEventListener();
    
    
    /**
     * Sets the auto message wrapping feature of the agent.  Message wrapping takes the original message and wraps it into a message of type RFC822 pushing all headers
     * into the message body.  Only routing information is propagated up from the original message.
     * @param wrappingEnabled True if the agent automatically wraps messages.  False otherwise.
     */
    public void setWrappingEnabled(boolean wrappingEnabled);
    
    /**
     * Indicates if the agent automatically wraps messages into RFC822 envelopes for hiding headers.
     * @return True if the agent automatically wraps messages.
     */
    public boolean isWrappingEnabled();
    
    /**
     * Sets the policy resolver for publicly discovered certificates
     * @param publicPolicyResolver The policy resolver for publicly discovered certificates
     */
    public void setPublicPolicyResolver(PolicyResolver publicPolicyResolver);
    
    /**
     * Gets the policy resolver for publicly discovered certificates
     * @return The policy resolver for publicly discovered certificates
     */
    public PolicyResolver getPublicPolicyResolver();
    
    /**
     * Sets the policy resolvers for privately discovered certificates
     * @param privatePolicyResolver The policy resolvers for privately discovered certificates
     */
    public void setPrivatePolicyResolver(PolicyResolver privatePolicyResolver);
    
    /**
     * Gets the policy resolvers for privately discovered certificates
     * @return The policy resolvers for privately discovered certificates
     */
    public PolicyResolver getPrivatePolicyResolver();
    
    /**
     * Sets the policy filter engine for the agent.
     * @param filter The policy filter engine for the agent.
     */
    public void setPolicyFilter(PolicyFilter filter);
    
    /**
     * Gets the policy filter engine for the agent.
     * @return The policy filter engine for the agent.
     */
    public PolicyFilter getPolicyFilter();
    
    /**
     * Sets the trust model for enforcing message trust
     * @param trustModel The trust model for enforcing message trust
     */
    public void setTrustModel(TrustModel trustModel);
    
    /**
     * Gets the trust model for enforcing message trust
     * @return The trust model for enforcing message trust
     */
    public TrustModel getTrustModel();
}
