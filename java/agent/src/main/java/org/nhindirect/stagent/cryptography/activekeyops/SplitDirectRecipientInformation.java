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

package org.nhindirect.stagent.cryptography.activekeyops;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.ProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.nhindirect.stagent.cryptography.EncryptionAlgorithm;

/**
 * An implementation that of the DirectRecipientInformation interface that allows for an optimized set of decryption operations.
 * Unlike the BouncyCastle library which performs both the decryption of the message symmetric key and the decryption of the message content
 * using only one JCE provider, this implementation allows the two different decryption operations to be split across different JCE providers.  
 * This is optimal in situations where asymmetric private key information MUST be protected by a PKCS11 token.  If the keyEncProvider is set to
 * the JCE provider of a PKCS11 token and the encProvider is set to an in process JCE provider, this allows for the asymmetric operations to be performed on
 * the token but for the symmetric decryption to be performed in process.  This minimizes the amount of information that needs to be sent to the 
 * PKCS11 token. 
 * @author Greg Meyer
 * @since 2.1
 */
public class SplitDirectRecipientInformation extends KeyTransRecipientInformation implements DirectRecipientInformation
{
	protected final String encProvider;
	protected final String keyEncProvider;
	
	protected final KeyTransRecipientInfo info;
	
	/**
	 * Constructor
	 * @param info The recip info
	 * @param encAlg The encryption algorithm.
	 * @param data The data that is to be decrypted.
	 * @param encProvider The name of the JCE provider used to decrypt the messgae content using the message symmetric key.
	 * @param keyEncProvider The name of the JCE provider used to decrypt the messgae content using the message symmetric key.  This
	 * is sometime known as the key encryption key provider.
	 */
	public SplitDirectRecipientInformation(KeyTransRecipientInfo info, AlgorithmIdentifier encAlg,
	        InputStream data, String encProvider, String keyEncProvider)
	{
		super(info, encAlg, data);
		this.encProvider = encProvider;
		this.keyEncProvider = keyEncProvider;
		this.info = info;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getDecryptedContent(Key key) throws CMSException, NoSuchProviderException 
	{
		return this.getContent(key, encProvider);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public CMSTypedStream getContentStream(Key key /*private key*/ , String  prov /*ignored, use class variables instead*/)
        throws CMSException, NoSuchProviderException
    {
		// this is the symmetric key
        final byte[]  encryptedKey = info.getEncryptedKey().getOctets();
        // this is the algorithm that protects the symmetric key
        final String  keyExchangeAlgorithm = getExchangeEncryptionAlgorithmName(_keyEncAlg.getObjectId());
        // this is the algorithm of the symmetric key to actually decrypt the content
        final String  alg = EncryptionAlgorithm.fromOID(_encAlg.getObjectId().getId(), EncryptionAlgorithm.AES128_CBC).getAlgName();
        
        try
        {
            Cipher  keyCipher =  Cipher.getInstance(keyExchangeAlgorithm, keyEncProvider);
            Key     sKey;
            
            try
            {
            	// the original BC libraries attempted to do an UNWRAP assuming that the 
            	// same provider was used for secret key decryption and message decryption
            	// when these too operations are split into separate providers, using an unwrap method
            	// may result in a secret key handle that may not be usable by the another provider
            	// for that reason, this class will do a straight up decrypt of the message's internal
            	// secret key and hand that key off to the "encProvider" provider
                keyCipher.init(Cipher.DECRYPT_MODE, key);

                sKey = new SecretKeySpec(keyCipher.doFinal(encryptedKey), alg);
            }
            catch (GeneralSecurityException e)
            {        	
                keyCipher.init(Cipher.DECRYPT_MODE, key);

                sKey = new SecretKeySpec(keyCipher.doFinal(encryptedKey), alg);
            }
            catch (IllegalStateException e) 
            {
                keyCipher.init(Cipher.DECRYPT_MODE, key);

                sKey = new SecretKeySpec(keyCipher.doFinal(encryptedKey), alg);
            }
            catch (UnsupportedOperationException e) 
            {
                keyCipher.init(Cipher.DECRYPT_MODE, key);

                sKey = new SecretKeySpec(keyCipher.doFinal(encryptedKey), alg);
            }
            catch (ProviderException e)
            {
                keyCipher.init(Cipher.DECRYPT_MODE, key);

                sKey = new SecretKeySpec(keyCipher.doFinal(encryptedKey), alg);
            }
            return getContentFromSessionKey(sKey, encProvider);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new CMSException("can't find algorithm.", e);
        }
        catch (InvalidKeyException e)
        {
            throw new CMSException("key invalid in message.", e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new CMSException("required padding not supported.", e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new CMSException("illegal blocksize in message.", e);
        }
        catch (BadPaddingException e)
        {
            throw new CMSException("bad padding in message.", e);
        }
    }
	
    private String getExchangeEncryptionAlgorithmName(DERObjectIdentifier oid)
    {
        if (PKCSObjectIdentifiers.rsaEncryption.equals(oid))
        {
            return "RSA/ECB/PKCS1Padding";
        }
        
        return oid.getId();
    }
}
