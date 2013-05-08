package org.nhindirect.policy;

import java.util.Vector;

public class OperationPolicyExpressionFactory 
{
	public static OperationPolicyExpression getInstance(PolicyOperator operator, Vector<PolicyExpression> operands)
	{
		return new OperationPolicyExpressionImpl(operator, operands);
	}
	
	public static class OperationPolicyExpressionImpl implements OperationPolicyExpression
	{
		static final long serialVersionUID = -9131661511886002211L;
		
		protected final PolicyOperator operator;
		protected final Vector<PolicyExpression> operands;
		
		protected OperationPolicyExpressionImpl(PolicyOperator operator, Vector<PolicyExpression> operands)
		{
			this.operator = operator;
			this.operands = new Vector<PolicyExpression>(operands);
		}

		@Override
		public PolicyExpressionType getExpressionType() 
		{
			return PolicyExpressionType.OPERATION;
		}

		@Override
		public PolicyOperator getPolicyOperator() 
		{
			return operator;
		}

		@Override
		public Vector<PolicyExpression> getOperands() 
		{
			return operands;
		}
	}
}
