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

package org.nhindirect.stagent.policy.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.InternetAddress;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.policy.PolicyResolver;

/**
 * Certificate policy resolver that groups policies by email domain.  Each domain may have a unique set of policies.
 * @author Greg Meyer
 * @since 2.0
 */
public class DomainPolicyResolver implements PolicyResolver
{
	protected Map<String, Collection<PolicyExpression>> incomingPolicies;
	
	protected Map<String, Collection<PolicyExpression>> outgoingPolicies;	
	
	/**
	 * Constructs a resolver where both incoming and outgoing messages use the same policies.
	 * @param policies Map of email domains to policies.
	 */
    public DomainPolicyResolver(Map<String, Collection<PolicyExpression>> policies)
    {
    	setPolicies(policies, true);
    	setPolicies(policies, false);
    }
    
	/**
	 * Constructs a resolver splitting out incoming policies from outgoing policies.
	 * @param incomingPolicies Map of email domains to policies for incoming messages.
	 * @param outgoingPolicies Map of email domains to policies for outgoing messages.
	 */
    public DomainPolicyResolver(Map<String, Collection<PolicyExpression>> incomingPolicies,
    		Map<String, Collection<PolicyExpression>> outgoingPolicies)
    {
    	setPolicies(incomingPolicies, true);
    	setPolicies(outgoingPolicies, false);
    }
    
    /**
     * Sets the map of domains to policies.
     * @param policies Map of email domains to policies.
     * @param incoming Indicates if the map should be applied to incoming or outgoing messages.  true is setting for incoming messages, false for 
     * outgoing messages.
     */
	public void setPolicies(Map<String, Collection<PolicyExpression>> policies, boolean incoming) 
	{
		if (policies == null)
			throw new IllegalArgumentException("Policies cannot be null");
	
		Map<String, Collection<PolicyExpression>> toPolicies;
		
		if (incoming)
		{
			this.incomingPolicies = new HashMap<String, Collection<PolicyExpression>>();
			toPolicies = this.incomingPolicies;
		}
		else
		{
			this.outgoingPolicies = new HashMap<String, Collection<PolicyExpression>>();
			toPolicies = this.outgoingPolicies;
		}
		
		// copy this map, but make all the domains upper case for lookups
		//Set<Entry<String, Collection<PolicyExpression>>> entrySet = ;
		for (Entry<String, Collection<PolicyExpression>> entry : policies.entrySet())
		{
			toPolicies.put(entry.getKey().toUpperCase(Locale.getDefault()), entry.getValue());
		}
	} 
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<PolicyExpression> getOutgoingPolicy(InternetAddress address)
	{
		return getPolicies(address, false);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<PolicyExpression> getIncomingPolicy(InternetAddress address)
	{
		return getPolicies(address, true);
	}
	
	/**
	 * Gets the policies for a given email address.  The email address's domain name is used
	 * to look up the policy collection.
	 * @param address The email address used to lookup policies.
	 * @param incoming Indicates if the incoming or outgoing policy map should be searched.  true for incoming messages, false for outgoing
	 * @return
	 */
    protected Collection<PolicyExpression> getPolicies(InternetAddress address, boolean incoming)
	{
	    if (address == null)
	    {
	        throw new IllegalArgumentException("Address cannot be null");
	    }
	    
	    final Map<String, Collection<PolicyExpression>> searchPolicies = 
	    		(incoming) ? this.incomingPolicies : this.outgoingPolicies;
	    
	    // get the certificates for this address's domain and convert to upper case
	    final String domain = NHINDAddress.getHost(address).toUpperCase(Locale.getDefault());
	    
	    Collection<PolicyExpression> retPolocies = searchPolicies.get(domain);
	    
	    if (retPolocies == null)
	    	retPolocies = Collections.emptyList(); // return an empty list of no policies are found
	    
	    return retPolocies;
	}	
}
