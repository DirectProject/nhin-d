package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

public class BasicContraintsExtensionField extends AbstractExtensionField<Boolean> implements ExtensionField<Boolean>
{
	static final long serialVersionUID = -2492905720544301651L;
	
	public BasicContraintsExtensionField(boolean required)
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
				this.policyValue = PolicyValueFactory.getInstance(false);
				return;
			}
		}
		
		final BasicConstraints constraints = BasicConstraints.getInstance(exValue);
		
		this.policyValue = PolicyValueFactory.getInstance(constraints.isCA());
	}

	@Override
	public ExtentionIdentifier getExtentionIdentifier() 
	{
		return ExtentionIdentifier.BASIC_CONSTRAINTS;
	}	
}
