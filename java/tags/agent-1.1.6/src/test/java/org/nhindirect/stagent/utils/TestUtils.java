package org.nhindirect.stagent.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.NHINDAgentTest;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.testmodules.AgentTestModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class TestUtils 
{
	// base directory for test certificates
	private static final String certBasePath = "src/test/resources/certs/"; 
	
	// use a local key store for tests
	private static KeyStore keyStore;
	
	private static final String internalStorePassword = "h3||0 wor|d";	
	private static final String pkPassword = "pKpa$$wd";
	
	
	static
	{
		try
		{
			int pos = Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			System.out.println(pos);
			
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
	
	 @SuppressWarnings("unchecked") 
	 public static <T> TypeLiteral<Collection<T>> collectionOf(final Class<T> parameterType) 
	 { 
	        return (TypeLiteral<Collection<T>>) TypeLiteral.get(new ParameterizedType() 
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
	            	return Collection. class; 
	            } 
	            public Type getOwnerType() 
	            { 
	            	return null; 
	            } 
	        }); 
	 }
	 
	public static DefaultNHINDAgent getStockAgent(Collection<String> domains) throws Exception
	{
		File fl = new File("testfile");
		int idx = fl.getAbsolutePath().lastIndexOf("testfile");
		
		String path = fl.getAbsolutePath().substring(0, idx);
		
		String internalKeystoreFile = path + "src/test/resources/keystores/internalKeystore";		
		
		KeyStoreCertificateStore service = new KeyStoreCertificateStore(internalKeystoreFile, 
				internalStorePassword, pkPassword);
		
		X509Certificate caCert = TestUtils.getExternalCert("cacert");
		X509Certificate externCaCert = TestUtils.getExternalCert("externCaCert");
		X509Certificate secureHealthEmailCACert = TestUtils.getExternalCert("secureHealthEmailCACert");
		X509Certificate msCACert = TestUtils.getExternalCert("msanchor");
		X509Certificate cernerDemos = TestUtils.getExternalCert("cernerDemosCaCert");
		
		// anchors cert validation
		Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
		anchors.add(caCert);
		anchors.add(externCaCert);
		anchors.add(secureHealthEmailCACert);
		anchors.add(msCACert);
		anchors.add(cernerDemos);	
		
		AgentTestModule mod = new AgentTestModule(domains, anchors, internalKeystoreFile, internalStorePassword, pkPassword);
		
		Injector inj = Guice.createInjector(mod);
		return inj.getInstance(DefaultNHINDAgent.class);
	}
	
	public static String readResource(String _rec) throws Exception
	{
		
		int BUF_SIZE = 2048;		
		int count = 0;
	
		BufferedInputStream imgStream = new BufferedInputStream(NHINDAgentTest.class.getResourceAsStream(_rec));
				
		ByteArrayOutputStream ouStream = new ByteArrayOutputStream();
		if (imgStream != null) 
		{
			byte buf[] = new byte[BUF_SIZE];
			
			while ((count = imgStream.read(buf)) > -1)
			{
				ouStream.write(buf, 0, count);
			}
			
			try 
			{
				imgStream.close();
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
	
	public static X509Certificate loadCertificate(String certFileName) throws Exception
	{
		File fl = new File(certBasePath + certFileName);
		
		byte[] data =  FileUtils.readFileToByteArray(fl);
		
		X509Certificate retVal = certFromData(data);//(X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(str);
		
		
		return retVal;
	}	
	
    private static X509Certificate certFromData(byte[] data)
    {
    	X509Certificate retVal = null;
        try 
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            
            // lets try this a as a PKCS12 data stream first
            try
            {
            	KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
            	
            	localKeyStore.load(bais, "".toCharArray());
            	Enumeration<String> aliases = localKeyStore.aliases();


        		// we are really expecting only one alias 
        		if (aliases.hasMoreElements())        			
        		{
        			String alias = aliases.nextElement();
        			X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);
        			
    				// check if there is private key
    				Key key = localKeyStore.getKey(alias, "".toCharArray());
    				if (key != null && key instanceof PrivateKey) 
    				{
    					retVal = X509CertificateEx.fromX509Certificate(cert, (PrivateKey)key);
    				}
    				else
    					retVal = cert;
    					
        		}
            }
            catch (Exception e)
            {
            	// must not be a PKCS12 stream, go on to next step
            }
   
            if (retVal == null)            	
            {
            	//try X509 certificate factory next       
                bais.reset();
                bais = new ByteArrayInputStream(data);

                retVal = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);            	
            }
            bais.close();
        } 
        catch (Exception e) 
        {
            throw new NHINDException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return retVal;
    }	
}