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

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;

import com.google.inject.ImplementedBy;

/**
 * Certificate resolver implementations are responsible for retrieving public X509Certificates from a 
 * certificate repository.  Repositories may include a simple keystore file, a machine cert store,
 * a URI, or a DNS cert implementation.    
 * @author Greg Meyer
 * @author Umesh Madan
 */
@ImplementedBy(KeyStoreCertificateStore.class)
public interface CertificateResolver
{
	/**
	 * Retrieves a collection of certificates for a given InternetAddress.
	 * @param address  The InternetAddress used to lookup the certificate.
	 * @return An X509Certificate collection containing the address in its E or CN field.
	 */
	public Collection<X509Certificate> getCertificates(InternetAddress address);
}
