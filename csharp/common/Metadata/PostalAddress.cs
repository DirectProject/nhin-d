/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Metadata
{
    /// <summary>
    /// Represents a US Postal address
    /// </summary>
    public struct PostalAddress
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
    }
}
