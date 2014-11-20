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
using System.Collections.Generic;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Enumeration of HITSP C80 Confidentialty values
    /// </summary>
    /// <remarks>
    /// Some values (e.g., HIV, etc.) omitted on purpose because they provide confidential information (!)
    /// </remarks>
    public enum C80Confidentialty
    {
        /// <summary>
        /// Only clinicians may see this item, billing and administration persons can not access
        /// this item without special permission
        /// </summary>
        Clinician,
        /// <summary>
        /// Access only to individual persons who are mentioned explicitly as actors of this
        /// service and whose actor type warrants that access
        /// </summary>
        Individual,
        /// <summary>
        /// Low confidentialty
        /// </summary>
        Low,
        /// <summary>
        /// Normal confidentiality
        /// </summary>
        Normal,
        /// <summary>
        /// Restricted access, e.g. only to providers having a current
        /// care relationship to the patient
        /// </summary>
        Restricted,
        /// <summary>
        /// Very restricted access as declared by the Privacy Officer of
        /// the record holder
        /// </summary>
        VeryRestricted,
        /// <summary>
        /// Information for which the patient seeks heightened
        /// confidentiality.
        /// </summary>
        Sensitive,
        /// <summary>
        /// Information not to be disclosed or discussed with patient except through physician
        /// assigned to patient in this case.
        /// </summary>
        Taboo
    }

    /// <summary>
    /// Represents the confidentiality level of an item.
    /// </summary>
    public static class C80ConfidentialityUtils
    {

        /// <summary>
        /// Returns a <see cref="CodedValue"/> for the code
        /// </summary>
        public static CodedValue ToCodedValue(this C80Confidentialty code)
        {
            KeyValuePair<string, string> pair = Decode(code);
            return new CodedValue(pair.Key, pair.Value, "2.16.840.1.113883.1.11.10228");
        }

        private static Dictionary<C80Confidentialty, KeyValuePair<string, string>> m_C80Confidentiality_mappings
            = new Dictionary<C80Confidentialty, KeyValuePair<string, string>>()
                  {
                      {C80Confidentialty.Clinician, new KeyValuePair<string, string>("C", "Clinician")},
                      {C80Confidentialty.Individual, new KeyValuePair<string, string>("I", "Individual")},
                      {C80Confidentialty.Low, new KeyValuePair<string, string>("L", "Low")},
                      {C80Confidentialty.Normal, new KeyValuePair<string, string>("N", "Normal")},
                      {C80Confidentialty.Restricted, new KeyValuePair<string, string>("R", "Restricted")},
                      {C80Confidentialty.VeryRestricted, new KeyValuePair<string, string>("V", "Very Restricted")},
                      {C80Confidentialty.Sensitive, new KeyValuePair<string, string>("S", "Sensitive")},
                      {C80Confidentialty.Taboo, new KeyValuePair<string, string>("T", "Taboo")}
                  };

        /// <summary>
        /// Returns the code/label pair for the provided enumeration code
        /// </summary>
        public static KeyValuePair<string, string> Decode(C80Confidentialty code)
        {
            return CodeDictionary[code];
        }


        /// <summary>
        /// The dictionary mapping coded values to code/label pairs.
        /// </summary>
        public static Dictionary<C80Confidentialty, KeyValuePair<string, string>> CodeDictionary
        {
            get
            {
                return m_C80Confidentiality_mappings;
            }
        }
    }
}
