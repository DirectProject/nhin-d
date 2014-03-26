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

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERSequence;
import org.nhindirect.policy.PolicyExpressionReferenceType;
import org.nhindirect.policy.PolicyExpressionType;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyValue;

/**
 * Abstract implementation of the {@link X509Field} interface.
 * @author Greg Meyer
 * @since 1.0
 *
 * @param <P> The object type of the evaluated field of the X509Certiciate.
 */
public abstract class AbstractX509Field<P> implements X509Field<P>
{	
	static final long serialVersionUID = 8705426787272691746L;
	
	protected transient PolicyValue<P> policyValue;
	
	protected transient X509Certificate certificate;
	
	protected boolean required;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PolicyExpressionReferenceType getPolicyExpressionReferenceType() 
	{
		return PolicyExpressionReferenceType.CERTIFICATE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PolicyExpressionType getExpressionType() 
	{
		return PolicyExpressionType.REFERENCE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PolicyValue<P> getPolicyValue() 
	{
		if (this.policyValue == null)
			throw new IllegalStateException("Policy value is null");
		
		return this.policyValue;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequired()
	{
		return required;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public void setRequired(boolean required)
	{
		this.required = required;
	}
	
	/**
	 * Converts an encoded internal octet string object to a DERObject
	 * @param ext The encoded octet string as a byte array
	 * @return The converted DERObject
	 * @throws PolicyProcessException
	 */
    protected DERObject getObject(byte[] ext)
            throws PolicyProcessException
    {
    	ASN1InputStream aIn = null;
        try
        {
            aIn = new ASN1InputStream(ext);
            ASN1OctetString octs = (ASN1OctetString)aIn.readObject();
        	IOUtils.closeQuietly(aIn);
            
            aIn = new ASN1InputStream(octs.getOctets());
            return aIn.readObject();
        }
        catch (Exception e)
        {
            throw new PolicyProcessException("Exception processing data ", e);
        }
        finally
        {
        	IOUtils.closeQuietly(aIn);
        }
    }	
    
	/**
	 * Converts an encoded internal sequence object to a DERObject
	 * @param ext The encoded sequence as a byte array
	 * @return The converted DERObject
	 * @throws PolicyProcessException
	 */
    protected DERObject getDERObject(byte[] ext)
            throws PolicyProcessException
    {
    	ASN1InputStream aIn = null;
        try
        {
            aIn = new ASN1InputStream(ext);
            DERSequence seq = (DERSequence)aIn.readObject();
        	IOUtils.closeQuietly(aIn);
            
            aIn = new ASN1InputStream(seq.getDEREncoded());
            return aIn.readObject();
        }
        catch (Exception e)
        {
            throw new PolicyProcessException("Exception processing data ", e);
        }
        finally
        {
        	IOUtils.closeQuietly(aIn);
        }
    }	
    
    /**
     * {@inheritDoc}
     */
    ///CLOVER:OFF
    @Override
    public String toString()
    {
    	if (policyValue == null)
    		return "Unevaluated X509 field: " + getX509FieldType().toString();
    	else
    		return policyValue.toString();
    }
    ///CLOVER:ON
}
