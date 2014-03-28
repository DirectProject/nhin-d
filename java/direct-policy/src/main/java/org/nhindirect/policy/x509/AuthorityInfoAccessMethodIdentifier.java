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
 * Enumeration of access methods for the authority info access (AIA) extension field.
 * @author Greg Meyer
 * @since 1.0
 */
public enum AuthorityInfoAccessMethodIdentifier 
{
	/**
	 * OCSP server location URL<br>
	 * Access Method Name: OCSP
	 */
	OCSP("1.3.6.1.5.5.7.48.1", "OCSP"),

	/**
	 * Location of the issuer certificate.  Generally takes the form of a URL<br>
	 * Access Method Name: caIssuers
	 */
	CA_ISSUERS("1.3.6.1.5.5.7.48.2", "caIssuers");
	
	protected final String id;
	
	protected final String name;
	
	private AuthorityInfoAccessMethodIdentifier(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Gets the object identifier (OID) of the access method.
	 * @return The object identifier (OID) of the access method.
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Gets the name of the access method.
	 * @return The name of the access method.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets an AuthorityInfoAccessMethodIdentifier from an access id.
	 * @param id The id of the access method.
	 * @return The AuthorityInfoAccessMethodIdentifier that matches the request id.  If the request id does not match
	 * a known access method id, then null is returned;
	 */
	public static AuthorityInfoAccessMethodIdentifier fromId(String id)
	{
		if (id.equals(OCSP.getId()))
			return OCSP;
		else if (id.equals(CA_ISSUERS.getId()))
			return CA_ISSUERS;
		
		return null;
	}
}
