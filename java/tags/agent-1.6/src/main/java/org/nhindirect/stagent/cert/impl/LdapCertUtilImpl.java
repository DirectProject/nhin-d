/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Manjiri Namjoshi      NM019057@cerner.com
 
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

package org.nhindirect.stagent.cert.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.X509CertificateEx;


public class LdapCertUtilImpl implements LdapCertUtil{
	
	private LdapEnvironment ldapEnvironment;
	
	private String keyStorePassword;
	
	private String certificateFormat;
	
	public LdapCertUtilImpl(LdapEnvironment ldapEnvironment, String keyStorePassword, String certificateFormat) {
		this.ldapEnvironment = ldapEnvironment;
		this.keyStorePassword = keyStorePassword;
		this.certificateFormat = certificateFormat;
	}

	public Collection<X509Certificate> ldapSearch(String subjectName) {
		DirContext ctx = null;
		try {
			ctx = getInitialDirContext(ldapEnvironment.getEnv());

			final SearchControls ctls = getDefaultSearchControls();
			
			NamingEnumeration<SearchResult> searchResult = ctx.search(
					ldapEnvironment.getLdapSearchBase(), 
					ldapEnvironment.getLdapSearchAttribute() + "=" + subjectName,
					ctls);
					
			ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
			while (searchResult != null && searchResult.hasMoreElements()) {
				final SearchResult certEntry = searchResult.nextElement();
				if (certEntry != null) {
					final Attributes certAttributes = certEntry.getAttributes();
					if (certAttributes != null) {
						// get only the returning cert attribute (for now, ignore all other attributes)
						final Attribute certAttribute = certAttributes.get(ldapEnvironment.getReturningCertAttribute());
						if (certAttribute != null) {
							NamingEnumeration<? extends Object> allValues = certAttribute.getAll();
							// LDAP may contain a collection of certificates.
							while(allValues.hasMoreElements()) {
								String ksBytes = (String) allValues.nextElement();
								Base64 base64 = new Base64();
								byte[] decode = base64.decode(ksBytes.getBytes());
								ByteArrayInputStream inputStream = new ByteArrayInputStream(decode);
								if(certificateFormat.equalsIgnoreCase("pkcs12")) {
									try {
										processPKCS12FileFormatAndAddToCertificates(inputStream, certificates);
									}
									catch(Exception e) {
										closeDirContext(ctx);
										throw new NHINDException(e);
									}
								}
								else {
									if(certificateFormat.equalsIgnoreCase("X.509") || certificateFormat.equalsIgnoreCase("X509")) {
										CertificateFactory cf = CertificateFactory.getInstance("X.509");
										X509Certificate addCert = (X509Certificate)cf.generateCertificate(inputStream);
										certificates.add(addCert);
									}
									else {
										closeDirContext(ctx);
										throw new NHINDException("Invalid certificate format requested");
									}
								}
							}
						}
					}
				}
			}
			return certificates;
		} catch (NamingException e) {
			closeDirContext(ctx);
			throw new NHINDException(e);
		} catch (CertificateException e) {
			closeDirContext(ctx);
			throw new NHINDException(e);
		}
	}
	
	protected void processPKCS12FileFormatAndAddToCertificates(ByteArrayInputStream inputStream,
			ArrayList<X509Certificate> certificates) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		KeyStore localKeyStore = KeyStore.getInstance("PKCS12");
		localKeyStore.load(inputStream, keyStorePassword == null ? null : keyStorePassword.toCharArray());
		
		IOUtils.closeQuietly(inputStream);
		
		Enumeration<String> aliases = localKeyStore.aliases();

		// even though we are iterating over this enumeration, we
		// are really expecting only one alias 
		while (aliases.hasMoreElements())
		{
			String alias = aliases.nextElement();
	
			Certificate cert = localKeyStore.getCertificate(alias);
			if (cert != null && cert instanceof X509Certificate)
			{
				X509Certificate addCert;
		
				// check if there is private key
				Key key = localKeyStore.getKey(alias, keyStorePassword == null ? null : keyStorePassword.toCharArray());
				if (key != null && key instanceof PrivateKey) {
					addCert = X509CertificateEx.fromX509Certificate((X509Certificate)cert, (PrivateKey)key);
					certificates.add(addCert);
				}
			}
		}
	}
	
	
	// /CLOVER:OFF
	protected SearchControls getDefaultSearchControls() {
		SearchControls ctls = new SearchControls();
		ctls.setReturningObjFlag(true);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		ctls.setReturningAttributes(new String[] {ldapEnvironment.getReturningCertAttribute()});
		return ctls;
	}
	
	protected InitialDirContext getInitialDirContext(
			Hashtable<String, String> env) throws NamingException {
		return new InitialDirContext(env);
	}
	
	protected void closeDirContext(DirContext dirContext) {
		if (dirContext != null) {
			try {
				dirContext.close();
			} catch (NamingException e) {
				// Not fatal since we're just trying to close a connection
			}
		}

	}

	public LdapEnvironment getLdapEnvironment() {
		return ldapEnvironment;
	}

	// /CLOVER:ON

}
