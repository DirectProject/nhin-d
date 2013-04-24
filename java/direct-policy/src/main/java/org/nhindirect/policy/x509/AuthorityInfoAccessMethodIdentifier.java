package org.nhindirect.policy.x509;

public enum AuthorityInfoAccessMethodIdentifier 
{
	OCSP("1.3.6.1.5.5.7.48.1", "OCSP"),

	CA_ISSUERS("1.3.6.1.5.5.7.48.2", "caIssuers");
	
	protected final String id;
	
	protected final String name;
	
	private AuthorityInfoAccessMethodIdentifier(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
}
