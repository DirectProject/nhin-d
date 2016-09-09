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

package org.nhindirect.common.crypto.jceproviders.safenetprotect.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;

/**
 * Key wrapper and unwrapper implementation class for AES cipher. 
 * @author Greg Meyer
 * @since 2.1
 *
 */
public class AES extends AbstractWrappableCipher 
{	
	protected static final String CLAZZ_NAME = "au.com.safenet.crypto.provider.slot0.AES";
	
	/**
	 * Constructor
	 */
	public AES()
	{	
		try
		{
			internalClazz = this.getClass().getClassLoader().loadClass(CLAZZ_NAME);

			internalCipher = CipherSpi.class.cast(internalClazz.newInstance());
			
			initEngineSession();
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Failed to construct AES engine.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    protected byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException
    {
		try
		{		
			final Class<?> mechParamClazz = getClass().getClassLoader().loadClass(MECH_PARAM_CLAZZ_NAME);
			final Class<?> mechClazz = getClass().getClassLoader().loadClass(MECH_CLAZZ_NAME);
			final Constructor<?> mechConst = mechClazz.getConstructor(Integer.TYPE, mechParamClazz);
			final Object mech = mechConst.newInstance(4225, null);
			
			final Class<?> cryObjClazz = getClass().getClassLoader().loadClass(CRY_OBJECT_CLAZZ_NAME);
			
			final Method wrapKeyMeth = localSession.getClass().getMethod("wrapKey", mechClazz, cryObjClazz, cryObjClazz, byte[].class, Integer.TYPE);			
			
			final Object wrapper = toCrptokiObject(this.cipherKey);
			final Object wrappee = toCrptokiObject(key);
			
			int size = (Integer)wrapKeyMeth.invoke(localSession, mech, wrapper, wrappee, null, 0);
			
			final byte[] wrapBuffer = new byte[size];

			wrapKeyMeth.invoke(localSession, mech, wrapper, wrappee, wrapBuffer, 0);		
			
			return wrapBuffer;
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}

    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Key engineUnwrap(byte[] paramArrayOfByte, String paramString, int paramInt) throws InvalidKeyException, NoSuchAlgorithmException
	{
		try
		{
			int objectClass;
			int keyType;
			
			switch (paramInt)
			{
				case Cipher.PRIVATE_KEY:
					objectClass = 3;
					break;
				case Cipher.SECRET_KEY:
					objectClass = 4;
					break;
				default:
					throw new IllegalArgumentException("Key type " + paramInt + " is not supported.");
			}
			
			if (paramString.equals("DSA"))
			{
				keyType = 1;
			} 
			else if (paramString.equals("RSA"))
			{
				keyType = 0;
			} 
			else if (paramString.equals("DH"))
			{
				keyType = 2;
			}
			else
			{
				keyType = getSecretKeyType(paramString);
			}
				
			
			final Class<?> mechClazz = getClass().getClassLoader().loadClass(MECH_CLAZZ_NAME);
			final Class<?> cryObjClazz = getClass().getClassLoader().loadClass(CRY_OBJECT_CLAZZ_NAME);
			
			final Class<?> mechParamClazz = getClass().getClassLoader().loadClass(MECH_PARAM_CLAZZ_NAME);
			final Constructor<?> mechConst = mechClazz.getConstructor(Integer.TYPE, mechParamClazz);
			final Object mech = mechConst.newInstance(4225, null);
			
			final Class<?> attrArrayClass = Class.forName("[Ljprov.cryptoki.Attribute;");
			final Method unwrapKeyMeth = localSession.getClass().getMethod("unwrapKey", mechClazz, cryObjClazz, byte[].class, Integer.TYPE, Integer.TYPE, attrArrayClass);		
			
			final Class<?> attClass = getClass().getClassLoader().loadClass(ATTRIBUTE_CLAZZ_NAME);
			final Constructor<?> attrConst = attClass.getConstructor(Integer.TYPE, Object.class);
			final Object attr1 = attrConst.newInstance(0, objectClass); // Object class... 3 = private key, 4 = secret key
			final Object attr2 = attrConst.newInstance(256, keyType);  // Algorithm of the unwrappee... 
			final Object attr3 = attrConst.newInstance(354, Boolean.TRUE); // extractable
			final Object attr4 = attrConst.newInstance(259, Boolean.TRUE); // sensitive
			final Object attr5 = attrConst.newInstance(268, Boolean.TRUE); // derived
			
			final Object attr = Array.newInstance(attClass, 5);
			Array.set(attr, 0, attr1);
			Array.set(attr, 1, attr2);
			Array.set(attr, 2, attr3);
			Array.set(attr, 3, attr4);
			Array.set(attr, 4, attr5);
			
			final Object wrapper = toCrptokiObject(this.cipherKey);
			
			final Object unwrappedKey = unwrapKeyMeth.invoke(localSession, mech, wrapper, paramArrayOfByte, 0, paramArrayOfByte.length, attrArrayClass.cast(attr));
			
			switch (keyType)
			{
				case 1:
				{
					final Class<?> dsaClass = getClass().getClassLoader().loadClass(DSA_KEY_CLAZZ_NAME);
					final Constructor<?> cst = dsaClass.getDeclaredConstructor(cryObjClazz);
					cst.setAccessible(true);
					return (Key)cst.newInstance(unwrappedKey);
				}
				case 0:
				{
					final Class<?> rsaClass = getClass().getClassLoader().loadClass(RSA_KEY_CLAZZ_NAME);
					final Constructor<?> cst = rsaClass.getDeclaredConstructor(cryObjClazz);
					cst.setAccessible(true);
					return (Key)cst.newInstance(unwrappedKey);
				}
			}
			
			final Class<?> basicSecKeyClass = getClass().getClassLoader().loadClass(BASIC_SECRET_KEY_CLAZZ_NAME);
			final Constructor<?> cst = basicSecKeyClass.getDeclaredConstructor(String.class, cryObjClazz);
			cst.setAccessible(true);
			return (Key)cst.newInstance(paramString, unwrappedKey);			
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}
}
