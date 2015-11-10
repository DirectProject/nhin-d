/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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

package org.nhindirect.stagent.cryptography;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;

/**
 * Enumeration of supported encryption algorithms for message encryption. 
 * @author Greg Meyer
 * @author Umesh Madan
 */
public enum EncryptionAlgorithm 
{
    RSA_3DES("RSA_3DES" ,SMIMEEnvelopedGenerator.DES_EDE3_CBC),
    AES128("AES128" ,SMIMEEnvelopedGenerator.AES128_CBC),
    AES192("AES192" ,SMIMEEnvelopedGenerator.AES192_CBC),
    AES256("AES256" ,SMIMEEnvelopedGenerator.AES256_CBC),
    DSA("DSA", CMSSignedDataGenerator.ENCRYPTION_DSA),
    RSA("RSA", CMSSignedDataGenerator.ENCRYPTION_RSA),
    RSAandMGF1("RSAandMGF1", CMSSignedDataGenerator.ENCRYPTION_RSA_PSS),
    ECDSA("ECDSA", CMSSignedDataGenerator.ENCRYPTION_ECDSA),
    DES_EDE3_CBC("DESEDE/CBC/PKCS5Padding", CMSEnvelopedGenerator.DES_EDE3_CBC),
    AES128_CBC("AES/CBC/PKCS5Padding", CMSEnvelopedGenerator.AES128_CBC),
    AES192_CBC("AES/CBC/PKCS5Padding", CMSEnvelopedGenerator.AES192_CBC),
    AES256_CBC("AES/CBC/PKCS5Padding", CMSEnvelopedGenerator.AES256_CBC);
    
    protected final String algName;
    protected final String OID;
    
    private EncryptionAlgorithm(String algName, String OID)
    {
    	this.algName = algName;
    	this.OID = OID;
    }
    
    /**
     * Gets the encryption algorithm from an algorithm name
     * @param algorithmNam The encryption algorithm name
     * @param defaultAlgorithm The default algorithm to return is a corresponding name cannot be found
     * @return A encryption algorithm enumeration object corresponding the name
     */
    public static EncryptionAlgorithm fromString(String algorithmName, EncryptionAlgorithm defaultAlgorithm)
    {
    	if (algorithmName == null || algorithmName.isEmpty())
    		return defaultAlgorithm;
    	
    	if (algorithmName.equalsIgnoreCase(RSA_3DES.getAlgName()))
    		return RSA_3DES;
    	else if (algorithmName.equalsIgnoreCase(AES128.getAlgName()))
    		return AES128;
    	else if (algorithmName.equalsIgnoreCase(AES192.getAlgName()))
    		return AES192;
    	else if (algorithmName.equalsIgnoreCase(AES256.getAlgName()))
    		return AES256;
    	else if (algorithmName.equalsIgnoreCase(DSA.getAlgName()))
    		return DSA;   
    	else if (algorithmName.equalsIgnoreCase(RSA.getAlgName()))
    		return RSA;    
    	else if (algorithmName.equalsIgnoreCase(RSAandMGF1.getAlgName()))
    		return RSAandMGF1;  
    	else if (algorithmName.equalsIgnoreCase(ECDSA.getAlgName()))
    		return ECDSA;        	
    	else
    		return defaultAlgorithm;
    }
    
    /**
     * Gets the encryption algorithm from an OID.
     * @param OID The OID of the encryption algorithm
     * @param defaultAlgorithm The default algorithm to return is a corresponding OID cannot be found
     * @return A encryption algorithm enumeration object corresponding the OID
     */
    public static EncryptionAlgorithm fromOID(String OID, EncryptionAlgorithm defaultAlgorithm)
    {
    	if (StringUtils.isEmpty(OID))
    		return defaultAlgorithm;
    	
    	if (OID.equalsIgnoreCase(RSA_3DES.getOID()))
    		return RSA_3DES;
    	else if (OID.equalsIgnoreCase(RSA_3DES.getOID()))
    		return RSA_3DES;
    	else if (OID.equalsIgnoreCase(AES128.getOID()))
    		return AES128;
    	else if (OID.equalsIgnoreCase(AES192.getOID()))
    		return AES192;
    	else if (OID.equalsIgnoreCase(AES256.getOID()))
    		return AES256;
    	else if (OID.equalsIgnoreCase(DSA.getOID()))
    		return DSA;
    	else if (OID.equalsIgnoreCase(RSA.getOID()))
    		return RSA;
    	else if (OID.equalsIgnoreCase(RSAandMGF1.getOID()))
    		return RSAandMGF1;  	
    	else if (OID.equalsIgnoreCase(ECDSA.getOID()))
    		return ECDSA;  
    	else
    		return defaultAlgorithm;
    }
    
    /**
     * Gets the OID of the encryption algorithm.
     * @return The OID of the encryption algorithm
     */
    public String getOID()
    {
    	return this.OID;
    }
    
    /**
     * Gets the name of the encryption algorithm.
     * @return The name of the encryption algorithm
     */
    public String getAlgName()
    {
    	return this.algName;
    }
}
