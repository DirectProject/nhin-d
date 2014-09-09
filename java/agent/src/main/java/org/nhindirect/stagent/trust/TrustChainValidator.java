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

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERObject;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.x509.AuthorityInfoAccessExtentionField;
import org.nhindirect.policy.x509.AuthorityInfoAccessMethodIdentifier;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.Thumbprint;

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
	
	private static final String CA_ISSUER_CHECK_STRING = AuthorityInfoAccessMethodIdentifier.CA_ISSUERS.getName() + ":";
	
	protected static final int DEFAULT_URL_CONNECTION_TIMEOUT = 10000; // 10 seconds	
	
	protected static final int DEFAULT_URL_READ_TIMEOUT = 10000; // 10 hour seconds	
	
	private static int DefaultMaxIssuerChainLength = 5;

	private Collection<CertificateResolver> certResolvers = Collections.emptyList();
	
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
            
        			
        	/*
        	 *  Disable CRL checking in cert path validation for now until a better implementation is put together
        	 */
        	params.setRevocationEnabled(false);
        	// JCE will only allow OSCP checking when revocation checking is enabled
        	// however some implementations will fail if revocation checking is turned on, but the CRL
        	// extension does not exist. for compatibility reasons, only turn this on if CRL extension points are defined
	        /*
        	params.setRevocationEnabled(CRLRevocationManager.isCRLDispPointDefined(certificate));
	        {
	        	// populate the CRL store from the revocation manager
	        	CRLRevocationManager mgr = CRLRevocationManager.getInstance();
	        	Set<CRL> crls = mgr.getCRLCollection();
	        	
	        	CertStore crlStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(crls), CryptoExtensions.getJCEProviderName()); 
	        	params.addCertStore(crlStore);
	        }
            */
        	certPath = factory.generateCertPath(certs);
        	CertPathValidator pathValidator = CertPathValidator.getInstance("PKIX", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("CertPathValidator", "PKIX"));    		
    		

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
    	final DERObject checkIssuerExValue = getExtensionValue(checkIssuer, "2.5.29.14");
    	
    	for (X509Certificate anchor : anchors)
    	{
    		if (Thumbprint.toThumbprint(anchor).equals(Thumbprint.toThumbprint(checkIssuer)))
    			return true; // already found the certificate issuer... done
    		
    		// thumbprint may not be enough... it is possible that there might be change of anchors but they keep the same subject key id
    		final DERObject anchorExValue = getExtensionValue(anchor, "2.5.29.14");
    		
    		if (checkIssuerExValue != null && anchorExValue != null && anchorExValue.equals(checkIssuerExValue))
    			return true;
    		
    	}
    	return false;
    }
    
    protected void resolveIssuers(X509Certificate certificate, /*in-out*/Collection<X509Certificate> issuers, int chainLength, Collection<X509Certificate> anchors)
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
		
    	// first check to see there is an AIA extension with one ore more caIssuer entries and attempt to resolve the
		// intermediate via the URL
		final Collection<X509Certificate> issuerCerts = getIntermediateCertsByAIA(certificate);
		
		// if we could not find intermediate certs by the AIA extension, then fall back to the old method
		// of using resolvers
		if (issuerCerts.isEmpty())
		{	
	    	final String address = this.getIssuerAddress(certificate);
	
	    	if (address == null || address.isEmpty())
	    		return;// not much we can do about this... the resolver interface only knows how to work with addresses
	    		
			// look in each resolver...  the list could be blasted across 
			// multiple resolvers
	    	for (CertificateResolver publicResolver : certResolvers)
	    	{
		
				Collection<X509Certificate> holdCerts = null;
				try
				{
					holdCerts = publicResolver.getCertificates(new InternetAddress(address));
				}
	    		catch (AddressException e)
	    		{
	    			continue;
	    		}
				catch (Exception e)
				{
					/* no-op*/
				}
				if (holdCerts != null && holdCerts.size() > 0)
					issuerCerts.addAll(holdCerts);
	
	        }
		}
		
		if (issuerCerts.size() == 0)
			return; // no intermediates.. just return
		
		boolean issuerFoundInAnchors = false;
		Collection<X509Certificate> searchForParentIssuers  = new ArrayList<X509Certificate>();
		for (X509Certificate issuerCert : issuerCerts)
		{
			if (issuerCert.getSubjectX500Principal().equals(issuerPrin) && !isIssuerInCollection(issuers, issuerCert)
					&& !isIssuerInAnchors(anchors, issuerCert) /* if we hit an anchor then stop */)
			{
				searchForParentIssuers.add(issuerCert);

			}
			else if (isIssuerInAnchors(anchors, issuerCert))
			{
				issuerFoundInAnchors = true;
				break;
			}
		}
		// if the issuer was not found in the list of anchors,
		// the go up the next level in the chain
		if (!issuerFoundInAnchors)
		{
			for (X509Certificate issuerCert : searchForParentIssuers)
			{
				issuers.add(issuerCert);
				
				// see if this issuer also has intermediate certs
				resolveIssuers(issuerCert, issuers, chainLength + 1, anchors);
			}
		}
    }
    
    /**
     * Retrieves intermediate certificate using the AIA extension.
     * @param certificate The certificate to search for AIA extensions.
     * @return Returns a collection of intermediate certs using the AIA extension.  If the AIA extension does not exists
     * or the certificate cannot be downloaded from the URL, then an empty list is returned.
     */
    protected Collection<X509Certificate> getIntermediateCertsByAIA(X509Certificate certificate)
    {
    	final Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
    
    	// check to see if there are extensions
    	final AuthorityInfoAccessExtentionField aiaField = new AuthorityInfoAccessExtentionField(false);
    	
    	try
    	{
    		// we can get all names from the AuthorityInfoAccessExtentionField objects
    		aiaField.injectReferenceValue(certificate);
    		
    		final Collection<String> urlPairs = aiaField.getPolicyValue().getPolicyValue();
    		
    		// look through all of the values (if they exist) for caIssuers
    		for (String urlPair : urlPairs)
    		{
    			if (urlPair.startsWith(CA_ISSUER_CHECK_STRING))
    			{
    				// the url pair is in the format of caIssuer:URL... need to break it 
    				// apart to get the url
    				final String url = urlPair.substring(CA_ISSUER_CHECK_STRING.length());
    				
    				// now pull the certificate from the URL
    				try
    				{
    					final Collection<X509Certificate> intermCerts = downloadCertsFromAIA(url);
    					retVal.addAll(intermCerts);
    				}
    				catch (NHINDException e)
    				{
    					LOGGER.warn("Intermediate cert cannot be resolved from AIA extension.", e);
    				}
    			}
    		}
    	}
    	///CLOVER:OFF
    	catch (PolicyProcessException e)
    	{
    		LOGGER.warn("Intermediate cert cannot be resolved from AIA extension.", e);
    	}
    	///CLOVER:ON
    	
    	return retVal;
    }
    
	/**
	 * Downloads a cert from the AIA URL and returns the result as certificate.
	 * <br>
	 * AIA extensions may refer to collection files such as P7b or P7c.  For this reason, this method
	 * has been deprecated.
	 * @param url The URL of the certificate that will be downloaded.
	 * @return The certificate downloaded from the AIA extension URL
	 * @deprecated As of 2.1, replaced by {@link #downloadCertsFromAIA(String)}
	 */
	protected X509Certificate downloadCertFromAIA(String url) throws NHINDException
	{
		InputStream inputStream = null;

		X509Certificate retVal = null;
		
		try
		{
			// in this case the cert is a binary representation
			// of the CERT URL... transform to a string
			final URL certURL = new URL(url);
			
			final URLConnection connection = certURL.openConnection();
			
			// the connection is not actually made until the input stream
			// is open, so set the timeouts before getting the stream
			connection.setConnectTimeout(DEFAULT_URL_CONNECTION_TIMEOUT);
			connection.setReadTimeout(DEFAULT_URL_READ_TIMEOUT);
			
			// open the URL as in input stream
			inputStream = connection.getInputStream();
			
			
			retVal = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(inputStream);
		}
		catch (Exception e)
		{
			throw new NHINDException("Failed to download certificate from AIA extension.", e);
		}
		finally
		{
			IOUtils.closeQuietly(inputStream);
		}
		
		return retVal;
	}
    
	/**
	 * Downloads certificates from the AIA URL and returns the result as a collection of certificates.
	 * @param url The URL listed in the AIA extension to locate the certificates.
	 * @return The certificates downloaded from the AIA extension URL
	 */
	@SuppressWarnings("unchecked")
	protected Collection<X509Certificate> downloadCertsFromAIA(String url) throws NHINDException
	{
		InputStream inputStream = null;

		Collection<? extends Certificate> retVal = null;
		
		try
		{
			// in this case the cert is a binary representation
			// of the CERT URL... transform to a string
			final URL certURL = new URL(url);
			
			final URLConnection connection = certURL.openConnection();
			
			// the connection is not actually made until the input stream
			// is open, so set the timeouts before getting the stream
			connection.setConnectTimeout(DEFAULT_URL_CONNECTION_TIMEOUT);
			connection.setReadTimeout(DEFAULT_URL_READ_TIMEOUT);
			
			// open the URL as in input stream
			inputStream = connection.getInputStream();
			
			// download the 
			retVal = CertificateFactory.getInstance("X.509").generateCertificates(inputStream);
		}
		catch (Exception e)
		{
			throw new NHINDException("Failed to download certificates from AIA extension.", e);
		}
		finally
		{
			IOUtils.closeQuietly(inputStream);
		}
		
		return (Collection<X509Certificate>)retVal;
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
    
    
    private DERObject getExtensionValue(X509Certificate cert, String oid)
    {	
        byte[]  bytes = cert.getExtensionValue(oid);
        if (bytes == null)
        {
            return null;
        }

        return getObject(bytes);
    }
    
    private DERObject getObject(byte[] ext)
    {
    	ASN1InputStream aIn = null;
        try
        {
            aIn = new ASN1InputStream(ext);
            ASN1OctetString octs = (ASN1OctetString)aIn.readObject();
        	IOUtils.closeQuietly(aIn);
            
            aIn = new ASN1InputStream(octs.getOctets());
            return aIn.readObject();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Exception processing data ", e);
        }
        finally
        {
        	IOUtils.closeQuietly(aIn);
        }
    }	
}
