using System;
using System.Net;
using System.Net.Sockets;
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Extension methods for DNS Resolution.
    /// </summary>
    public static class Extensions
    {

        /// <summary>
        /// Creates a dotted domain name from a domain name and an email localPart for use in DNS queries.
        /// </summary>
        /// <param name="dnsDomain">The domain name to construct the DNS domain name from</param>
        /// <param name="localPart">The local part of the email address</param>
        /// <returns>The dotted domain name representing the email address for lookup</returns>
        public static string ConstructEmailDnsDomainName(this string dnsDomain, string localPart)
        {
            if (string.IsNullOrEmpty(dnsDomain))
            {
                throw new ArgumentException("value null or empty", "dnsDomain");
            }
            if (string.IsNullOrEmpty(localPart))
            {
                throw new ArgumentException("value null or empty", "localPart");
            }

            string extendedName;

            if (dnsDomain[0] == '.')
            {
                if (localPart[localPart.Length - 1] != '.')
                {
                    extendedName = localPart + dnsDomain;
                }
                else
                {
                    extendedName = localPart + dnsDomain.Substring(1);
                }
            }
            else
            {
                if (localPart[localPart.Length - 1] != '.')
                {
                    extendedName = localPart + '.' + dnsDomain;
                }
                else
                {
                    extendedName = localPart + dnsDomain;
                }
            }

            return extendedName;
        }

        /// <summary>
        /// Extracts the email or subject name from the certificate.
        /// </summary>
        /// <param name="cert">The certificate instance this extension method is attached to</param>
        /// <returns>The email name associated with the certificate, the subject name if
        /// the email name is not found, or null if neither is found.</returns>
        public static string ExtractName(this X509Certificate2 cert)
        {
            string name = cert.GetNameInfo(X509NameType.EmailName, false);
            if (string.IsNullOrEmpty(name))
            {
                name = cert.GetNameInfo(X509NameType.SimpleName, false);
            }

            return name;
        }
        
        /// <summary>
        /// Extracts the raw 4 byte IP address from this object. This method compensates for the obsolete .Address property on IPAddress
        /// </summary>
        /// <param name="address"></param>
        /// <returns></returns>
        public static uint ToIPV4(this IPAddress address)
        {
            if (address.AddressFamily != AddressFamily.InterNetwork)
            {
                throw new NotSupportedException("Not IP4 address");
            }
            
            //return (uint) address.Address; // This property is obsolete!
            
            byte[] ipBytes = address.GetAddressBytes();
            if (ipBytes == null || ipBytes.Length != 4)
            {
                throw new NotSupportedException();
            }
            
            return (uint) (ipBytes[0] << 24 | (ipBytes[1] << 16) | (ipBytes[2] << 8) | (ipBytes[3] << 0));
        }
    }
}