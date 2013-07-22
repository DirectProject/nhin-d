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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.InternetAddress;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.stagent.policy.PolicyResolver;

/**
 * Certificate policy resolver that returns the same collection of policies regardless of message direction or email address.
 * @author Greg Meyer
 * @since 2.0
 */
public class UniversalPolicyResolver implements PolicyResolver
{
	protected Collection<PolicyExpression> expressions;
	
	/**
	 * Construct a resolver with a single policy expression.
	 * @param expression The policy expression applied to all domains and messages directions.
	 */
    public UniversalPolicyResolver(PolicyExpression expression)
    {
        if (expression == null)
            throw new IllegalArgumentException("Empty or null expressions are not allowed");
    	
    	expressions = new ArrayList<PolicyExpression>();
    	expressions.add(expression);
    }
    
    /**
     * Construct a resolver with a collection of policy expressions.
     * @param expressions The policy expressions applied to all domains and messages directions.
     */
    public UniversalPolicyResolver(Collection<PolicyExpression> expressions)
    {
    	setExpressions(expressions);
    }      
    
    /**
     * Sets the policy expressions applied to all domains and messages directions.
     * @param expressions Tthe policy expressions applied to all domains and messages directions.
     */
    public void setExpressions(Collection<PolicyExpression> expressions)
	{
        if (expressions == null || expressions.size() == 0)
            throw new IllegalArgumentException("Empty or null expressions are not allowed");
        
        this.expressions = new ArrayList<PolicyExpression>(expressions);
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public Collection<PolicyExpression> getOutgoingPolicy(InternetAddress address)
	{
	    if (address == null)
	    {
	        throw new IllegalArgumentException();
	    }
	    
	    return Collections.unmodifiableCollection(expressions);
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
	public Collection<PolicyExpression> getIncomingPolicy(InternetAddress address)
	{
	    if (address == null)
	    {
	        throw new IllegalArgumentException();
	    }
	    
	    return Collections.unmodifiableCollection(expressions);
	}
}
