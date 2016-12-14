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

package org.nhindirect.stagent.cert;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.WrappableKeyProtectionManager;
import org.nhindirect.common.crypto.exceptions.CryptoException;
import org.nhindirect.stagent.AgentError;
import org.nhindirect.stagent.NHINDException;

/**
 * Implementation of the X509CertificateEx class that utilized wrapped private keys and installs
 * them in the backing PKCS11 token only when first needed.  In some cases, the wrapped private key data
 * is retrieved but never used wasting resources in unwrapping the keys.
 * @author Greg Meyer
 * @since 2.1
 */
public class WrappedOnDemandX509CertificateEx extends X509CertificateEx
{
	protected final KeyStoreProtectionManager mgr;
	protected final byte[] wrappedData;
	protected PrivateKey wrappedKey;
	
	/**
	 * Creates an instance by proviing the certificate, the wrapped private key, and KeyStoreProtectionManager that can unwrap the
	 * key.
	 * @param mgr The keystore protection manager used to unwrap the private key
	 * @param cert The certificate.
	 * @param wrappedData A wrapped representation of the private key
	 * @return An X509CertificateEx instance
	 */
	public static X509CertificateEx fromX509Certificate(KeyStoreProtectionManager mgr, X509Certificate cert, byte[] wrappedData)
	{
		if (cert == null || wrappedData == null || wrappedData.length == 0)
			throw new IllegalArgumentException("Cert or wrapped data cannot be null");
		
		if (mgr == null) 
			throw new IllegalArgumentException("KeyStore manager cannot be null");
		
		if (!(mgr instanceof WrappableKeyProtectionManager))
			throw new IllegalArgumentException("Key store must implement the WrappableKeyProtectionManager interface");
		
		return new WrappedOnDemandX509CertificateEx(mgr, cert, wrappedData);
	}
	
	/**
	 * Protected constructor
	 * @param mgr The keystore protection manager used to unwrap the private key
	 * @param cert The certificate.
	 * @param wrappedData A wrapped representation of the private key
	 */
	protected WrappedOnDemandX509CertificateEx(KeyStoreProtectionManager mgr, X509Certificate cert, byte[] wrappedData)
	{
		super(cert, null);
		
		this.mgr = mgr;
		this.wrappedData = wrappedData;
	}
	
    /**
     * {@inheritDoc}}
     */
	@Override
    public boolean hasPrivateKey()
    {
    	return wrappedData != null;
    }
    
    /**
     * {@inheritDoc}}
     */
    public synchronized PrivateKey getPrivateKey()
    {
    	// this is on demand, so it needs to be synchronized
    	
    	if (wrappedKey != null)
    		return wrappedKey;
    		
    	final WrappableKeyProtectionManager wrapManager = (WrappableKeyProtectionManager)mgr;
    	// get the key algorithm from the public key... this will be needed
    	// as a parameter to the unwrap method
    	final String keyAlg = this.internalCert.getPublicKey().getAlgorithm();
    	try
    	{
    		wrappedKey = (PrivateKey)wrapManager.unwrapWithSecretKey((SecretKey)mgr.getPrivateKeyProtectionKey(), wrappedData, keyAlg, Cipher.PRIVATE_KEY);
    	}
    	catch (CryptoException e)
    	{
    		throw new NHINDException(AgentError.Unexpected, "Failed to access wrapped private key.", e);
    	}
    	
    	return wrappedKey;
    }    
}
