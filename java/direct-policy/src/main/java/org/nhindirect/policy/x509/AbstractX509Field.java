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

public abstract class AbstractX509Field<P> implements X509Field<P>
{	
	static final long serialVersionUID = 8705426787272691746L;
	
	protected transient PolicyValue<P> policyValue;
	
	protected transient X509Certificate certificate;
	
	protected boolean required;
	
	@Override
	public PolicyExpressionReferenceType getPolicyExpressionReferenceType() 
	{
		return PolicyExpressionReferenceType.CERTIFICATE;
	}
	
	@Override
	public PolicyExpressionType getExpressionType() 
	{
		return PolicyExpressionType.REFERENCE;
	}
	
	@Override
	public PolicyValue<P> getPolicyValue() 
	{
		if (this.policyValue == null)
			throw new IllegalStateException("Policy value is null");
		
		return this.policyValue;
	}
	
	@Override
	public boolean isRequired()
	{
		return required;
	}
	
	@Override 
	public void setRequired(boolean required)
	{
		this.required = required;
	}
	
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
}
