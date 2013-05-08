package org.nhindirect.policy;

public interface ReferencePolicyExpression<R, P> extends LiteralPolicyExpression<P>
{
	public PolicyExpressionReferenceType getPolicyExpressionReferenceType();
	
	public void injectReferenceValue(R value) throws PolicyProcessException;
}
