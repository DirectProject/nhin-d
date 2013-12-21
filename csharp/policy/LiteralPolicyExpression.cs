using System;

namespace Health.Direct.Policy
{
    
    public class LiteralPolicyExpression<T> : ILiteralPolicyExpression<T>
    {
        private readonly IPolicyValue<T> m_policyValue;

        
        public LiteralPolicyExpression(IPolicyValue<T> value)
        {
            m_policyValue = value;
        }

        public T Policy
        {
            get { return m_policyValue.GetPolicyValue(); }
            set{}
        }

        public IPolicyValue<T> GetPolicyValue()
        {
            return m_policyValue;
        }

        public PolicyExpressionType GetExpressionType()
        {
            return PolicyExpressionType.LITERAL;
        }

        //Todo: Do I need this?
        public override bool Equals(object obj)
        {
            if (obj == null) return false;

            if (obj.GetType() == typeof(ILiteralPolicyExpression<T>))
            {
                return m_policyValue.Equals(((ILiteralPolicyExpression<T>)obj).GetPolicyValue());
            }
            return m_policyValue.Equals(obj);
        }

        protected bool Equals(LiteralPolicyExpression<T> other)
        {
            return Equals(m_policyValue, other.m_policyValue);
        }

        public override int GetHashCode()
        {
            return (m_policyValue != null ? m_policyValue.GetHashCode() : 0);
        }
    }
}