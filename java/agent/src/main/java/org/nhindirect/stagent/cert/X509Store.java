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

package org.nhindirect.stagent.cert;

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;

import com.google.inject.ImplementedBy;

/**
 * Responsible for maintaining and managing a certificate repository.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
@ImplementedBy(KeyStoreCertificateStore.class)
public interface X509Store 
{
	/**
	 * Gets a collection of certificates where the cert's E or CN field match the subject name.
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
    public Collection<X509Certificate> getAllCertificates();    
}
