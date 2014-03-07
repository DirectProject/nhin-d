using System;

namespace Health.Direct.Policy.x509.Standard
{
    public class PublicKeyAlgorithmIdentifier
    {
        public static readonly PublicKeyAlgorithmIdentifier RSA =
            new PublicKeyAlgorithmIdentifier("1.2.840.113549.1.1.1", "RSA");

        public static readonly PublicKeyAlgorithmIdentifier DSA = new PublicKeyAlgorithmIdentifier("1.2.840.10040.4.1",
            "DSA");

        public readonly string OID;
        public readonly String Name;


        private PublicKeyAlgorithmIdentifier(string oid, string rfcName)
        {
            OID = oid;
            Name = rfcName;
        }
    }
}