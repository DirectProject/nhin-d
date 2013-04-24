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
using System.Globalization;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Utility functions for HL7 V2 data types
    /// </summary>
    public static class HL7Util
    {
        /// <summary>
        /// The standard HL7 DateTime Format at full precision
        /// </summary>
        public static string DateTimeFormat = "yyyyMMddHHmmss";

        /// <summary>
        /// The standard HL7 DateTime format at the precision of days
        /// </summary>
        public static string ShortDateTimeFormat = "yyyyMMdd";

        /// <summary>
        /// The standard HL7 DateTime format at the precision of hours and minutes
        /// </summary>
        public static string MediumDateTimeFormat = "yyyyMMddHHmm";


        /// <summary>
        /// Parses a UTC HL7 formatted datetime string and returns the UTC <see cref="DateTime"/>
        /// </summary>
        public static DateTime? DateTimeFromHL7Value(string value)
        {
            if (value == null) return null;
            DateTime dt;
            bool worked = false;

            string[] formats = new string[] {DateTimeFormat,  MediumDateTimeFormat, ShortDateTimeFormat };
            
            worked = DateTime.TryParseExact(value,formats, CultureInfo.InvariantCulture, DateTimeStyles.AssumeUniversal, out dt);
            if (worked)
                return dt.ToUniversalTime();
            else
                return null;
        }

        /// <summary>
        /// Formats the provided value as a string suitable for inclusion in HL7
        /// </summary>
        public static string ToHL7Value(Sex? s)
        {
            if (s == null) return "U";
            switch (s)
            {
                case Sex.Female:
                    return "F";
                case Sex.Male:
                    return "M";
                case Sex.Other:
                    return "U";
            }
            return "U";
        }

        /// <summary>
        /// Parses the provided value (which should represent an HL7 value) as a <see cref="Sex"/> value
        /// </summary>
        public static Sex? FromHL7Value(string s)
        {
            switch (s)
            {
                case "F": return Sex.Female;
                case "M": return Sex.Male;
                case "U": return null;
                default: throw new ArgumentException();
            }
        }

        private static string TrimField(string s)
        {
            s = s.Trim();
            if (s == "") return null;
            return s;
        }

        /// <summary>
        /// Splits the supplied field by the '^' character, returning at least <paramref name="returnedFields"/> fields
        /// and erroring if at least <paramref name="minFields"/> are not found
        /// </summary>
        public static List<string> SplitField(string field, int minFields, int returnedFields)
        {
            List<string> splitFields = field.Split('^').Select(s => TrimField(s)).ToList<string>();
            if (splitFields.Count < minFields) throw new ArgumentException();
            List<string> fields;
            if (splitFields.Count >= returnedFields)
                fields = splitFields;
            else
            {
                fields = new List<string>(Enumerable.Repeat<string>(null, returnedFields));
                for (int i = 0; i < splitFields.Count; i++)
                    fields[i] = splitFields[i];
            }
            return fields;
        }


    }
}