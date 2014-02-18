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

import java.util.Hashtable;

import org.nhindirect.stagent.cert.impl.annotation.LdapEnvironmentAnnot;
import org.nhindirect.stagent.cert.impl.annotation.LdapReturningAttributes;
import org.nhindirect.stagent.cert.impl.annotation.LdapSearchBase;
import org.nhindirect.stagent.cert.impl.annotation.LdapSearchFilter;

import com.google.inject.Inject;

/**
 * This class represents the necessary parameters for the LDAP operations
 * @author NM019057
 *
 */
public class LdapEnvironment {
	
	/**
	 * LDAP environment properties
	 */
	private Hashtable<String, String> env;
	
	/**
	 * The attribute to be returned. (Private certificates, trust anchors).
	 * It is a required field
	 */
	private String returningCertAttribute;
	
	/**
	 * LDAP base to use for searching for the certificate. 
	 */
	private String ldapSearchBase;
	
	/**
	 * LDAP filter to use with subjectName
	 * It is a required field.
	 */
	private String ldapSearchAttribute;
	
	@Inject
	public LdapEnvironment(@LdapEnvironmentAnnot Hashtable<String, String> env,
			@LdapReturningAttributes String returningCertAttribute, @LdapSearchBase String ldapSearchBase, @LdapSearchFilter String ldapSearchAttribute) {
		super();
		this.env = env;
		this.returningCertAttribute = returningCertAttribute;
		this.ldapSearchBase = ldapSearchBase;
		this.ldapSearchAttribute = ldapSearchAttribute;
	}

	public Hashtable<String, String> getEnv() {
		return env;
	}

	public String getReturningCertAttribute() {
		return returningCertAttribute;
	}

	public String getLdapSearchBase() {
		return ldapSearchBase;
	}

	public String getLdapSearchAttribute() {
		return ldapSearchAttribute;
	}	
}
