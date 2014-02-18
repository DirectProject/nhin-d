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

package org.nhindirect.stagent.policy.impl.provider;

import java.util.Collection;
import java.util.Map;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.stagent.policy.PolicyResolver;
import org.nhindirect.stagent.policy.impl.DomainPolicyResolver;

import com.google.inject.Provider;

public class DomainPolicyResolverProvider implements Provider<PolicyResolver>
{
	protected final Map<String, Collection<PolicyExpression>> incomingPolicies;
	
	protected final Map<String, Collection<PolicyExpression>> outgoingPolicies;	
	
	public DomainPolicyResolverProvider(Map<String, Collection<PolicyExpression>> policies)
	{
		this.incomingPolicies = policies;
		this.outgoingPolicies = policies;
	}
	
    public DomainPolicyResolverProvider(Map<String, Collection<PolicyExpression>> incomingPolicies,
    		Map<String, Collection<PolicyExpression>> outgoingPolicies)
    {
		this.incomingPolicies = incomingPolicies;
		this.outgoingPolicies = outgoingPolicies;
    }
    
	public PolicyResolver get()
	{
		return new DomainPolicyResolver(incomingPolicies, outgoingPolicies);
	}
}
