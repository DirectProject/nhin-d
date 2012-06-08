package org.nhindirect.stagent.keystore;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;

public class KeyStoreCreate 
{
	private static final String internalStorePassword = "h3||0 wor|d";
	private static final String pkPassword = "pKpa$$wd";
	
	private static final String certsBasePath;
	private static final KeyStoreCertificateStore service;
	
	static 
	{
		File fl = new File("testfile");
		int idx = fl.getAbsolutePath().lastIndexOf("testfile");
		
		String path = fl.getAbsolutePath().substring(0, idx);
		
		File internalKeystoreFile = new File(path + "src/test/resources/keystores/internalKeystore");
		
		service = new KeyStoreCertificateStore(internalKeystoreFile, internalStorePassword, pkPassword);
		
		certsBasePath = path + "src/test/resources/certs/" ;
		
	}
	
	
    private static InputStream fullStream ( String fname ) throws IOException {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        
        dis.close();
        
        return bais;
    }	
	
	public static void main(String[] args)
	{
		try
		{		
			importCert("highlandclinic_old", "highlandclinic@securehealthemail.com.der", null);
			importCert("user1", "user1.der", "user1key.der");
			importCert("cacert", "cacert.der", null);
			importCert("gm2552", "gm2552.der", "gm2552Key.der");
			importCert("secureHealthEmailCACert", "secureHealthEmailCACert.der", null);
			importCert("msanchor", "msanchor.der", null);
			importCert("mshost", "mshost.der", null);
			importCert("externUser1", "externUser1.der", "externUser1key.der");
			importCert("externCaCert", "externCaCert.der", null);
			importCert("cernerdemos", "cernerdemos.der", "cernerdemosKey.der");
			importCert("cernerDemosCaCert", "cernerDemosCaCert.der", null);
			importCert("ryan", "ryan.der", "ryanKey.der");
			importCert("RDI-CA-certificate", "RDI-CA-certificate.der", null);
			importCert("ses", "ses.der", null);			
			importCert("highlandclinic", "highlandclinic.der", "highlandclinicKey.der");
			importCert("bob", "bob.der", null);
			importCert("umesh", "umesh.der", "umeshKey.der");
			importCert("dev", "dev.der", "devkey.der");
			importCert("messagingexternal", "messagingExternal.der", "messagingExternalKey.der");
			importCert("AlAndersonPublicCert", "AlAnderson@hospitalA.direct.visionshareinc.com.der", null);
			importCert("testemailPubOrgCert", "test.email.com.der", "test.email.comKey.der");
			importCert("expiredTest", "expired.der", "expiredKey.der");
			importCert("altnameonly", "altNameOnly.der", "altNameOnlyKey.der");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		
		
		
		// create external key store
	}
	
	private static void importCert(String alias, String certFile, String pkFile)
	{
		try
		{
			System.out.println("Importing cert with alias \"" + alias + "\"");
				
			if (service.getByAlias(alias) == null)
			{
				InputStream inStream = new FileInputStream(certsBasePath + certFile);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
				inStream.close();
				    
				PrivateKey ff = null;
				if (pkFile != null && pkFile.length() > 0)
				{
		            InputStream btInstream = fullStream (certsBasePath + pkFile);
		            byte[] key = new byte[btInstream.available()];
		            KeyFactory kf = KeyFactory.getInstance("RSA");
		            btInstream.read ( key, 0, btInstream.available() );
		            btInstream.close();
		            PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( key );
		            ff = kf.generatePrivate (keysp);			
				}	            
	            service.add(ff != null ? X509CertificateEx.fromX509Certificate(cert, ff) : cert, alias);
				System.out.println("Alias added:\r\n\tCert DN: " + service.getByAlias(alias).getSubjectDN().getName() + "\r\n");
				
			}
			else
			{
				System.out.println("Alias already exists:\r\n\tCert DN: " + service.getByAlias(alias).getSubjectDN().getName() + "\r\n");
			}
		}
		catch (Exception e)
		{
			System.out.println("Error importing cert: " + e.getLocalizedMessage());
		}
	
	}
}
