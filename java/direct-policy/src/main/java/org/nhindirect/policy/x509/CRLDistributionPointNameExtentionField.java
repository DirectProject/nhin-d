package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

public class CRLDistributionPointNameExtentionField extends AbstractExtensionField<Collection<String>> implements ExtensionField<Collection<String>>
{
	static final long serialVersionUID = -6318762452965780915L;
	
	public CRLDistributionPointNameExtentionField(boolean required)
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
				final Collection<String> coll = Collections.emptyList();
				this.policyValue = PolicyValueFactory.getInstance(coll);
				return;
			}
		}
		
		final CRLDistPoint distPoints = CRLDistPoint.getInstance(exValue);
		
		final Collection<String> retVal = new ArrayList<String>();
		
        for (DistributionPoint distPoint : distPoints.getDistributionPoints())
    	{
        	if (distPoint.getDistributionPoint() != null && distPoint.getDistributionPoint().getType() == DistributionPointName.FULL_NAME)
        	{
        		final GeneralNames names = GeneralNames.getInstance(distPoint.getDistributionPoint().getName());
	
        		for (GeneralName name : names.getNames())
        		{
        			retVal.add(name.getName().toString());
        		}
        	}
        }
		
		if (retVal.isEmpty() && isRequired())
			throw new PolicyRequiredException("Extention " + getExtentionIdentifier().getDisplay() + " is marked as required by is not present.");		
        
		this.policyValue = PolicyValueFactory.getInstance(retVal);
	}

	@Override
	public ExtentionIdentifier getExtentionIdentifier() 
	{
		return ExtentionIdentifier.CRL_DISTRIBUTION_POINTS;
	}
}
