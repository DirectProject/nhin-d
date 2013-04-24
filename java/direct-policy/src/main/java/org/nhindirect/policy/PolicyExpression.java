package org.nhindirect.policy;

import java.io.Serializable;

public interface PolicyExpression extends Serializable
{
	public PolicyExpressionType getExpressionType();
}
