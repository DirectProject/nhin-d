using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography.X509Certificates;

namespace DnsResolver
{
    /// <summary>
    /// Extension methods for DNS Resolution.
    /// </summary>
    public static class Extensions
    {
        const string SubjectNamePrefix = "CN=";
        const string EmailNamePrefix = "E=";

        /// <summary>
        /// Extracts the email or subject name from the certificate.
        /// </summary>
        /// <param name="cert">The certificate instance this extension method is attached to</param>
        /// <returns>The email name associated with the certificate, the subject name if
        /// the email name is not found, or null if neither is found.</returns>
        public static string ExtractEmailNameOrName(this X509Certificate2 cert)
        {
            string[] parts = cert.Subject.Split(',');
            if (parts != null)
            {
                for (int i = 0; i < parts.Length; ++i)
                {
                    string prefix = EmailNamePrefix;
                    int index = parts[i].IndexOf(prefix);
                    if (index < 0)
                    {
                        prefix = SubjectNamePrefix;
                        index = parts[i].IndexOf(prefix);
                    }
                    if (index >= 0)
                    {
                        return parts[i].Substring(index + prefix.Length).Trim();
                    }
                }
            }

            return null;
        }


    }
}
