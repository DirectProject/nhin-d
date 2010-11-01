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
using System.Net.Mail;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Represents a document package recipient
    /// </summary>
    public class Recipient : IEquatable<Recipient>
    {
        /// <summary>
        /// The person to which the package is intended, <c>null</c> if not a person sender
        /// </summary>
        public Person Person { get; set; }
        /// <summary>
        /// The institution to which the package is intended
        /// </summary>
        public Institution? Institution { get; set; }

        /// <summary>
        /// The Health Internet Address of the recipient
        /// </summary>
        public Telecom? TelecomAddress { get; set; }

        /// <summary>
        /// String representation of the Recipient
        /// </summary>
        public override string ToString()
        {
            return String.Format("{0} @ {1} ({2})", Person == null ? "none" : Person.ToString(), Institution == null ? "none" : Institution.ToString(), TelecomAddress == null ? "none" : TelecomAddress.ToString());
        }

        /// <summary>
        /// Formats the recipient as a combination XON/XCN/XTN data type
        /// </summary>
        public string ToXONXCNXTN()
        {
            return String.Format("{0}|{1}|{2}",
                                 Institution == null ? "" : Institution.Value.ToXON(), 
                                 Person == null ? "" : Person.ToXCN(),
                                 TelecomAddress == null ? "" : TelecomAddress.Value.ToXTN());
        }

        /// <summary>
        /// Initializes a new Recipient from a string of combination XON/XCN data type
        /// </summary>
        public static Recipient FromXONXCNXTN(string xonxcnxtn)
        {
            //TODO: more specific exception
            if (xonxcnxtn == null) throw new ArgumentException();
            string[] fields = xonxcnxtn.Split('|');
            
            Recipient r = new Recipient();

            if (fields[0].Trim() == "")
            {
                r.Institution = null;
            }
            else
            {
                r.Institution = Metadata.Institution.FromXON(fields[0]);
            }
            if ((fields.Length == 1) || (fields[1].Trim() == ""))
            {
                r.Person = null;
            }
            else
            {
                r.Person = Person.FromXCN(fields[1]);
            }
            if ((fields.Length < 3) || (fields[2].Trim() == ""))
            {
                r.TelecomAddress = null;
            }
            else
            {
                r.TelecomAddress = Telecom.FromXTN(fields[2]);
            }
            if ((r.Person == null) && (r.Institution == null) && (r.TelecomAddress == null)) throw new ArgumentException();
            return r;
        }

        /// <summary>
        /// Tests equality between this instance and another
        /// </summary>
        public bool Equals(Recipient other)
        {
            bool personEqual = (Person == null && other.Person == null) || (Person != null && Person.Equals(other.Person));
            bool institutionEqual = (Institution == null && other.Institution == null) || (Institution != null && Institution.Equals(other.Institution));
            bool telecomEqual = (TelecomAddress == null && other.TelecomAddress == null) || (TelecomAddress != null && TelecomAddress.Equals(other.TelecomAddress));
            return personEqual && institutionEqual && telecomEqual;
        }
    }
}