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

public class DomainPolicyResolver implements PolicyResolver
{
	protected Map<String, Collection<PolicyExpression>> incomingPolicies;
	
	protected Map<String, Collection<PolicyExpression>> outgoingPolicies;	
	
    public DomainPolicyResolver(Map<String, Collection<PolicyExpression>> policies)
    {
    	setPolicies(policies, true);
    	setPolicies(policies, false);
    }
    
    public DomainPolicyResolver(Map<String, Collection<PolicyExpression>> incomingPolicies,
    		Map<String, Collection<PolicyExpression>> outgoingPolicies)
    {
    	setPolicies(incomingPolicies, true);
    	setPolicies(outgoingPolicies, false);
    }
    
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
		//Set<Entry<String, Collection<X509Certificate>>> entrySet = ;
		for (Entry<String, Collection<PolicyExpression>> entry : policies.entrySet())
		{
			toPolicies.put(entry.getKey().toUpperCase(Locale.getDefault()), entry.getValue());
		}
	} 
	
	@Override
	public Collection<PolicyExpression> getOutgoingPolicy(InternetAddress address)
	{
		return getPolicies(address, false);
	}
	
	@Override
	public Collection<PolicyExpression> getIncomingPolicy(InternetAddress address)
	{
		return getPolicies(address, true);
	}
	
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
