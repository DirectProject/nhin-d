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

package org.nhindirect.stagent.cryptography.activekeyops;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.nhindirect.stagent.CryptoExtensions;

/**
 * Default implementation of the DirectSignedDataGenerator that functions as a pass through to the BouncyCastle 
 * CMSSignedDataGenerator implementation.
 * @author Greg Meyer
 * @since 2.1
 */
public class DefaultDirectSignedDataGenerator implements DirectSignedDataGenerator
{
	protected final CMSSignedDataGenerator generator;
	protected final String sigProvider;
	
	/**
	 * Constructor
	 * @param sigProvider The name of the JCE provider used to perform the signing and digest operations.  If this is null or empty,
	 * the CryptoExtensions.getJCESensitiveProviderName() value will be used.
	 */
	public DefaultDirectSignedDataGenerator(String sigProvider)
	{
		this.sigProvider =  (StringUtils.isEmpty(sigProvider)) ? CryptoExtensions.getJCESensitiveProviderName() : sigProvider;
		generator = new CMSSignedDataGenerator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSigner(PrivateKey key, X509Certificate cert,
		    String digestOID, AttributeTable  signedAttr,
		    AttributeTable unsignedAttr) throws IllegalArgumentException
    {
		generator.addSigner(key, cert, digestOID, signedAttr, unsignedAttr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCertificatesAndCRLs(CertStore certStore) throws CertStoreException, CMSException
	{
		generator.addCertificatesAndCRLs(certStore);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CMSSignedData generate(CMSProcessable content) throws NoSuchAlgorithmException, NoSuchProviderException, CMSException
	{
		return generator.generate(content, false, sigProvider);
	}
}
