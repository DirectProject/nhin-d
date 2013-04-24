package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

public class ExtendedKeyUsageExtensionField extends AbstractExtensionField<Collection<String>> implements ExtensionField<Collection<String>>
{
	static final long serialVersionUID = -2492905720544301651L;
	
	public ExtendedKeyUsageExtensionField(boolean required)
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
				final Collection<String> emptyList = Collections.emptyList();
				this.policyValue = PolicyValueFactory.getInstance(emptyList);
				return;
			}
		}
		
		final ExtendedKeyUsage usages = ExtendedKeyUsage.getInstance(exValue);
		
		@SuppressWarnings("unchecked")
		final Collection<DERObjectIdentifier> purposeList = usages.getUsages();
		
		final Collection<String> usageList = new ArrayList<String>();
		for (DERObjectIdentifier purpose : purposeList)
			usageList.add(purpose.getId());
		
		this.policyValue = PolicyValueFactory.getInstance(usageList);
	}

	@Override
	public ExtentionIdentifier getExtentionIdentifier() 
	{
		return ExtentionIdentifier.EXTENDED_KEY_USAGE;
	}	
}
