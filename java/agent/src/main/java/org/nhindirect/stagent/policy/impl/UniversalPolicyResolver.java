package org.nhindirect.stagent.policy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.InternetAddress;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.stagent.policy.PolicyResolver;


public class UniversalPolicyResolver implements PolicyResolver
{
	protected Collection<PolicyExpression> expressions;
	
    public UniversalPolicyResolver(PolicyExpression expression)
    {
        if (expression == null)
            throw new IllegalArgumentException("Empty or null expressions are not allowed");
    	
    	expressions = new ArrayList<PolicyExpression>();
    	expressions.add(expression);
    }
    
    public UniversalPolicyResolver(Collection<PolicyExpression> expressions)
    {
    	setExpressions(expressions);
    }      
    
    public void setExpressions(Collection<PolicyExpression> expressions)
	{
        if (expressions == null || expressions.size() == 0)
            throw new IllegalArgumentException("Empty or null expressions are not allowed");
        
        this.expressions = new ArrayList<PolicyExpression>(expressions);
	}

	public Collection<PolicyExpression> getOutgoingPolicy(InternetAddress address)
	{
	    if (address == null)
	    {
	        throw new IllegalArgumentException();
	    }
	    
	    return Collections.unmodifiableCollection(expressions);
	}
	
	public Collection<PolicyExpression> getIncomingPolicy(InternetAddress address)
	{
	    if (address == null)
	    {
	        throw new IllegalArgumentException();
	    }
	    
	    return Collections.unmodifiableCollection(expressions);
	}
}
