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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enumeration of object identifiers (OIDs) for supported X509 certificate extension fields.
 * @author Greg Meyer
 * @since 1.0
 */
public enum ExtensionIdentifier 
{
	/**
	 * Key usage extension.  Defined by RFC5280 section 4.2.1.3
	 */
	KEY_USAGE("2.5.29.15", "KeyUsage", "Key Usage", KeyUsageExtensionField.class),
	
	/**
	 * Subject alternative name extension.  Defined by RFC5230 section 4.2.1.6
	 */
	SUBJECT_ALT_NAME("2.5.29.17", "SubjectAltName", "Subject Alternative Name", SubjectAltNameExtensionField.class),
	
	/**
	 * Subject direct attributes extension.  Defined by RFC5280 section 4.2.1.8
	 */
	SUBJECT_DIRECTORY_ATTRIBUTES("2.5.29.9", "SubjectDirectoryAttributes", "Subject Key Attributes", new ArrayList<AttributeReferenceClass>()),
	
	/**
	 * Subject key identifier extension.  Defined by RFC5280 section 4.2.1.2
	 */
	SUBJECT_KEY_IDENTIFIER("2.5.29.14", "SubjectKeyIdentifier", "Subject Key Identifier", SubjectKeyIdentifierExtensionField.class),
	
	/**
	 * Issuer alternative name extension.  Defined by RFC5280 section 4.2.1.7
	 */
	ISSUER_ALT_NAME("2.5.29.18", "IssuerAltName", "Issuer Alternative Name", new ArrayList<AttributeReferenceClass>()),

	/**
	 * Authority key identifier extension.  Defined by RFC5280 section 4.2.1.1
	 */
	AUTHORITY_KEY_IDENTIFIER("2.5.29.35", "AuthorityKeyIdentifier", "Authority Key Identifier", 
			Arrays.asList(new AttributeReferenceClass("KeyId", AuthorityKeyIdentifierKeyIdExtensionField.class), 
					new AttributeReferenceClass("CertIssuers", null), 
					new AttributeReferenceClass("SerialNumber", null))),

	/**
	 * Certificate policies extension.  Defined by RFC5280 section 4.2.1.4
	 */
	CERTIFICATE_POLICIES("2.5.29.32", "CertificatePolicies", "Certificate Policies", 
			Arrays.asList(new AttributeReferenceClass("PolicyOIDs", CertificatePolicyIndentifierExtensionField.class), 
					new AttributeReferenceClass("CPSUrls", CertificatePolicyCpsUriExtensionField.class))),	

	/**
	 * Policy mappings extension.  Defined by RFC5280 section 4.2.1.5
	 */
	POLICY_MAPPINGS("2.5.29.33", "PolicyMappings", "Policy Mappings", new ArrayList<AttributeReferenceClass>()),

	/**
	 * Basic constraints extension.  Defined by RFC5280 section 4.2.1.9
	 */
	BASIC_CONSTRAINTS("2.5.29.19", "BasicConstraints", "Basic Constraints", 
			Arrays.asList(new AttributeReferenceClass("CA", BasicContraintsExtensionField.class), 
					new AttributeReferenceClass("MaxPathLength", null))),

	/**
	 * Name constraints extension.  Defined by RFC5280 section 4.2.1.10
	 */
	NAME_CONSTRAINTS("2.5.29.30", "NameConstraints", "Name Constraints", new ArrayList<AttributeReferenceClass>()),

	/**
	 * Policy constraints extension.  Defined by RFC5280 section 4.2.1.11
	 */
	POLICY_CONSTRAINTS("2.5.29.36", "PolicyConstraints", "Policy Constraints", new ArrayList<AttributeReferenceClass>()),

	/**
	 * Extended key usage extension.  Defined by RFC5280 section 4.2.1.12
	 */
	EXTENDED_KEY_USAGE("2.5.29.37", "ExtKeyUsageSyntax", "Extended Key Usage", ExtendedKeyUsageExtensionField.class),

	/**
	 * Certificate revocation list distribution points extension.  Defined by RFC5280 section 4.2.1.13
	 */
	CRL_DISTRIBUTION_POINTS("2.5.29.31", "CRLDistributionPoints", "CRL Distribution Points", 
			Arrays.asList(new AttributeReferenceClass("FullName", CRLDistributionPointNameExtentionField.class), 
					new AttributeReferenceClass("RelativeToIssuer", null), 
					new AttributeReferenceClass("Reasons", null), 
					new AttributeReferenceClass("CRLIssuer", null))),

	/**
	 * Inhibit any policy extension.  Defined by RFC5280 section 4.2.1.14
	 */
	INHIBIT_ANY_POLICY("2.5.29.54", "InhibitAnyPolicy", "Inhibit Any Policy", new ArrayList<AttributeReferenceClass>()),

	/**
	 * Freshest certificate revocation list extension.  Defined by RFC5280 section 4.2.1.15
	 */
	FRESHEST_CRL("2.5.29.46", "FreshestCRL", "Freshest CRL", 
			Arrays.asList(new AttributeReferenceClass("FullName", null), 
					new AttributeReferenceClass("RelativeToIssuer", null), 
					new AttributeReferenceClass("Reasons", null), 
					new AttributeReferenceClass("CRLIssuer", null))),

	/**
	 * Authority information access extension.  Defined by RFC5280 section 4.2.2.1
	 */
	AUTHORITY_INFO_ACCESS("1.3.6.1.5.5.7.1.1", "AuthorityInfoAccessSyntax", "AuthorityInfoAccessSyntax", 
			Arrays.asList(new AttributeReferenceClass("Url", AuthorityInfoAccessExtentionField.class), 
					new AttributeReferenceClass("AccessMethod", null), 
					new AttributeReferenceClass("OCSPLocation", AuthorityInfoAccessOCSPLocExtentionField.class))),

	/**
	 * Subject information access extension.  Defined by RFC5280 section 4.2.2.2
	 */
	SUBJECT_INFO_ACCESS("1.3.6.1.5.5.7.1.11", "SubjectInfoAccessSyntax", "Subject Information Access", 
			Arrays.asList(new AttributeReferenceClass("Url", null), 
					new AttributeReferenceClass("AccessMethod", null), 
					new AttributeReferenceClass("OCSPLocation", null)));
	
	protected final String id;
	protected final String rfcName;
	protected final String display;
	protected final Collection<AttributeReferenceClass> subAttributes;
	protected final Class<? extends ExtensionField<?>> referenceClass;
	
	protected static final Map<String, ExtensionIdentifier> tokenFieldMap; 
	
	static
	{
		tokenFieldMap = new HashMap<String, ExtensionIdentifier>();
		
		final ExtensionIdentifier[] extensions = (ExtensionIdentifier[].class.cast(ExtensionIdentifier.class.getEnumConstants()));
		for (ExtensionIdentifier extension : extensions)	
			for (String token : extension.getFieldTokens())
				tokenFieldMap.put(token, extension);
	}
	
	private ExtensionIdentifier(String id, String rfcName, String display, List<AttributeReferenceClass> subAttributes)
	{
		this.id = id;
		this.rfcName = rfcName;
		this.display = display;
		this.subAttributes = new ArrayList<AttributeReferenceClass>(subAttributes);
		this.referenceClass = null;
		
		//add required flag option to each sub attribute
		if (this.subAttributes.size() > 0)
		{
			final Collection<AttributeReferenceClass> addAttributes = new ArrayList<AttributeReferenceClass>();
					
			for (AttributeReferenceClass addRefClass: this.subAttributes)		
				addAttributes.add(new AttributeReferenceClass(addRefClass.getAttribute() + "+", addRefClass.getReferenceClass()));
			
			this.subAttributes.addAll(addAttributes);
		}
	}
	
	private ExtensionIdentifier(String id, String rfcName, String display, Class<? extends ExtensionField<?>> referenceClass)
	{
		this.id = id;
		this.rfcName = rfcName;
		this.display = display;
		this.subAttributes = null;
		this.referenceClass = referenceClass;		
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
	
	/**
	 * Some extensions may contain complex structure and multiple value may be extracted from the extension or may required
	 * additional qualifiers to identify a specific value.  This method
	 * get a list of attribute token names or qualifiers that can be access from the extension.  If the extension does not support sub attributes
	 * or qualifiers, then this method returns a single entry with the extension name.
	 * @return Gets a list of attribute token names or qualifiers that can be access from the extension.
	 */
	public Collection<String> getFieldTokens()
	{
		if (subAttributes == null || subAttributes.isEmpty())
			return Arrays.asList("X509.TBS.EXTENSION." + this.rfcName, "X509.TBS.EXTENSION." + this.rfcName + "+");
		
		final Collection<String> names = new ArrayList<String>();
		for (AttributeReferenceClass attrRef : subAttributes)
			names.add("X509.TBS.EXTENSION." + this.rfcName + "." + attrRef.getAttribute());
		
		return names;
	}
	
	/**
	 * Gets the class implementing the extension.
	 * @return The class implementing the extension.
	 */
	public Class<? extends ExtensionField<?>> getReferenceClass(String tokenName)
	{
		Class<? extends ExtensionField<?>> retVal = null;
		
		if (referenceClass != null)
			return referenceClass;
		else
		{
			int idx = tokenName.lastIndexOf(".");
			if (idx >= 0)
			{
				final String name = tokenName.substring(idx + 1);
				
				for (AttributeReferenceClass attrRef : subAttributes)
				{
					if (name.equals(attrRef.getAttribute()))
					{
						retVal = attrRef.getReferenceClass();
						break;
					}		
				}
			}
		}
		
		return retVal;
	}	
	
	/**
	 * Gets the extension associated with a specific token string.
	 * @param token The token used to look up the ExtensionIdentifier.
	 * @return The ExtensionIdentifier associated with the token.  If the token does not represent a known extension, then null is returned,.
	 */
	public static ExtensionIdentifier fromToken(String token)
	{
		return tokenFieldMap.get(token);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return rfcName;
	}
	
	public static class AttributeReferenceClass
	{
		protected final String attribute;
		protected final Class<? extends ExtensionField<?>> referenceClass;
		
		public AttributeReferenceClass(String attribute, Class<? extends ExtensionField<?>> referenceClass)
		{
			this.attribute = attribute;
			this.referenceClass = referenceClass;
		}
		
		public String getAttribute()
		{
			return attribute;
		}
		
		public Class<? extends ExtensionField<?>> getReferenceClass()
		{
			return referenceClass;
		}
	}	
}
