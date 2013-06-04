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

package org.nhindirect.trustbundle.core;

/**
 * Application class for creating PKCS12 files from X509 DER encoded files and PKCS8 DER encoded private key files.  Unlike the Java keytool application,
 * CreatePKCS12 creates pcks7 files without a passphrase and can accept encrypted private key files.
 * 
 * @author Satyajeet
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.LoadStoreParameter;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

///CLOVER:OFF
public class CreateSignedPKCS7 
{

	private static File certFile;
	private static File metaFile;	
	private static File p12certiFile;
	private static File createFile;
	private String error ="";
	private boolean metaExists = false;
	private static String defaultPwd="";

	static
	{
		CryptoExtensions.registerJCEProviders();
	}

	/**
	 * Main entry point when running as an application.  Use the -help option for usage.
	 * @param argv Application arguments.
	 */
	public String getParameters(String anchorDir, String metaDataFile, String certificateDir, String passkey,String destDir, String bundleName)
	{
		
		if(anchorDir.equalsIgnoreCase("Select Trust Anchor Directory")
				|| anchorDir.equalsIgnoreCase("You pressed cancel"))
		{
			
			error = "Error: Kindly Provide A Trust Anchor Directory";
			return error;
		}
		else if(destDir.equalsIgnoreCase("Select Trust Bundle Destination Directory")
				|| destDir.equalsIgnoreCase("You pressed cancel")){
			error = "Error: Kindly Provide A Trust Bundle Destination Directory";
			return error;
		}else if(certificateDir.equalsIgnoreCase("Select Certificate File")
				|| certificateDir.equalsIgnoreCase("You pressed cancel") ||!certificateDir.endsWith(".p12")){
			
			error = "Error: Kindly Provide A Certificate File";
			return error;
		}
		/*else if(passkey.equalsIgnoreCase("")){
			error = "Error: Kindly Provide Pass key for Certificate File";
			return error;
		}*/
		else if(bundleName.equalsIgnoreCase("") || !bundleName.endsWith(".p7m")){
			error = "Error: Kindly Provide A Proper Trust Bundle Name";
			return error;
		}else{

			if(!metaDataFile.equalsIgnoreCase("Select Meta Data File")
					&& !metaDataFile.equalsIgnoreCase("You pressed cancel")){
				//System.out.println("Metadata file field value");
				if(metaDataFile.endsWith(".xml"))
				{
					metaFile = new File(metaDataFile);
					metaExists = true;
				}
				else
				{
					error = "Error: Kindly Provide A XML Meta data file";
					return error;
				}
			} 

			//Check Pass key is provided or not
			if(passkey!=null)
			{
				defaultPwd = passkey;
			}
			
			createFile = new File(destDir+"/"+bundleName);
			//System.out.println("File will be at:"+createFile);
			//System.out.println("Meta File will be at:"+metaFile);
			
			p12certiFile = new File(certificateDir);
			//System.out.println("File12 will be at:"+certificateDir);
			if (create(anchorDir, createFile, metaFile, metaExists,p12certiFile,defaultPwd) != null){
				//error = "Created pcks7 file " + createFile.getAbsolutePath();
				error = "Message: " + bundleName+" created successfully!";
				return error;
			}else{
				if(error != null && !error.equalsIgnoreCase(""))
					error = "Error: Creation of"+ bundleName +"file failed!";
				return error;
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
	public  File create(String anchorDir, File createFile, File metaFile, boolean metaExists, File p12certiFile, String passKey)
	{



		File pkcs7File = null;	
		FileOutputStream outStr = null;
		InputStream inStr = null;
		// load cert file
		try
		{
			// Create the unsigned Trust Bundle
			CreateUnSignedPKCS7 unSignedPKCS7 = new CreateUnSignedPKCS7();
			File unsigned = unSignedPKCS7.create(anchorDir, createFile, metaFile, metaExists);
			byte[] unsignedByte = loadFileData(unsigned);	

			//CMSTypedData     msg = new CMSProcessableByteArray(unsignedByte);
			CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
			CMSSignedData unsignedData = new CMSSignedData(unsignedByte);

			// Create the certificate array
			KeyStore ks = java.security.KeyStore.getInstance("PKCS12", "BC");
			
			ks.load(new FileInputStream(p12certiFile),defaultPwd.toCharArray());

			
			ArrayList<X509Certificate>   certList = new ArrayList<X509Certificate>();
			
			Enumeration<String> aliases = ks.aliases();
			while (aliases.hasMoreElements()) {
				String alias = (String)aliases.nextElement();
						//System.out.println("The KS Alias:"+alias);
				if(ks.getKey(alias, defaultPwd.toCharArray()) != null && ks.getKey(alias, defaultPwd.toCharArray()) instanceof PrivateKey){					
				
					ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build((PrivateKey)ks.getKey(alias, defaultPwd.toCharArray()));
					X509CertificateHolder holder = new X509CertificateHolder(ks.getCertificate(alias).getEncoded());
					
					certList.add((X509Certificate)ks.getCertificate(alias));	
					gen.addSignerInfoGenerator(
							new JcaSignerInfoGeneratorBuilder(
									new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
							.build(sha1Signer,holder ));
				}
			}
									
			Store  certStores = new JcaCertStore(certList);
			gen.addCertificates(certStores);	
			CMSSignedData sigData = gen.generate(new CMSProcessableByteArray(unsignedData.getEncoded()), true);
			SignedData encapInfo = SignedData.getInstance(sigData.getContentInfo().getContent());
			//System.out.println("The Encap Content info:"+new String(encapInfo.getEncapContentInfo().getContent().getDERObject().getEncoded(),"UTF-8"));

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

	/*
	 * Creates the output file descriptor and creates the new file on the file system.
	 */
	private static File getPKCS7OutFile(File createFile)
	{
		try
		{
			if (createFile == null)
			{
	
				String fileName = certFile.getName();
	
				int index = fileName.lastIndexOf(".");
				if (index > -1)
					fileName = fileName.substring(0, index);
	
				fileName += ".p7m";
				CreateSignedPKCS7.createFile = createFile = new File(fileName);
			}		
	
			if (createFile.exists())
				createFile.delete();
	
			createFile.createNewFile();
		}
		catch (IOException io) {
			return null;
		}
		catch (Exception e) {
			return null;
		}
	
			return createFile;		
	}	

	/*
	 * Loads the raw data from the provided file to a byte array.
	 */
	private static byte[] loadFileData(File file)
	{	
		byte [] fileBytes =null;
		try
		{
			//FileUtils.touch(file);
		fileBytes = FileUtils.readFileToByteArray(file);
		}
		catch (IOException e) {
			return null;
		}
		return fileBytes;
	}	



}
