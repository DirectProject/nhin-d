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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Base abstract class for a cipher wrapper.  This class delegates methods to an underlying JProv service class except
 * for key wrapping functions.
 * @author Greg Meyer
 * @since 2.1
 */
public abstract class AbstractWrappableCipher extends CipherSpi 
{
	protected static final String DSA_KEY_CLAZZ_NAME = "au.com.safenet.crypto.provider.DSAPrivKey";
	
	protected static final String RSA_KEY_CLAZZ_NAME = "au.com.safenet.crypto.provider.RSAPrivKeyCrt";		
	
	protected static final String BASIC_SECRET_KEY_CLAZZ_NAME = "au.com.safenet.crypto.provider.BasicSecretKey";
	
	protected static final String BASIC_KEY_CLAZZ_NAME = "au.com.safenet.crypto.provider.BasicKey";
	
	protected static final String CRYPTOKEY_CLAZZ_NAME = "jprov.cryptoki.Cryptoki";
	
	protected static final String ATTRIBUTE_CLAZZ_NAME = "jprov.cryptoki.Attribute";
		
	protected static final String MECH_CLAZZ_NAME = "jprov.cryptoki.Mechanism";
	
	protected static final String MECH_PARAM_CLAZZ_NAME = "jprov.cryptoki.MechanismParam";
	
	protected static final String CRY_OBJECT_CLAZZ_NAME = "jprov.cryptoki.CryptokiObject";
	
	protected CipherSpi internalCipher;
	
	protected Class<?> internalClazz;
	
	protected int mode;
	
	protected Key cipherKey;
	
	protected SecureRandom rand;
	
	protected AlgorithmParameters algParams;
	
	protected AlgorithmParameterSpec algSpecs;	
	
	protected Object localSession;
	
	/**
	 * Constructor
	 */
	public AbstractWrappableCipher()
	{
		super();
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
			throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
	{
		final Method m = safeGetMethod("engineDoFinal", byte[].class, Integer.TYPE, Integer.TYPE, byte[].class, Integer.TYPE);
		return (Integer)safeInvoke(m, input, inputOffset, inputLen, output, outputOffset);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen)
			throws IllegalBlockSizeException, BadPaddingException
	{		
		final Method m = safeGetMethod("engineDoFinal", byte[].class, Integer.TYPE, Integer.TYPE);
		return (byte[])safeInvoke(m, input, inputOffset, inputLen);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int engineGetBlockSize()
	{
		final Method m = safeGetMethod("engineGetBlockSize");
		return (Integer)safeInvoke(m);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected byte[] engineGetIV()
	{
		final Method m = safeGetMethod("engineGetIV");
		return (byte[])safeInvoke(m);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int engineGetOutputSize(int inputLen)
	{
		final Method m = safeGetMethod("engineGetOutputSize");
		return (Integer)safeInvoke(m);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AlgorithmParameters engineGetParameters()
	{
		final Method m = safeGetMethod("engineGetParameters");
		return (AlgorithmParameters)safeInvoke(m);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random)
			throws InvalidKeyException, InvalidAlgorithmParameterException
	{
		if (opmode == Cipher.WRAP_MODE || opmode == Cipher.UNWRAP_MODE)
		{
			goWrapMode(opmode, key, random, params, null);
			
			return;
		}
		
		final Method m = safeGetMethod("engineInit", Integer.TYPE, Key.class, AlgorithmParameters.class, SecureRandom.class);
		safeInvoke(m, opmode, key, params, random);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random)
			throws InvalidKeyException, InvalidAlgorithmParameterException
	{
		if (opmode == Cipher.WRAP_MODE || opmode == Cipher.UNWRAP_MODE)
		{
			goWrapMode(opmode, key, random, null, params);
			return;
		}
		
		final Method m = safeGetMethod("engineInit", Integer.TYPE, Key.class, AlgorithmParameterSpec.class, SecureRandom.class);
		safeInvoke(m, opmode, key, params, random);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException
	{
		if (opmode == Cipher.WRAP_MODE || opmode == Cipher.UNWRAP_MODE)
		{
			
			goWrapMode(opmode, key, random, null, null);
			
			return;
		}
		
		final Method m = safeGetMethod("engineInit", Integer.TYPE, Key.class, SecureRandom.class);
		safeInvoke(m, opmode, key, random);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void engineSetMode(String mode) throws NoSuchAlgorithmException
	{
		final Method m = safeGetMethod("engineSetMode", String.class);
		safeInvoke(m, mode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void engineSetPadding(String padding) throws NoSuchPaddingException
	{
		final Method m = safeGetMethod("engineSetPadding", String.class);
		safeInvoke(m, padding);
	}

	@Override
	protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
			throws ShortBufferException
	{
		final Method m = safeGetMethod("engineUpdate", byte[].class, Integer.TYPE, Integer.TYPE, byte[].class, Integer.TYPE);
		return (Integer)safeInvoke(m, input, inputOffset, inputLen, output, outputOffset);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen)
	{
		final Method m = safeGetMethod("engineUpdate", byte[].class, Integer.TYPE, Integer.TYPE);
		return (byte[])safeInvoke(m, input, inputOffset, inputLen);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void finalize()
	{		
		final Method m = safeGetMethod("finalize");
		safeInvoke(m);
		
		// now create a new local sessions from the slot
		try
		{
			final Method closeSession = localSession.getClass().getMethod("closeSession", Integer.TYPE);
			closeSession.invoke(localSession);
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Failed to close HSM session.");
		}
	}
	
	/**
	 * Transport a key into a native token handle object.
	 * @param paramKey The key to transfor
	 * @return An object that represents a handle to the underlying token object
	 * @throws KeyStoreException
	 */
	protected Object toCrptokiObject(Key paramKey) throws KeyStoreException
	{

		Object basicKey = null;
		
		try
		{
			final Class<?> basicSecretKeyClazz = getClass().getClassLoader().loadClass(BASIC_SECRET_KEY_CLAZZ_NAME);
			final Class<?> basicKeyClazz = getClass().getClassLoader().loadClass(BASIC_KEY_CLAZZ_NAME);
			
			if (paramKey instanceof SecretKeySpec)
			{
				final SecretKeySpec localSecretKeySpec = (SecretKeySpec) paramKey;
	
	
				final Constructor<?> cst = basicSecretKeyClazz.getConstructor(localSession.getClass(), String.class, byte[].class);
				
				basicKey = cst.newInstance(localSession, localSecretKeySpec.getAlgorithm(), paramKey.getEncoded());
			
			}
			else
			{
				if (basicKeyClazz.isAssignableFrom(paramKey.getClass()))
				{
					basicKey = paramKey;
				}
				else if (paramKey instanceof PrivateKey)
				{
					// this might be a private key coming from an external
					// source such as a byte stream or a file
					
					/**
					 * 
					 * TODO: finish this
					final PrivateKey privKey = (PrivateKey)paramKey;
					if (privKey.getAlgorithm().equals("DSA"))
					{
						final Class<?> dsaClass = getClass().getClassLoader().loadClass(DSA_KEY_CLAZZ_NAME);
						final Constructor<?> cst = dsaClass.getDeclaredConstructor(cryObjClazz);
						cst.setAccessible(true);
						return (Key)cst.newInstance(unwrappedKey);
					} 
					else if (privKey.getAlgorithm().equals("RSA"))
					{
						keyType = 0;
					} 
					**/
					throw new IllegalStateException("Key is not of a recognized type.");
				}
				
				else
					throw new IllegalStateException("Key is not of a recognized type.");
			}
			
			final Field field = this.safeGetField("key", basicKeyClazz);
			return field.get(basicKey);
		}
		catch (IllegalStateException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Could not get internal CrptokiObject.", e);
		}
	}
	
	/**
	 * 
	 * @param mode
	 * @param key
	 * @param rand
	 * @param algParams
	 * @param algSpecs
	 */
	protected void goWrapMode(int mode, Key key, SecureRandom rand, AlgorithmParameters algParams, AlgorithmParameterSpec algSpecs)
	{
		this.mode = mode;
		this.cipherKey = key;
		this.rand = rand;
		this.algParams = algParams;
		this.algSpecs = algSpecs;
	}	
	
	/**
	 * Initializes this service engine by gaining a session to the underlying token
	 */
	protected void initEngineSession()
	{
		try
		{
			final Class<?> crptClass = getClass().getClassLoader().loadClass(CRYPTOKEY_CLAZZ_NAME);
			final Method getSlot = crptClass.getMethod("getSlot", Integer.TYPE);
			final Object slot = getSlot.invoke(null, 0);
			
			// now create a new local sessions from the slot
			final Method openSession = slot.getClass().getMethod("openSession", Integer.TYPE);
			localSession = openSession.invoke(slot, 2);
		} 
		catch (Exception e)
		{
			throw new IllegalStateException("Failed to get internal engine session.", e);
		}
		
	}
	
	/**
	 * Gets a field object from a class.  This method searches super classes up the chain of inheritance.
	 * @param fieldName The name of the field
	 * @param clazz The class object that the field belongs to
	 * @return The field object
	 */
	protected Field safeGetField(String fieldName, Class<?> clazz)
	{
		Field f = null;
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) 
		{
			try
			{
				f = c.getDeclaredField(fieldName);
				
				if (f != null)
				{
					f.setAccessible(true);
					break;
				}
			} 
			catch (NoSuchFieldException e)
			{
				continue;
			} 
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
        }
			
		if (f == null)
			throw new IllegalStateException("Can't find field " + fieldName + " in class " + clazz.getName());
		
		return f;

	}
	
	/**
	 * Gets a method object within the internal cipher class.  This method searches super classes up the chain of inheritance.
	 * @param methodName  The name of the method
	 * @param params The paramaters
	 * @return
	 */
	protected Method safeGetMethod(String methodName, Class<?>...params)
	{
		Method m = null;
		for (Class<?> c = internalClazz; c != null; c = c.getSuperclass()) 
		{
			try
			{
				m = (params != null && params.length > 0) ? c.getDeclaredMethod(methodName, params) :
						c.getDeclaredMethod(methodName);
				
				if (m != null)
					break;
			} 
			catch (NoSuchMethodException e)
			{
				continue;
			} 
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
        }
			
		if (m == null)
			throw new IllegalStateException("Can't find method " + methodName);
		
		return m;

	}

	/**
	 * Invokes a method
	 * @param m The method to invoke
	 * @param params The parameters of the method
	 * @return The return object of invoking the method
	 */
	protected Object safeInvoke(Method m, Object...params)
	{
		try
		{	
			m.setAccessible(true);
			
			return m.invoke(internalCipher, params);
		} 
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}		
	}
	
	/**
	 * Gets the token implementation's key code for a type of key.
	 * @param paramString The type of key
	 * @return The underlying code matching the type of key.
	 */
	protected int getSecretKeyType(String paramString)
	{
		if (paramString.equals("CAST"))
		{
			return 22;
		}
		if (paramString.equals("CAST128"))
		{
			return 24;
		}
		if (paramString.equals("DES"))
		{
			return 19;
		}
		if ((paramString.equals("DESede")) || (paramString.equals("DESedeX919")))
		{
			return 21;
		}
		if (paramString.equals("IDEA"))
		{
			return 26;
		}
		if (paramString.equals("RC2"))
		{
			return 17;
		}
		if (paramString.equals("RC4"))
		{
			return 18;
		}
		if (paramString.equals("RC5"))
		{
			return 25;
		}
		if (paramString.equals("AES"))
		{
			return 31;
		}
		if (paramString.equals("HMAC"))
		{
			return 16;
		}

		throw new IllegalArgumentException("Unsupported key type: " + paramString);
	}
}
