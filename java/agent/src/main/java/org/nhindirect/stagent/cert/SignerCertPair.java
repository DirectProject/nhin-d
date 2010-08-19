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

import org.bouncycastle.cms.SignerInformation;

/**
 * A pair object containing signer information and the certificate used in the signature.
 * @author Greg Meyer
 *
 */
public class SignerCertPair 
{
	private final SignerInformation signer;
	private final X509Certificate cert;
	
	/**
	 * Construct a pair with the signer information the certificate.
	 * @param _signer Infomorationg about the the signer of a message.
	 * @param _cert The certificate used to sign a message.
	 */
	public SignerCertPair(SignerInformation _signer, X509Certificate _cert)
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
