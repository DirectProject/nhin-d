using System;
using System.Collections.Generic;
using System.Text;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.Operators;

namespace Health.Direct.Policy
{
    public class OperationPolicyExpression : IOperationPolicyExpression
    {
        protected readonly OperatorBase Operator;
		protected readonly List<IPolicyExpression> Operands;

        
        /**
		 * Constructor
		 * @param operator The operator that will be executed when the expression is evaluated.
		 * @param operands The parameters that will be used by the operator when the expression is evaluated.
		 */
        public OperationPolicyExpression(OperatorBase pOperator, IEnumerable<IPolicyExpression> operands)
		{
			this.Operator = pOperator;
            this.Operands = new List<IPolicyExpression>(operands);
		}

		/// <inheritdoc />
		public PolicyExpressionType GetExpressionType() 
		{
			return PolicyExpressionType.OPERATION;
		}

		/// <inheritdoc />
        public OperatorBase GetPolicyOperator() 
		{
			return Operator;
		}

		/// <inheritdoc />
        public List<IPolicyExpression> GetOperands() 
		{
            return Operands;
		}
		
		public override string ToString()
		{

		    StringBuilder builder = new StringBuilder("Operator: ")
		        .Append(Operator);
			
			int i = 1;

		    foreach (var operand in Operands)
		    {
		      
				try
				{
					builder.Append("\r\nOperand ").Append(i++).Append(": ").Append(operand);
				}
				catch (Exception e)
				{
					Console.WriteLine(e);

				}
			}
			
			return builder.ToString();
		}
    }
}
