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

package org.nhindirect.policy;

import java.util.Vector;

/**
 * Factory class for creating {@link OperationPolicyExpression} instances.
 * @author Greg Meyer
 * @since 1.0
 *
 */
public class OperationPolicyExpressionFactory 
{
	/**
	 * Creates an instance from a {@link OperationPolicyExpression}
	 * @param operator The operator that will be executed when the expression is evaluated.
	 * @param operands The parameters that will be used by the operator when the expression is evaluated.
	 * @return A new instance of a {@link OperationPolicyExpression}.
	 */
	public static OperationPolicyExpression getInstance(PolicyOperator operator, Vector<PolicyExpression> operands)
	{
		return new OperationPolicyExpressionImpl(operator, operands);
	}
	
	/**
	 * Default implementation of the {@link OperationPolicyExpression} interface.
	 * @author Greg Meyer
	 * @since 1.0
	 * 
	 */
	public static class OperationPolicyExpressionImpl implements OperationPolicyExpression
	{
		static final long serialVersionUID = -9131661511886002211L;
		
		protected final PolicyOperator operator;
		protected final Vector<PolicyExpression> operands;
		
		/**
		 * Constructor
		 * @param operator The operator that will be executed when the expression is evaluated.
		 * @param operands The parameters that will be used by the operator when the expression is evaluated.
		 */
		protected OperationPolicyExpressionImpl(PolicyOperator operator, Vector<PolicyExpression> operands)
		{
			this.operator = operator;
			this.operands = new Vector<PolicyExpression>(operands);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PolicyExpressionType getExpressionType() 
		{
			return PolicyExpressionType.OPERATION;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PolicyOperator getPolicyOperator() 
		{
			return operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Vector<PolicyExpression> getOperands() 
		{
			return operands;
		}
		
		///CLOVER:OFF
		@Override
		public String toString()
		{
			final StringBuilder builder = new StringBuilder("Operator: ").append(operator.toString());
			
			int i = 1;
			
			for (PolicyExpression operand : operands)
			{
				try
				{
					builder.append("\r\nOperand ").append(i++).append(": ").append(operand.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			return builder.toString();
		}
		///CLOVER:ON
	}
}
