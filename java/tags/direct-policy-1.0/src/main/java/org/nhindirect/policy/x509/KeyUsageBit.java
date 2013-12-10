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
 * Enumeration of bits used in key usage extension field
 * @author Greg Meyer
 * @since 1.0
 */
public enum KeyUsageBit 
{

	/**
	 * Digital signature<br>
	 * Binary: 10000000<br>
	 * Hex: 0x80<br>
	 * Dec: 128<br>
	 */
	DIGITAL_SIGNATURE((1 << 7), "digitalSignature"),
	
	/**
	 * Non repudiation<br>
	 * Binary: 1000000<br>
	 * Hex: 0x40<br>
	 * Dec: 64<br>
	 */
	NON_REPUDIATION((1 << 6), "nonRepudiation"),
	
	/**
	 * Key encipherment<br>
	 * Binary: 100000<br>
	 * Hex: 0x20<br>
	 * Dec: 32<br>
	 */
	KEY_ENCIPHERMENT((1 << 5), "keyEncipherment"),
	
	/**
	 * Data encipherment<br>
	 * Binary: 10000<br>
	 * Hex: 0x10<br>
	 * Dec: 16<br>
	 */
	DATA_ENCIPHERMENT((1 << 4), "dataEncipherment"),
	
	/**
	 * Key agreement<br>
	 * Binary: 100<br>
	 * Hex: 0x08<br>
	 * Dec: 8<br>
	 */
	KEY_AGREEMENT((1 << 3), "keyAgreement"),
	
	/**
	 * Certificate signing<br>
	 * Binary: 100<br>
	 * Hex: 0x04<br>
	 * Dec: 4<br>
	 */
	KEY_CERT_SIGN((1 << 2), "keyCertSign"),
	
	/**
	 * CRL signing<br>
	 * Binary: 10<br>
	 * Hex: 0x02<br>
	 * Dec: 2<br>
	 */
	CRL_SIGN((1 << 1), "crlSign"), 
	
	/**
	 * Encipherment only<br>
	 * Binary: 1<br>
	 * Hex: 0x01<br>
	 * Dec: 1<br>
	 */
	ENCIPHERMENT_ONLY((1 << 0), "encipherOnly"),
	
	/**
	 * Dicipherment only<br>
	 * Binary: 1000000000000000<br>
	 * Hex: 0x8000<br>
	 * Dec: 32768<br>
	 */
	DECIPHER_ONLY((1 << 15), "decipherOnly");
	
	protected final int bit;
	protected final String name;
	
	private KeyUsageBit(int bit, String name)
	{
		this.bit = bit;
		this.name = name;
	}
	
	/**
	 * Gets the key usage bit as an integer.
	 * @return The key usage bit as an integer.
	 */
	public int getUsageBit()
	{
		return bit;
	}
	
	/**
	 * Gets the name of the key usage bit.
	 * @return The name of the key usage bit
	 */
	public String getName()
	{
		return name;
	}
}
