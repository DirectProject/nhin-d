package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;


import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;
import org.nhindirect.policy.utils.PolicyUtils;

public class SubjectKeyIdentifierExtensionField extends AbstractExtensionField<String> implements ExtensionField<String>
{
	static final long serialVersionUID = 4940506438781925043L;
	
	public SubjectKeyIdentifierExtensionField(boolean required)
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
				this.policyValue = PolicyValueFactory.getInstance("");
				return;
			}
		}
		
		final SubjectKeyIdentifier keyId = SubjectKeyIdentifier.getInstance(exValue);
		
		keyId.getKeyIdentifier();
		
		
		this.policyValue = PolicyValueFactory.getInstance(PolicyUtils.createByteStringRep(keyId.getKeyIdentifier()));
	}

	@Override
	public ExtentionIdentifier getExtentionIdentifier() 
	{
		return ExtentionIdentifier.SUBJECT_KEY_IDENTIFIER;
	}	
	
	
}
