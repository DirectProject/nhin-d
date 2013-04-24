package org.nhindirect.policy.x509;

public enum RDNAttributeIdentifier 
{
	/*
	 * From RFC5280 section 4.1.2.4 and RFC4519
	 */
	
	COMMON_NAME("2.5.4.3", "CN"),
	
	COUNTRY("2.5.4.6", "C"),
	
	ORGANIZATION("2.5.4.10", "O"),
	
	ORGANIZATIONAL_UNIT("2.5.4.11", "OU"),
	
	STATE("2.5.4.8", "ST"),
	
	LOCALITY("2.5.4.7", "L"),
	
	EMAIL("1.2.840.113549.1.9.1", "E"),
	
	DOMAIN_COMPONENT("0.9.2342.19200300.100.1.25", "DC"),
	
	DISTINGUISHED_NAME_QUALIFIER("2.5.4.46", "DNQUALIFIER"),
	
	SERIAL_NUMBER("2.5.4.5", "SERIALNUMBER"),
	
	SURNAME("2.5.4.4", "SN"),
	
	TITLE("2.5.4.12", "TITLE"),
	
	GIVEN_NAME("2.5.4.42", "GIVENNAME"),

	INITIALS("2.5.4.43", "INITIALS"),
	
	PSEUDONYM("2.5.4.65", "PSEUDONYM"),

	GERNERAL_QUALIFIER("2.5.4.64", "GERNERAL_QUALIFIER"),
	
	DISTINGUISHED_NAME("2.5.4.49", "DN");
	
	protected final String id;
	
	protected final String name;
	
	private RDNAttributeIdentifier(String id, String name)
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
