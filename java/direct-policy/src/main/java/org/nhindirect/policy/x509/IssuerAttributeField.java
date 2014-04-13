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
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

/**
 * Issuer field of TBS section of certificate
 * <p>
 * The policy value of this field is returned as a string containing the value of a specific attribute in the issuer relative distinguished name (RDN).
 * <br>
 * If the requested attribute does not exist in the issue RDN, the policy value returned by this class
 * evaluates to an empty string.
 * @author Greg Meyer
 * @since 1.0
 */
public class IssuerAttributeField extends AbstractTBSField<Collection<String>>
{
	static final long serialVersionUID = -8303963586265595510L;
	
	protected final RDNAttributeIdentifier rdnAttributeId;
	
	/**
	 * Constructor
	 * @param required Indicates if the field is required to be present in the certificate to be compliant with the policy.
	 * @param rdnAttributeId Id of the attribute to extract from the issuer RDN
	 */		
	public IssuerAttributeField(boolean required, RDNAttributeIdentifier rdnAttributeId)
	{
		super(required);
		this.rdnAttributeId = rdnAttributeId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TBSFieldName getFieldName() 
	{
		return TBSFieldName.ISSUER;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void injectReferenceValue(X509Certificate value) throws PolicyProcessException 
	{
		this.certificate = value;
		
		if (rdnAttributeId.equals(RDNAttributeIdentifier.DISTINGUISHED_NAME))
		{
			final Collection<String> str = Arrays.asList(certificate.getIssuerX500Principal().getName(X500Principal.RFC2253));
			this.policyValue = PolicyValueFactory.getInstance(str);
			return;
		}
		
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
		
		final X509Name x509Name = getX509Name(tbsStruct);
		
		@SuppressWarnings("unchecked")
		final Vector<String> values = x509Name.getValues(new DERObjectIdentifier(getRDNAttributeFieldId().getId()));
		
		if (values.isEmpty() && this.isRequired())
			throw new PolicyRequiredException(getFieldName() + " field attribute " + rdnAttributeId.getName()  + " is marked as required but is not present.");
		
		final Collection<String> retVal = values; 
			

		this.policyValue = PolicyValueFactory.getInstance(retVal);
	}

	
	/**
	 * Gets the issuer field as an X509Name from the certificate TBS structure.
	 * @param tbsStruct The TBS structure of the certificate
	 * @return the issuer field as an X509Name from the certificate TBS structure.
	 */
	protected X509Name getX509Name(TBSCertificateStructure tbsStruct)
	{
		return tbsStruct.getIssuer();
	}
	
	/**
	 * Gets the requested RDN attribute id.
	 * @return The requested RDN attribute id.
	 */
	public RDNAttributeIdentifier getRDNAttributeFieldId()
	{
		return rdnAttributeId;
	}
}
