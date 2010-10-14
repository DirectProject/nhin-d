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
        public MailAddress HealthInternetAddress { get; set; }

        /// <summary>
        /// String representation of the Recipient
        /// </summary>
        public override string ToString()
        {
            return String.Format("{0} @ {1}", Person == null ? "none" : Person.ToString(), Institution == null ? "none" : Institution.ToString());
        }

        /// <summary>
        /// Formats the recipient as a combination XON/XCN data type
        /// </summary>
        public string ToXONXCN()
        {
            return String.Format("{0}|{1}",
                Institution == null ? "" : Institution.Value.ToXON(), 
                Person == null ? "" : Person.ToXCN());
        }

        /// <summary>
        /// Initializes a new Recipient from a string of combination XON/XCN data type
        /// </summary>
        public static Recipient FromXONXCN(string xonxcn)
        {
            //TODO: more specific exception
            if (xonxcn == null) throw new ArgumentException();
            string[] fields = xonxcn.Split('|');
            
            Recipient r = new Recipient();

            if (fields[0].Trim() == "")
            {
                r.Institution = null;
            }
            else
            {
                r.Institution = Metadata.Institution.FromXON(fields[0]);
            }
            if (fields.Length == 1 || fields[1].Trim() == "")
            {
                r.Person = null;
            }
            else
            {
                r.Person = Person.FromXCN(fields[1]);
            }
            if (r.Person == null && r.Institution == null) throw new ArgumentException();
            return r;
        }

        /// <summary>
        /// Tests equality between this instance and another
        /// </summary>
        public bool Equals(Recipient other)
        {
            bool personEqual = (Person == null && other.Person == null) || (Person != null && Person.Equals(other.Person));
            bool institutionEqual = (Institution == null && other.Institution == null) || (Institution != null && Institution.Equals(other.Institution));
            return personEqual && institutionEqual;
        }
    }
}
