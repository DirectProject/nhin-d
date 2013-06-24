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
 * Enumeration of object identifiers (OIDs) of extended key usages used in the extended key usage certificate extension
 * @author Greg Meyer
 * @since 1.0
 */
public enum ExtendedKeyUsageIdentifier 
{
	/**
	 * Any use<br>
	 * OID: 2.5.29.37.0
	 */
	ANY_EXTENDED_KEY_USAGE("2.5.29.37.0"),
	
	/**
	 * Server authentication<br>
	 * OID: 1.3.6.1.5.5.7.3.1
	 */
	ID_KP_SERVER_AUTH("1.3.6.1.5.5.7.3.1"),

	/**
	 * Client authentication<br>
	 * OID: 1.3.6.1.5.5.7.3.2
	 */
	ID_KP_CLIENT_AUTH("1.3.6.1.5.5.7.3.2"),
	
	/**
	 * Code signing<br>
	 * OID: 1.3.6.1.5.5.7.3.3
	 */
	ID_KP_CODE_SIGNING("1.3.6.1.5.5.7.3.3"),
	
	/**
	 * Email protection<br>
	 * OID: 1.3.6.1.5.5.7.3.4
	 */
	ID_KP_EMAIL_PROTECTION("1.3.6.1.5.5.7.3.4"),

	/**
	 * IP security end system<br>
	 * OID: 1.3.6.1.5.5.7.3.5
	 */
	ID_KP_IPSEC_END_SYSTEM("1.3.6.1.5.5.7.3.5"),

	/**
	 * IP security tunnel<br>
	 * OID: 1.3.6.1.5.5.7.3.6
	 */
	ID_KP_IPSEC_TUNNEL("1.3.6.1.5.5.7.3.6"),

	/**
	 * IP security user<br>
	 * OID: 1.3.6.1.5.5.7.3.7
	 */
	ID_KP_IPSEC_USER("1.3.6.1.5.5.7.3.7"),
	
	/**
	 * Time stamping<br>
	 * OID: 1.3.6.1.5.5.7.3.8
	 */
	ID_KP_TIME_STAMPING("1.3.6.1.5.5.7.3.8"),

	/**
	 * OCSP signing<br>
	 * OID: 1.3.6.1.5.5.7.3.9
	 */
	ID_KP_OCSP_SIGNING("1.3.6.1.5.5.7.3.9"),
	
	/**
	 * Data validation<br>
	 * OID: 1.3.6.1.5.5.7.3.10
	 */
	ID_KP_DVCS("1.3.6.1.5.5.7.3.10"),
	
	/**
	 * OID: 1.3.6.1.5.5.7.3.11
	 */
	ID_KP_SBGP_CERT_AA_SERVER_AUTH("1.3.6.1.5.5.7.3.11"),

	/**
	 * Server based certification validation protocol responder<br>
	 * OID: 1.3.6.1.5.5.7.3.12
	 */
	ID_KP_SCVP_RESPONDER("1.3.6.1.5.5.7.3.12"),

	/**
	 * EAP over PPP<br>
	 * OID: 1.3.6.1.5.5.7.3.13
	 */
	ID_KP_EAP_OVER_PPP("1.3.6.1.5.5.7.3.13"),

	/**
	 * EAP over LAM<br>
	 * OID: 1.3.6.1.5.5.7.3.14
	 */
	ID_KP_EAP_OVER_LAN("1.3.6.1.5.5.7.3.14"),

	/**
	 * Server based certification validation protocol responder<br>
	 * OID: 1.3.6.1.5.5.7.3.15
	 */
	ID_KP_SCVP_SERVER("1.3.6.1.5.5.7.3.15"),

	/**
	 * Server based certification validation protocol client<br>
	 * OID: 1.3.6.1.5.5.7.3.16
	 */	
	ID_KP_SCVP_CLIENT("1.3.6.1.5.5.7.3.16"),

	/**
	 * Internet key exchange<br>
	 * OID: 1.3.6.1.5.5.7.3.17
	 */
	ID_KP_IPSEC_IKE("1.3.6.1.5.5.7.3.17"),

	/**
	 * Control And Provisioning of Wireless Access Points, Access Controller<br>
	 * OID: 1.3.6.1.5.5.7.3.18
	 */
	ID_KP_CAP_WAP_AC("1.3.6.1.5.5.7.3.18"),
	
	/**
	 * Control And Provisioning of Wireless Access Points, Wireless Termination Points<br>
	 * OID: 1.3.6.1.5.5.7.3.19
	 */
	ID_KP_CAP_WAP_WPT("1.3.6.1.5.5.7.3.19");
	
	protected final String usageId;
	
	private ExtendedKeyUsageIdentifier(String usageId)
	{
		this.usageId = usageId;
	}
	
	/**
	 * Gets the object identifier (OID) of the extended key usage.
	 * @return The object identifier (OID) of the extended key usage
	 */
	public String getId()
	{
		return usageId;
	}
}
