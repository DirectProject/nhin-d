/**
 * CRL distribution point extension field.
 * <p>
 * The policy value of this extension is returned as a collection of strings containing the URL of the CRL distribution point.  This class only
 * retrieves distribution points using the fullName attribute of the DistributionPointName structure as opposed to the nameRelativeToCRLIssuer
 * attribute.
 * <br>
 * If the extension does not exist in the certificate or non of the distribution points use the fullName attribute, this policy value returned by this class
 * evaluates to an empty collection.
 * @author Greg Meyer
 * @since 1.0
 */

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

/**
 * Extended key usage extension field.
 * <p>
 * The policy value of this extension is returned as a collection of strings containing object identifiers (OIDs) of the extended key usages.  
 * <br>
 * If the extension does not exist in the certificate, the policy value returned by this class
 * evaluates to an empty collection.
 * @author Greg Meyer
 * @since 1.0
 */
public class ExtendedKeyUsageExtensionField extends AbstractExtensionField<Collection<String>> implements ExtensionField<Collection<String>>
{
	static final long serialVersionUID = -2492905720544301651L;
	
	/**
	 * Constructor
	 * @param required Indicates if the field is required to be present in the certificate to be compliant with the policy.
	 */	
	public ExtendedKeyUsageExtensionField(boolean required)
	{
		super(required);
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtensionIdentifier getExtentionIdentifier() 
	{
		return ExtensionIdentifier.EXTENDED_KEY_USAGE;
	}	
}
