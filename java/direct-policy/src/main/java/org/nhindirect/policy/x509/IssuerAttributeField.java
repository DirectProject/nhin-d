package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.PolicyValueFactory;

public class IssuerAttributeField extends AbstractTBSField<Collection<String>>
{
	static final long serialVersionUID = -8303963586265595510L;
	
	protected final RDNAttributeIdentifier rdnAttributeId;
	
	public IssuerAttributeField(boolean required, RDNAttributeIdentifier rdnAttributeId)
	{
		super(required);
		this.rdnAttributeId = rdnAttributeId;
	}
	
	@Override
	public TBSFieldName getFieldName() 
	{
		return TBSFieldName.ISSUER;
	}

	@Override
	public void injectReferenceValue(X509Certificate value) throws PolicyProcessException 
	{
		this.certificate = value;
		
		if (rdnAttributeId.equals(RDNAttributeIdentifier.DISTINGUISHED_NAME))
		{
			final Collection<String> str = Arrays.asList(certificate.getIssuerX500Principal().getName(X500Principal.RFC2253));
			this.policyValue = PolicyValueFactory.getInstance(str);
			return;
		}
		
		DERObject tbsValue = null;
		
		try
		{
			tbsValue = this.getDERObject(certificate.getTBSCertificate());
		}
		catch (Exception e)
		{
			throw new PolicyProcessException("Exception parsing TBS certificate fields.", e);
		}
		
		final TBSCertificateStructure tbsStruct = TBSCertificateStructure.getInstance(tbsValue);
		
		final X509Name x509Name = getX509Name(tbsStruct);
		
		@SuppressWarnings("unchecked")
		final Vector<String> values = x509Name.getValues(new DERObjectIdentifier(getRDNAttributeFieldId().getId()));
		
		if (values.isEmpty() && this.isRequired())
			throw new PolicyRequiredException(getFieldName() + " field attribute " + rdnAttributeId.getName()  + " is marked as required but is not present.");
		
		final Collection<String> retVal = values; 
			

		this.policyValue = PolicyValueFactory.getInstance(retVal);
	}

	protected X509Name getX509Name(TBSCertificateStructure tbsStruct)
	{
		return tbsStruct.getIssuer();
	}
	
	public RDNAttributeIdentifier getRDNAttributeFieldId()
	{
		return rdnAttributeId;
	}
}
