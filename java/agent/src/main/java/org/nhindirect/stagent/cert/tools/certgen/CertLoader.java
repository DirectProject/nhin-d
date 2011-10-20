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

package org.nhindirect.stagent.cert.tools.certgen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.FileUtils;
import org.nhindirect.stagent.CryptoExtensions;

/**
 * Loads certificates and associated private key files from the file system.  Passwords are optional, but must be presend
 * if the private key file is encrypted.
 * @author Greg Meyer
 *
 */
///CLOVER:OFF
class CertLoader 
{
    static
    {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }	
	
	public static CertCreateFields loadCertificate(File certFile, File keyFile, char[] password) throws Exception
	{
		byte[] certData = loadFileData(certFile);
		byte[] keyData = loadFileData(keyFile);
		
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream inStr = new ByteArrayInputStream(certData);
		java.security.cert.Certificate holdCert = cf.generateCertificate(inStr);
		X509Certificate cert = (X509Certificate)holdCert;
		inStr.close();
		
		KeyFactory kf = KeyFactory.getInstance("RSA", CryptoExtensions.getJCEProviderName());
		PKCS8EncodedKeySpec keysp = null;
		if (password != null && password.length > 0)
		{
			EncryptedPrivateKeyInfo encInfo = new EncryptedPrivateKeyInfo(keyData);
			PBEKeySpec keySpec = new PBEKeySpec(password);
			String alg = encInfo.getAlgName();
			
			SecretKeyFactory secFactory = SecretKeyFactory.getInstance(alg, CryptoExtensions.getJCEProviderName()); 
			SecretKey secKey = secFactory.generateSecret(keySpec);
			keysp = encInfo.getKeySpec(secKey, CryptoExtensions.getJCEProviderName());
		}
		else
		{
			keysp = new PKCS8EncodedKeySpec ( keyData );
		}
			
		PrivateKey privKey = kf.generatePrivate (keysp);
		
		Map<String, Object> attributes = getAttributes(cert);
		
		Calendar now = Calendar.getInstance();
		Calendar exp = Calendar.getInstance();
		exp.setTime(cert.getNotAfter());
		
		long diff = exp.getTimeInMillis() - now.getTimeInMillis();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		// TODO: get the key strength
		int keyStr = 1024;  // just hard coded
		
		CertCreateFields retVal = new CertCreateFields(attributes, certFile, keyFile, password, (int)diffDays, keyStr, cert, privKey);
		
		return retVal;
	}
	
	private static Map<String, Object> getAttributes(X509Certificate cert)
	{
		Map<String, Object> retVal = new HashMap<String, Object>();
		
		// for now just do a simple parse of the DN
		Map<String, String> oidMap = new HashMap<String, String>();
		oidMap.put("1.2.840.113549.1.9.1", "EMAILADDRESS");  // OID for email address
		String prinName = cert.getSubjectX500Principal().getName(X500Principal.RFC1779, oidMap);   
		
		String[] attributes = prinName.split(",");
		if (attributes != null)
			for (String attr : attributes)
			{
				String[] nameValue = attr.split("=");
				if  (nameValue != null && nameValue.length == 2)
					retVal.put(nameValue[0].trim(), nameValue[1].trim());
			}
		
		return retVal;
	}
	
	private static byte[] loadFileData(File file) throws Exception
	{		
		return FileUtils.readFileToByteArray(file);
	}	
}
///CLOVER:ON