using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Policy.Extensions
{
    public static class X509Ext
    {
        public static List<string> GetCriticalExtensionOIDs(this X509Certificate2 cert)
        {
            var criticalOIDs = new List<string>();
            foreach (X509Extension extension in cert.Extensions)
            {
                if (extension.Critical)
                {
                    criticalOIDs.Add(extension.Oid.Value);
                }
            }
            return criticalOIDs;
        }

        public static List<string> GetExtensionOIDs(this X509Certificate2 cert)
        {
            var criticalOIDs = new List<string>();
            foreach (X509Extension extension in cert.Extensions)
            {
                criticalOIDs.Add(extension.Oid.Value);
            }
            return criticalOIDs;
        }

        public static T GetExtensionIdentifier<T>(this X509Certificate2 cert, string oid) where T : X509Extension
        {
            foreach (var extension in cert.Extensions)
            {
                if (extension.Oid.Value == oid)
                {
                    return (T) extension;
                }
            }
            return null;
        } 
    }
}