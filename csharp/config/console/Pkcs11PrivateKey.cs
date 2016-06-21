using Org.BouncyCastle.Crypto.Parameters;

namespace Health.Direct.Config.Console
{
    /// <summary>
    /// Pkcs#11 private key shame container.
    /// </summary>
    public class Pkcs11PrivateKey
    {
        /// <summary>
        /// Value of CKA_ID attribute.
        /// Hex encoded string. 
        /// Private key identifier. 
        /// </summary>
        public string Id { get; }

        /// <summary>
        /// Value of CKA_LABEL attribute.
        /// Label of the private key
        /// </summary>
        public string Label { get; }

        /// <summary>
        /// Public part of the key
        /// </summary>
        public RsaKeyParameters PublicKey { get; }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="id">Value of CKA_ID attribute</param>
        /// <param name="label">Value of CKA_LABEL attribute.</param>
        /// <param name="publicKey"><see cref="RsaKeyParameters"/> of public Modulus and public Exponent represent the Public part of the key or null</param>
        internal Pkcs11PrivateKey(string id, string label, RsaKeyParameters publicKey)
        {
            Id = id;
            Label = label;
            PublicKey = publicKey;
        }
    }
}