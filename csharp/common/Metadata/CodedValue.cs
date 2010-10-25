/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Represents a coded value (code with a label)
    /// </summary>
    public class CodedValue : IEquatable<CodedValue>
    {
        /// <summary>
        /// Initializes an instance with the supplied code and label.
        /// </summary>
        /// <param name="code">The code for this instance</param>
        /// <param name="label">The label for this instance</param>
        /// <param name="scheme">The coding scheme for this code</param>
        public CodedValue(string code, string label, string scheme)
        {
            Code = code;
            Label = label;
            Scheme = scheme;

        }

        /// <summary>
        /// The class code for this instance.
        /// </summary>
        public string Code { get; private set; }

        /// <summary>
        /// The label for this instance.
        /// </summary>
        public string Label { get; private set; }

        /// <summary>
        /// The coding scheme for this instance
        /// </summary>
        public string Scheme { get; private set; }

        /// <summary>
        /// Returns a string representation of the object
        /// </summary>
        public override string ToString()
        {
            return String.Format("Code: {0} ({1}) from {2}", Code, Label, Scheme);
        }

        /// <summary>
        /// Tests equality between this instance and another one
        /// </summary>
        public bool Equals(CodedValue other)
        {
            if (other == null) return false;
            return Code == other.Code && Scheme == other.Scheme;
        }

        /// <summary>
        /// Tests equality between this instance and another one
        /// </summary>
        public override bool Equals(Object other)
        {
            if (!(other is CodedValue)) return false;
            return Equals(other as CodedValue);
        }

        /// <summary>
        /// Hash code for CodedValues
        /// </summary>
        public override int GetHashCode()
        {
            return Code.GetHashCode();
        }

    }
}