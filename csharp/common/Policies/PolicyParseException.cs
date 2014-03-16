using System;

namespace Health.Direct.Common.Policies
{
    /// <summary>
    /// Thrown when errors are encountered when parsing a expression from a lexicon.
    /// </summary>
    public class PolicyParseException : PolicyProcessException
    {
        public PolicyParseException()
        {
        }

        public PolicyParseException(String msg) :base(msg){}

        public PolicyParseException(String msg, Exception ex) : base(msg, ex){}

    }
}