using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Health.Direct.Policy.Operators;

namespace Health.Direct.Policy
{
    [Serializable]
    public class OperationPolicyExpression : IOperationPolicyExpression
    {
        protected readonly OperatorBase m_operator;
		protected readonly List<IPolicyExpression> m_operands;

        public OperationPolicyExpression()
        {
        }

        /**
		 * Constructor
		 * @param operator The operator that will be executed when the expression is evaluated.
		 * @param operands The parameters that will be used by the operator when the expression is evaluated.
		 */
        public OperationPolicyExpression(OperatorBase pOperator, List<IPolicyExpression> operands)
		{
			this.m_operator = pOperator;
			this.m_operands = new List<IPolicyExpression>(operands);
		}

		/// <inheritdoc />
		public PolicyExpressionType GetExpressionType() 
		{
			return PolicyExpressionType.OPERATION;
		}

		/// <inheritdoc />
        public OperatorBase GetPolicyOperator() 
		{
			return m_operator;
		}

		/// <inheritdoc />
		public List<IPolicyExpression> GetOperands() 
		{
            return m_operands;
		}
		
		public override string ToString()
		{

		    StringBuilder builder = new StringBuilder("Operator: ")
		        .Append(m_operator);
			
			int i = 1;

		    foreach (var operand in m_operands)
		    {
		      
				try
				{
					builder.Append("\r\nOperand ").Append(i++).Append(": ").Append(operand.ToString());
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
