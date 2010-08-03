package org.nhindirect.stagent.cert;

import java.security.cert.X509Certificate;
import java.util.Collection;

/**
 * Responsible for maintaining and managing a certificate repository.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public interface IX509Store 
{
	/**
	 * Gets a certificate where the cert's E or CN field match the subject name.
	 * @param subjectName The subject name to search for.
	 * @return A collection of certificates matching the subject name. 
	 */
    public Collection<X509Certificate> getCertificates(String subjectName);
    
    /**
     * Determines if a certificate exists in the certificate store.  Although not specific in the interface
     * definition, certificate thumbprinting is recommended for certificate searching.
     * @param cert The certificate to search for.
     * @return True if the certificate exist in the store.  False otherwise.
     */
    public boolean contains(X509Certificate cert);        
        
    /**
     * Adds a certificate to the store.
     * @param cert The certificate to add to the store.
     */
    public void add(X509Certificate cert);
    
    /**
     * Adds a collection of certificates to the store.
     * @param certs The certificates to add to the store.
     */
    public void add(Collection<X509Certificate> certs);
    
    /**
     * Removes a certificate from the store.
     * @param cert The certificate to remove from the store.
     */
    public void remove(X509Certificate cert);
    
    /**
     * Removes a collection certificates from the store.
     * @param certs The certificates to remove from the store.
     */
    public void remove(Collection<X509Certificate> certs);

    /**
     * Removes certificates from the store matching the subject name.
     * @param subjectName The subject name of the certificates to remove.
     */
    public void remove(String subjectName);

    /**
     * Updates an existing certificate in the store with a new representation of the certificate.
     * @param cert Updates an existing certificate in the store.
     */
    public void update(X509Certificate cert);
    
    /**
     * Updates a collection of existing certificate in the store with a new representations of the certificates.
     * @param cert Updates a collection of existing certificates in the store.
     */
    public void update( Collection<X509Certificate> certs);
    
    /**
     * Gets all certificates in the store.
     * @return A collection of certificates in the store.
     */
    public Collection<X509Certificate> getCertificates();    
}
