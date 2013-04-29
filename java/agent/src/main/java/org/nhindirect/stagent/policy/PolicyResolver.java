package org.nhindirect.stagent.policy;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.policy.PolicyExpression;

public interface PolicyResolver 
{
	public Collection<PolicyExpression> getOutgoingPolicy(InternetAddress address);
	
	public Collection<PolicyExpression> getIncomingPolicy(InternetAddress address);
}
