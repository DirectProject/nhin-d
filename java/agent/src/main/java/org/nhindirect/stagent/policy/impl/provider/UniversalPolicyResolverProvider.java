package org.nhindirect.stagent.policy.impl.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.stagent.policy.PolicyResolver;
import org.nhindirect.stagent.policy.impl.UniversalPolicyResolver;

import com.google.inject.Provider;

public class UniversalPolicyResolverProvider implements Provider<PolicyResolver>
{
	protected final Collection<PolicyExpression> expressions;
	
	public UniversalPolicyResolverProvider(PolicyExpression expression)
	{
    	expressions = new ArrayList<PolicyExpression>();
    	expressions.add(expression);
	}
	
    public UniversalPolicyResolverProvider(Collection<PolicyExpression> expressions)
    {
    	this.expressions = expressions;
    }  
    
	public PolicyResolver get()
	{
		return new UniversalPolicyResolver(expressions);
	}
}
