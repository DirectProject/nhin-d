using System;

namespace Health.Direct.Policy
{
    /// <summary>
    /// Generic exception for errors that occur during the policy engine process.
    /// </summary>
    public class PolicyProcessException : Exception
    {
        public PolicyProcessException()
        {
        }

        public PolicyProcessException(String msg):base(msg){}

        public PolicyProcessException(string msg, Exception ex) : base(msg, ex){}
    }
}