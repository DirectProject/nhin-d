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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.nhindirect.common.rest.auth.BasicAuthCredential;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;

/**
 * A BasicAuthCredentialStore that is bootstrapped with all credentials and roles.
 * @author Greg Meyer
 * @since 1.3
 */
public class BootstrapBasicAuthCredentialStore implements BasicAuthCredentialStore
{
	protected Map<String, BasicAuthCredential> credentialMap;
	
	/**
	 * Constructor
	 */
	public BootstrapBasicAuthCredentialStore()
	{
		credentialMap =  new HashMap<String, BasicAuthCredential>();
	}
	
	/**
	 * Constructor that accepts a collection of credentials
	 * @param credentials Collections of credentials used to validate requests.
	 */
	public BootstrapBasicAuthCredentialStore(List<BasicAuthCredential> credentials)
	{
		this();
		
		setCredentails(credentials);
	}

	/**
	 * Sets the collections of credentials used to validate requests.
	 * @param credentials Collections of credentials used to validate requests.
	 */
	public void setCredentails(List<BasicAuthCredential> credentials)
	{
		for (BasicAuthCredential cred : credentials)
			credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
	}

	/**
	 * Sets a list of credentials delimited by a ",".  The credentials are in the following order:
	 * <br><i>username,password,role</i>
	 * @param credentials List of credentials delimited by a ",".
	 */
	public void setCredentialsAsDelimetedString(List<String> credentials)
	{
		for (String str : credentials)
		{
			final String parsedStr[] = str.split(",");
			final BasicAuthCredential cred = new DefaultBasicAuthCredential(parsedStr[0], parsedStr[1], parsedStr[2]);
			credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
		}
	}
	
	/**
	 * Sets a list of credentials as a set of properties delimited by a ",".  The credentials are in the following order:
	 * <br><i>username,password,role</i>
	 * @param credentials List of credentials a set of properties delimited by a ",".
	 */
	public void setCredentialsAsProperties(Properties credentials)
	{
		for (Entry<Object, Object> entry : credentials.entrySet())
		{
			final String parsedStr[] = entry.getValue().toString().split(",");
			final BasicAuthCredential cred = new DefaultBasicAuthCredential(parsedStr[0], parsedStr[1], parsedStr[2]);
			credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicAuthCredential getCredential(String name) 
	{
		return credentialMap.get(name.toUpperCase(Locale.getDefault()));
	}
}
