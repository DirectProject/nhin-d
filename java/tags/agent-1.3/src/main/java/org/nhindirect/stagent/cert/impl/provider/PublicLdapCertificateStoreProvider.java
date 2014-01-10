/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   jrenolds68@gmail.com
 
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

package org.nhindirect.stagent.cert.impl.provider;

import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.cert.impl.LdapPublicCertUtilImpl;

import com.google.inject.Provider;

/**
 * Guice provider for the public LDAP certificate store.
 * @author jrenolds68@gmail.com
 * @since 1.2
 */
public class PublicLdapCertificateStoreProvider implements Provider<CertificateResolver> {
	private final CertificateStore bootstrapStore;
	private final CertStoreCachePolicy policy;
	
	/**
	 * Constructor
	 * @param bootstrapStore Bootstrap store for offline certificates
	 * @param policy Cache policy
	 */
	public PublicLdapCertificateStoreProvider(CertificateStore bootstrapStore, CertStoreCachePolicy policy) {
		this.bootstrapStore = bootstrapStore;
		this.policy = policy;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CertificateResolver get() {
		LdapPublicCertUtilImpl utilImpl = new LdapPublicCertUtilImpl();
		
		return new LDAPCertificateStore(utilImpl, bootstrapStore, policy);
	}
}
