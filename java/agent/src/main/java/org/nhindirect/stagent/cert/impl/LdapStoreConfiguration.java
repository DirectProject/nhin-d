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

/**
 * This class represents the configurable parameters for LDAP service
 * @author NM019057
 *
 */
public class LdapStoreConfiguration {
	
	/**
	 * URL of LDAP service containing certificates.
	 * The LDAP provider will attempt to use each URL in turn, until it is able 
	 * to create a successful connection
	 * It is a required field
	 */
	private String[] ldapURLs;
	
	/**
	 * value for the connection timeout environment property
	 * com.sun.jndi.ldap.read.timeout (in milliseconds).
	 * It is an optional field
	 */
	private String ldapConnectionTimeOut;
	
	/**
	 * the environment property for specifying the security level to use
	 * java.naming.security.credentials
	 * It is a optional field. If null, then authentication mechanism for LDAP is
	 * "none". If non-null, then the authentication mechanism is "simple".
	 * Currently, only "none" and "simple" mechanisms are supported 
	 */
	private EmployLdapAuthInformation employLdapAuthInformation;
	
	/**
	 * LDAP base to use for searching for the certificate.
	 * It is a required field
	 */
	private String ldapSearchBase;
	
	/**
	 * LDAP filter to use with subjectName
	 * It is a required field.
	 */
	private String ldapSearchAttribute;
	
	/**
	 * The attribute to be returned. (Private certificates, trust anchors).
	 * It is a required field
	 */
	private String returningCertAttribute;
	
	/**
	 * The expected format of the retrieved certificates.
	 * The two formats supported are PKCS12 and  X.509
	 * It is a required field.  
	 */
	private String certificateFormat;
	
	/**
	 * Password for decrypting encrypted files 
	 * (e.g.: pkcs12 files which hold private keys). 
	 * It is an optional field.
	 */
	private String ldapCertPassphrase;

	/**
	 * Code execution may result in NPE if any of the constructor arguments
	 * are null.
	 * @param ldapURL
	 * @param ldapSearchBase
	 * @param ldapSearchAttribute
	 * @param returningCertAttribute
	 * @param certificateFormat
	 */
	public LdapStoreConfiguration(String[] ldapURLs, String ldapSearchBase,
			String ldapSearchAttribute, String returningCertAttribute,
			String certificateFormat) {
		super();
		this.ldapURLs = ldapURLs;
		this.ldapSearchBase = ldapSearchBase;
		this.ldapSearchAttribute = ldapSearchAttribute;
		this.returningCertAttribute = returningCertAttribute;
		this.certificateFormat = certificateFormat;
	}

	/**
	 * The optional fields can be null, but the code execution may result in NPE
	 * if any of the required fields are null 
	 * @param ldapURL
	 * @param ldapConnectionTimeOut
	 * @param employLdapAuthInformation
	 * @param ldapSearchBase
	 * @param ldapSearchAttribute
	 * @param returningCertAttribute
	 * @param certificateFormat
	 * @param ldapCertPassphrase
	 */
	public LdapStoreConfiguration(String[] ldapURLs, String ldapConnectionTimeOut,
			EmployLdapAuthInformation employLdapAuthInformation,
			String ldapSearchBase, String ldapSearchAttribute,
			String returningCertAttribute, String certificateFormat,
			String ldapCertPassphrase) {
		super();
		this.ldapURLs = ldapURLs;
		this.ldapConnectionTimeOut = ldapConnectionTimeOut;
		this.employLdapAuthInformation = employLdapAuthInformation;
		this.ldapSearchBase = ldapSearchBase;
		this.ldapSearchAttribute = ldapSearchAttribute;
		this.returningCertAttribute = returningCertAttribute;
		this.certificateFormat = certificateFormat;
		this.ldapCertPassphrase = ldapCertPassphrase;
	}

	public String[] getLdapURLs() {
		return ldapURLs;
	}

	public void setLdapURLs(String[] ldapURLs) {
		this.ldapURLs = ldapURLs;
	}

	public String getLdapConnectionTimeOut() {
		return ldapConnectionTimeOut;
	}

	public void setLdapConnectionTimeOut(String ldapConnectionTimeOut) {
		this.ldapConnectionTimeOut = ldapConnectionTimeOut;
	}

	public EmployLdapAuthInformation getEmployLdapAuthInformation() {
		return employLdapAuthInformation;
	}

	public void setEmployLdapAuthInformation(
			EmployLdapAuthInformation employLdapAuthInformation) {
		this.employLdapAuthInformation = employLdapAuthInformation;
	}

	public String getLdapSearchBase() {
		return ldapSearchBase;
	}

	public void setLdapSearchBase(String ldapSearchBase) {
		this.ldapSearchBase = ldapSearchBase;
	}

	public String getLdapSearchAttribute() {
		return ldapSearchAttribute;
	}

	public void setLdapSearchAttribute(String ldapSearchAttribute) {
		this.ldapSearchAttribute = ldapSearchAttribute;
	}

	public String getReturningCertAttribute() {
		return returningCertAttribute;
	}

	public void setReturningCertAttribute(String returningCertAttribute) {
		this.returningCertAttribute = returningCertAttribute;
	}

	public String getCertificateFormat() {
		return certificateFormat;
	}

	public void setCertificateFormat(String certificateFormat) {
		this.certificateFormat = certificateFormat;
	}

	public String getLdapCertPassphrase() {
		return ldapCertPassphrase;
	}

	public void setLdapCertPassphrase(String ldapCertPassphrase) {
		this.ldapCertPassphrase = ldapCertPassphrase;
	}
}
