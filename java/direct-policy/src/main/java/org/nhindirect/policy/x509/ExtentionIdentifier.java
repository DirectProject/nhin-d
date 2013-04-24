package org.nhindirect.policy.x509;

public enum ExtentionIdentifier 
{
	KEY_USAGE("2.5.29.15", "KeyUsage", "Key Usage"),
	
	SUBJECT_ALT_NAME("2.5.29.17", "SubjectAltName", "Subject Alternative Name"),
	
	SUBJECT_DIRECTORY_ATTRIBUTES("2.5.29.9", "SubjectDirectoryAttributes", "Subject Key Attributes"),
	
	SUBJECT_KEY_IDENTIFIER("2.5.29.14", "SubjectKeyIdentifier", "Subject Key Identifier"),
	
	ISSUER_ALT_NAME("2.5.29.18", "IssuerAltName", "Issuer Alternative Name"),

	AUTHORITY_KEY_IDENTIFIER("2.5.29.35", "AuthorityKeyIdentifier", "Authority Key Identifier"),

	CERTIFICATE_POLICIES("2.5.29.32", "CertificatePolicies", "Certificate Policies"),	

	POLICY_MAPPINGS("2.5.29.33", "PolicyMappings", "Policy Mappings"),

	BASIC_CONSTRAINTS("2.5.29.19", "BasicConstraints", "Basic Constraints"),

	NAME_CONSTRAINTS("2.5.29.30", "NameConstraints", "Name Constraints"),

	POLICY_CONSTRAINTS("2.5.29.36", "PolicyConstraints", "Policy Constraints"),

	EXTENDED_KEY_USAGE("2.5.29.37", "ExtKeyUsageSyntax", "Extended Key Usage"),

	CRL_DISTRIBUTION_POINTS("2.5.29.31", "CRLDistributionPoints", "CRL Distribution Points"),

	INHIBIT_ANY_POLICY("2.5.29.54", "InhibitAnyPolicy", "Inhibit Any Policy"),

	FRESHEST_CRL("2.5.29.46", "FreshestCRL", "Freshest CRL"),

	AUTHORITY_INFO_ACCESS("1.3.6.1.5.5.7.1.1", "AuthorityInfoAccessSyntax", "Authority Information Access"),

	SUBJECT_INFO_ACCESS("1.3.6.1.5.5.7.1.11", "SubjectInfoAccessSyntax", "Subject Information Access");
	
	protected final String id;
	protected final String rfcName;
	protected final String display;
	
	private ExtentionIdentifier(String id, String rfcName, String display)
	{
		this.id = id;
		this.rfcName = rfcName;
		this.display = display;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getRfcName()
	{
		return rfcName;
	}
	
	public String getDisplay()
	{
		return display;
	}
}
