/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.policy.x509;

/**
 * Enumeration of object identifiers (OIDs) for supported X509 certificate extension fields.
 * @author Greg Meyer
 * @since 1.0
 */
public enum ExtentionIdentifier 
{
	/**
	 * Key usage extension.  Defined by RFC5280 section 4.2.1.3
	 */
	KEY_USAGE("2.5.29.15", "KeyUsage", "Key Usage"),
	
	/**
	 * Subject alternative name extension.  Defined by RFC5230 section 4.2.1.6
	 */
	SUBJECT_ALT_NAME("2.5.29.17", "SubjectAltName", "Subject Alternative Name"),
	
	/**
	 * Subject direct attributes extension.  Defined by RFC5280 section 4.2.1.8
	 */
	SUBJECT_DIRECTORY_ATTRIBUTES("2.5.29.9", "SubjectDirectoryAttributes", "Subject Key Attributes"),
	
	/**
	 * Subject key identifier extension.  Defined by RFC5280 section 4.2.1.2
	 */
	SUBJECT_KEY_IDENTIFIER("2.5.29.14", "SubjectKeyIdentifier", "Subject Key Identifier"),
	
	/**
	 * Issuer alternative name extension.  Defined by RFC5280 section 4.2.1.7
	 */
	ISSUER_ALT_NAME("2.5.29.18", "IssuerAltName", "Issuer Alternative Name"),

	/**
	 * Authority key identifier extension.  Defined by RFC5280 section 4.2.1.1
	 */
	AUTHORITY_KEY_IDENTIFIER("2.5.29.35", "AuthorityKeyIdentifier", "Authority Key Identifier"),

	/**
	 * Certificate policies extension.  Defined by RFC5280 section 4.2.1.4
	 */
	CERTIFICATE_POLICIES("2.5.29.32", "CertificatePolicies", "Certificate Policies"),	

	/**
	 * Policy mappings extension.  Defined by RFC5280 section 4.2.1.5
	 */
	POLICY_MAPPINGS("2.5.29.33", "PolicyMappings", "Policy Mappings"),

	/**
	 * Basic constraints extension.  Defined by RFC5280 section 4.2.1.9
	 */
	BASIC_CONSTRAINTS("2.5.29.19", "BasicConstraints", "Basic Constraints"),

	/**
	 * Name constraints extension.  Defined by RFC5280 section 4.2.1.10
	 */
	NAME_CONSTRAINTS("2.5.29.30", "NameConstraints", "Name Constraints"),

	/**
	 * Policy constraints extension.  Defined by RFC5280 section 4.2.1.11
	 */
	POLICY_CONSTRAINTS("2.5.29.36", "PolicyConstraints", "Policy Constraints"),

	/**
	 * Extended key usage extension.  Defined by RFC5280 section 4.2.1.12
	 */
	EXTENDED_KEY_USAGE("2.5.29.37", "ExtKeyUsageSyntax", "Extended Key Usage"),

	/**
	 * Certificate revocation list distribution points extension.  Defined by RFC5280 section 4.2.1.13
	 */
	CRL_DISTRIBUTION_POINTS("2.5.29.31", "CRLDistributionPoints", "CRL Distribution Points"),

	/**
	 * Inhibit any policy extension.  Defined by RFC5280 section 4.2.1.14
	 */
	INHIBIT_ANY_POLICY("2.5.29.54", "InhibitAnyPolicy", "Inhibit Any Policy"),

	/**
	 * Freshest certificate revocation list extension.  Defined by RFC5280 section 4.2.1.15
	 */
	FRESHEST_CRL("2.5.29.46", "FreshestCRL", "Freshest CRL"),

	/**
	 * Authority information access extension.  Defined by RFC5280 section 4.2.2.1
	 */
	AUTHORITY_INFO_ACCESS("1.3.6.1.5.5.7.1.1", "AuthorityInfoAccessSyntax", "Authority Information Access"),

	/**
	 * Subeject information access extension.  Defined by RFC5280 section 4.2.2.2
	 */
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
	
	/**
	 * Gets the object identifier (OID) of the extension.
	 * @return The object identifier (OID) of the extension.
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Gets the name of the extension as defined by RFC5280.
	 * @return The name of the extension as defined by RFC5280.
	 */
	public String getRfcName()
	{
		return rfcName;
	}
	
	/**
	 * Gets a human readable display name of the extension.
	 * @return A human readable display name of the extension.
	 */
	public String getDisplay()
	{
		return display;
	}
}
