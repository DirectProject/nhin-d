package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyValueFactory;

public class SubjectAttributeField extends IssuerAttributeField
{
	static final long serialVersionUID = -6488771961800809924L;
	
	public SubjectAttributeField(boolean required, RDNAttributeIdentifier rdnAttributeId)
	{
		super(required, rdnAttributeId);
	}
	
	@Override
	public void injectReferenceValue(X509Certificate value) throws PolicyProcessException 
	{
		this.certificate = value;
		
		if (rdnAttributeId.equals(RDNAttributeIdentifier.DISTINGUISHED_NAME))
		{
			final Collection<String> str = Arrays.asList(certificate.getSubjectX500Principal().getName(X500Principal.RFC2253));
			this.policyValue = PolicyValueFactory.getInstance(str);
			return;
		}
		
		super.injectReferenceValue(value);
	}
	
	@Override
	public TBSFieldName getFieldName() 
	{
		return TBSFieldName.SUBJECT;
	}
	
	protected X509Name getX509Name(TBSCertificateStructure tbsStruct)
	{
		return tbsStruct.getSubject();
	}
}
