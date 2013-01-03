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

package org.nhindirect.stagent.cert.tools;

/**
 * Application class for creating PKCS12 files from X509 DER encoded files and PKCS8 DER encoded private key files.  Unlike the Java keytool application,
 * CreatePKCS12 creates pcks12 files without a passphrase and can accept encrypted private key files.
 * 
 * @author Greg Meyer
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.CryptoExtensions;
///CLOVER:OFF
public class CreatePKCS12 
{

	private static File certFile;
	private static File keyFile;
	private static String password;
	private static String p12Pass = "";
	private static File createFile;
	
	
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	/**
	 * Main entry point when running as an application.  Use the -help option for usage.
	 * @param argv Application arguments.
	 */
	public static void main (String[] argv)
	{
		if (argv.length == 0)
		{
            printUsage();
            System.exit(-1);			
		}
		
		// Check parameters
        for (int i = 0; i < argv.length; i++)
        {
            String arg = argv[i];

            // Options
            if (!arg.startsWith("-"))
            {
                System.err.println("Error: Unexpected argument [" + arg + "]\n");
                printUsage();
                System.exit(-1);
            }
            else if (arg.equalsIgnoreCase("-cert"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing X509 certificate file.");
                    System.exit(-1);
                }
                
                certFile = new File(argv[++i]);
                
            }
            else if (arg.equals("-key"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing PCKS8 key file.");
                    System.exit(-1);
                }
                keyFile = new File(argv[++i]);
            }
            else if (arg.equals("-pass"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing key file password.");
                    System.exit(-1);
                }
                password = argv[++i];
            }
            else if (arg.equals("-p12pass"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing p12 file passphrase.");
                    System.exit(-1);
                }
                p12Pass = argv[++i];
            }    
            else if (arg.equals("-out"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing output file.");
                    System.exit(-1);
                }
                createFile = new File(argv[++i]);
            }
            else if (arg.equals("-help"))
            {
                printUsage();
                System.exit(-1);
            }            
            else
            {
                System.err.println("Error: Unknown argument " + arg + "\n");
                printUsage();
                System.exit(-1);
            }
        }

        if (validateParameters())        
        	if (create(certFile, keyFile, password, createFile) != null)
        		System.out.println("Created pcks12 file " + createFile.getAbsolutePath());
        
        System.exit(0);
    }

	/*
	 * Validate the parameters when run from the command line.
	 */
	private static boolean validateParameters()
	{
		return (certFile != null && keyFile != null);
	}
	
	/**
	 * Creates a PCKS12 file from the certificate and key files.
	 * @param certFile The X509 DER encoded certificate file.
	 * @param keyFile The PCKS8 DER encoded private key file.
	 * @param password Option password for the private key file.  This is required if the private key file is encrypted.  Should be null or empty
	 * if the private key file is not encrypted.
	 * @param createFile Optional file descriptor for the output file of the pkcs12 file.  If this is null, the file name is based on the 
	 * certificate file name.
	 * @return File descriptor of the created pcks12 file.  Null if an error occurred.  
	 */
	public static File create(File certFile, File keyFile, String password, File createFile)
	{
		File pkcs12File = null; 
		
		CreatePKCS12.certFile = certFile;
		CreatePKCS12.keyFile = keyFile;
		
		FileOutputStream outStr = null;
		InputStream inStr = null;
		// load cert file
		try
		{
			KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
			localKeyStore.load(null, null);
			
			byte[] certData = loadFileData(certFile);
			byte[] keyData = loadFileData(keyFile);
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			inStr = new ByteArrayInputStream(certData);
			java.security.cert.Certificate cert = cf.generateCertificate(inStr);
			
			IOUtils.closeQuietly(inStr);
			
			KeyFactory kf = KeyFactory.getInstance("RSA", CryptoExtensions.getJCEProviderName());
			PKCS8EncodedKeySpec keysp = null;
			if (password != null && !password.isEmpty())
			{
				EncryptedPrivateKeyInfo encInfo = new EncryptedPrivateKeyInfo(keyData);
				PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
				String alg = encInfo.getAlgName();
				
				SecretKeyFactory secFactory = SecretKeyFactory.getInstance(alg, CryptoExtensions.getJCEProviderName()); 
				SecretKey secKey = secFactory.generateSecret(keySpec);
				keysp = encInfo.getKeySpec(secKey, CryptoExtensions.getJCEProviderName());
			}
			else
			{
				keysp = new PKCS8EncodedKeySpec ( keyData );
			}
				
			Key privKey = kf.generatePrivate (keysp);
			
			char[] array = "".toCharArray();
			
			localKeyStore.setKeyEntry("privCert", privKey, array,  new java.security.cert.Certificate[] {cert});

			pkcs12File = getPKCS12OutFile(createFile);
			outStr = new FileOutputStream(pkcs12File);
			localKeyStore.store(outStr, p12Pass.toCharArray());			
		}
		catch (Exception e)
		{
			System.err.println("Failed to create pcks12 file: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(outStr);
			IOUtils.closeQuietly(inStr);		
		}
		
		
		return pkcs12File;
	}
	
	/*
	 * Creates the output file descriptor and creates the new file on the file system.
	 */
	private static File getPKCS12OutFile(File createFile) throws Exception
	{
		if (createFile == null)
		{
			
			String fileName = certFile.getName();
			
			int index = fileName.lastIndexOf(".");
			if (index > -1)
				fileName = fileName.substring(0, index);
			
			fileName += ".p12";
			CreatePKCS12.createFile = createFile = new File(fileName);
		}		

		if (createFile.exists())
			createFile.delete();
		
		createFile.createNewFile();
		
		return createFile;		
	}
	
	/*
	 * Loads the raw data from the provided file to a byte array.
	 */
	private static byte[] loadFileData(File file) throws Exception
	{		
		return FileUtils.readFileToByteArray(file);
	}
	
	/*
	 * Prints the command line usage. 
	 */
    private static void printUsage()
    {
        StringBuffer use = new StringBuffer();
        use.append("Usage:\n");
        use.append("java CreatePKCS12 (options)...\n\n");
        use.append("options:\n");
        use.append("-cert    X509 File      X509 DER formatted certificate file.\n");
        use.append("\n");
        use.append("-key     Key File       PCKS8 DER formatted private key file.\n");
        use.append("\n");
        use.append("-pass    Passwd         Optional passphrase for private key file.\n");
        use.append("			Default: \"\"\n\n");
        use.append("-p12pass P12 Passwd     Optional passphrase for the newly created p12 file.\n");
        use.append("			Default: \"\"\n\n");
        use.append("-out     Out File       Optional output file name.\n");
        use.append("			Default: <CertFileName>.p12\n\n");    

        System.err.println(use);        
    }
	
}
///CLOVER:ON