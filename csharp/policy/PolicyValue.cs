using System;

namespace Health.Direct.Policy
{
    [Serializable]
    public class PolicyValue<T> : IPolicyValue<T>
    {
        protected readonly T Value;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="value">The value contained within the <see cref="IPolicyValue{T}"/> instance.</param>
        public PolicyValue(T value)
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