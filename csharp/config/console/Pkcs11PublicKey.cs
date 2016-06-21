using System;
using Org.BouncyCastle.Crypto.Parameters;

namespace Health.Direct.Config.Console
{
    /// <summary>
    /// Pkcs#11 public key and certificate container
    /// </summary>
    public class Pkcs11PublicKey
    {
        public string Id { get; }

        public string Label { get; }

        public byte[] Data { get; }

        public RsaKeyParameters PublicKey { get; }


        /// <summary>
        /// 
        /// </summary>
        /// <param name="id">Value of CKA_ID attribute</param>
        /// <param name="label">Value of CKA_LABEL attribute.</param>
        /// <param name="publicKey"><see cref="RsaKeyParameters"/> of public Modulus and public Exponent represent the Public part of the key or null</param>
        internal Pkcs11PublicKey(string id, string label, RsaKeyParameters publicKey)
        {
            Id = id;
            Label = label;
            PublicKey = publicKey;
        }


        internal Pkcs11PublicKey(string id, string label, byte[] data)
        {
            if (data == null)
                throw new ArgumentNullException(nameof(data));
            
            Id = id;
            Label = label;
            Data = data;
            
        }
    }
}