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

public class MessageSignature 
{
	private boolean signatureValid;
	private SignerInformation signer;
	private boolean useOrgCertificate;
	private boolean thumbprintVerified;
	private X509Certificate signerCert;
	
	public MessageSignature(SignerInformation signer, boolean useOrgCert, X509Certificate cert)
	{
		if (signer == null)
			throw new IllegalArgumentException();
		
		this.signer = signer;
		this.signatureValid = false;
		this.useOrgCertificate = useOrgCert;
		this.thumbprintVerified = false;
		this.signerCert = cert;
	}

	public X509Certificate getSignerCert() 
	{
		return signerCert;
	}

	public boolean isSignatureValid() 
	{
		return signatureValid;
	}

	public SignerInformation getSigner() 
	{
		return signer;
	}

	public boolean isUseOrgCertificate() 
	{
		return useOrgCertificate;
	}

	public boolean isThumbprintVerified() 
	{
		return thumbprintVerified;
	}
	
	public boolean checkSignature()
	{		
        try
        {
        	signatureValid = signer.verify(signerCert, "BC");     		    	
        }
        catch (Exception e)
        {     
        	// TODO: Log an error
        	signatureValid = false; 
        }
                
        return signatureValid;
	}
	
	public boolean checkThumbprint(NHINDAddress messageSender)
	{				
		thumbprintVerified = false;
		try
		{
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
			
		}
		catch (Exception e) {/* no-op */}
		
		return thumbprintVerified;
	}
}
