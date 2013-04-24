package org.nhindirect.policy.x509;

public enum SignatureAlgorithmIdentifier 
{
	/* 
	 * Algorithms defined in rfc3279 section 2.2 and
	 * rfc4055 section 5
	 */
	
	MD2("1.2.840.113549.2.1", "md2"),
	
	MD5("1.2.840.113549.2.5", "md5"),

	SHA1("1.3.14.3.2.26", "sha1"),

	SHA256("2.16.840.1.101.3.4.2.1", "sha256"),
	
	SHA384("2.16.840.1.101.3.4.2.2", "sha384"),
	
	SHA512("2.16.840.1.101.3.4.2.3", "sha512"),
	
	SHA224("2.16.840.1.101.3.4.2.4", "sha224"),
	
	MD2RSA("1.2.840.113549.1.1.2", "md2RSA"),
	
	MD5RSA("1.2.840.113549.1.1.4", "md5RSA"),

	SHA1DSA("1.2.840.10040.4.3", "sha1DSA"),
	
	SHA1RSA("1.2.840.113549.1.1.5", "sha1RSA"),

	SHA256RSA("1.2.840.113549.1.1.11", "sha256RSA"),

	SHA384RSA("1.2.840.113549.1.1.12", "sha384RSA"),
	
	SHA512RSA("1.2.840.113549.1.1.13", "sha512RSA"),
	
	SHA224RSA("1.2.840.113549.1.1.14", "sha224RSA"),
	
	SHA1ECDSA("1.2.840.10045.4.1", "sha1ECDSA");
	
	protected final String algId;
	
	protected final String algName;
	
	private SignatureAlgorithmIdentifier(String algId, String algName)
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
