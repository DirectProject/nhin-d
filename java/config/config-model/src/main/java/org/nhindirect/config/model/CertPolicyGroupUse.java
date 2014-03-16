/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.config.model;

import org.codehaus.enunciate.json.JsonRootType;

/**
 * Certificate policy usages.
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@JsonRootType
public class CertPolicyGroupUse
{
	private CertPolicy policy;
	private CertPolicyUse policyUse;
	private boolean incoming;
	private boolean outgoing;
	
	/**
	 * Empty constructor
	 */
	public CertPolicyGroupUse()
	{
		
	}

	/**
	 * Gets the policy that is associated with the policy group.
	 * @return The policy that is associated with the policy group.
	 */
	public CertPolicy getPolicy() 
	{
		return policy;
	}

	/**
	 * Sets the policy that is associated with the policy group.
	 * @param policy The policy that is associated with the policy group.
	 */
	public void setPolicy(CertPolicy policy) 
	{
		this.policy = policy;
	}

	/**
	 * Gets the usage enumeration that indicates where the policy should be applied.
	 * @return The usage enumeration that indicates where the policy should be applied.
	 */
	public CertPolicyUse getPolicyUse() 
	{
		return policyUse;
	}

	/**
	 * Sets the usage enumeration that indicates where the policy should be applied.
	 * @param policyUse The usage enumeration that indicates where the policy should be applied.
	 */
	public void setPolicyUse(CertPolicyUse policyUse) 
	{
		this.policyUse = policyUse;
	}

	/**
	 * Indicates if the policy should be applied to incoming messages.
	 * @return True if the policy should be applied to incoming messages.  False otherwise.
	 */
	public boolean isIncoming() 
	{
		return incoming;
	}

	/**
	 * Sets the incoming message usage flag.
	 * @param incoming True if the policy should be applied to incoming messages.  False otherwise.
	 */
	public void setIncoming(boolean incoming) 
	{
		this.incoming = incoming;
	}

	/**
	 * Indicates if the policy should be applied to outgoing messages.
	 * @return True if the policy should be applied to outgoing messages.  False otherwise.
	 */
	public boolean isOutgoing() 
	{
		return outgoing;
	}

	/**
	 * Sets the outgoing message usage flag.
	 * @param outgoing True if the policy should be applied to outgoing messages.  False otherwise.
	 */
	public void setOutgoing(boolean outgoing) 
	{
		this.outgoing = outgoing;
	}
}
///CLOVER:ON
