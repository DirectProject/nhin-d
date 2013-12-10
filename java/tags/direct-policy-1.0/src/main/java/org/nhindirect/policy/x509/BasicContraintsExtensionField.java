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
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

/**
 * Basic constraint extension field.
 * <p>
 * The policy value of this extension is returned boolean value indicating if the certificate is a CA or not.  It does not return the maximum path length.
 * <br>
 * If the extension does not exist in the certificate, the policy value returned by this class evaluates to false.
 *
 * @author Greg Meyer
 * @since 1.0
 */
public class BasicContraintsExtensionField extends AbstractExtensionField<Boolean> implements ExtensionField<Boolean>
{
	static final long serialVersionUID = -2492905720544301651L;
	
	/**
	 * Constructor
	 * @param required Indicates if the field is required to be present in the certificate to be compliant with the policy.
	 */	
	public BasicContraintsExtensionField(boolean required)
	{
		super(required);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void injectReferenceValue(X509Certificate value) throws PolicyProcessException
	{
		this.certificate = value;
		
		final DERObject exValue = getExtensionValue(value);
		
		if (exValue == null)
		{
			if (isRequired())
				throw new PolicyRequiredException("Extention " + getExtentionIdentifier().getDisplay() + " is marked as required by is not present.");
			else
			{
				this.policyValue = PolicyValueFactory.getInstance(false);
				return;
			}
		}
		
		final BasicConstraints constraints = BasicConstraints.getInstance(exValue);
		
		this.policyValue = PolicyValueFactory.getInstance(constraints.isCA());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtensionIdentifier getExtentionIdentifier() 
	{
		return ExtensionIdentifier.BASIC_CONSTRAINTS;
	}	
}
