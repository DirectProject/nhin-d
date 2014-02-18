using System;

namespace Health.Direct.Policy.x509.Standard
{
    /// <summary>
    /// Enumeration of algorithms used for signing an X509 certificate
    /// Defined in RFC3279 section 2.2
    /// </summary>
    public class SignatureAlgorithmIdentifier
    {
        public static readonly SignatureAlgorithmIdentifier MD2
            = new SignatureAlgorithmIdentifier("1.2.840.113549.2.1", "md2");

        public static readonly SignatureAlgorithmIdentifier MD5
            = new SignatureAlgorithmIdentifier("1.2.840.113549.2.5", "md5");

        public static readonly SignatureAlgorithmIdentifier SHA1
            = new SignatureAlgorithmIdentifier("1.3.14.3.2.26", "sha1");

        public static readonly SignatureAlgorithmIdentifier SHA256
            = new SignatureAlgorithmIdentifier("2.16.840.1.101.3.4.2.1", "sha256");

        public static readonly SignatureAlgorithmIdentifier SHA384
            = new SignatureAlgorithmIdentifier("2.16.840.1.101.3.4.2.2", "sha384");

        public static readonly SignatureAlgorithmIdentifier SHA512
            = new SignatureAlgorithmIdentifier("2.16.840.1.101.3.4.2.3", "sha512");

        public static readonly SignatureAlgorithmIdentifier SHA224
            = new SignatureAlgorithmIdentifier("2.16.840.1.101.3.4.2.4", "sha224");

        public static readonly SignatureAlgorithmIdentifier MD2RSA
            = new SignatureAlgorithmIdentifier("1.2.840.113549.1.1.2", "md2RSA");

        public static readonly SignatureAlgorithmIdentifier MD5RSA
            = new SignatureAlgorithmIdentifier("1.2.840.113549.1.1.4", "md5RSA");

        public static readonly SignatureAlgorithmIdentifier SHA1DSA
            = new SignatureAlgorithmIdentifier("1.2.840.10040.4.3", "sha1DSA");

        public static readonly SignatureAlgorithmIdentifier SHA1RSA
            = new SignatureAlgorithmIdentifier("1.2.840.113549.1.1.5", "sha1RSA");

        public static readonly SignatureAlgorithmIdentifier SHA256RSA
            = new SignatureAlgorithmIdentifier("1.2.840.113549.1.1.11", "sha256RSA");

        public static readonly SignatureAlgorithmIdentifier SHA384RSA
            = new SignatureAlgorithmIdentifier("1.2.840.113549.1.1.12", "sha384RSA");

        public static readonly SignatureAlgorithmIdentifier SHA512RSA
            = new SignatureAlgorithmIdentifier("1.2.840.113549.1.1.13", "sha512RSA");

        public static readonly SignatureAlgorithmIdentifier SHA224RSA
            = new SignatureAlgorithmIdentifier("1.2.840.113549.1.1.14", "sha224RSA");

        public static readonly SignatureAlgorithmIdentifier SHA1ECDSA
            = new SignatureAlgorithmIdentifier("1.2.840.10045.4.1", "sha1ECDSA");




        public readonly string OID;
        public readonly String Name;

        private SignatureAlgorithmIdentifier(string oid, string rfcName)
        {
            OID = oid;
            Name = rfcName;
        }
    }
}