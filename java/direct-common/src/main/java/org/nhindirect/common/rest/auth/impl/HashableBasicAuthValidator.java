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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.nhindirect.common.crypto.exceptions.CryptoException;
import org.nhindirect.common.rest.auth.BasicAuthCredential;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;
import org.nhindirect.common.rest.auth.NHINDPrincipal;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;
import org.nhindirect.common.rest.auth.exceptions.NoSuchUserException;

/**
 * Basic Auth validator where credential passwords are stored in various formats.  It is generally not a good idea to store plain text representations
 * of sensitive credential information.  This validator accepts stored credentials in various protected forms such as hashes.  When credentials for
 * requests are valiated, the password is converted into the appropriate stored format and compared against that format.
 * @author Greg Meyer
 * @since 1.3
 *
 */
public class HashableBasicAuthValidator extends AbstractBasicAuthValidator
{
    protected final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', 
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};	
	
    /**
     * Credentials are stored in clear/plain text.
     */
	public static final String HASH_CLEAR = "Clear";
	
    /**
     * Credentials are stored as MD5 hashes.
     */
	public static final String HASH_MD5 = "MD5";
	
    /**
     * Credentials are stored as SHA1 hashes.
     */
	public static final String HASH_SHA1 = "SHA1";
	
    /**
     * Credentials are stored as SHA256 hashes.
     */
	public static final String HASH_SHA256 = "SHA256";
	
    /**
     * Credentials are stored as SHA512 hashes.
     */
	public static final String HASH_SHA512 = "SHA512";
	
	protected static final Map<String, String> DIGEST_TYPE_MAP ;
	
	protected String hashType = HASH_CLEAR;
	
	static 
	{
		DIGEST_TYPE_MAP = new HashMap<String, String>();
		
		DIGEST_TYPE_MAP.put(HASH_MD5, "MD5");
		DIGEST_TYPE_MAP.put(HASH_SHA1, "SHA-1");
		DIGEST_TYPE_MAP.put(HASH_SHA256, "SHA-256");
		DIGEST_TYPE_MAP.put(HASH_SHA512, "SHA-512");
	}
	
	/**
	 * Constructor
	 */
	public HashableBasicAuthValidator()
	{
		super();
	}
	
	/**
	 * Constructor that accepts a credential storage implementation.  Password are assumed to be stored as plain text.
	 * @param credStore The credential storage medium.
	 */
	public HashableBasicAuthValidator(BasicAuthCredentialStore credStore)
	{
		super(credStore);
		this.credStore = credStore;
	}
	
	/**
	 * Constructor that accepts a credential storage implementation and hash type.
	 * @param credStore The credential storage medium.
	 * @param hashType The hash protection algorithm of the passwords.
	 */
	public HashableBasicAuthValidator(BasicAuthCredentialStore credStore, String hashType)
	{
		super(credStore);
		setHashType(hashType);
	}
	
	/**
	 * Sets the hash type or the password.
	 * @param hashType The hash protection algorithm of the passwords.
	 */
	public void setHashType(String hashType)
	{
		if (DIGEST_TYPE_MAP.get(hashType) == null)
			throw new IllegalArgumentException("Unknown hash type " + hashType);
		
		this.hashType = hashType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NHINDPrincipal authenticate(String subject, String password) throws BasicAuthException
	{
		// get the user from the credential store
		final BasicAuthCredential cred = credStore.getCredential(subject);
		
		if (cred == null)
			throw new NoSuchUserException();
		
		try
		{
			final String hashPass = convertPassToHash(password);
			
			if (hashPass.compareTo(cred.getPassword()) != 0)
				throw new BasicAuthException("Invalid credentials.");
			
			return new NHINDPrincipal(cred.getUser(), cred.getRole());
		}
		catch (CryptoException e)
		{
			throw new BasicAuthException("Failed to validate password.", e);
		}
	}
	
	/**
	 * Converts the password to the appropriate hash representation.
	 * @param password The plain text password that is part of the resource request.
	 * @return The password in the appropriate hash representation.
	 * @throws CryptoException
	 */
	protected String convertPassToHash(String password) throws CryptoException
	{
		if (hashType.compareToIgnoreCase(HASH_CLEAR) == 0)
			return password;
		
		final String digistAlg = DIGEST_TYPE_MAP.get(hashType);
		
		try
		{
			final MessageDigest md = MessageDigest.getInstance(digistAlg);
	
			md.update(password.getBytes());
	        final byte[] digest = md.digest();
	        
	        return createStringRep(digest);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new CryptoException("Algorithm not supported.", e);
		}
	}
	
	/**
	 * Creates a string representation of a hash digest.
	 * @param digest The digest to convert to a string representation.
	 * @return A string representation of a hash digest.
	 */
	private String createStringRep(byte[] digest)
	{
        final StringBuffer buf = new StringBuffer(digest.length * 2);

        for (byte bt : digest) 
        {
            buf.append(hexDigits[(bt & 0xf0) >> 4]);
            buf.append(hexDigits[bt & 0x0f]);
        }

        return buf.toString();
	}
}
