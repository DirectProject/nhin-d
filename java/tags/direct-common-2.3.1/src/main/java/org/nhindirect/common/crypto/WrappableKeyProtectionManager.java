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

package org.nhindirect.common.crypto;

import java.security.Key;

import javax.crypto.SecretKey;

import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * Wraps keys with secret keys.  This is generally utilized with PKCS11 modules to create a temporary key on the token.  Sensitive key
 * material is "wrapped" or "unwrapped" using a secret key without the decrypted sensitive key information ever being present in process
 * memory.  Temporary keys are destroyed in the token when the unwrapped key object are dereferenced.  
 * <p>
 * Depending on implementations, the key encryption key may also only be present on the token.  This is the preferred method so as to not expose
 * the secret key information in process memory.  The algorithm used to wrap and unwrap keys may also be dependent on implementation.
 * @author Greg Meyer
 * @Since 1.5
 */
public interface WrappableKeyProtectionManager 
{
	/**
	 * Wraps a key with a symmetric secret key encryption key.
	 * @param kek The key encryption key.
	 * @param keyToWrap The key to be wrapped.
	 * @return A wrapped representation of the key as a byte array.
	 * @throws CryptoException
	 */
	public byte[] wrapWithSecretKey(SecretKey kek, Key keyToWrap) throws CryptoException;
	
	/**
	 * Unwraps the key with a symmetric secret key encryption key.  
	 * @param kek The key encryption key.
	 * @param wrappedData The wrapped key as a byte array.
	 * @param keyAlg The algorithm of the key that is being decrypted.  Typical parameters are "RSA", "DSA", and "AES" depending on the key type.
	 * @param keyType The type of key that is wrapped.  Valid values should use the Cipher.PRIVATE_KEY and Cipher.SECRET_KEY constants.
	 * @return The unwrapped key.  Depending on implementation, the actual key material may not be available using the getEncoded() method; the
	 * sensitive key information is held on the token.  The returned key may still be used for cryptographic operations, but the Provider name parameter
	 * of the cryptographic functions will generally need to match the Provider name of the underlying keystore.
	 * @throws CryptoException
	 */
	public Key unwrapWithSecretKey(SecretKey kek, byte[] wrappedData, String keyAlg, int keyType) throws CryptoException;
}
