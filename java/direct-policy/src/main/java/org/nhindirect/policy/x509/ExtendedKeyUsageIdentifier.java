package org.nhindirect.policy.x509;

public enum ExtendedKeyUsageIdentifier 
{
	ANY_EXTENDED_KEY_USAGE("2.5.29.37.0"),
	
	ID_KP_SERVER_AUTH("1.3.6.1.5.5.7.3.1"),

	ID_KP_CLIENT_AUTH("1.3.6.1.5.5.7.3.2"),
	
	ID_KP_CODE_SIGNING("1.3.6.1.5.5.7.3.3"),
	
	ID_KP_EMAIL_PROTECTION("1.3.6.1.5.5.7.3.4"),

	ID_KP_IPSEC_END_SYSTEM("1.3.6.1.5.5.7.3.5"),

	ID_KP_IPSEC_TUNNEL("1.3.6.1.5.5.7.3.6"),

	ID_KP_IPSEC_USER("1.3.6.1.5.5.7.3.7"),
	
	ID_KP_TIME_STAMPING("1.3.6.1.5.5.7.3.8"),

	ID_KP_OCSP_SIGNING("1.3.6.1.5.5.7.3.9"),
	
	ID_KP_DVCS("1.3.6.1.5.5.7.3.10"),
	
	ID_KP_SBGP_CERT_AA_SERVER_AUTH("1.3.6.1.5.5.7.3.11"),

	ID_KP_SCVP_RESPONDER("1.3.6.1.5.5.7.3.12"),

	ID_KP_EAP_OVER_PPP("1.3.6.1.5.5.7.3.13"),

	ID_KP_EAP_OVER_LAN("1.3.6.1.5.5.7.3.14"),

	ID_KP_SCVP_SERVER("1.3.6.1.5.5.7.3.15"),

	ID_KP_SCVP_CLIENT("1.3.6.1.5.5.7.3.16"),

	ID_KP_IPSEC_IKE("1.3.6.1.5.5.7.3.17"),

	ID_KP_CAP_WAP_AC("1.3.6.1.5.5.7.3.18"),
	
	ID_KP_CAP_WAP_WPT("1.3.6.1.5.5.7.3.19");
	
	protected final String usageId;
	
	private ExtendedKeyUsageIdentifier(String usageId)
	{
		this.usageId = usageId;
	}
	
	public String getId()
	{
		return usageId;
	}
}
