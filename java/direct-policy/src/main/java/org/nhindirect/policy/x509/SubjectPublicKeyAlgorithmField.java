package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;

import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyValueFactory;

public class SubjectPublicKeyAlgorithmField extends AbstractTBSField<String>
{
	static final long serialVersionUID = -1094029946830031432L;
	
	public SubjectPublicKeyAlgorithmField()
	{
		super(true);
	}
	
	@Override
	public TBSFieldName getFieldName() 
	{
		return TBSFieldName.SUBJECT_PUBLIC_KEY_INFO;
	}

	@Override
	public void injectReferenceValue(X509Certificate value) throws PolicyProcessException 
	{
		this.certificate = value;
		
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

		this.policyValue = PolicyValueFactory.getInstance(tbsStruct.getSubjectPublicKeyInfo().
				getAlgorithmId().getObjectId().toString());
	}

}
