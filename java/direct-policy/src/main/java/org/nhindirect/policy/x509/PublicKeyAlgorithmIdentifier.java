package org.nhindirect.policy.x509;

public enum PublicKeyAlgorithmIdentifier 
{
	RSA("1.2.840.113549.1.1.1", "RSA");
	
	protected final String algId;
	
	protected final String algName;
	
	private PublicKeyAlgorithmIdentifier(String algId, String algName)
	{
		this.algId = algId;
		this.algName = algName;
	}
	
	public String getId()
	{
		return algId;
	}
	
	public String getName()
	{
		return algName;
	}
}
