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
 * 
 * @author Amulya Kumar Mishra
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.asn1.cms.SignedDataParser;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.asn1.cms.OriginatorInfo;

///CLOVER:OFF
public class ViewTrustBundlePKCS7 
{

	private static File certiFile;
	private String error ="";
	

	static
	{
		CryptoExtensions.registerJCEProviders();
	}

	/**
	 * Main entry point when running as an application.  Use the -help option for usage.
	 * @param argv Application arguments.
	 */
	public String getParameters(String trustDir)
	{
		if(trustDir.equalsIgnoreCase("Select Trust Anchor Directory")
				|| trustDir.equalsIgnoreCase("You pressed cancel"))
		{
			error = "Error: Kindly Provide Trust Anchor Directory";
			return error;
		}
		else{			
			
			certiFile = new File(trustDir);
			
			if(!trustDir.endsWith(".p7c") && !trustDir.endsWith(".p7m") && !trustDir.endsWith(".p7b"))
			{
				error = "Error:Please provide a valid file!";
				return error;
			}
			else if (viewBundle(certiFile) == true){
				//error = "Created pcks7 file " + createFile.getAbsolutePath();
				return error;
			}else{
				if(error != null && !error.equalsIgnoreCase(""))
				error = "Preview of pkcs7 file failed!";
				return error;
			}
		}    
	}


	@SuppressWarnings({ "rawtypes" })
	public  boolean  viewBundle(File trustDir)
	{		
		try
		{ 
	     
			//System.out.println("File:"+trustDir.getName());
			if(!trustDir.getName().endsWith(".p7m"))
			{
			        byte[] trustBundleByte = loadFileData(trustDir);   
			        CertificateFactory cf = CertificateFactory.getInstance("X.509");
			        CMSSignedData  dataParser = new CMSSignedData(trustBundleByte);
			        ContentInfo contentInfo = dataParser.getContentInfo();
			        SignedData signedData = SignedData.getInstance(contentInfo.getContent());
			        Enumeration certificates = signedData.getCertificates().getObjects();
		
			        StringBuffer output = new StringBuffer();
			        int counter = 1;
			        String chk = "Absent";
			     // Build certificate path
			        
			        while (certificates.hasMoreElements()) {
			            DERObject certObj = (DERObject) certificates.nextElement();
			            InputStream in = new ByteArrayInputStream(certObj.getDEREncoded());
			            X509Certificate cert = (X509Certificate) cf.generateCertificate(in);
			            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
			            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
			            output.append("Trust Anchor :"+counter+"\n");
			            output.append("Common Name :"+IETFUtils.valueToString(cn.getFirst().getValue())+"\n");
			            output.append("DN :"+cert.getSubjectDN().getName()+"\n\n");
			            counter++;
			        } 
			        if(signedData.getEncapContentInfo().getContent() != null){	        	
			        //chk = new String(signedData.getEncapContentInfo().getContent().getDERObject().getEncoded(),"UTF-8");
			        	chk = new String(signedData.getEncapContentInfo().getContent().getDERObject().getDEREncoded(),"UTF-8");
			        }
			        output.append("Meta Data :\n"+chk);             
			        error = output.toString();
			}//end of if check of file type
			else
			{
				
				StringBuffer output = new StringBuffer();
		        int counter = 1;
		        String chk = "Absent";
				byte[] trustBundleByte = loadFileData(trustDir);   
				
				CMSSignedData  dataParser = new CMSSignedData(trustBundleByte);
		        ContentInfo contentInfo = dataParser.getContentInfo();
		        SignedData signedData = SignedData.getInstance(contentInfo.getContent());
		        
		        
		        CMSSignedData encapInfoBundle = new CMSSignedData(new CMSProcessableByteArray(signedData.getEncapContentInfo().getContent().getDERObject().getEncoded()),contentInfo);
		        SignedData encapMetaData = SignedData.getInstance(encapInfoBundle.getContentInfo().getContent());
		        //System.out.println("ENCAP META DATA"+new String(encapMetaData.getEncapContentInfo().getContent().getDERObject().getEncoded(),"UTF-8"));
		        
		        
		        CMSProcessableByteArray cin = new CMSProcessableByteArray(((ASN1OctetString)encapMetaData.getEncapContentInfo().getContent()).getOctets());
		        CertificateFactory ucf = CertificateFactory.getInstance("X.509");
		        
		        CMSSignedData  unsignedParser = new CMSSignedData(cin.getInputStream());
		        ContentInfo unsginedEncapInfo = unsignedParser.getContentInfo();
		        SignedData metaData = SignedData.getInstance(unsginedEncapInfo.getContent());
		        Enumeration certificates = metaData.getCertificates().getObjects();
		        
		        // Build certificate path
		        
		        while (certificates.hasMoreElements()) {
		            DERObject certObj = (DERObject) certificates.nextElement();
		            InputStream bin = new ByteArrayInputStream(certObj.getDEREncoded());
		            X509Certificate cert = (X509Certificate) ucf.generateCertificate(bin);
		            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
		            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
		            output.append("Trust Anchor :"+counter+"\n");
		            output.append("Common Name :"+IETFUtils.valueToString(cn.getFirst().getValue())+"\n");
		            output.append("DN :"+cert.getSubjectDN().getName()+"\n\n");
		            counter++;
		        } 
		        if(metaData.getEncapContentInfo().getContent() != null){	        	
		        //chk = new String(signedData.getEncapContentInfo().getContent().getDERObject().getEncoded(),"UTF-8");
		        	chk = new String(metaData.getEncapContentInfo().getContent().getDERObject().getDEREncoded(),"UTF-8");
		        }
		        output.append("Meta Data :\n"+chk);             
		        error = output.toString();
		        		        
			} //end of .p7m check if
		}//end of try
		catch (IOException io)
		{			
			//io.printStackTrace(System.err);
			return false;
		}
		catch (CMSException cm) {
			//cm.printStackTrace(System.err);
			return false;
		}
		catch (Exception e) {
			//e.printStackTrace(System.err);
			return false;
		}
		return true;
		
	}	
	
	/*
	 * Loads the raw data from the provided file to a byte array.
	 */
	private static byte[] loadFileData(File file)
	{		
		byte [] fileBytes = null;
		try
		{
			fileBytes = FileUtils.readFileToByteArray(file);
		}
		catch (IOException io) {
			//io.printStackTrace(System.err);
			return null;
		}
		catch (Exception e) {
			//e.printStackTrace(System.err);
			return null;
		}
		return fileBytes;
	}	


}
