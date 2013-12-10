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
 * This Class is responsible to create PKCS7 Signed Trust Bundle.
 * The Signed Trust Bundles certificates field contains one or more certificates intended to assist 
 * a Relying party in building a certification path from a trusted “root” or “top-level certification authority”
 * to one or more of the Signers in the Signer Infos field. At a minimum, this MUST include each signer’s certificate.
 * Contains one or more Signer Info in the CMS SignedDataSignerInfos field.
 * Contains the Unsigned Trust Bundle in the CMS SignedDataencapContentInfoeContent field wrapped in a CMS Data object, with eContentType of id-data.
 * @author Amulya
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
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

public class CreateSignedPKCS7 {

	private static File certFile;
	private static File metaFile;
	private static File p12certiFile;
	private static File createFile;
	private String error = "";
	private boolean metaExists = false;
	private static String defaultPwd = "";

	static {
		CryptoExtensions.registerJCEProviders();
	}

	
	/**
	 * This Method validates the user input before proceeding into the generation of singed bundle.
	 * @param anchorDir :The Directory where the .der files are present.
	 * @param metaDataFile :One XML file as per required specification of TrustBundle metadata schema 
	 * @param certificateFile : The .p12 file.
	 * @param passkey :Pass Key for the .p12 file if present or else it should be blank
	 * @param destDir : The Destination folder where the output .p7m files will be created
	 * @param bundleName : The .p7m File name
	 * @return String: The Feedback for the validations.
	 */
	public String getParameters(String anchorDir, String metaDataFile,
			String certificateFile, String passkey, String destDir,
			String bundleName) {

		if (anchorDir.equalsIgnoreCase("Select Trust Anchor Directory")
				|| anchorDir.equalsIgnoreCase("You pressed cancel")) {

			error = "Error: Kindly Provide A Trust Anchor Directory";
			return error;
		} else if (destDir
				.equalsIgnoreCase("Select Trust Bundle Destination Directory")
				|| destDir.equalsIgnoreCase("You pressed cancel")) {
			error = "Error: Kindly Provide A Trust Bundle Destination Directory";
			return error;
		} else if (certificateFile.equalsIgnoreCase("Select Certificate File")
				|| certificateFile.equalsIgnoreCase("You pressed cancel")
				|| !certificateFile.endsWith(".p12")) {

			error = "Error: Kindly Provide A Certificate File with .p12 extension";
			return error;
		}
		else if (bundleName.equalsIgnoreCase("")
				|| !bundleName.endsWith(".p7m")) {
			error = "Error: Kindly Provide A Proper Trust Bundle Name with extension .p7m";
			return error;
		} else {

			if (!metaDataFile.equalsIgnoreCase("Select Meta Data File")
					&& !metaDataFile.equalsIgnoreCase("You pressed cancel")) {
				if (metaDataFile.endsWith(".xml")) {
					metaFile = new File(metaDataFile);
					metaExists = true;
				} else {
					error = "Error: Kindly Provide A XML Meta data file";
					return error;
				}
			}
			// Check Pass key is provided or not
			if (passkey != null) {
				defaultPwd = passkey;
			}

			createFile = new File(destDir + "/" + bundleName);

			p12certiFile = new File(certificateFile);

			if (create(anchorDir, createFile, metaFile, metaExists,
					p12certiFile, defaultPwd) != null) {

				error = "Message: " + bundleName + " created successfully!";
				return error;
			} else {
				if (error != null && !error.equalsIgnoreCase(""))
				{
					error = "Error: Creation of" + bundleName + "file failed!";
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
	 * @param anchorDir :The Directory where the .der files are present.
	 * @param createFile : The .p7m File name.
	 * @param metaFile :One XML file as per required specification of TrustBundle metadata schema. 
	 * @param p12certiFile : The .p12 file.
	 * @param passkey :Pass Key for the .p12 file if present or else it should be blank.
	 * @param destDir : The Destination folder where the output .p7m files will be created.
	 * 	 * @return File : Returns the created SignedBundle as a .p7m file.
	 */
	public File create(String anchorDir, File createFile, File metaFile,
			boolean metaExists, File p12certiFile, String passKey) {

		File pkcs7File = null;
		FileOutputStream outStr = null;
		InputStream inStr = null;
		
		try {
			// Create the unsigned Trust Bundle
			CreateUnSignedPKCS7 unSignedPKCS7 = new CreateUnSignedPKCS7();
			File unsigned = unSignedPKCS7.create(anchorDir, createFile,
					metaFile, metaExists);
			byte[] unsignedByte = loadFileData(unsigned);

			
			CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
			CMSSignedData unsignedData = new CMSSignedData(unsignedByte);

			// Create the certificate array
			KeyStore ks = java.security.KeyStore.getInstance("PKCS12", "BC");
			ks.load(new FileInputStream(p12certiFile), defaultPwd.toCharArray());

			ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();

			Enumeration<String> aliases = ks.aliases();
			while (aliases.hasMoreElements()) {
				String alias = (String) aliases.nextElement();
				if (ks.getKey(alias, defaultPwd.toCharArray()) != null
						&& ks.getKey(alias, defaultPwd.toCharArray()) instanceof PrivateKey) {

					ContentSigner sha1Signer = new JcaContentSignerBuilder(
							"SHA256withRSA").setProvider("BC").build(
							(PrivateKey) ks.getKey(alias,
									defaultPwd.toCharArray()));
					X509CertificateHolder holder = new X509CertificateHolder(ks
							.getCertificate(alias).getEncoded());

					certList.add((X509Certificate) ks.getCertificate(alias));
					gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
							new JcaDigestCalculatorProviderBuilder()
									.setProvider("BC").build()).build(
							sha1Signer, holder));
				}
			}

			Store certStores = new JcaCertStore(certList);
			gen.addCertificates(certStores);
			CMSSignedData sigData = gen.generate(new CMSProcessableByteArray(
					unsignedData.getEncoded()), true);
			//SignedData encapInfo = SignedData.getInstance(sigData.getContentInfo().getContent());

			pkcs7File = getPKCS7OutFile(createFile);
			outStr = new FileOutputStream(pkcs7File);
			outStr.write(sigData.getEncoded());

		} catch (CMSException e) {
			// e.printStackTrace(System.err);
			return null;
		} catch (IOException e) {
			// e.printStackTrace(System.err);
			return null;
		} catch (KeyStoreException e) {

			// e.printStackTrace(System.err);
			return null;
		} catch (NoSuchProviderException e) {

			// e.printStackTrace(System.err);
			return null;
		} catch (NoSuchAlgorithmException e) {

			// e.printStackTrace(System.err);
			return null;
		} catch (CertificateException e) {

			// e.printStackTrace(System.err);
			return null;
		} catch (UnrecoverableKeyException e) {

			// e.printStackTrace(System.err);
			return null;
		} catch (OperatorCreationException e) {

			// e.printStackTrace(System.err);
			return null;
		} catch (Exception e) {

			// e.printStackTrace(System.err);
			return null;
		} finally {
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
	private static File getPKCS7OutFile(File crtFile) {
		try {
			if (crtFile != null) {

				if (crtFile.exists())
					crtFile.delete();
				
				String file = crtFile.getAbsolutePath();

				CreateSignedPKCS7.createFile =  new File(file);
			}

		} catch (Exception e) {
			return null;
		}

		return createFile;
	}

	/*
	 * Loads the raw data from the provided file to a byte array.
	 */
	private static byte[] loadFileData(File file) {
		byte[] fileBytes = null;
		try {
			// FileUtils.touch(file);
			fileBytes = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			return null;
		}
		return fileBytes;
	}

}
