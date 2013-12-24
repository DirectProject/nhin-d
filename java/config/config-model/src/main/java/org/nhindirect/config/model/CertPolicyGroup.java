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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import org.codehaus.enunciate.json.JsonRootType;

///CLOVER:OFF
@JsonRootType
public class CertPolicyGroup 
{
	private String policyGroupName;
	private Collection<CertPolicyGroupUse> policies;
    private Calendar createTime;  
    
    /**
     * Empty constructor
     */
    public CertPolicyGroup()
    {
    	
    }

    /**
     * Gets the policy group name.
     * @return The policy group name.
     */
	public String getPolicyGroupName() 
	{
		return policyGroupName;
	}

	/**
	 * Gets the policy group name.
	 * @param policyGroupName The policy group name.
	 */
	public void setPolicyGroupName(String policyGroupName) 
	{
		this.policyGroupName = policyGroupName;
	}

	/**
	 * Gets the collection of policy usages for this group.
	 * @return The collection of policy usages for this group.
	 */
	public Collection<CertPolicyGroupUse> getPolicies() 
	{
		if (policies == null)
			policies = Collections.emptyList();
		
		return Collections.unmodifiableCollection(policies);
	}

	/**
	 * Sets the collection of policy usages for this group.
	 * @param policies The collection of policy usages this group.
	 */
	public void setPolicies(Collection<CertPolicyGroupUse> policies) 
	{
		this.policies = new ArrayList<CertPolicyGroupUse>(policies);
	}

	/**
	 * Gets the date/time this group was created.
	 * @return The date/time this group was created.
	 */
	public Calendar getCreateTime() 
	{
		return createTime;
	}

	/**
	 * Sets the date/time this group was created.
	 * @param createTime The date/time this group was created.
	 */
	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}
    
    
}
///CLOVER:ON
