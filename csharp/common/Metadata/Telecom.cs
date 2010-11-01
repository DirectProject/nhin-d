/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Vassil Peytchev  vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Net.Mail;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Represents a telecom address
    /// </summary>
    public struct Telecom : IEquatable<Telecom>
    {
        private MailAddress _email;
        private string _phone;

        /// <summary>
        /// Initializes an instition without an assigning authority
        /// </summary>
        public Telecom(MailAddress email)
        {
            _email = email;
            _phone = null;
        }

        /// <summary>
        /// Initializes an institution with an assinging authority
        /// </summary>
        public Telecom(MailAddress email, string phone)
        {
            _email = email;
            _phone = phone;
        }

        /// <summary>
        /// The email address component of the telecom address
        /// </summary>
        public MailAddress Email
        {
            get { return _email; }
            set { _email = value; }
        }

        /// <summary>
        /// The unformatted phone component of the telecom address
        /// </summary>
        public string Phone
        {
            get { return _phone; }
            set { _phone = value; }
        }
        /// <summary>
        /// Formats the telecom as an XTN datatype
        /// </summary>
        public string ToXTN()
        {
            if (_phone == null)
            {
                return String.Format("^^Internet^{0}^^^^^{1}^^", _email.Address, _email.DisplayName);
            }
            return String.Format("^^Internet^{0}^^^^^{1}^^^{2}", _email.Address, _email.DisplayName, _phone);
        }

        /// <summary>
        /// Parses the XTN datatype and returns the corresponding <see cref="Telecom"/>
        /// </summary>
        public static Telecom FromXTN(string i)
        {
            List<string> fields = HL7Util.SplitField(i, 1, 10);
            return new Telecom( new MailAddress(fields[3], fields[8]), fields[11]);
        }

        /// <summary>
        /// String representation of this institution.
        /// </summary>
        public override string ToString()
        {
            return string.Format("{0}, AA: {1}", Email.ToString(), Phone ?? "none");
        }

        /// <summary>
        /// Tests equality between this instance and another
        /// </summary>
        public bool Equals(Telecom other)
        {
            if (Phone == null && other.Phone == null)
                return Email == other.Email;
            else
                return (Email == other.Email && Phone== other.Phone);
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