using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Globalization;

namespace NHINDirect.Metadata
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
