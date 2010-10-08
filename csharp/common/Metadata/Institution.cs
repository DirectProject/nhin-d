using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Metadata
{
    /// <summary>
    /// Represents an institution
    /// </summary>
    public struct Institution : IEquatable<Institution>
    {
        private string m_name;
        private string m_assigningAuthority;

        /// <summary>
        /// Initializes an instition without an assigning authority
        /// </summary>
        public Institution(string name)
        {
            m_name = name;
            m_assigningAuthority = null;
        }

        /// <summary>
        /// Initializes an institution with an assinging authority
        /// </summary>
        public Institution(string name, string assigningAuthority)
        {
            m_name = name;
            m_assigningAuthority = assigningAuthority;
        }

        /// <summary>
        /// The instition name
        /// </summary>
        public string Name { get { return m_name; } }

        /// <summary>
        /// The assigning authority responsible for the name (should be an OID)
        /// </summary>
        public string AssigningAuthority { get { return m_assigningAuthority; } }

        /// <summary>
        /// Formats the institution as an XON datatype
        /// </summary>
        public string ToXON()
        {
            if (AssigningAuthority == null)
                return Name;
            return String.Format("{0}^^^^^^^^^{1}", Name, AssigningAuthority);
        }

        /// <summary>
        /// Parses the XON datatype and returns the corresponding <see cref="Institution"/>
        /// </summary>
        public static Institution FromXON(string i)
        {
            List<string> fields = HL7Util.SplitField(i, 1, 10);
            return new Institution(fields[0], fields[9]);
        }

        /// <summary>
        /// String representation of this institution.
        /// </summary>
        public override string ToString()
        {
            return string.Format("{0}, AA: {1}", Name, AssigningAuthority ?? "none");
        }

        /// <summary>
        /// Tests equality between this instance and another
        /// </summary>
        public bool Equals(Institution other)
        {
            if (AssigningAuthority == null && other.AssigningAuthority == null)
                return Name == other.Name;
            else
                return (Name == other.Name && AssigningAuthority == other.AssigningAuthority);
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
