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

///CLOVER:OFF
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.X509CertificateEx;

/**
 * Application class for removing password and private key passphrase protection from PKCS12 files for importing into the Direct Project
 * configuration UI.
 * 
 * @author Greg Meyer
 */
public class StripP12Passphrase 
{
	private static File p12File;
	private static String filePassPhrase = "";
	private static String keyPassPhrase = "";
	private static File createFile;
	
	/*
	 * Load BC the JCS provider.
	 */
	static
	{
    	CryptoExtensions.registerJCEProviders();
	}
	
	/**
	 * Main entry point when running as an application.  Use the -help option for usage.
	 * @param argv Application arguments.
	 */
	public static void main (String[] argv)
	{
		if (argv.length == 0)
		{
			System.err.println("Invalid number of arguments: can't have 0 arguments.");
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
            else if (arg.equalsIgnoreCase("-p12"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: p12 file name.");
                    System.exit(-1);
                }
                
                p12File = new File(argv[++i]);
                
            }
            else if (arg.equals("-filePass"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing p12 file passphrase.");
                    System.exit(-1);
                }
                filePassPhrase = argv[++i];
            }
            else if (arg.equals("-keyPass"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing private key passphrase.");
                    System.exit(-1);
                }
                keyPassPhrase = argv[++i];
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
        {
        	stripP12File();
        }
        System.exit(0);
    }

	/*
	 * Main strip operation of removing the password and passphrase and creating a new p12 file.
	 */
	private static void stripP12File()
	{
		FileOutputStream outStr = null;
		try
		{
			byte[] p12Data = loadFileData(p12File);
		
			if (p12Data != null)
			{
				X509CertificateEx p12Cert = certFromData(p12Data);
				if (p12Cert == null)
					return;
				
				File outFile = getPKCS12OutFile();
				
				
				KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
				localKeyStore.load(null, null);
				
				char[] emptyPass = "".toCharArray();
				
				localKeyStore.setKeyEntry("privCert", p12Cert.getPrivateKey(), emptyPass,  new java.security.cert.Certificate[] {p12Cert});


				outStr = new FileOutputStream(outFile);
				localKeyStore.store(outStr, emptyPass);	
				
				System.out.println("Created pcks12 file " + createFile.getAbsolutePath());
			}
		}
		catch (Exception e)
		{
			System.out.println("Could not create p12 file " + e.getMessage());
		}
		finally
		{
			IOUtils.closeQuietly(outStr);		
		}
	}
	
	/*
	 * Valid program parameters
	 */
	private static boolean validateParameters()
	{
		
		if (p12File == null)
		{
			System.out.println("Missing input p12 file name");
			return false;
		}
		if (!p12File.exists())
		{
			System.out.println("P12 file " + p12File.getAbsolutePath() + " does not exist.");
			return false;
		}
		
		return true;
	}
	
	/*
	 * Loads the raw data from the provided file to a byte array.
	 */
	private static byte[] loadFileData(File file) throws Exception
	{		
		return FileUtils.readFileToByteArray(file);
	}
	
	/*
	 * Creates the output file descriptor and creates the new file on the file system.
	 */
	private static File getPKCS12OutFile() throws Exception
	{
		if (createFile == null)
		{
			
			String fileName = p12File.getName();
			
			int index = fileName.lastIndexOf(".");
			if (index > -1)
				fileName = fileName.substring(0, index);
			
			fileName += "_nopass.p12";
			createFile = new File(fileName);
		}		

		if (createFile.exists())
			createFile.delete();
		
		createFile.createNewFile();
		
		return createFile;		
	}
	
	/*
	 * Load the exiting p12 file using the provided password and private key passphrase.
	 */
   private static X509CertificateEx certFromData(byte[] data)
   {
    	X509CertificateEx retVal = null;
        try 
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            
            // lets try this a as a PKCS12 data stream first
            try
            {
            	KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
            	
            	localKeyStore.load(bais, filePassPhrase.toCharArray());
            	Enumeration<String> aliases = localKeyStore.aliases();


        		// we are really expecting only one alias 
        		if (aliases.hasMoreElements())        			
        		{
        			String alias = aliases.nextElement();
        			X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);
        			
    				// check if there is private key
    				Key key = localKeyStore.getKey(alias, keyPassPhrase.toCharArray());
    				if (key != null && key instanceof PrivateKey) 
    				{
    					retVal = X509CertificateEx.fromX509Certificate(cert, (PrivateKey)key);
    				}
    					
        		}
            }
            catch (Exception e)
            {
            	// must not be a PKCS12 stream, go on to next step
            	System.out.println("Error decoding p12 input file: " + e.getMessage());
            }
   
            IOUtils.closeQuietly(bais);
        } 
        catch (Exception e) 
        {
            throw new NHINDException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return retVal;
    }
	
   /*
    * Print program usage.
    */
    private static void printUsage()
    {
        StringBuffer use = new StringBuffer();
        use.append("Usage:\n");
        use.append("java StripP12Passphrase (options)...\n\n");
        use.append("options:\n");
        use.append("-p12        p12 File         P12 formatted file to strip the passphrase from.\n");
        use.append("\n");
        use.append("-filePass   File passphrase  Optional file passphrase protecting the p12 file.\n");
        use.append("            Default: \"\"\n\n");
        use.append("-keyPass    Key passphrase   Optional private key passphrase protecting the internal private key.\n");
        use.append("            Default: \"\"\n\n");        
        use.append("-out        Out File         Optional output file name.\n");
        use.append("            Default: <p12 file>_nopass.p12\n\n");    

        System.err.println(use);        
    }
}
///CLOVER:ON