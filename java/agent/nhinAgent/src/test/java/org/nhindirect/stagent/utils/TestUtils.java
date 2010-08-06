package org.nhindirect.stagent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.nhindirect.stagent.cert.X509CertificateEx;

public class TestUtils 
{
	// use a local key store for tests
	private static KeyStore keyStore;
	
	private static final String internalStorePassword = "h3||0 wor|d";	
	private static final String pkPassword = "pKpa$$wd";
	
	
	static
	{
		try
		{
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			File fl = new File("testfile");
			int idx = fl.getAbsolutePath().lastIndexOf("testfile");
			
			String path = fl.getAbsolutePath().substring(0, idx);
			
			File internalKeystoreFile = new File(path + "src/test/resources/keystores/internalKeystore");			
			
			FileInputStream inStream = new FileInputStream(internalKeystoreFile);
			
			keyStore.load(inStream, internalStorePassword.toCharArray());	
			
			inStream.close();
		}
		catch (Exception e)
		{
			
		}
	}
	
	public static X509CertificateEx getInternalCert(String alias) throws Exception
	{
		X509Certificate cert = (X509Certificate)keyStore.getCertificate(alias);
		
		return X509CertificateEx.fromX509Certificate(cert, (PrivateKey)keyStore.getKey("user1", pkPassword.toCharArray()));
	}
	
	
	public static X509Certificate getExternalCert(String alias) throws Exception
	{
		return  (X509Certificate)keyStore.getCertificate(alias);		
	}	
	
	public static X509Certificate getInternalCACert(String alias) throws Exception
	{
		return  (X509Certificate)keyStore.getCertificate(alias);		
	}	
	
	public static X509Certificate getExternalCACert(String alias) throws Exception
	{
		return  (X509Certificate)keyStore.getCertificate(alias);		
	}	
	
	
	
}
