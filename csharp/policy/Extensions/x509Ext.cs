using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Policy.Extensions
{
    public static class X509Ext
    {
        public static List<string> GetCriticalExtensionOIDs(this X509Certificate2 cert)
        {
            List<string> criticalOIDs = new List<string>();
            foreach (X509Extension extension in cert.Extensions)
            {
                if (extension.Critical)
                {
                    criticalOIDs.Add(extension.Oid.Value);
                }
            }
            return criticalOIDs;
        }

        public static List<string> GetExtensionIdentifier(this X509Certificate2 cert)
        {
            List<string> criticalOIDs = new List<string>();
            foreach (X509Extension extension in cert.Extensions)
            {
                if (extension.Critical)
                {
                    criticalOIDs.Add(extension.Oid.Value);
                }
            }
            return criticalOIDs;
        } 
    }
}