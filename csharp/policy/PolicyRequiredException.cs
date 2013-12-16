using System;

namespace Health.Direct.Policy
{
    public class PolicyRequiredException : PolicyProcessException
    {
        public PolicyRequiredException() { }


        public PolicyRequiredException(String msg) : base(msg) { }

        public PolicyRequiredException(String msg, Exception ex) : base(msg, ex) { }

    }
}
