package org.nhindirect.stagent.cert;

import java.security.cert.X509Certificate;

import javax.mail.internet.InternetAddress;

/**
 * Certificate store implementations are responsible for retrieving public X509Certificates from a 
 * certificate repository.  Repositories may include a simple keystore file, a machine cert store,
 * a URI, or a DNS cert implementation.    
 * @author Greg Meyer
 * @author Umesh Madan
 */
public interface ICertificateStore 
{
	/**
	 * Retrieves a certificate for a given InternetAddress.
	 * @param address  The InternetAddress used to lookup the certificate.
	 * @return An X509Certificate containing the address in its E or CN field.
	 */
	public X509Certificate getCertificate(InternetAddress address);
}
