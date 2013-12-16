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


using System;
using System.Collections.Generic;

namespace Health.Direct.Policy
{
    /// <summary>
    /// Factory class that generates a <see cref="IPolicyValue{T}" /> instance for an actual value.
    /// </summary>
    public class PolicyValueFactory<T>
    {
        private PolicyValueFactory(){}
 
        /// <summary>
        /// Creates an instance of <see cref="IPolicyValue{T}" /> containing the given value.
        /// </summary>
        /// <param name="value"> The value contained within the generated <see cref="IPolicyValue{T}" /> instance.</param>
        /// <returns>New instance of a <see cref="IPolicyValue{T}" /> instance containing the given value.</returns>
        public static IPolicyValue<T> GetInstance(T value)
        {
            return new PolicyValueImpl(value);
        }

        public static IPolicyValue<int> GetInstance(int value)
        {
            return new PolicyValueIntImpl(value);
        }

        public static IPolicyValue<bool> GetInstance(bool value)
        {
            return new PolicyValueBoolImpl(value);
        }
            
        [Serializable]
        public class PolicyValueImpl : IPolicyValue<T>
        {
            protected readonly T Value;

            /// <summary>
            /// 
            /// </summary>
            /// <param name="value">The value contained within the <see cref="IPolicyValue{T}"/> instance.</param>
            internal PolicyValueImpl(T value)
            {
                Value = value;
            }

            /// <inheritdoc />
            public T GetPolicyValue()
            {
                return Value;
            }

            /// <summary>
            /// Returns the ToString representation of the internal policy value.
            /// </summary>
            /// <returns></returns>
            public override string ToString()
            {
                return Value.ToString();
            }


            /// <summary>
            /// Returns the Equals representation of the internal policy value.
            /// </summary>
            /// <returns></returns>
            public override bool Equals(object obj)
            {
                if (obj == null)
                    return false;
                if (obj is IPolicyValue<T>)
                    return Value.Equals(((IPolicyValue<T>)obj).GetPolicyValue());

                return Value.Equals(obj);
            }

            /// <summary>
            /// Returns the GetHashCode representation of the internal policy value.
            /// </summary>
            /// <returns></returns>
            public override int GetHashCode()
            {
                return Value.GetHashCode();
            }
        }
    }
    

    [Serializable]
    public class PolicyValueIntImpl : IPolicyValue<int>
    {
        protected readonly int Value;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="value">The value contained within the <see cref="IPolicyValue{T}"/> instance.</param>
        internal PolicyValueIntImpl(int value)
        {
            Value = value;
        }

        /// <inheritdoc />
        public int GetPolicyValue()
        {
            return Value;
        }

        /// <summary>
        /// Returns the ToString representation of the internal policy value.
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            return Value.ToString();
        }


        /// <summary>
        /// Returns the Equals representation of the internal policy value.
        /// </summary>
        /// <returns></returns>
        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            var value = obj as IPolicyValue<int>;
            if (value != null)
                return Value.Equals(value.GetPolicyValue());

            return Value.Equals(obj);
        }

        /// <summary>
        /// Returns the GetHashCode representation of the internal policy value.
        /// </summary>
        /// <returns></returns>
        public override int GetHashCode()
        {
            return Value.GetHashCode();
        }
    }

    [Serializable]
    public class PolicyValueBoolImpl : IPolicyValue<bool>
    {
        protected readonly bool Value;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="value">The value contained within the <see cref="IPolicyValue{T}"/> instance.</param>
        internal PolicyValueBoolImpl(bool value)
        {
            Value = value;
        }

        /// <inheritdoc />
        public bool GetPolicyValue()
        {
            return Value;
        }

        /// <summary>
        /// Returns the ToString representation of the internal policy value.
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            return Value.ToString();
        }


        /// <summary>
        /// Returns the Equals representation of the internal policy value.
        /// </summary>
        /// <returns></returns>
        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            var value = obj as IPolicyValue<bool>;
            if (value != null)
                return Value.Equals(value.GetPolicyValue());

            return Value.Equals(obj);
        }

        /// <summary>
        /// Returns the GetHashCode representation of the internal policy value.
        /// </summary>
        /// <returns></returns>
        public override int GetHashCode()
        {
            return Value.GetHashCode();
        }
    }
}
