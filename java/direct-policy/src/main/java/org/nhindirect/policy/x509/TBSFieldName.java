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
 * Enumeration of to be signed fields in an X509 certificate as defined by RFC5280.
 * @author Greg Meyer
 * @since 1.0
 */
public enum TBSFieldName 
{
	/**
	 * Certificate version
	 */
	VERSION("Version", "Version", new ArrayList<AttributeReferenceClass>()),
	
	/**
	 * Certificate serial number
	 */
	SERIAL_NUMBER("SerialNumber", "Serial Number", SerialNumberAttributeField.class),
	
	/**
	 * Certificate signature algorithm
	 */
	SIGNATURE("Signature", "Signature", new ArrayList<AttributeReferenceClass>()),
	
	/**
	 * Distinguished name of certificate signer
	 */
	ISSUER("Issuer", "Issuer", rdnsToReferenceClass(IssuerAttributeField.class)),
	
	/**
	 * Certificate valid to and valid from dates
	 */
	VALIDITY("Validity", "Validity", Arrays.asList(new AttributeReferenceClass("ValidFrom", null),
                                                  new AttributeReferenceClass("ValidTo", null))),
	
	/**
	 * Distinguished name of the entity associated with the public key
	 */	
	SUBJECT("Subject", "Subject", rdnsToReferenceClass(SubjectAttributeField.class)),
	
	/**
	 * Certificate issuer unique id
	 */
	ISSUER_UNIQUE_ID("IssuerUniqueID", "Issuer Unique Identifier", new ArrayList<AttributeReferenceClass>()),
	
	/**
	 * Certificate subject unique id
	 */
	SUBJECT_UNIQUE_ID("SubjectUniqueID", "Subject Unique Identifier", new ArrayList<AttributeReferenceClass>()),
	
	/**
	 * Certificate extension fields
	 */
	EXTENSIONS("Extensions", "Extensions", new ArrayList<AttributeReferenceClass>()),
	
	/**
	 * Certificate public key and algorithm
	 */
	SUBJECT_PUBLIC_KEY_INFO("SubjectPublicKeyInfo", "Subject Public Key Info", 
			Arrays.asList(new AttributeReferenceClass("Algorithm", SubjectPublicKeyAlgorithmField.class), 
					new AttributeReferenceClass("Size", SubjectPublicKeySizeField.class)));

	
	protected final String rfcName;
	protected final String display;
	protected final Collection<AttributeReferenceClass> subAttributes;
	protected final Class<? extends TBSField<?>> referenceClass;
	protected static final Map<String, TBSFieldName> tokenFieldMap; 
	
	
	
	static
	{
		tokenFieldMap = new HashMap<String, TBSFieldName>();
		
		final TBSFieldName[] fields = (TBSFieldName[].class.cast(TBSFieldName.class.getEnumConstants()));
		for (TBSFieldName field : fields)	
			for (String token : field.getFieldTokens())
				tokenFieldMap.put(token, field);
	}

	
	static List<AttributeReferenceClass> rdnsToReferenceClass(Class<? extends TBSField<?>> refClass)
	{
		final ArrayList<AttributeReferenceClass> retVal = new ArrayList<AttributeReferenceClass>();
		
		for (RDNAttributeIdentifier rdn : RDNAttributeIdentifier.class.getEnumConstants())
			retVal.add(new AttributeReferenceClass(rdn.getName(), refClass));
		
		return retVal;
	}
	
	private TBSFieldName(String rfcName, String display, List<AttributeReferenceClass> subAttributes)
	{
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
	
	private TBSFieldName(String rfcName, String display, Class<? extends TBSField<?>> referenceClass)
	{
		this.rfcName = rfcName;
		this.display = display;
		this.subAttributes = null;
		this.referenceClass = referenceClass;		
	}
	
	/**
	 * Gets the name of the field as defined by RFC5280.
	 * @return The name of the field as defined by RFC5280.
	 */
	public String getRfcName()
	{
		return rfcName;
	}
	
	/**
	 * Gets a human readable display name of the field.
	 * @return A human readable display name of the field.
	 */
	public String getDisplay()
	{
		return display;
	}
	
	/**
	 * Some fields may contain complex structure and multiple value may be extracted from the field or may required
	 * additional qualifiers to identify a specific value.  This method
	 * get a list of attribute token names or qualifiers that can be access from the field.  If the field does not support sub attributes
	 * or qualifiers, then this method returns a single entry with the field name.
	 * @return Gets a list of attribute token names or qualifiers that can be access from the field.
	 */
	public Collection<String> getFieldTokens()
	{
		if (subAttributes == null || subAttributes.isEmpty())
			return Arrays.asList("X509.TBS." + this.rfcName, "X509.TBS." + this.rfcName + "+");
		
		final Collection<String> names = new ArrayList<String>();
		for (AttributeReferenceClass attrRef : subAttributes)
			names.add("X509.TBS." + this.rfcName + "." + attrRef.getAttribute());
		
		return names;
	}
	
	/**
	 * Gets the class implementing the field name.
	 * @return The class implementing the field name.
	 */
	public Class<? extends TBSField<?>> getReferenceClass(String tokenName)
	{
		Class<? extends TBSField<?>> retVal = null;
		
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
	 * Gets the field name associated with a specific token string.
	 * @param token The token used to look up the TBSFieldName.
	 * @return The TBSFieldName associated with the token.  If the token does not represent a known field, then null is returned,.
	 */
	public static TBSFieldName fromToken(String token)
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
		protected final Class<? extends TBSField<?>> referenceClass;
		
		public AttributeReferenceClass(String attribute, Class<? extends TBSField<?>> referenceClass)
		{
			this.attribute = attribute;
			this.referenceClass = referenceClass;
		}
		
		public String getAttribute()
		{
			return attribute;
		}
		
		public Class<? extends TBSField<?>> getReferenceClass()
		{
			return referenceClass;
		}
	}
}
