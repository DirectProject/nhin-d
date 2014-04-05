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
 * General name types as describe in section 4.2.1.6 of RFC5280
 * @author Greg Meyer
 * @since 1.0
 */
public enum GeneralNameType 
{
	/**
	 * Other name<br>
	 * Type Name: otherName
	 */
	OTHER_NAME(0, "otherName"),
	
	/**
	 * RFC822 (email address) name<br>
	 * Type Name: rfc822
	 */
	RFC822_NAME(1, "rfc822"),
	
	/**
	 * DNS name<br>
	 * Type Name: dns
	 */
	DNS_NAME(2, "dns"),
	
	/**
	 * x400 address<br>
	 * Type Name: x400Address
	 */
	X400ADDRESS_NAME(3, "x400Address"),
	
	/**
	 * Directory name<br>
	 * Type Name: directory
	 */
	DIRECTORY_NAME(4, "directory"),
	
	/**
	 * EDI party name<br>
	 * Type Name: ediParty
	 */
	EDI_PARTY_NAME(5, "ediParty"),
	
	/**
	 * Uniform resource id name<br>
	 * Type Name: uniformResourceIdentifier
	 */
	UNIFORM_RESOURCE_IDENTIFIER_NAME(6, "uniformResourceIdentifier"),
	
	/**
	 * IP address<br>
	 * Type Name: ipaddress
	 */
	IP_ADDRESS_NAME(7, "ipaddress"),
	
	/**
	 * Registered id name<br>
	 * Type Name: registeredId
	 */
	REGISTERED_ID_NAME(8, "registeredId");
	
	protected final int tag;
	protected final String display;
	protected static final Map<Integer, GeneralNameType> tagToTypeMap; 
	
	static
	{
		tagToTypeMap = new HashMap<Integer, GeneralNameType>();
		
		final GeneralNameType[] types = (GeneralNameType[].class.cast(GeneralNameType.class.getEnumConstants()));
		for (GeneralNameType type : types)
			tagToTypeMap.put(type.getTag(), type);
	}
	
	private GeneralNameType(int tag, String display)
	{
		this.tag = tag;
		this.display = display;			
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return display;
	}
	
	/**
	 * Gets the display name of this type
	 * @return The display name of this type
	 */
	public String getDisplay()
	{
		return display;
	}
	
	/**
	 * Gets the tag id of this type
	 * @return The tag id of this type
	 */
	public int getTag()
	{
		return tag;
	}

	/**
	 * Gets a {@link GeneralNameType} for a given tag.
	 * @param tag The tag used to lookup the {@link GeneralNameType}
	 * @return A {@link GeneralNameType} for the given tag.  If the tag does not match a known type, the null is returned.
	 */
	public static GeneralNameType fromTag(int tag)
	{
		return tagToTypeMap.get(tag);
	}
	
}
