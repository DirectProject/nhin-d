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
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

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
	private static final String DEFAULT_JCE_PROVIDER_STRING = "BC";
	private static final String JCE_PROVIDER_STRING_SYS_PARAM = "org.nhindirect.stagent.cryptography.JCEProviderName";
	
	private static final int RFC822Name_TYPE = 1; // name type constant for Subject Alternative name email address
	private static final int DNSName_TYPE = 2; // name type constant for Subject Alternative name domain name	
	
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
	 * Gets the configured JCE crypto provider string for crypto operations.  This is configured using the
	 * -Dorg.nhindirect.stagent.cryptography.JCEProviderName JVM parameters.  If the parameter is not set or is empty,
	 * then the default string "BC" (BouncyCastle provider) is returned.  By default the agent installs the BouncyCastle provider.
	 * @return The name of the JCE provider string.
	 */
	public static String getJCEProviderName()
	{
		String retVal = System.getProperty(JCE_PROVIDER_STRING_SYS_PARAM);
		
		if (retVal == null || retVal.isEmpty())
			retVal = DEFAULT_JCE_PROVIDER_STRING;
		
		return retVal;
	}
	
	/**
	 * Overrides the configured JCE crypto provider string.  If the name is empty or null, the default string "BC" (BouncyCastle provider)
	 * is used.
	 * @param name The name of the JCE provider.
	 */
	public static void setJCEProviderName(String name)
	{
		if (name == null || name.isEmpty())
			System.setProperty(JCE_PROVIDER_STRING_SYS_PARAM, DEFAULT_JCE_PROVIDER_STRING);
		else
			System.setProperty(JCE_PROVIDER_STRING_SYS_PARAM, name);
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
     * Checks if the subject is contained in the certificates alternate subject names.  Specifically 
     * the rfc822Name name and DNSName types are checked.
     * @param cert The certificate to check.
     * @param subjectName The subject name to check in the alternate names.
     * @return True if the subjectName is contained in the alternate subject names.  False otherwise.
     */
    public static boolean containsEmailAddressInSubjectAltName(X509Certificate cert, String subjectName)
    {
        boolean searchingForEmailAddress = subjectName.toLowerCase(Locale.getDefault()).startsWith("emailaddress=");
        subjectName = searchingForEmailAddress ? subjectName.toLowerCase().replaceFirst("^emailaddress=", "") : subjectName;    	
    	
    	Collection<List<?>> altNames = null;
    	try
    	{
    		altNames = cert.getSubjectAlternativeNames();
    	}
    	catch (CertificateParsingException ex)
    	{
    		return false;
    	}	
		
    	if (altNames != null)
		{
    		for (List<?> entries : altNames)
    		{
    			if (entries.size() >= 2) // should always be the case according the altNames spec, but checking to be defensive
    			{
    				
    				Integer nameType = (Integer)entries.get(0);
    				if (nameType == RFC822Name_TYPE || nameType == DNSName_TYPE)
    				{
    					String name = (String)entries.get(1);
    					if (name.toLowerCase(Locale.getDefault()).equals(subjectName.toLowerCase()))
    						return true;
    				}
    				
    			}
    		}
		}
    	
    	return false;
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
                
        return cert.getSubjectDN().getName().toUpperCase(Locale.getDefault()).contains(name.toUpperCase(Locale.getDefault()));
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
        return cert.getSubjectDN().getName().toUpperCase(Locale.getDefault()).contains(distinguishedName.toUpperCase(Locale.getDefault()));
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
	        CertStore certs = signedData.getCertificatesAndCRLs("Collection", CryptoExtensions.getJCEProviderName());
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
    
    /**
     * Gets the address name associated with the certificate.  It may be an email address or a domain name.
     * @param certificate The certificate to search
     * @return The address of domain associated with a certificate.
     */
    public static String getSubjectAddress(X509Certificate certificate)
    {
    	String address = "";
    	// check alternative names first
    	Collection<List<?>> altNames = null;
    	try
    	{    		
    		altNames = certificate.getSubjectAlternativeNames();
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
    	X500Principal issuerPrin = certificate.getSubjectX500Principal();
    	
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
