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

import java.security.cert.X509Certificate;

import org.bouncycastle.cms.SignerInformation;
import org.nhindirect.stagent.cert.Thumbprint;

/**
 * Contains information specific to a discrete signer of a message.  Includes the singer information and the certificate used to sign the message (optimally
 * extracted from the signature).  This is a subset of the CMS signed data.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class DefaultMessageSignatureImpl implements MessageSignature
{
	private boolean signatureValid;
	private SignerInformation signer;
	private boolean useOrgCertificate;
	private boolean thumbprintVerified;
	private X509Certificate signerCert;
	
	/**
	 * Constructs a message signature from the singer info and the certificate used to sign the message.
	 * @param signer Information about the individual signature such as the signers id and algorithms used to sign.
	 * @param useOrgCert Indicates if the certificate used is a org level or individual level certificate
	 * @param cert The public certificate used to sign the message for this signer.
	 */
	public DefaultMessageSignatureImpl(SignerInformation signer, boolean useOrgCert, X509Certificate cert)
	{
		if (signer == null)
			throw new IllegalArgumentException();
		
		this.signer = signer;
		this.signatureValid = false;
		this.useOrgCertificate = useOrgCert;
		this.thumbprintVerified = false;
		this.signerCert = cert;
	}

	/**
	 * Get the certificate used to sign the message for this specific signer.
	 * @return The certificate used to sign the message.
	 */
	public X509Certificate getSignerCert() 
	{
		return signerCert;
	}

	/**
	 * Indicate if the signature has been validated for authenticity and consistency.
	 * @return True if the signature is valid.  False otherwise.
	 */
	public boolean isSignatureValid() 
	{
		return signatureValid;
	}

	/**
	 * Gets the signer information for this specific signature.
	 * @return The signer information for this specific signature.
	 */
	public SignerInformation getSigner() 
	{
		return signer;
	}

	/**
	 * Indicate if the certificate used to sign the message for this signer is an org level or individual level cert.
	 * @return True if the certificate is an org level cert.  False otherwise.
	 */
	public boolean isUseOrgCertificate() 
	{
		return useOrgCertificate;
	}

	/**
	 * Indicates if the signature certificate has been verified against a senders certificate. 
	 * @return True if the thumb print has been verified.  False otherwise.  checkThumbprint should be
	 * called first before calling this method.
	 */
	public boolean isThumbprintVerified() 
	{
		return thumbprintVerified;
	}
	
	/**
	 * Verifies if the signature is valid using the signature certificate.
	 * @return True if the signature is valid.  False otherwise.
	 */
	public boolean checkSignature()
	{		
        try
        {
        	signatureValid = signer.verify(signerCert, CryptoExtensions.getJCEProviderName());     		    	
        }
        catch (Exception e)
        {     
        	// TODO: Log an error
        	signatureValid = false; 
        }
                
        return signatureValid;
	}
	
	/**
	 * Validates if the senders certificate matches the signature certificate using certificate thumb printing.
	 * @param messageSender The senders address.  The address should contain the senders public certificate.
	 * @return True if the thumb print of the signature matches the senders certificate thumb print.  False otherwise.
	 */
	public boolean checkThumbprint(NHINDAddress messageSender)
	{				
		thumbprintVerified = false;
		//try
		//{
			// generate a thumb print of our cert
			Thumbprint sigThumbprint = Thumbprint.toThumbprint(this.getSignerCert());
			
			if (messageSender.hasCertificates())
			// now iterate through the sender's certificates until a thumb print match is found
				for (X509Certificate checkCert : messageSender.getCertificates())
					if (sigThumbprint.equals(Thumbprint.toThumbprint(checkCert)))
					{
						thumbprintVerified = true;
						break;
					}
			
		//}
		//catch (Exception e) {/* no-op */}
		
		return thumbprintVerified;
	}
}
