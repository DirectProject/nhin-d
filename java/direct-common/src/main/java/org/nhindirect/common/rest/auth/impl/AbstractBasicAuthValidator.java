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

package org.nhindirect.common.rest.auth.impl;

import org.apache.commons.codec.binary.Base64;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;
import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.NHINDPrincipal;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;

/**
 * Abstract implementation of a BasicAuth validator.  Specific implementation will access credentials for validation from a provided storage medium
 * and authenticate requests based on the store credential format.
 * @author Greg Meyer
 * @since 1.3
 */
public abstract class AbstractBasicAuthValidator implements BasicAuthValidator
{
	protected BasicAuthCredentialStore credStore;
	
	/**
	 * Constructor
	 */
	public AbstractBasicAuthValidator()
	{
		
	}
	
	/**
	 * Constructor with a provided credential storage implementation.
	 * @param credStore The credential storage medium.
	 */
	public AbstractBasicAuthValidator(BasicAuthCredentialStore credStore)
	{
		setAuthStore(credStore);
	}
	
	/**
	 * Sets the credential storage implementation.
	 * @param credStore The credential storage medium.
	 */
	public void setAuthStore(BasicAuthCredentialStore credStore)
	{
		this.credStore = credStore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NHINDPrincipal authenticate(String rawAuth) throws BasicAuthException
	{
		// raw auth should start with "Basic" and contain a space
		// split the string by a space
		int idx = rawAuth.indexOf(" ");
		final String parsedRawAuth = (idx >= 0) ? rawAuth.substring(idx + 1) : rawAuth;
		
		// first decode
		final String authString = new String(Base64.decodeBase64(parsedRawAuth));
		
		// now parse into username and password 
		final String[] userPass = authString.split(":");
		
		return authenticate(userPass[0], userPass[1]);
	}	
}
