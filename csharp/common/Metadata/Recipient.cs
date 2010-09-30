using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.Net.Mail;

namespace NHINDirect.Metadata
{
    /// <summary>
    /// Represents a document package recipient
    /// </summary>
    public class Recipient
    {
        /// <summary>
        /// The person to which the package is intended, <c>null</c> if not a person sender
        /// </summary>
        public Person Person { get; set; }
        /// <summary>
        /// The institution to which the package is intended
        /// </summary>
        public Institution Institution { get; set; }

        /// <summary>
        /// The Health Internet Address of the recipient
        /// </summary>
        public MailAddress HealthInternetAddress { get; set; }
    }
}
