package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyValueFactory;

public class SignatureAlgorithmField extends AbstractX509Field<String>
{
	static final long serialVersionUID = 7058163730634509206L;
	
	public SignatureAlgorithmField()
	{
		
	}
	
	@Override
	public void injectReferenceValue(X509Certificate value) throws PolicyProcessException
	{
		this.certificate = value;
		
		this.policyValue = PolicyValueFactory.getInstance(value.getSigAlgOID());
	}

	@Override
	public X509FieldType getX509FieldType() 
	{
		// TODO Auto-generated method stub
		return X509FieldType.SIGNATURE_ALGORITHM;
	}	
}
