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

import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.Thumbprint;
import org.nhindirect.stagent.cert.impl.CRLRevocationManager;

/**
 * Validates the trust chain of a certificate with a set of anchors.  If a certificate resolver is present, the validator will search
 * for intermediate certificates.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class TrustChainValidator 
{
	private static final int RFC822Name_TYPE = 1; // name type constant for Subject Alternative name email address
	private static final int DNSName_TYPE = 2; // name type constant for Subject Alternative name domain name	
	
	private static int DefaultMaxIssuerChainLength = 5;

	private Collection<CertificateResolver> certResolvers;
	
	private int maxIssuerChainLength = DefaultMaxIssuerChainLength;
	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(TrustChainValidator.class);
	
	static
	{
		// use OCSP when available
		Security.setProperty("ocsp.enable", "true");
	}
	
	/**
	 * Indicates if the TrustChainValidator has a certificate resolvers for resolving intermediates certificates.
	 * @return True is an intermediate certificate resolver is present.  False otherwise.
	 */
	public boolean isCertificateResolver()
	{
		return certResolvers != null;
	}
	
	/**
	 * Gets the intermediate certificate resolvers.  This is generally a resolver capable of resolving public certificates.
	 * @return The intermediate certificate resolvers.
	 */
	public Collection<CertificateResolver> getCertificateResolver()
	{
		return certResolvers;
	}
	
	/**
	 * Sets the intermediate certificate resolvers.  This is generally a resolver capable of resolving public certificates.
	 * @param resolver the intermediate certificate resolver.
	 */
	public void setCertificateResolver(Collection<CertificateResolver> resolver)
	{
		certResolvers = resolver;
	}
	
	/**
	 * Indicates if a certificate is considered to be trusted by resolving a valid certificate trust chain with the provided anchors.
	 * @param certificate The certificate to check.
	 * @param anchors A list of trust anchors used to check the trust chain.
	 * @return Returns true if the certificate can find a valid trust chain in the collection of anchors.  False otherwise.
	 */
    public boolean isTrusted(X509Certificate certificate, Collection<X509Certificate> anchors)
    {    	
    	if (certificate == null)
    		throw new IllegalArgumentException();
    	
    	if (anchors == null || anchors.size() == 0)
    		return false; // no anchors... conspiracy theory?  trust no one    
    	
    	try
    	{
        	// check if the certificate is in the list of anchors... this is a valid trust model
    		if (isIssuerInAnchors(anchors, certificate))
    			return true;
    		
    		
    		CertPath certPath = null;
        	CertificateFactory factory = CertificateFactory.getInstance("X509");
        	
        	List<Certificate> certs = new ArrayList<Certificate>();
        	certs.add(certificate);
        	
        	// check for intermediates
        	if (certResolvers != null)
        	{
        		Collection<X509Certificate> intermediatesCerts = resolveIntermediateIssuers(certificate, anchors);
        		if (intermediatesCerts != null && intermediatesCerts.size() > 0)
        			certs.addAll(intermediatesCerts);
        	}
        	
        	Set<TrustAnchor> trustAnchorSet = new HashSet<TrustAnchor>();
        		
        	for (X509Certificate archor : anchors)
        		trustAnchorSet.add(new TrustAnchor(archor, null));
        	
            PKIXParameters params = new PKIXParameters(trustAnchorSet); 
            
        	// JCE will only allow OSCP checking when revocation checking is enabled
        	// however some implementations will fail if revocation checking is turned on, but the CRL
        	// extension does not exist. for compatibility reasons, only turn this on if CRL extension points are defined
	        params.setRevocationEnabled(CRLRevocationManager.isCRLDispPointDefined(certificate));
            
        	certPath = factory.generateCertPath(certs);
        	CertPathValidator pathValidator = CertPathValidator.getInstance("PKIX", CryptoExtensions.getJCEProviderName());    		
    		

        	pathValidator.validate(certPath, params);
    		return true;
    	}
    	catch (Exception e)
    	{
    		LOGGER.warn("Certificate " + certificate.getSubjectX500Principal().getName() + " is not trusted.", e);
    	}
    	
    	return false;    	
    }     	
    
    private Collection<X509Certificate> resolveIntermediateIssuers(X509Certificate certificate, Collection<X509Certificate> anchors)
    {
    	Collection<X509Certificate> issuers = new ArrayList<X509Certificate>();
        resolveIntermediateIssuers(certificate, issuers, anchors);
        return issuers;
    }   
    
    private void resolveIntermediateIssuers(X509Certificate certificate, /*in-out*/Collection<X509Certificate> issuers, Collection<X509Certificate> anchors)
    {
        if (certificate == null)
        {
            throw new IllegalArgumentException("Certificate cannot be null.");
        }
        if (issuers == null)
        {
        	throw new IllegalArgumentException("Issuers collection cannot be null.");
        }
        
        resolveIssuers(certificate, issuers, 0, anchors);
    }       
    
    private boolean isIssuerInCollection(Collection<X509Certificate> issuers, X509Certificate checkIssuer)
    {
    	for (X509Certificate issuer : issuers)
    	{
    		if (checkIssuer.getSubjectX500Principal().equals(issuer.getSubjectX500Principal()) 
    				&& Thumbprint.toThumbprint(issuer).equals(Thumbprint.toThumbprint(checkIssuer)))
    			return true; // already found the certificate issuer... done
    	}
    	return false;
    }
    
    private boolean isIssuerInAnchors(Collection<X509Certificate> anchors, X509Certificate checkIssuer)
    {
    	for (X509Certificate anchor : anchors)
    	{
    		if (Thumbprint.toThumbprint(anchor).equals(Thumbprint.toThumbprint(checkIssuer)))
    			return true; // already found the certificate issuer... done
    	}
    	return false;
    }
    
    private void resolveIssuers(X509Certificate certificate, /*in-out*/Collection<X509Certificate> issuers, int chainLength, Collection<X509Certificate> anchors)
    {
    	
    	X500Principal issuerPrin = certificate.getIssuerX500Principal();
    	if (issuerPrin.equals(certificate.getSubjectX500Principal()))
    	{
    		// I am my own issuer... self signed cert
    		// no intermediate between me, myself, and I
    		return;
    	}
    	
    	// look in the issuer list and see if the certificate issuer already exists in the list
    	for (X509Certificate issuer : issuers)
    	{
    		if (issuerPrin.equals(issuer.getSubjectX500Principal()))
    			return; // already found the certificate issuer... done
    	}
    	
    	if (chainLength >= maxIssuerChainLength)
    	{
    		// can't go any further than the max number of links in the chain.
    		// bail out with what we have now
    		return;
    	}
    	
    	String address = this.getIssuerAddress(certificate);

    	if (address == null || address.isEmpty())
    		return;// not much we can do about this... the resolver interface only knows how to work with addresses
    	
		Collection<X509Certificate> issuerCerts = new ArrayList<X509Certificate>();
		
		// look in each resolver...  the list could be blasted across 
		// multiple resolvers
    	for (CertificateResolver publicResolver : certResolvers)
    	{
    		try
    		{	
    			Collection<X509Certificate> holdCerts = publicResolver.getCertificates(new InternetAddress(address));
    			if (holdCerts != null && holdCerts.size() > 0)
    				issuerCerts.addAll(holdCerts);
    		}
    		catch (AddressException e)
    		{
    			// no-op
    		}
        }

		
		if (issuerCerts.size() == 0)
			return; // no intermediates.. just return
		
		for (X509Certificate issuerCert : issuerCerts)
		{
			if (issuerCert.getSubjectX500Principal().equals(issuerPrin) && !isIssuerInCollection(issuers, issuerCert)
					&& !isIssuerInAnchors(anchors, issuerCert) /* if we hit an anchor then stop */)
			{
				issuers.add(issuerCert);
				
				// see if this issuer also has intermediate certs
				resolveIssuers(issuerCert, issuers, chainLength + 1, anchors);
			}
		}
    }
    
    
    private String getIssuerAddress(X509Certificate certificate)
    {
    	String address = "";
    	// check alternative names first
    	Collection<List<?>> altNames = null;
    	try
    	{    		
    		altNames = certificate.getIssuerAlternativeNames();
    	}
    	catch (CertificateParsingException ex)
    	{
    		/* no -op */
    	}	
		
    	if (altNames != null)
		{
    		for (List<?> entries : altNames)
    		{
    			if (entries.size() >= 2) // should always be the case according the altNames spec, but checking to be defensive
    			{
    				
    				Integer nameType = (Integer)entries.get(0);
    				// prefer email over over domain?
    				if (nameType == RFC822Name_TYPE)    					
    					address = (String)entries.get(1);
    				else if (nameType == DNSName_TYPE && address.isEmpty())
    					address = (String)entries.get(1);    				
    			}
    		}
		}
    	
    	if (!address.isEmpty())
    		return address;
    	
    	// can't find issuer address in alt names... try the principal 
    	X500Principal issuerPrin = certificate.getIssuerX500Principal();
    	
    	// get the domain name
		Map<String, String> oidMap = new HashMap<String, String>();
		oidMap.put("1.2.840.113549.1.9.1", "EMAILADDRESS");  // OID for email address
		String prinName = issuerPrin.getName(X500Principal.RFC1779, oidMap);    
		
		// see if there is an email address first in the DN
		String searchString = "EMAILADDRESS=";
		int index = prinName.indexOf(searchString);
		if (index == -1)
		{
			searchString = "CN=";
			// no Email.. check the CN
			index = prinName.indexOf(searchString);
			if (index == -1)
				return ""; // no CN... nothing else that can be done from here
		}
		
		// look for a "," to find the end of this attribute
		int endIndex = prinName.indexOf(",", index);
		if (endIndex > -1)
			address = prinName.substring(index + searchString.length(), endIndex);
		else 
			address= prinName.substring(index + searchString.length());
		
		return address;
    }
}
