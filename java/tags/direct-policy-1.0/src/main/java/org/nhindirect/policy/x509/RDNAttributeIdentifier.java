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

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of relative distinguished name object identifiers (OIDs) found in the subject and issuer fields of the TBS section of an X509 Certificate.
 * These OIDs are used to select a specific attribute from a distinguished name.
 * <p>
 * @author Greg Meyer
 * @since 1.0
 */
public enum RDNAttributeIdentifier 
{
	/*
	 * From RFC5280 section 4.1.2.4 and RFC4519
	 */
	
	/**
	 * Common name attribute<br>
	 * RDN Name: CN 
	 */
	COMMON_NAME("2.5.4.3", "CN"),
	
	/**
	 * Country attribute<br>
	 * RDN Name: C
	 */
	COUNTRY("2.5.4.6", "C"),
	
	/**
	 * Organization attribute<br>
	 * RDN Name: O
	 */
	ORGANIZATION("2.5.4.10", "O"),
	
	/**
	 * Organizational unit attribute<br>
	 * RDN Name: OU
	 */
	ORGANIZATIONAL_UNIT("2.5.4.11", "OU"),
	
	/**
	 * State attribute<br>
	 * RDN Name: ST
	 */
	STATE("2.5.4.8", "ST"),
	
	/**
	 * Locality (city) attribute<br>
	 * RDN Name: L
	 */
	LOCALITY("2.5.4.7", "L"),
	
	/**
	 * Legacy email attribute<br>
	 * RDN Name: E
	 */
	EMAIL("1.2.840.113549.1.9.1", "E"),
	
	/**
	 * Domain component attribute<br>
	 * RDN Name: DC
	 */
	DOMAIN_COMPONENT("0.9.2342.19200300.100.1.25", "DC"),
	
	/**
	 * Distinguished name qualifier attribute<br>
	 * RDN Name: DNQUALIFIER
	 */
	DISTINGUISHED_NAME_QUALIFIER("2.5.4.46", "DNQUALIFIER"),
	
	/**
	 * Serial number attribute<br>
	 * RDN Name: SERIALNUMBER
	 */
	SERIAL_NUMBER("2.5.4.5", "SERIALNUMBER"),
	
	/**
	 * Surname attribute<br>
	 * RDN Name: SN
	 */
	SURNAME("2.5.4.4", "SN"),
	
	/**
	 * Title name attribute<br>
	 * RDN Name: TITLE
	 */
	TITLE("2.5.4.12", "TITLE"),
	
	/**
	 * Given name attribute<br>
	 * RDN Name: GIVENNAME
	 */
	GIVEN_NAME("2.5.4.42", "GIVENNAME"),

	/**
	 * Initials attribute<br>
	 * RDN Name: INITIALS
	 */
	INITIALS("2.5.4.43", "INITIALS"),
	
	/**
	 * Pseudonym attribute<br>
	 * RDN Name: PSEUDONYM
	 */
	PSEUDONYM("2.5.4.65", "PSEUDONYM"),

	/**
	 * General qualifier attribute<br>
	 * RDN Name: GERNERAL_QUALIFIER
	 */
	GERNERAL_QUALIFIER("2.5.4.64", "GERNERAL_QUALIFIER"),
	
	/**
	 * Distinguished name attribute
	 * <p>
	 * This attribute is overloaded by the policy engine and returns the full relative distinguished name using RFC2253 formatting<br>
	 * RDN Name: GERNERAL_QUALIFIER
	 */
	DISTINGUISHED_NAME("2.5.4.49", "DN");
	
	protected final String id;
	
	protected final String name;
	
	protected static final Map<String, RDNAttributeIdentifier> nameFieldMap; 
	
	static
	{
		nameFieldMap = new HashMap<String, RDNAttributeIdentifier>();
		
		final RDNAttributeIdentifier[] rdns = (RDNAttributeIdentifier[].class.cast(RDNAttributeIdentifier.class.getEnumConstants()));
		for (RDNAttributeIdentifier rdn : rdns)
			nameFieldMap.put(rdn.getName(), rdn);
	}
	
	private RDNAttributeIdentifier(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Gets the object identifier (OID) of the RDN attribute.
	 * @return The object identifier (OID) of the RDN attribute.
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Gets the name of the attribute as it is commonly displayed in an X509 certificate viewer
	 * @return The name of the attribute as it is commonly displayed in an X509 certificate viewer
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return name;
	}
	
	/**
	 * Gets the RDNAttributeIdentifier associated with the RDN name.  This method also accepts a parsed token ending with the 
	 * RDN name.the RDNAttributeIdentifier associated with the RDN name.
	 * @param name Name or parsed token used to lookup the RDNAttributeIdentifier.
	 * @return The RDNAttributeIdentifier associated with the RDN name.   If the name does not represent a known RDN, then null is returned.
	 */
	public static RDNAttributeIdentifier fromName(String name)
	{
		String lookupName;
		int idx = name.lastIndexOf(".");
		if (idx >= 0)
			lookupName = name.substring(idx + 1);
		else
			lookupName = name;
		
		return nameFieldMap.get(lookupName);
	}
}
