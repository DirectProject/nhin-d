package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

public class AuthorityInfoAccessOCSPLocExtentionField extends AbstractExtensionField<Collection<String>> implements ExtensionField<Collection<String>>
{
	static final long serialVersionUID = 3202084104837274531L;
	
	public AuthorityInfoAccessOCSPLocExtentionField(boolean required)
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
		
		final AuthorityInformationAccess aia = AuthorityInformationAccess.getInstance(exValue);
		
		final Collection<String> retVal = new ArrayList<String>();
		
        for (AccessDescription accessDescription : aia.getAccessDescriptions())
    	{
        	if (accessDescription.getAccessMethod().equals(AccessDescription.id_ad_ocsp))
        		retVal.add(accessDescription.getAccessLocation().getName().toString());
        }
		
		if (retVal.isEmpty() && isRequired())
			throw new PolicyRequiredException("Extention " + getExtentionIdentifier().getDisplay() + " is marked as required by is not present.");		
        
		this.policyValue = PolicyValueFactory.getInstance(retVal);
	}

	@Override
	public ExtentionIdentifier getExtentionIdentifier() 
	{
		return ExtentionIdentifier.AUTHORITY_INFO_ACCESS;
	}
}
