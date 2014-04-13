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
 * Enumeration of the field type of the X509 certificate.  These are broken in two fields and aggregated structure
 * as defined by RFC 5280
 * @author Greg Meyer
 * @since 1.0
 */
public enum X509FieldType 
{
	/**
	 * The certificates signature.
	 */
	SIGNATURE("Signature", "Signature", null),
	
	/**
	 * The algorithm used to sign the certificates.
	 */
	SIGNATURE_ALGORITHM("Algorithm", "Algorithm", SignatureAlgorithmField.class),
	
	/**
	 * The to be signed fields of the certificate.  These fields are used to generate the certificate signature.
	 */
	TBS("TbsCertificate", "To Be Signed Certificate", null);
	
	protected final String rfcName;
	protected final String display;
	protected final Class<? extends X509Field<?>> referenceClass;
	protected static final Map<String, X509FieldType> tokenFieldMap; 
	
	static
	{
		tokenFieldMap = new HashMap<String, X509FieldType>();
		
		final X509FieldType[] fields = (X509FieldType[].class.cast(X509FieldType.class.getEnumConstants()));
		for (X509FieldType field : fields)
		{
			tokenFieldMap.put(field.getFieldToken(), field);
			tokenFieldMap.put(field.getFieldToken() + "+", field);
		}
	}
	
	private X509FieldType(String rfcName, String display, Class<? extends X509Field<?>> referenceClass)
	{
		this.rfcName = rfcName;
		this.display = display;
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
	 * Gets the class implementing the field type.
	 * @return The class implementing the field type.
	 */
	public Class<? extends X509Field<?>> getReferenceClass()
	{
		return referenceClass;
	}
	
	/**
	 * Gets the token of the field used in a lexicon parser.
	 * @return  The token of the field used in a lexicon parser.
	 */
	public String getFieldToken()
	{
		return "X509." + rfcName;
	}
	
	/**
	 * Gets the field type associated with a specific token string.
	 * @param token The token used to look up the X509FieldType.
	 * @return The X509FieldType associated with the token.  If the token does not represent a known field, then null is returned.
	 */
	public static X509FieldType fromToken(String token)
	{
		return tokenFieldMap.get(token);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return getFieldToken();
	}
}
