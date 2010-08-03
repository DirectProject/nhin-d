package org.nhindirect.stagent.cert;

import java.security.cert.X509Certificate;

import org.bouncycastle.cms.SignerInformation;

/**
 * A pair object containing signer information and the certificate used in the singnature.
 * @author Greg Meyer
 *
 */
public class SingnerCertPair 
{
	private final SignerInformation signer;
	private final X509Certificate cert;
	
	/**
	 * Construct a pair with the signer information the certificate.
	 * @param _signer Infomorationg about the the signer of a message.
	 * @param _cert The certificate used to sign a message.
	 */
	public SingnerCertPair(SignerInformation _signer, X509Certificate _cert)
	{
		if (_signer == null || _cert == null)
			throw new IllegalArgumentException();
		
		signer = _signer;
		cert = _cert;
	}
	
	/**
	 * Gets the signer information.
	 * @return The signer information.
	 */
	public SignerInformation getSigner()
	{
		return signer;
	}
	
	/**
	 * Gets the certificate used to sign a message.
	 * @return The certificate used to sign a message.
	 */
	public X509Certificate getCertificate()
	{
		return cert;
	}
}
