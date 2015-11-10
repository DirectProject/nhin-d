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

package org.nhindirect.gateway.smtp.provider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.mailet.Mailet;
import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.impl.BootstrappedPKCS11Credential;
import org.nhindirect.common.crypto.impl.StaticPKCS11TokenKeyStoreProtectionManager;
import org.nhindirect.gateway.GatewayConfiguration;
import org.nhindirect.gateway.smtp.james.mailet.SecurityAndTrustMailetOptions;
import org.nhindirect.stagent.options.OptionsManager;

import com.google.inject.Provider;

/**
 * KeyStoreProtectionManager provider that creates instances of the StaticPKCS11TokenKeyStoreProtectionManager concrete class.  Keystore
 * configuration is read from the Mailet configuration.
 * @author Greg Meyer
 *
 */
public class StaticPKCS11TokenKeyStoreProtectionManagerProvider implements Provider<KeyStoreProtectionManager>, MailetAwareProvider
{
	static
	{		
		initJVMParams();
	}
	
	private synchronized static void initJVMParams()
	{
		/*
		 * Mailet configuration parameters
		 */
		final Map<String, String> JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PIN, "org.nhindirect.gateway.smtp.james.mailet.KeystoreManagerPin");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_KEYSTORE_PASSPHRASE_ALIAS, "org.nhindirect.gateway.smtp.james.mailet.KeystorePassPhraseAlias");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PRIVATE_KEY_PASSPHRASE_ALIAS, "org.nhindirect.gateway.smtp.james.mailet.PrivateKeyPassPhraseAlias");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_STORE_TYPE, "org.nhindirect.gateway.smtp.james.mailet.KeyManagerStoreType");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_PROVIDER, "org.nhindirect.gateway.smtp.james.mailet.KeystoreManagerPKCS11Provider");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_CONFIG_FILE, "org.nhindirect.gateway.smtp.james.mailet.KeystoreManagerPKCS11ConfigFile");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_PROVIDER_CUSTOM_CONFIG_FILE, "org.nhindirect.gateway.smtp.james.mailet.KeystoreManagerPKCS11ProviderCustomConfigFile");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_PROVIDER_CUSTOM_CONFIG_STRING, "org.nhindirect.gateway.smtp.james.mailet.KeystoreManagerPKCS11ProviderCustomConfigString");

		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	protected Mailet mailet;
	
	/**
	 * Constructor
	 */
	public StaticPKCS11TokenKeyStoreProtectionManagerProvider()
	{
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMailet(Mailet mailet) 
	{
		this.mailet = mailet;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public KeyStoreProtectionManager get()
	{
		final String pin = GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PIN, mailet, "");
		final String keyStorePassPhraseAlias = 
				GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.KEYSTORE_MGR_KEYSTORE_PASSPHRASE_ALIAS, mailet, "");
		final String privateKeyPassPhraseAlias = 
				GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PRIVATE_KEY_PASSPHRASE_ALIAS, mailet, "");
		final String storeType = 
				GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.KEYSTORE_MGR_STORE_TYPE, mailet, "");
		final String pkcs11Provider = 
				GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_PROVIDER, mailet, "");
		final String pkcs11Config = 
				GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_CONFIG_FILE, mailet, "");		
		final String pkcs11CustomConfig = 
				GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_PROVIDER_CUSTOM_CONFIG_FILE, mailet, "");	
		final String pkcs11CustomConfigString = 
				GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.KEYSTORE_MGR_PKCS11_PROVIDER_CUSTOM_CONFIG_STRING, mailet, "");	
		
		final BootstrappedPKCS11Credential cred = new BootstrappedPKCS11Credential(pin);
		
		try
		{
			final StaticPKCS11TokenKeyStoreProtectionManager mgr = new StaticPKCS11TokenKeyStoreProtectionManager();
			
			mgr.setCredential(cred);
			mgr.setKeyStorePassPhraseAlias(keyStorePassPhraseAlias);
			mgr.setPrivateKeyPassPhraseAlias(privateKeyPassPhraseAlias);
			
			if (!StringUtils.isEmpty(storeType))
				mgr.setKeyStoreType(storeType);
			
			if (!StringUtils.isEmpty(pkcs11Provider))
				mgr.setKeyStoreProviderName(pkcs11Provider);
			
			if (!StringUtils.isEmpty(pkcs11Config))
				mgr.setPcks11ConfigFile(pkcs11Config);
			
			if (!StringUtils.isEmpty(pkcs11CustomConfig))
			{
				final String str = FileUtils.readFileToString(new File(pkcs11CustomConfig));
				mgr.setKeyStoreSourceAsString(str);
			}
			
			if (!StringUtils.isEmpty(pkcs11CustomConfigString))
			{
				final InputStream str = new ByteArrayInputStream(pkcs11CustomConfigString.getBytes());
				mgr.setKeyStoreSource(str);
			}
			mgr.initTokenStore();
			
			return mgr;
		}
		catch (Throwable e)
		{
			throw new IllegalArgumentException("Failed to create key store manager.", e);
		}
	}
}
