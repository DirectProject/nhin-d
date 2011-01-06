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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.nhindirect.stagent.cert.SignerCertPair;
import org.nhindirect.stagent.cert.Thumbprint;

/**
 * Utility functions for searching for certificates.
 * @author Greg Meyer
 * @author Umesh Madan
 */
@SuppressWarnings("unchecked")
public class CryptoExtensions 
{
	private static CertificateFactory certFactory;
	
	static 
	{
		try
		{		
			certFactory = CertificateFactory.getInstance("X.509");
		}
		catch (CertificateException ex)
		{
			/*
			 * TODO: Handle Exception
			 */
		}
	}
	
	/**
	 * Compares the {@link Thumbprint thumbprints} of two certificates for equality.
	 * @param cert1 The first certificate to compare.
	 * @param cert2 The second certificate to compare.
	 * @return True if the certificates' thumbprints are equal.  False other wise.
	 */
	public static boolean isEqualThumbprint(X509Certificate cert1, X509Certificate cert2)
	{
		
		return Thumbprint.toThumbprint(cert1).equals(Thumbprint.toThumbprint(cert2));
	}
	
	/**
	 * Checks if a name is contained in a certificate's distinguished name. 
	 * @param cert The certificate to check.
	 * @param name The name to search for in the certificate's distinguished name.
	 * @return True if the name is found in the certificates distinguished name.  False otherwise.
	 */
    public static boolean certSubjectContainsName(X509Certificate cert, String name)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException();
        }
        return cert.getSubjectDN().getName().contains(name);
    }	
	
    /**
     * Matches a common name in a certificate.
     * @param cert The certificate to check for the common name.
     * @param name The common name to check for.  This method automatically prefixes the name with "CN="
     * @return True if the common name is contained in the certificate.  False otherwise.
     */
    public static boolean matchName(X509Certificate cert, String name)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException();
        }

        String distinguishedName = "CN=" + name;
        return cert.getSubjectDN().getName().contains(distinguishedName);
    }

	/**
	 * Searches CMS signed data for a given email name.  Signed data may consist of multiple signatures either from the same subject of from multiple
	 * subjects. 
	 * @param signedData The signed data to search.
	 * @param name The name to search for in the list of signers.
	 * @param excludeNames A list of names to exclude from the list.  Because the search uses a simple "contains" search, it is possible for the name parameter
	 * to be a substring of what is requested.  The excludeNames contains a super string of the name to remove unwanted names from the returned list.  This parameter
	 * may be null;
	 * @return A colllection of pairs consisting of the singer's X509 certificated and signer information that matches the provided name.  Returns
	 * an empty collection if a signer matching the name cannot be found in the signed data.
	 */
    public static Collection<SignerCertPair> findSignersByName(CMSSignedData signedData, String name, Collection<String> excludeNames)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException();
        }

        Collection retVal = null;
        
        try
        {
	        CertStore certs = signedData.getCertificatesAndCRLs("Collection", "BC");
	        SignerInformationStore  signers = signedData.getSignerInfos();
	        Collection<SignerInformation> c = signers.getSigners();
	        
	        for (SignerInformation signer : c)
	        {
	            Collection<? extends Certificate> certCollection = certs.getCertificates(signer.getSID());
	            if (certCollection != null && certCollection.size() > 0)
	            {
	            
	            	X509Certificate cert = (X509Certificate)certCollection.iterator().next();
	            	if (certSubjectContainsName(cert, name))
	            	{
	            		boolean exclude = false;
	            		
	            		// check if we need to exclude anything
	            		if (excludeNames != null)
	            			for (String excludeStr : excludeNames)
	            				if (certSubjectContainsName(cert, excludeStr))
	            				{
	            					exclude = true;
	            					break;
	            				}
	            			
	            		if (exclude)
	            			continue; // break out and don't include this cert
	            		
	            		if (retVal == null)
	            			retVal = new ArrayList<SignerCertPair>();	            		
	            		
	            		retVal.add(new SignerCertPair(signer, convertToProfileProvidedCertImpl(cert))); 
	            	}
	            } 
	        }
        }
        catch (Throwable e)
        {
        	
        }
        return retVal == null ? Collections.emptyList() : retVal;
    }

    /**
     * Searches a collection of X509Certificates for a certificate that matches the provided name.
     * @param certs The collection of certificates to search.
     * @param name The name to search for in the collection.
     * @return A certificate that matches the provided name.  Returns null if a matching certificate cannot be found in the collection.
     */
    public static X509Certificate findCertByName(Collection<X509Certificate> certs, String name)
    {
    	for (X509Certificate cert : certs)
    	{
    		if (certSubjectContainsName(cert, name))
    			return cert;
    	}
    	
    	return null;
    }
    
	/**
	 * Searches CMS signed data for a specific X509 certificate.
	 * @param signedData The signed data to search.
	 * @param name The certificate to search for in the signed data.
	 * @return A pair consisting of the singer's X509 certificated and signer information that matches the provided certificate.  Returns
	 * null if a signer matching the name cannot be found in the signed data.
	 */
    public static SignerCertPair findSignerByCert(CMSSignedData signedData, X509Certificate searchCert)
    {

    	if (searchCert == null)
        {
            throw new IllegalArgumentException();
        }

        try
        {	                	
        	SignerInformationStore  signers = signedData.getSignerInfos();
	        Collection<SignerInformation> c = signers.getSigners();
	        
	        for (SignerInformation signer : c)
	        {
	        	//signer.getSID().
	        	
	        	SignerId signerId = signer.getSID();

	        	if (signerId.getIssuer().equals(searchCert.getIssuerX500Principal()) && 
	        			signerId.getSerialNumber().equals(searchCert.getSerialNumber()))
	        	{
	        		return new SignerCertPair(signer, searchCert); 
	        	}	            			            	
	        }
        }
        catch (Exception e){}
        return null;
    }
    
	/*
	 * The certificate provider implementation may not be incomplete or may not provide all the necessary functionality such as 
	 * certificate verification.  This will convert the certificate into a cert backed by the default installed X509 certificate
	 * provider. 
	 */
    private static X509Certificate convertToProfileProvidedCertImpl(X509Certificate certToConvert)
    {
    	X509Certificate retVal = null;
    	
    	try
    	{
    		InputStream stream = new BufferedInputStream(new ByteArrayInputStream(certToConvert.getEncoded()));
    	
    		retVal = (X509Certificate)certFactory.generateCertificate(stream);
    	
    		stream.close();
    	}
    	catch (Exception e)
    	{
    		/*
    		 * TODO: handle exception
    		 */
    	}
    	
    	return retVal;
    }
}
