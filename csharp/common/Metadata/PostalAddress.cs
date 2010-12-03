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
    /// Represents a US Postal address
    /// </summary>
    public struct PostalAddress : IEquatable<PostalAddress>
    {
        private string m_Street;
        private string m_City;
        private string m_State; // TODO: should be enum, but that's tedious...
        private string m_Zip; // TODO: should format validate...

        /// <summary>
        /// PostalAddress street name
        /// </summary>
        public string Street { get { return m_Street; } set { m_Street = value; } }
        /// <summary>
        /// PostalAddress city name
        /// </summary>
        public string City { get { return m_City; } set { m_City = value; } }
        /// <summary>
        /// PostalAddress state code
        /// </summary>
        public string State { get { return m_State; } set { m_State = value; } }

        /// <summary>
        /// PostalAddress postal (ZIP) code
        /// </summary>
        public string Zip { get { return m_Zip; } set { m_Zip = value; } }

        /// <summary>
        /// Formats the address as an HL7 AD type
        /// </summary>
        /// <returns></returns>
        public string ToHL7Ad()
        {
            return string.Format("{0}^^{1}^{2}^{3}^USA",
                                 Street ?? "",
                                 City ?? "",
                                 State ?? "",
                                 Zip ?? "");

        }

        /// <summary>
        /// Parses an HL7 AD type field and returns a new instance
        /// </summary>
        public static PostalAddress FromHL7Ad(string ad)
        {
            List<string> fields = HL7Util.SplitField(ad, 1, 5);
            return new PostalAddress { Street = fields[0], City = fields[2], State = fields[3], Zip = fields[4] };
        }


        /// <summary>
        /// Tests equality between this instance and another
        /// </summary>
        public bool Equals(PostalAddress other)
        {
            return Street == other.Street && City == other.City && State == other.State && Zip == other.Zip;
        }

        /// <summary>
        /// Tests equality between this instance and another
        /// </summary>
        public override bool Equals(object obj)
        {
            if (obj == null) return false;
            if (obj is PostalAddress) return Equals((PostalAddress) obj);
            return false;
        }

        /// <summary>
        /// Returns a string representation of the address
        /// </summary>
        public override string ToString()
        {
            return String.Format("{0}\n{1},{2} {3}", Street, City, State, Zip);
        }

        /// <summary>
        /// Returns a hash of this instance.
        /// </summary>
        public override int GetHashCode()
        {
            return ToString().GetHashCode();
        }
    }
}