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

package org.nhindirect.stagent.trust.provider;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;

import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.provider.TrustAnchorCertificateStoreProvider;
import org.nhindirect.stagent.trust.TrustAnchorResolver;
import org.nhindirect.stagent.trust.DefaultTrustAnchorResolver;

import com.google.inject.Provider;

public class MultiDomainTrustAnchorResolverProvider implements Provider<TrustAnchorResolver>
{
	private final Map<String,Collection<X509Certificate>> incomingAnchors;
	private final Map<String,Collection<X509Certificate>> outgoingAnchors;
	
	public MultiDomainTrustAnchorResolverProvider(Map<String,Collection<X509Certificate>> incomingAnchors,
			Map<String,Collection<X509Certificate>> outgoingAnchors)
	{
		this.incomingAnchors = incomingAnchors;
		this.outgoingAnchors = outgoingAnchors;
	}	
	
	public TrustAnchorResolver get()
	{
		Provider<CertificateResolver> incomingProvider = getTrustAnchorStoreProvider(incomingAnchors);
		Provider<CertificateResolver> outgoingProvider = getTrustAnchorStoreProvider(outgoingAnchors);
		
		return new DefaultTrustAnchorResolver(outgoingProvider.get(), incomingProvider.get());
	}
	
	private Provider<CertificateResolver> getTrustAnchorStoreProvider(Map<String,Collection<X509Certificate>> certs)
	{
	   return new TrustAnchorCertificateStoreProvider(certs);
	}	
}
