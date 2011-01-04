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
using System.Linq;
using System.Text;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Enumeration for biological sex (male, female, other (XYY, etc.))
    /// </summary>
    public enum Sex
    {
        /// <summary>
        /// XY chromosomes
        /// </summary>
        Male,
        /// <summary>
        /// XX chromosomes
        /// </summary>
        Female,
        /// <summary>
        /// Other than XX or XY or other case of unclear biological sex
        /// </summary>
        Other
    }

    /// <summary>
    /// Represents a human person
    /// </summary>
    public class Person : IEquatable<Person>
    {

        /// <summary>
        /// First name
        /// </summary>
        public string First
        {
            get;
            set;
        }

        /// <summary>
        /// Last name
        /// </summary>
        public string Last
        {
            get;
            set;
        }

        /// <summary>
        /// Middle name or initial
        /// </summary>
        public string MI
        {
            get;
            set;
        }

        /// <summary>
        /// Suffix (e.g, III, Jr.)
        /// </summary>
        public string Suffix
        {
            get;
            set;
        }

        /// <summary>
        /// Prefix (e.g., Dr., Mr., etc.)
        /// </summary>
        public string Prefix
        {
            get;
            set;
        }

        /// <summary>
        /// Degree (e.g., M.D., M.D, PhD)
        /// </summary>
        public string Degree
        {
            get;
            set;
        }


        /// <summary>
        /// Biological sex of the person
        /// </summary>
        public Sex? Sex { get; set; }

        /// <summary>
        /// Date of birth
        /// </summary>
        public DateTime? Dob { get; set; }


        /// <summary>
        /// Home or primary address for this person.
        /// </summary>
        public PostalAddress? Address { get; set; }

        /// <summary>
        /// Tests if this instance equals another
        /// </summary>
        public bool Equals(Person other)
        {
            bool firstEquals = (First == null && other.First == null) || First == other.First;
            bool lastEquals = (Last == null && other.Last == null) || Last == other.Last;
            bool miEquals = (MI == null && other.MI == null) || MI == other.MI;
            bool suffixEquals = (Suffix == null && other.Suffix == null) || Suffix == other.Suffix;
            bool prefixEquals = (Prefix == null && other.Prefix == null) || Prefix == other.Prefix;
            bool degreeEquals = (Degree == null && other.Degree == null) || Degree == other.Degree;
            bool sexEquals = (Sex == null && other.Sex == null) || Sex == other.Sex;
            bool dobEquals = (Dob == null && other.Dob == null) || Dob == other.Dob;
            return firstEquals && lastEquals && miEquals && suffixEquals && prefixEquals && degreeEquals && sexEquals && dobEquals;
        }

        /// <summary>
        /// Tests if this instance equals another
        /// </summary>
        public override bool Equals(object other)
        {
            if (other == null) return false;
            if (other is Person) return Equals(other as Person);
            return false;
        }

        /// <summary>
        /// Returns a hash of this object
        /// </summary>
        public override int GetHashCode()
        {
            return ToString().GetHashCode();
        }

        /// <summary>
        /// Provides a string representation of the Person
        /// </summary>
        /// <returns>A string representation of the person</returns>
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append(Prefix == null ? "" : Prefix + " ");
            if (First != null) // Special case first name in case you only have a first name
            {
                sb.Append(First);
                sb.Append(MI != null || Last != null ? " " : "");
            }
            sb.Append(MI == null ? "" : MI + " ");
            sb.Append(Last == null ? "" : Last);
            sb.Append(Suffix == null ? "" : ", " + Suffix);
            sb.Append(Degree == null ? "" : ", " + Degree);
            sb.Append(Sex == null ? "" : " Sex: " + Sex.AsString());
            sb.Append(Dob == null ? "" : " Dob: " + Dob.Value.ToShortDateString());
            return sb.ToString();
        }

        /// <summary>
        /// Formats the person as an XCN HL7 datatype
        /// </summary>
        /// <returns>A string HL7 representation of the person</returns>
        public string ToXCN()
        {
            return String.Format("^{0}^{1}^{2}^{3}^{4}^{5}",
                                 Last ?? "",
                                 First ?? "",
                                 MI ?? "",
                                 Suffix ?? "",
                                 Prefix ?? "",
                                 Degree ?? "");
        }

        /// <summary>
        /// Parses an XCN and returns a Person
        /// </summary>
        public static Person FromXCN(string xcn)
        {
            if (xcn == null) throw new ArgumentException();

            Person p = new Person();

            List<string> fields = HL7Util.SplitField(xcn, 2, 7);
            p.Last = fields[1];
            p.First = fields[2];
            p.MI = fields[3];
            p.Suffix = fields[4];
            p.Prefix = fields[5];
            p.Degree = fields[6];

            return p;
        }

        /// <summary>
        /// Generates string values suitable for inclusion in a Slot for source patient information
        /// </summary>
        public IEnumerable<string> ToSourcePatientInfoValues(PatientID id)
        {
            string idValue = (id == null) ? "" : id.ToCx();
            string nameValue =  ToXCN();
            string dateValue =  Dob == null ? "" : Dob.ToHL7Date();
            string sexValue = Sex == null ? "" : Sex.AsString();
            string addressValue = Address == null ? "" : Address.Value.ToHL7Ad();
            yield return "PID-3|" + idValue;
            yield return "PID-5|" + nameValue;
            yield return "PID-7|" + dateValue;
            yield return "PID-8|" + sexValue;
            yield return "PID-11|" + addressValue;
        }

        /// <summary>
        /// Intializes a new <see cref="Person"/> from data found in a sourcePatientInfo field
        /// </summary>
        public static Person FromSourcePatientInfoValues(IEnumerable<string> values)
        {
            Person p = null;
            Sex? sex = null;
            DateTime? dob = null;
            PostalAddress? postal = null;

            foreach (string[] fields in values.Select(s => s.Split('|')))
            {

                if (fields.Length != 2) throw new ArgumentException();
                switch (fields[0])
                {
                    case "PID-3":
                        break;
                    case "PID-5":
                        p = Person.FromXCN(fields[1]);
                        break;
                    case "PID-7":
                        dob = HL7Util.DateTimeFromHL7Value(fields[1]);
                        break;
                    case "PID-8":
                        sex = HL7Util.FromHL7Value(fields[1]);
                        break;
                    case "PID-11":
                        postal = PostalAddress.FromHL7Ad(fields[1]);
                        break;
                }
            }

            if (p == null) throw new ArgumentException();
            if (dob != null) p.Dob = dob;
            if (sex != null) p.Sex = sex;
            if (postal != null) p.Address = postal;
            return p;

        }


    }
}