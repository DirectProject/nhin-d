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
using System.Security.Cryptography;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Represents a patient identifier
    /// </summary>
    public class PatientID : IEquatable<PatientID>
    {

        /// <summary>
        /// Initializes an instance with the specified ID and an OID based assinging authority
        /// </summary>
        public PatientID (string id, Oid oid)
            :this(id, oid.Value, "ISO")
        {

            m_oid = oid;
        }

        /// <summary>
        /// Initializes an instance with a custom assigning authority type
        /// </summary>
        public PatientID (string id, string assigningAuthority, string type)
        {
            m_id = id;
            m_aa = assigningAuthority;
            m_aaType = type;
            m_oid = null;
        }

        private string m_id;
        /// <summary>
        /// The patient ID
        /// </summary>
        public string Id {
            get { return m_id; }
            set { m_id = value; }
        }

        private string m_aa;
        /// <summary>
        /// The assigning authority for this ID.
        /// </summary>
        public string AssigningAuthority 
        { 
            get { return m_aa; }
            private set { m_aa = value;}
        }

        private string m_aaType;
        /// <summary>
        /// The type of assigning authority (ISO for OIDs)
        /// </summary>
        public string AssigningAuthorityType
        {
            get { return m_aaType; }
            private set { m_aaType = value; }
        }

        private Oid m_oid;
        /// <summary>
        /// If the assigning authority was set as an OID, this will be the <see cref="Oid"/>
        /// </summary>
        public Oid Oid 
        {
            get { return m_oid; }
            private set { m_oid = value; }
        }

        /// <summary>
        /// <c>true</c> if the assigning authority was set as an Oid type.
        /// </summary>
        public bool HasOid { get { return Oid != null; } }

        /// <summary>
        /// Formats this identifier as an HL7 CXN.
        /// </summary>
        public string ToCx()
        {
            return String.Format("{0}^^^&{1}&{2}", Id, AssigningAuthority, AssigningAuthorityType);
        }

        /// <summary>
        /// Formats this identifier as an HL7 CXN, with ampersands escaped as XML entities.
        /// </summary>
        public string ToEscapedCx()
        {
            return String.Format("{0}^^^&amp;{1}&amp;{2}", Id, AssigningAuthority, AssigningAuthorityType);
        }

        /// <summary>
        /// Creates an instance from a  CX string
        /// </summary>
        public static PatientID FromCx(string cx)
        {
            if (cx == null) { return null; }
            string[] parts = cx.Split('^');
            if (parts.Length < 4) throw new ArgumentException("Invalid CX: " + cx, "cx");
            string id = parts[0];
            string[] aa_parts = parts[3].Split('&');
            if (aa_parts.Length != 3) throw new ArgumentException(string.Format("Invalid CX AA part: {0}, {1} parts ", parts[3], aa_parts.Length), "cx");
            return new PatientID(id, aa_parts[1], aa_parts[2]);
        }

        /// <summary>
        /// Creates an instance from an escaped CX string
        /// </summary>
        public static PatientID FromEscapedCx(string cx)
        {
            return FromCx(cx.Replace("&amp;", "&"));
        }

        /// <summary>
        /// Provides a string representation of this object
        /// </summary>
        public override string ToString()
        {
            return String.Format("ID: \"{0}\", AA: \"{1}\"(\"{2}\" type)", Id, AssigningAuthority, AssigningAuthorityType);
        }

        /// <summary>
        /// Tests equality with another PatientID instance
        /// </summary>
        public bool Equals(PatientID other)
        {
            return (this.Id == other.Id) 
                && (this.AssigningAuthority == other.AssigningAuthority) 
                && (this.AssigningAuthorityType == other.AssigningAuthorityType);
        }

        /// <summary>
        /// Tests equality with another object
        /// </summary>
        public override bool Equals(object other)
        {
            if (other == null) return false;
            if (other is PatientID) return Equals(other as PatientID);
            return false;
        }

        /// <summary>
        /// Hash for this instance.
        /// </summary>
        public override int GetHashCode()
        {
            return ToString().GetHashCode();
        }
    }
}