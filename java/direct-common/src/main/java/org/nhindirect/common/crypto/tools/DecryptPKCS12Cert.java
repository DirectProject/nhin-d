package org.nhindirect.common.crypto.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

public class DecryptPKCS12Cert 
{
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	public static void main(String args[])
	{
		try
		{
			byte[] certData = FileUtils.readFileToByteArray(new File("/users/gm2552/desktop/Data5.cer"));
			
			SecretKeySpec privateKey = new SecretKeySpec(new byte[]{0,0,0,0,0,0,0, 36}, "");
			SecretKeySpec keyStoreKey = new SecretKeySpec(new byte[]{0,0,0,0,0,0,0, 49}, "");
			
    		final String oldKeystorePassPhrase = new String(keyStoreKey.getEncoded());
			final String oldPrivateKeyPassPhrase = new String(privateKey.getEncoded());
			
			//changePkcs12Protection(certData, oldKeystorePassPhrase.toCharArray(), oldPrivateKeyPassPhrase.toCharArray(), 
			//		"".toCharArray(), "".toCharArray());
			
			changePkcs12Protection(certData, "".toCharArray(), "".toCharArray(), 
							"".toCharArray(), "".toCharArray());
					
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static byte[] changePkcs12Protection(byte[] bytes, char[] oldKeyStorePassPhrase,
			char[] oldPrivateKeyPassPhrase, char[] newKeystorePassPhrase, char[] newPrivateKeyPassPhrase)
	{
		if (bytes == null || bytes.length == 0)
			throw new IllegalArgumentException("Pkcs byte stream cannot be null or empty.");
		
		byte[] retVal = null;
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    	final ByteArrayOutputStream outStr = new ByteArrayOutputStream();
        // lets try this a as a PKCS12 data stream first
        try
        {
        	final KeyStore localKeyStore = KeyStore.getInstance("PKCS12", "BC");
        	
        	localKeyStore.load(bais, oldKeyStorePassPhrase);
        	final Enumeration<String> aliases = localKeyStore.aliases();



    		// we are really expecting only one alias 
    		if (aliases.hasMoreElements())        			
    		{
    			final String alias = aliases.nextElement();
    			X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);
    			
				// check if there is private key
				final Key key = localKeyStore.getKey(alias, oldPrivateKeyPassPhrase);
				if (key != null && key instanceof PrivateKey) 
				{
					// now convert to a pcks12 format without the new passphrase
					
					localKeyStore.setKeyEntry("privCert", key, newPrivateKeyPassPhrase,  new java.security.cert.Certificate[] {cert});

					localKeyStore.store(outStr, newKeystorePassPhrase);	
					
					retVal = outStr.toByteArray();
					
				}
    		}
        }
        catch (Exception e)
        {
        	throw new RuntimeException("Failed to strip encryption for PKCS stream.", e);
        }
        finally
        {
        	try {bais.close(); }
        	catch (Exception e) {/* no-op */}
        	
        	try {outStr.close(); }
        	catch (Exception e) {/* no-op */}
        }

        return retVal;
	}
}
