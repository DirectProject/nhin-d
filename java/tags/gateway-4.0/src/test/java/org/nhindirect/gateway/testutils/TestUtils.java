package org.nhindirect.gateway.testutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.io.FileUtils;
import org.nhindirect.stagent.CryptoExtensions;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;



public class TestUtils 
{
	
	private static final String certBasePath = "src/test/resources/certs/";
	
	private static final String policyBasePath = "src/test/resources/policies/";
	
	public static String getTestConfigFile(String fileName)
	{
		File fl = new File("dummy");
		int idx = fl.getAbsolutePath().lastIndexOf("dummy");
		
		String path = fl.getAbsolutePath().substring(0, idx);
		
		return path + "src/test/resources/configFiles/" + fileName;	

	}	
	
	 @SuppressWarnings("unchecked") 
	 public static <T> TypeLiteral<Provider<T>> providerOf(final Class<T> parameterType) 
	 { 
	        return (TypeLiteral<Provider<T>>) TypeLiteral.get(new ParameterizedType() 
	        { 
	            public Type[] getActualTypeArguments()
	            {
	            	return new Type[] 
	                {
	            			parameterType
	            	}; 
	            } 
	            public Type getRawType() 
	            { 
	            	return Provider.class; 
	            } 
	            public Type getOwnerType() 
	            { 
	            	return null; 
	            } 
	        }); 
	 }	

	 public static byte[] readBytePolicyResource(String _rec) throws Exception
	 {
			final String msgResource = policyBasePath + _rec;
			
			return FileUtils.readFileToByteArray(new File(msgResource));
	 }
	 
	 public static String readStringPolicyResource(String _rec) throws Exception
	 {
		
			final String msgResource = policyBasePath + _rec;
		
			return FileUtils.readFileToString(new File(msgResource));
	 }
	 
	public static String readMessageResource(String _rec) throws Exception
	{
		
		int BUF_SIZE = 2048;		
		int count = 0;
	
		String msgResource = "/messages/" + _rec;
	
		InputStream stream = TestUtils.class.getResourceAsStream(msgResource);;
				
		ByteArrayOutputStream ouStream = new ByteArrayOutputStream();
		if (stream != null) 
		{
			byte buf[] = new byte[BUF_SIZE];
			
			while ((count = stream.read(buf)) > -1)
			{
				ouStream.write(buf, 0, count);
			}
			
			try 
			{
				stream.close();
			} 
			catch (IOException ieo) 
			{
				throw ieo;
			}
			catch (Exception e)
			{
				throw e;
			}					
		} 
		else
			throw new IOException("Failed to open resource " + _rec);

		return new String(ouStream.toByteArray());		
	}
	 
	public static X509Certificate loadCertificate(String certFileName, String keyFileName) throws Exception
	{
		
		if (keyFileName == null || keyFileName.isEmpty())
		{
			File fl = new File(certBasePath + certFileName);
			return (X509Certificate)CertificateFactory.getInstance("X509", "BC").generateCertificate(FileUtils.openInputStream(fl));
		}	
		else
		{
			return (X509Certificate)CertificateFactory.getInstance("X509", "BC").generateCertificate(new ByteArrayInputStream(loadPkcs12FromCertAndKey(certFileName, keyFileName)));
		}
	}	
	
    private static byte[] loadPkcs12FromCertAndKey(String certFileName, String keyFileName) throws Exception
	{
		byte[] retVal = null;
		try
		{
			KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
			
			localKeyStore.load(null, null);
			
			byte[] certData = loadCertificateData(certFileName);
			byte[] keyData = loadCertificateData(keyFileName);
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream inStr = new ByteArrayInputStream(certData);
			java.security.cert.Certificate cert = cf.generateCertificate(inStr);
			inStr.close();
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( keyData );
			Key privKey = kf.generatePrivate (keysp);
			
			char[] array = "".toCharArray();
			
			localKeyStore.setKeyEntry("privCert", privKey, array,  new java.security.cert.Certificate[] {cert});
			
			ByteArrayOutputStream outStr = new ByteArrayOutputStream();
			localKeyStore.store(outStr, array);
			
			retVal = outStr.toByteArray();
			
			outStr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return retVal;
	}   
    
    private static byte[] loadCertificateData(String certFileName) throws Exception
	{
		File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}    
}
