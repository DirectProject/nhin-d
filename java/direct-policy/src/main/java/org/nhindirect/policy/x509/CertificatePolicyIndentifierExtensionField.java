package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

public class CertificatePolicyIndentifierExtensionField extends AbstractExtensionField<Collection<String>> implements ExtensionField<Collection<String>>
{
	static final long serialVersionUID = -8206701895779041766L;
	
	public CertificatePolicyIndentifierExtensionField(boolean required)
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
		
		final Collection<String> retVal = new ArrayList<String>();
		
		final ASN1Sequence seq = (ASN1Sequence)exValue;
		
		@SuppressWarnings("unchecked")
		final Enumeration<DEREncodable> pols = seq.getObjects();
		while (pols.hasMoreElements())
		{
			final PolicyInformation pol = PolicyInformation.getInstance(pols.nextElement());
			
			retVal.add(pol.getPolicyIdentifier().getId());
		}
		
		this.policyValue = PolicyValueFactory.getInstance(retVal);
	}

	@Override
	public ExtentionIdentifier getExtentionIdentifier() 
	{
		return ExtentionIdentifier.CERTIFICATE_POLICIES;
	}	
}
