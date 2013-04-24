package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

public class KeyUsageExtensionField extends AbstractExtensionField<Integer> implements ExtensionField<Integer>
{
	static final long serialVersionUID = 9206471801632337363L;
	
	public KeyUsageExtensionField(boolean required)
	{
		super(required);
	}

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
				this.policyValue = PolicyValueFactory.getInstance(0);
				return;
			}
		}
		
		
		final KeyUsage keyUsage = new KeyUsage((DERBitString)exValue);
	
		
		final byte[] data = keyUsage.getBytes();
		
		final int intValue = (data.length == 1) ? data[0] & 0xff : (data[1] & 0xff) << 8 | (data[0] & 0xff);
		
		this.policyValue = PolicyValueFactory.getInstance(intValue);
	}

	@Override
	public ExtentionIdentifier getExtentionIdentifier() 
	{
		return ExtentionIdentifier.KEY_USAGE;
	}	
}
