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

package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;

import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyValueFactory;

/**
 * Subject public key info field of TBS section of certificate
 * <p>
 * The policy value of this extension is returned as a string containing the object identifier (OID) of the key algorithm of the subject's public key.
 * 
 * @author Greg Meyer
 * @since 1.0
 */
public class SubjectPublicKeyAlgorithmField extends AbstractTBSField<String>
{
	static final long serialVersionUID = -1094029946830031432L;
	
	/**
	 * Default contructor
	 */
	public SubjectPublicKeyAlgorithmField()
	{
		super(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TBSFieldName getFieldName() 
	{
		return TBSFieldName.SUBJECT_PUBLIC_KEY_INFO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void injectReferenceValue(X509Certificate value) throws PolicyProcessException 
	{
		this.certificate = value;
		
		DERObject tbsValue = null;
		
		try
		{
			tbsValue = this.getDERObject(certificate.getTBSCertificate());
		}
		///CLOVER:OFF
		catch (Exception e)
		{
			throw new PolicyProcessException("Exception parsing TBS certificate fields.", e);
		}
		///CLOVER:ON
		
		final TBSCertificateStructure tbsStruct = TBSCertificateStructure.getInstance(tbsValue);

		this.policyValue = PolicyValueFactory.getInstance(tbsStruct.getSubjectPublicKeyInfo().
				getAlgorithmId().getObjectId().toString());
	}

}
