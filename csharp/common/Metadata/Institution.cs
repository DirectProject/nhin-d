using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Metadata
{
    /// <summary>
    /// Represents an institution
    /// </summary>
    public struct Institution
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
    }
}
