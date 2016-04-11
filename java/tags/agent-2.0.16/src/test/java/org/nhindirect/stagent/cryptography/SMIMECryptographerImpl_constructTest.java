package org.nhindirect.stagent.cryptography;

import java.io.File;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.options.OptionsManagerUtils;

import junit.framework.TestCase;


public class SMIMECryptographerImpl_constructTest extends TestCase
{
	@Override
	public void setUp()
	{
		OptionsManagerUtils.clearOptionsManagerInstance();
	}
	
	@Override
	public void tearDown()
	{
		OptionsManagerUtils.clearOptionsManagerOptions();
	}
	
	public void testContructSMIMECryptographerImpl_defaultSettings()
	{
		SMIMECryptographerImpl impl = new SMIMECryptographerImpl();
		
		assertEquals(DigestAlgorithm.SHA1, impl.getDigestAlgorithm());
		assertEquals(EncryptionAlgorithm.AES128, impl.getEncryptionAlgorithm());
	}
	
	public void testContructSMIMECryptographerImpl_setAlgorithms()
	{
		SMIMECryptographerImpl impl = new SMIMECryptographerImpl(EncryptionAlgorithm.RSA_3DES, DigestAlgorithm.SHA384);
		
		assertEquals(DigestAlgorithm.SHA384, impl.getDigestAlgorithm());
		assertEquals(EncryptionAlgorithm.RSA_3DES, impl.getEncryptionAlgorithm());
	}
	
	public void testContructSMIMECryptographerImpl_JVMSettings()
	{
		System.setProperty("org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm", "AES256");
		System.setProperty("org.nhindirect.stagent.cryptographer.smime.DigestAlgorithm", "SHA256");
		
		try
		{
		
			SMIMECryptographerImpl impl = new SMIMECryptographerImpl();
		
			assertEquals(DigestAlgorithm.SHA256, impl.getDigestAlgorithm());
			assertEquals(EncryptionAlgorithm.AES256, impl.getEncryptionAlgorithm());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm", "");
			System.setProperty("org.nhindirect.stagent.cryptographer.smime.DigestAlgorithm", "");
		}
	}
	
	public void testContructSMIMECryptographerImpl_InvalidJVMSettings()
	{
		System.setProperty("org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm", "AES256323");
		System.setProperty("org.nhindirect.stagent.cryptographer.smime.DigestAlgorithm", "SHA2564323");
		
		try
		{
		
			SMIMECryptographerImpl impl = new SMIMECryptographerImpl();
		
			assertEquals(DigestAlgorithm.SHA1, impl.getDigestAlgorithm());
			assertEquals(EncryptionAlgorithm.AES128, impl.getEncryptionAlgorithm());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm", "");
			System.setProperty("org.nhindirect.stagent.cryptographer.smime.DigestAlgorithm", "");
		}
	}
	
	
	public void testContructSMIMECryptographerImpl_propFileSettings() throws Exception
	{
		File propFile = new File("./target/props/agentSettings.properties");
		if (propFile.exists())
			propFile.delete();
	
		System.setProperty("org.nhindirect.stagent.PropertiesFile", "./target/props/agentSettings.properties");
		
		OutputStream outStream = null;
		
		try
		{
			outStream = FileUtils.openOutputStream(propFile);
			outStream.write("org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm=AES192\r\n".getBytes());
			outStream.write("org.nhindirect.stagent.cryptographer.smime.DigestAlgorithm=SHA512".getBytes());
			outStream.flush();
			
		}
		finally
		{
			IOUtils.closeQuietly(outStream);
		}
		
		try
		{
		
			SMIMECryptographerImpl impl = new SMIMECryptographerImpl();
		
			assertEquals(DigestAlgorithm.SHA512, impl.getDigestAlgorithm());
			assertEquals(EncryptionAlgorithm.AES192, impl.getEncryptionAlgorithm());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm", "");
			System.setProperty("org.nhindirect.stagent.cryptographer.smime.DigestAlgorithm", "");
			System.setProperty("org.nhindirect.stagent.PropertiesFile", "");
			propFile.delete();
		}
	}
}
