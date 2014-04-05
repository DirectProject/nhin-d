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
 * Enumeration of algorithms used by the public key.
 * @author Greg Meyer
 * @since 1.0
 */
public enum PublicKeyAlgorithmIdentifier 
{
	/**
	 * RSA encryption<br>
	 * OID: 1.2.840.113549.1.1.1
	 */
	RSA("1.2.840.113549.1.1.1", "RSA"),
	
	/**
	 * DSA encryption<br>
	 * OID: 1.2.840.10040.4.1
	 */
	DSA("1.2.840.10040.4.1", "DSA");
	
	protected final String algId;
	
	protected final String algName;
	
	private PublicKeyAlgorithmIdentifier(String algId, String algName)
	{
		this.algId = algId;
		this.algName = algName;
	}
	
	/**
	 * Gets the object identifier (OID) of the public key algorithm.
	 * @return The object identifier (OID) of the public key algorithm.
	 */
	public String getId()
	{
		return algId;
	}
	
	/**
	 * Gets the name of the public key algorithm.
	 * @return The name of the public key algorithm.
	 */
	public String getName()
	{
		return algName;
	}
}
