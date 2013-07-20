/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Amulya Misra        Drajer LLC/G3Soft
   Satyajeet Mahapatra Drajer LLC/G3Soft

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

package org.nhindirect.trustbundle.core;

/**
 * Application class for creating PKCS12 files from X509 DER encoded files and PKCS8 DER encoded private key files.  Unlike the Java keytool application,
 * CreatePKCS12 creates pcks7 files without a passphrase and can accept encrypted private key files.
 * 
 * @author Greg Meyer
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;

///CLOVER:OFF
public class CreateUnSignedPKCS7 
{

	private static File certFile;
	private static File metaFile;	
	private static File createFile;
	private String error ="";
	private boolean metaExists = false;

	static
	{
		CryptoExtensions.registerJCEProviders();
	}

	/**
	 * Main entry point when running as an application.  Use the -help option for usage.
	 * @param argv Application arguments.
	 */
	public String getParameters(String anchorDir, String metaDataFile, String destDir, String bundleName)
	{
		if(anchorDir.equalsIgnoreCase("Select Trust Anchor Directory")
				|| anchorDir.equalsIgnoreCase("You pressed cancel"))
		{
			error = "Error: Kindly Provide Trust Anchor Directory";
			return error;
		}
		else if(destDir.equalsIgnoreCase("Select Trust Bundle Destination Directory")
				|| destDir.equalsIgnoreCase("You pressed cancel")){
			error = "Error: Kindly Provide Trust Bundle Destination Directory";
			return error;
		}else if(bundleName.equalsIgnoreCase("") || (!bundleName.endsWith(".p7c") && !bundleName.endsWith(".p7b"))){
			error = "Error: Kindly Provide A Proper Trust Bundle Name with a .p7b or .p7c extension";
			return error;
		}else{

			if(!metaDataFile.equalsIgnoreCase("Select Meta Data File")
					&& !metaDataFile.equalsIgnoreCase("You pressed cancel")){
				
				if(!metaDataFile.endsWith(".xml"))
				{
					error = "Error: Kindly Provide A XML Meta data file";
					return error;
				}
				else
				{
					metaFile = new File(metaDataFile);
					metaExists = true;
				}
			} 
			
			createFile = new File(destDir+"/"+bundleName);

			if (create(anchorDir, createFile, metaFile, metaExists) != null){
				error = "Message: " + bundleName+" created successfully!";
				return error;
			}else{
				if(error != null && !error.equalsIgnoreCase(""))
				{
					error = "Error: Creation of pkcs7 file failed!";
					return error;
				}
				else
				{
					error = "Bundle Creation Failed. Please verify the inputs.";
					return error;
				}
			}
		}
		    
	}


	/**
	 * Creates a pcks7 file from the certificate and key files.
	 * @param certFile The X509 DER encoded certificate file.
	 * @param keyFile The PCKS8 DER encoded private key file.
	 * @param password Option password for the private key file.  This is required if the private key file is encrypted.  Should be null or empty
	 * if the private key file is not encrypted.
	 * @param createFile Optional file descriptor for the output file of the pkcs12 file.  If this is null, the file name is based on the 
	 * certificate file name.
	 * @return File descriptor of the created pcks7 file.  Null if an error occurred.  
	 */
	public  File create(String anchorDir, File createFile, File metaFile, boolean metaExists)
	{
		File pkcs7File = null; 

		
		FileOutputStream outStr = null;
		InputStream inStr = null;
		// load cert file
		try
		{
			File userDir = new File(anchorDir);
	        File[] files = userDir.listFiles();
	        
	        X509Certificate[] certs = new X509Certificate[files.length]; 
	        ArrayList<X509Certificate>   certList = new ArrayList<X509Certificate>();
	         
	        int counter = 0;
	        for (File certFile : files) {
	            if (certFile.isFile() && !certFile.isHidden()) {
	               if(certFile.getName().endsWith(".der")){
	            	   byte[] certData = loadFileData(certFile);	
	            	   certs[counter] = getX509Certificate(certData);	            	  
	            	   certList.add(certs[counter]);
	            	   counter++;
	               } 
	            }
	           
	        }
	        if(counter == 0){
         	   error = "Trust Anchors are not available in specified folder!"; 
         	   return null;
            }	
	        byte[] metaDataByte;
	       if(metaExists){
	    	   metaDataByte = loadFileData(metaFile);   
	       }else{
	    	   metaDataByte = "Absent".getBytes();
	       }
		
			CMSTypedData     msg = new CMSProcessableByteArray(metaDataByte);						
			Store  certStores = new JcaCertStore(certList);
		
			CMSSignedDataGenerator gen = new CMSSignedDataGenerator();	      

			//SignedData data = new SignedData(arg0, arg1, arg2, arg3, arg4)
			gen.addCertificates(certStores);	
			CMSSignedData sigData = gen.generate(msg, metaExists);		
			//System.out.println("Inside Unsigned area: Create File:"+createFile);
			pkcs7File = getPKCS7OutFile(createFile);
			outStr = new FileOutputStream(pkcs7File);
			outStr.write(sigData.getEncoded());
			
		}
		catch (CMSException e)
		{			
			//e.printStackTrace(System.err);
			return null;
		} catch (IOException e) {
			//e.printStackTrace(System.err);
			return null;
		} catch (KeyStoreException e) {
			
			//e.printStackTrace(System.err);
			return null;
		} catch (NoSuchProviderException e) {
			
			//e.printStackTrace(System.err);
			return null;
		} catch (NoSuchAlgorithmException e) {
			
			//e.printStackTrace(System.err);
			return null;
		} catch (CertificateException e) {
			
			//e.printStackTrace(System.err);
			return null;
		} catch (UnrecoverableKeyException e) {
			
			//e.printStackTrace(System.err);
			return null;
		} catch (OperatorCreationException e) {
			
			//e.printStackTrace(System.err);
			return null;
		} catch (Exception e) {
			
			//e.printStackTrace(System.err);
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(outStr);
			IOUtils.closeQuietly(inStr);		
		}


		return pkcs7File;
	}

	/**
	 * This method is responsible for writing the .p7m file into the file system.
	 * @param createFile
	 * @returns : File
	 */
	private static File getPKCS7OutFile(File crtFile)
	{
		try
		{
			 if (crtFile != null)
				{
					if (crtFile.exists())
						crtFile.delete();
				
					String fileName = crtFile.getName();
					String filePath = crtFile.getAbsolutePath();
					//System.out.println("The Unsigned file path is:"+filePath);
					int index = fileName.lastIndexOf(".");
					if(!fileName.endsWith(".p7b") || !fileName.endsWith(".p7c"))
					{
						if (index > -1)
							fileName = fileName.substring(0, index);
							 fileName += ".p7c";
					}		 
					createFile = new File(filePath);
					//System.out.println("The Unsigned file created as:"+createFile.getAbsolutePath())
				}
		 }
				catch (Exception e) {
					// TODO: handle exception
					return null;
				}
				return createFile;
	}
	private static X509Certificate getX509Certificate(byte[] certificate) throws Exception {
		
			X509Certificate encCert = null;
			ByteArrayInputStream is;
			try {
				is = new ByteArrayInputStream(certificate);
				CertificateFactory x509CertFact = CertificateFactory.getInstance("X.509");
				encCert = (X509Certificate)x509CertFact.generateCertificate(is);
			} catch (Exception e1) {
				// TODO: handle exception
				throw new Exception("Error loading X.509 encryption cert - probably wrong format", e1);
				//return null;
			}
			return encCert;
	}

	/*
	 * Loads the raw data from the provided file to a byte array.
	 */
	private static byte[] loadFileData(File file) throws Exception
	{		
		return FileUtils.readFileToByteArray(file);
	}	


}
