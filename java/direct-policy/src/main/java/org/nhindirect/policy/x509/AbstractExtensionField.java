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
import java.util.Set;

import org.bouncycastle.asn1.DERObject;
import org.nhindirect.policy.PolicyProcessException;

/**
 * Abstract implementation of the {@link ExtensionField} interface.
 * @author Greg Meyer
 * @since 1.0
 * 
 * @param <P> The object type of the evaluated extension value.
 */
public abstract class AbstractExtensionField<P> extends AbstractTBSField<P> implements ExtensionField<P>
{
	static final long serialVersionUID = 5312652953460972702L;
	
	/**
	 * Constructor
	 * @param required Indicates if the field is required to be present in the certificate to be compliant with the policy.
	 */
	public AbstractExtensionField(boolean required)
	{
		super(required);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TBSFieldName getFieldName() 
	{
		return TBSFieldName.EXTENSIONS;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCritical() 
	{
		if (this.certificate == null)
			throw new IllegalStateException("Certificate value is null");
		
		final Set<String> criticalOIDs = certificate.getCriticalExtensionOIDs();
		
		return criticalOIDs.contains(getExtentionIdentifier().getId());
	}
	
	/**
	 * Gets the specified certificate extension field from the certificate as a DERObject.  The extension field is 
	 * determined by the concrete implementation's returned value of the {@link #getExtentionIdentifier()} method.
	 * @param cert  The certificate to extract the extension field from.
	 * @return The extension field as DERObject.  If the extension does not exist in the certificate, then null is returned.
	 * @throws PolicyProcessException
	 */
    protected DERObject getExtensionValue(X509Certificate cert)
            throws PolicyProcessException
    {
    	final String oid = getExtentionIdentifier().getId();
    	
        byte[]  bytes = cert.getExtensionValue(oid);
        if (bytes == null)
        {
            return null;
        }

        return getObject(bytes);
    }
    
    /**
     * {@inheritDoc}
     */
    ///CLOVER:OFF
    @Override
    public String toString()
    {
    	if (policyValue == null)
    		return "Unevaluated extension field: " + getExtentionIdentifier().toString();
    	else
    		return policyValue.toString();
    }
   ///CLOVER:ON
}
