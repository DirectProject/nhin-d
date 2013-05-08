package org.nhindirect.policy;

import java.io.Serializable;

public class LiteralPolicyExpressionFactory 
{
	public static <T> LiteralPolicyExpression<T> getInstance(PolicyValue<T> value)
	{
		return new LiteralPolicyExpressionImpl<T>(value);
	}
	
	public static <T> LiteralPolicyExpression<T> getInstance(T value)
	{
		return new LiteralPolicyExpressionImpl<T>(PolicyValueFactory.getInstance(value));
	}
	
	protected static class LiteralPolicyExpressionImpl<T> implements LiteralPolicyExpression<T>, Serializable
	{
		static final long serialVersionUID = -4788934771158627147L;
		
		protected final PolicyValue<T> value;
		
		protected LiteralPolicyExpressionImpl(PolicyValue<T> value)
		{
			this.value = value;
		}

		@Override
		public PolicyExpressionType getExpressionType() 
		{
			return PolicyExpressionType.LITERAL;
		}

		@Override
		public PolicyValue<T> getPolicyValue() 
		{
			return value;
		}
		
	}
}
