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
using System.Collections.Generic;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Represents an institution
    /// </summary>
    public struct Institution : IEquatable<Institution>
    {
        private string m_name;
        private string m_assigningAuthority;

        /// <summary>
        /// Initializes an instition without an assigning authority
        /// </summary>
        public Institution(string name)
        {
            m_name = name;
            m_assigningAuthority = null;
        }

        /// <summary>
        /// Initializes an institution with an assinging authority
        /// </summary>
        public Institution(string name, string assigningAuthority)
        {
            m_name = name;
            m_assigningAuthority = assigningAuthority;
        }

        /// <summary>
        /// The instition name
        /// </summary>
        public string Name { get { return m_name; } }

        /// <summary>
        /// The assigning authority responsible for the name (should be an OID)
        /// </summary>
        public string AssigningAuthority { get { return m_assigningAuthority; } }

        /// <summary>
        /// Formats the institution as an XON datatype
        /// </summary>
        public string ToXON()
        {
            if (AssigningAuthority == null)
                return Name;
            return String.Format("{0}^^^^^^^^^{1}", Name, AssigningAuthority);
        }

        /// <summary>
        /// Parses the XON datatype and returns the corresponding <see cref="Institution"/>
        /// </summary>
        public static Institution FromXON(string i)
        {
            List<string> fields = HL7Util.SplitField(i, 1, 10);
            return new Institution(fields[0], fields[9]);
        }

        /// <summary>
        /// String representation of this institution.
        /// </summary>
        public override string ToString()
        {
            return string.Format("{0}, AA: {1}", Name, AssigningAuthority ?? "none");
        }

        /// <summary>
        /// Tests equality between this instance and another
        /// </summary>
        public bool Equals(Institution other)
        {
            if (AssigningAuthority == null && other.AssigningAuthority == null)
                return Name == other.Name;
            else
                return (Name == other.Name && AssigningAuthority == other.AssigningAuthority);
        }

        /// <summary>
        /// Tests equality between this instance and another
        /// </summary>
        public override bool Equals(object obj)
        {
            if (obj == null) return false;
            if (obj is Institution) return Equals((Institution) obj);
            return false;
        }

        /// <summary>
        /// Returns the hashcode for the specified object
        /// </summary>
        public override int GetHashCode()
        {
            return ToString().GetHashCode();
        }
    }
}