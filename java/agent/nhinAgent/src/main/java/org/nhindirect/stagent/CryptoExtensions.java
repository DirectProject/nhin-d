package org.nhindirect.stagent;

import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.nhindirect.stagent.cert.SingnerCertPair;
import org.nhindirect.stagent.cert.Thumbprint;

/**
 * Utility functions for searching for certificates.
 * @author Greg Meyer
 * @author Umesh Madan
 */
@SuppressWarnings("unchecked")
public class CryptoExtensions 
{
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
	 * Searches CMS signed data for a given email name. 
	 * @param signedData The signed data to search.
	 * @param name The name to search for in the list of signers.
	 * @return A pair consisting of the singer's X509 certificated and signer information that matches the provided name.  Returns
	 * null if a signer matching the name cannot be found in the signed data.
	 */
    public static SingnerCertPair findSignerByName(CMSSignedData signedData, String name)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException();
        }

        try
        {
	        CertStore               certs = signedData.getCertificatesAndCRLs("Collection", "BC");
	        SignerInformationStore  signers = signedData.getSignerInfos();
	        Collection<SignerInformation> c = signers.getSigners();
	        
	        for (SignerInformation signer : c)
	        {
	            Collection<? extends Certificate> certCollection = certs.getCertificates(signer.getSID());
	            if (certCollection != null && certCollection.size() > 0)
	            {
	            
	            	X509Certificate cert = (X509Certificate)certCollection.iterator().next();
	            	if (certSubjectContainsName(cert, name))
	            		return new SingnerCertPair(signer, cert); 
	            	
	            } 
	        }
        }
        catch (Throwable e)
        {
        	
        }
        return null;
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
    public static SingnerCertPair findSignerByCert(CMSSignedData signedData, X509Certificate searchCert)
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
	        		return new SingnerCertPair(signer, searchCert); 
	        	}	            			            	
	        }
        }
        catch (Exception e){}
        return null;
    }
    

}
