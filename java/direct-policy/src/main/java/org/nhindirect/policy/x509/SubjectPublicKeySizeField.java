package org.nhindirect.policy.x509;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.RSAKey;

import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyValueFactory;

public class SubjectPublicKeySizeField extends AbstractTBSField<Integer>
{
	static final long serialVersionUID = -1094029946830031432L;
	
	public SubjectPublicKeySizeField()
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
		int retVal = 0;
		
		this.certificate = value;
		
		final PublicKey pubKey = this.certificate.getPublicKey();
		
		if (pubKey instanceof RSAKey)
		{
			retVal = ((RSAKey)pubKey).getModulus().bitLength();
		}
		else if (pubKey instanceof DSAKey)
		{
			retVal = ((DSAKey)pubKey).getParams().getP().bitLength();
		}
		else
		{
			// undertermined
			retVal = 0;
		}
		
		this.policyValue = PolicyValueFactory.getInstance(retVal);
	}
}
