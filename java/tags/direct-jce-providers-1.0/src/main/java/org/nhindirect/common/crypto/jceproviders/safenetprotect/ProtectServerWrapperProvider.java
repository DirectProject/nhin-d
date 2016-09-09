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

package org.nhindirect.common.crypto.jceproviders.safenetprotect;

import java.lang.reflect.Method;
import java.security.Provider;
import java.security.Security;
import java.util.Map;

import org.nhindirect.common.crypto.jceproviders.safenetprotect.impl.AES;
import org.nhindirect.common.crypto.jceproviders.safenetprotect.impl.KeyS;
import org.nhindirect.common.crypto.jceproviders.safenetprotect.impl.RSA;

/**
 * Wrapper/shim class that acts as provider around the SafeNet JCProv library.  Specifially, this provider enables
 * key wrapping and unwrapping via Cipher interface.  Tested against the SafeNet ProtectServer HSM. 
 * @author Greg Meyer
 * @since 2.1
 */
public class ProtectServerWrapperProvider extends Provider
{
	static final String SAFENET_PROV_CLAZZ_NAME = "au.com.safenet.crypto.provider.SAFENETProvider";
	
	static
	{
		// check to see a safenet provider already exists
		Provider safeNetProvider = null;
		final Provider[] existingProvs = Security.getProviders();
		
		if (existingProvs != null)
		{
			for (Provider existingProv : existingProvs)
			{
				if (existingProv.getClass().getName().startsWith(SAFENET_PROV_CLAZZ_NAME));
				{
					safeNetProvider = existingProv;
					break;
				}
			}
		}
		
		if (safeNetProvider == null)
		{
			try
			{
				final Class<?> providerClazz = ProtectServerWrapperProvider.class.getClassLoader().loadClass(SAFENET_PROV_CLAZZ_NAME);
				
				safeNetProvider = Provider.class.cast(providerClazz.newInstance());
				Security.addProvider(safeNetProvider);
			}
			catch (Exception e)
			{
				throw new IllegalStateException("Failed to load underlying SAFENET provider");
			}			
		}
			
		if (Security.getProvider("SAFENET") == null)
		{
			try
			{
				final Class<?> providerClazz = ProtectServerWrapperProvider.class.getClassLoader().loadClass("au.com.safenet.crypto.provider.SAFENETProvider");
				final Method m = providerClazz.getMethod("addProviders");
				m.invoke(null);
			}
			catch (Exception e)
			{
				throw new IllegalStateException("Failed to load underlying SAFENET provider");
			}
		}
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4481793797539807494L;

	/**
	 * Constructor
	 */
	public ProtectServerWrapperProvider()
	{
		super("SAFENETPROTECTWRAPPER", 1.0, "SAFENET ProtectServer Security Provider Wrapper");
		
		try
		{
			final Class<?> providerClazz = getClass().getClassLoader().loadClass(SAFENET_PROV_CLAZZ_NAME);
			
			Provider safeNetProvider = Provider.class.cast(providerClazz.newInstance());
			
			for (Map.Entry<Object,Object> entry : safeNetProvider.entrySet())				
				this.put(entry.getKey(), entry.getValue());
			
			
			/*
			 * Override the internal wrapper properties
			 */
			this.put("Cipher.AES", AES.class.getName());
			this.put("Cipher.RSA", RSA.class.getName());
			this.put("KeyStore.CRYPTOKIWRAPPER", KeyS.class.getName());
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Failed to override provider properties.");
		}
		
	}

}
