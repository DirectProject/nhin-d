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
import org.bouncycastle.cms.CMSSignedDataGenerator;

/**
 * Enumeration of supported digest algorithms for message signatures. 
 * @author Greg Meyer
 * @author Umesh Madan
 */
public enum DigestAlgorithm 
{
    MD5("MD5", CMSSignedDataGenerator.DIGEST_MD5),
    SHA1("SHA1", CMSSignedDataGenerator.DIGEST_SHA1),
    SHA256("SHA256", CMSSignedDataGenerator.DIGEST_SHA256),
    SHA384("SHA384", CMSSignedDataGenerator.DIGEST_SHA384),
    SHA512("SHA512", CMSSignedDataGenerator.DIGEST_SHA512);
    
    protected final String algName;
    protected final String OID;
    
    private DigestAlgorithm(String algName, String OID)
    {
    	this.algName = algName;
    	this.OID = OID;
    }
    
    /**
     * Gets the digest algorithm from an algorithm name
     * @param algorithmNam The digest algorithm name
     * @param defaultAlgorithm The default algorithm to return is a corresponding name cannot be found
     * @return A digest algorithm enumeration object corresponding the name
     */
    public static DigestAlgorithm fromString(String algorithmName, DigestAlgorithm defaultAlgorithm)
    {
    	if (algorithmName == null || algorithmName.isEmpty())
    		return defaultAlgorithm;
    	
    	if (algorithmName.equalsIgnoreCase(SHA1.getAlgName()))
    		return SHA1;
    	else if (algorithmName.equalsIgnoreCase(SHA256.getAlgName()))
    		return SHA256;
    	else if (algorithmName.equalsIgnoreCase(SHA384.getAlgName()))
    		return SHA384;
    	else if (algorithmName.equalsIgnoreCase(SHA512.getAlgName()))
    		return SHA512;
    	else if (algorithmName.equalsIgnoreCase(MD5.getAlgName()))
    		return MD5;
    	else
    		return defaultAlgorithm;
    }
    
    /**
     * Gets the digest algorithm from an OID.
     * @param OID The OID of the digest algorithm
     * @param defaultAlgorithm The default algorithm to return is a corresponding OID cannot be found
     * @return A digest algorithm enumeration object corresponding the OID
     */
    public static DigestAlgorithm fromOID(String OID, DigestAlgorithm defaultAlgorithm)
    {
    	if (StringUtils.isEmpty(OID))
    		return defaultAlgorithm;
    	
    	if (OID.equalsIgnoreCase(SHA1.getOID()))
    		return SHA1;
    	else if (OID.equalsIgnoreCase(SHA256.getOID()))
    		return SHA256;
    	else if (OID.equalsIgnoreCase(SHA384.getOID()))
    		return SHA384;
    	else if (OID.equalsIgnoreCase(SHA512.getOID()))
    		return SHA512;
    	else if (OID.equalsIgnoreCase(MD5.getOID()))
    		return MD5;    	
    	else
    		return defaultAlgorithm;
    }
    
    /**
     * Gets the OID of the digest algorithm.
     * @return The OID of the digest algorithm
     */
    public String getOID()
    {
    	return this.OID;
    }
    
    /**
     * Gets the name of the digest algorithm.
     * @return The name of the digest algorithm
     */
    public String getAlgName()
    {
    	return this.algName;
    }
}
