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
 * Enumeration of algorithms used for signing an X509 certificate
 * @author gm2552
 * @since 1.0
 */
public enum SignatureAlgorithmIdentifier 
{
	/* 
	 * Algorithms defined in rfc3279 section 2.2 and
	 * rfc4055 section 5
	 */
	
	/**
	 * MD2<br>
	 * OID: 1.2.840.113549.2.1
	 */
	MD2("1.2.840.113549.2.1", "md2"),
	
	/**
	 * MD2<br>
	 * OID: 1.2.840.113549.2.5
	 */
	MD5("1.2.840.113549.2.5", "md5"),

	/**
	 * SHA1<br>
	 * OID: 1.3.14.3.2.26
	 */
	SHA1("1.3.14.3.2.26", "sha1"),

	/**
	 * SHA256<br>
	 * OID: 2.16.840.1.101.3.4.2.1
	 */
	SHA256("2.16.840.1.101.3.4.2.1", "sha256"),
	
	/**
	 * SHA384<br>
	 * OID: 2.16.840.1.101.3.4.2.2
	 */
	SHA384("2.16.840.1.101.3.4.2.2", "sha384"),
	
	/**
	 * SHA512<br>
	 * OID: 2.16.840.1.101.3.4.2.3
	 */
	SHA512("2.16.840.1.101.3.4.2.3", "sha512"),
	
	/**
	 * SAH224<br>
	 * OID: 2.16.840.1.101.3.4.2.4
	 */
	SHA224("2.16.840.1.101.3.4.2.4", "sha224"),
	
	/**
	 * MD2 with RSA<br>
	 * OID: 1.2.840.113549.1.1.2
	 */
	MD2RSA("1.2.840.113549.1.1.2", "md2RSA"),
	
	/**
	 * MD5 with RSA<br>
	 * OID: 1.2.840.113549.1.1.4
	 */
	MD5RSA("1.2.840.113549.1.1.4", "md5RSA"),

	/**
	 * SHA1 with DSA<br>
	 * OID: 1.2.840.10040.4.3
	 */
	SHA1DSA("1.2.840.10040.4.3", "sha1DSA"),
	
	/**
	 * SHA1 with RSA<br>
	 * OID: 1.2.840.113549.1.1.5
	 */
	SHA1RSA("1.2.840.113549.1.1.5", "sha1RSA"),

	/**
	 * SHA256 with RSA<br>
	 * OID: 1.2.840.113549.1.1.11
	 */
	SHA256RSA("1.2.840.113549.1.1.11", "sha256RSA"),

	/**
	 * SHA384 with RSA<br>
	 * OID: 1.2.840.113549.1.1.12
	 */
	SHA384RSA("1.2.840.113549.1.1.12", "sha384RSA"),
	
	/**
	 * SHA512 with RSA<br>
	 * OID: 1.2.840.113549.1.1.13
	 */
	SHA512RSA("1.2.840.113549.1.1.13", "sha512RSA"),
	
	/**
	 * SHA224 with RSA<br>
	 * OID: 1.2.840.113549.1.1.14
	 */
	SHA224RSA("1.2.840.113549.1.1.14", "sha224RSA"),
	
	/**
	 * SHA1 with ECDSA<br>
	 * OID: 1.2.840.10045.4.1
	 */
	SHA1ECDSA("1.2.840.10045.4.1", "sha1ECDSA");
	
	protected final String algId;
	
	protected final String algName;
	
	private SignatureAlgorithmIdentifier(String algId, String algName)
	{
		this.algId = algId;
		this.algName = algName;
	}
	
	/**
	 * Gets the object identifier (OID) of the signature algorithm.
	 * @return The object identifier (OID) of the signature algorithm.
	 */
	public String getId()
	{
		return algId;
	}
	
	/**
	 * Gets the name of the signature algorithm.
	 * @return The name of the signature algorithm.
	 */
	public String getName()
	{
		return algName;
	}
}
