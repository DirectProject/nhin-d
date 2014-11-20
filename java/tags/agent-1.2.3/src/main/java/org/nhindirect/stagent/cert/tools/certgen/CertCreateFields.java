/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
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

package org.nhindirect.stagent.cert.tools.certgen;

import java.io.File;
import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;

/**
 * Container for fields related to generating certificates.
 * @author Greg Meyer
 *
 */
///CLOVER:OFF
class CertCreateFields 
{
	private Map<String, Object> attributes;
	private File newCertFile;
	private File newKeyFile;
	private char[] newPassword;
	private int expDays;
	private int keyStrength;
	private X509Certificate signerCert;
	private Key signerKey;
	
	public CertCreateFields(Map<String, Object> attributes, File newCertFile, File newKeyFile,
			char[] newPassword, int expDays, int keyStrength, X509Certificate signerCert, Key signerKey)
	{
		this.attributes = attributes;
		this.newCertFile = newCertFile;
		this.newKeyFile = newKeyFile;
		this.newPassword = newPassword;
		this.expDays = expDays;
		this.keyStrength = keyStrength;
		this.signerCert = signerCert;
		this.signerKey = signerKey;
	}

	public  Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public File getNewCertFile() {
		return newCertFile;
	}

	public File getNewKeyFile() {
		return newKeyFile;
	}

	public char[] getNewPassword() {
		return newPassword;
	}

	public int getExpDays() {
		return expDays;
	}

	public int getKeyStrength() {
		return keyStrength;
	}

	public X509Certificate getSignerCert() {
		return signerCert;
	}

	public Key getSignerKey() {
		return signerKey;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void setNewCertFile(File newCertFile) {
		this.newCertFile = newCertFile;
	}

	public void setNewKeyFile(File newKeyFile) {
		this.newKeyFile = newKeyFile;
	}

	public void setNewPassword(char[] newPassword) {
		this.newPassword = newPassword;
	}

	public void setExpDays(int expDays) {
		this.expDays = expDays;
	}

	public void setKeyStrength(int keyStrength) {
		this.keyStrength = keyStrength;
	}

	public void setSignerCert(X509Certificate signerCert) {
		this.signerCert = signerCert;
	}

	public void setSignerKey(Key signerKey) {
		this.signerKey = signerKey;
	}
	
}
///CLOVER:ON