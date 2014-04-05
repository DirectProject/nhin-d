/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using Health.Direct.Common.Policies;

namespace Health.Direct.Policy
{
    
    public class LiteralPolicyExpression<T> : ILiteralPolicyExpression<T>
    {
        private readonly IPolicyValue<T> m_policyValue;

        
        public LiteralPolicyExpression(IPolicyValue<T> value)
        {
            m_policyValue = value;
        }

        public LiteralPolicyExpression(T value)
        {
            m_policyValue = new PolicyValue<T>(value);
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