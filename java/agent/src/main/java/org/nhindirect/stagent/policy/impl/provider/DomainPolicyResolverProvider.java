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
