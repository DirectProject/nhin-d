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

package org.nhindirect.stagent.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.trust.TrustAnchorResolver;
import org.nhindirect.stagent.trust.TrustModel;

import com.google.inject.Provider;

import java.util.Arrays;

public class DefaultNHINDAgentProvider implements Provider<NHINDAgent> 
{
	private Collection<String> domains;
	private Collection<Provider<CertificateResolver>> publicCerts;
	private Provider<CertificateResolver> privateCerts;
	private Provider<TrustAnchorResolver> trustResolver;
	
    @SuppressWarnings("unchecked")
	public DefaultNHINDAgentProvider(Collection<String> domains, Provider<CertificateResolver> publicCerts,
			Provider<CertificateResolver> privateCerts, Provider<TrustAnchorResolver> trustResolver)
	{
		this(domains, Arrays.asList(publicCerts), privateCerts, trustResolver);
	}
	
	public DefaultNHINDAgentProvider(Collection<String> domains, Collection<Provider<CertificateResolver>> publicCerts,
			Provider<CertificateResolver> privateCerts, Provider<TrustAnchorResolver> trustResolver)
	{
		this.domains = domains;
		this.publicCerts = publicCerts;
		this.privateCerts = privateCerts;
		this.trustResolver = trustResolver;
	}	
	
	public NHINDAgent get()
	{
		Collection<CertificateResolver> publicResolvers = new ArrayList<CertificateResolver>();
		for (Provider<CertificateResolver> provider : publicCerts)
			publicResolvers.add(provider.get());
		
		return new DefaultNHINDAgent(domains, privateCerts.get(), publicResolvers, trustResolver.get(),
				TrustModel.Default, SMIMECryptographerImpl.Default);
	}
}
