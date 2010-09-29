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
    public class Person
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
        public Address? Address { get; set; }

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

        string FormatHL7Value(string prop)
        {
            return (prop == null ? "^" : prop + "^");
        }

        /// <summary>
        /// Generates string values suitable for inclusion in a Slot for source patient information
        /// </summary>
        public IEnumerable<string> ToSourcePatientInfoValues(PatientID id)
        {
            string idValue = (id == null) ? "" : id.ToEscapedCx();
            string nameValue =  ToXCN();
            string dateValue =  Dob == null ? "" : Dob.Value.ToHL7Date();
            string sexValue = Sex == null ? "" : Sex.AsString();
            string addressValue = Address == null ? "" : Address.Value.ToHL7Ad();
            yield return "PID-3|" + idValue;
            yield return "PID-5|" + nameValue;
            yield return "PID-7|" + dateValue;
            yield return "PID-8|" + sexValue;
            yield return "PID-11|" + addressValue;
        }


    }
}
