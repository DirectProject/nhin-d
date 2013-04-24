package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;
import org.nhindirect.policy.utils.PolicyUtils;

public class AuthorityKeyIdentifierKeyIdExtensionField extends AbstractExtensionField<String> implements ExtensionField<String>
{
	static final long serialVersionUID = 854783066376299385L;
	
	public AuthorityKeyIdentifierKeyIdExtensionField(boolean required) 
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
		
		final AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(exValue);
		
		byte[] keyId =  aki.getKeyIdentifier();
		if (keyId == null)
		{	
			if (isRequired())
				throw new PolicyRequiredException("Extention " + getExtentionIdentifier().getDisplay() + " is marked as required by is not present.");
			else
			{
				this.policyValue = PolicyValueFactory.getInstance("");
				return;
			}
		}
		
		this.policyValue = PolicyValueFactory.getInstance(PolicyUtils.createByteStringRep(keyId));
	}

	@Override
	public ExtentionIdentifier getExtentionIdentifier() 
	{
		return ExtentionIdentifier.AUTHORITY_KEY_IDENTIFIER;
	}	
	
}
