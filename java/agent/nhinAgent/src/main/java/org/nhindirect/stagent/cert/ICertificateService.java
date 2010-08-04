package org.nhindirect.stagent.cert;

import javax.mail.internet.InternetAddress;

/**
 * Certificate service implementations extend the ICertificateStore interface to include retrieving private X509Certificates from a 
 * certificate repository.  Private certificates include the certificates private key used for message signing and decryption.    
 * @author Greg Meyer
 * @author Umesh Madan
 */
public interface ICertificateService extends ICertificateStore 
{
	/**
	 * Retrieves a certificate for a given InternetAddress.
	 * @param address  The InternetAddress used to lookup the certificate.
	 * @return An X509Certificate containing the address in its E or CN field.
	 */	
	public X509CertificateEx getPrivateCertificate(InternetAddress subjectName);
}
