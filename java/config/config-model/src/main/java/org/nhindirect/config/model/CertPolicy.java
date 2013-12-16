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

import java.util.Calendar;

import org.codehaus.enunciate.json.JsonRootType;
import org.nhindirect.policy.PolicyLexicon;

///CLOVER:OFF
@JsonRootType
public class CertPolicy 
{
	private String policyName;
	
	private byte[] policyData;
  
	private Calendar createTime;  
	
	private PolicyLexicon lexicon;
	
	/**
	 * Empty constructor
	 */
	public CertPolicy()
	{
		
	}

	/**
	 * Gets the name of the policy.  Policy names are unique and case sensitive.
	 * @return The name of the policy.
	 */
	public String getPolicyName() 
	{
		return policyName;
	}

	/**
	 * Sets the name of the policy.  Policy names are unique and case sensitive.
	 * @param policyName The name of the policy.
	 */
	public void setPolicyName(String policyName) 
	{
		this.policyName = policyName;
	}

	/**
	 * Gets the policy definition in byte array format.
	 * @return The policy definition.
	 */
	public byte[] getPolicyData() 
	{
		return policyData;
	}

	/**
	 * Sets the policy definition in byte array format.
	 * @param policyData The policy definition
	 */
	public void setPolicyData(byte[] policyData) 
	{
		this.policyData = policyData;
	}

	/**
	 * Gets the time the policy was imported into the system.
	 * @return The time the policy was imported into the system.
	 */
	public Calendar getCreateTime() 
	{
		return createTime;
	}

	/**
	 * Sets the time the policy was imported into the system.
	 * @param createTime The time the policy was imported into the system.
	 */
	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}
	
	/**
	 * Gets the lexicon that this policy is written in.
	 * @return The lexicon that this policy is written in.
	 */
	public PolicyLexicon getLexicon()
	{
		return this.lexicon;
	}
	
	/**
	 * Sets the lexicon that this policy is written in.
	 * @param lexicon The lexicon that this policy is written in.
	 */
	public void setLexicon(PolicyLexicon lexicon)
	{
		this.lexicon = lexicon;
	}
}
///CLOVER:ON

