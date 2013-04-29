package org.nhindirect.policy;

public interface LiteralPolicyExpression<P> extends PolicyExpression
{
	public PolicyValue<P> getPolicyValue();
}
