package org.nhindirect.policy.x509;

public enum KeyUsageBit 
{

	DIGITAL_SIGNATURE((1 << 7), "digitalSignature"),
	
	NON_REPUDIATION((1 << 6), "nonRepudiation"),
	
	KEY_ENCIPHERMENT((1 << 5), "keyEncipherment"),
	
	DATA_ENCIPHERMENT((1 << 4), "dataEncipherment"),
	
	KEY_AGREEMENT((1 << 3), "keyAgreement"),
	
	KEY_CERT_SIGN((1 << 2), "keyCertSign"),
	
	CRL_SIGN((1 << 1), "crlSign"), 
	
	ENCIPHERMENT_ONLY((1 << 0), "encipherOnly"),
	
	DECIPHER_ONLY((1 << 15), "decipherOnly");
	
	protected final int bit;
	protected final String name;
	
	private KeyUsageBit(int bit, String name)
	{
		this.bit = bit;
		this.name = name;
	}
	
	public int getUsageBit()
	{
		return bit;
	}
	
	public String getName()
	{
		return name;
	}
}
