using System;

namespace Health.Direct.Policy.X509
{
    [Flags]
    public enum KeyUsageBit
    {
        /// <summary>
        /// Digital signature
        /// Binary: 1000 0000
        /// Hex: 0x80
        /// Dec: 128
        /// </summary>
        DigitalSignature = (1 << 7),



        /// <summary>
        /// Non repudiation
        /// Binary: 1000000
        /// Hex: 0x40
        /// Dec: 64
        /// </summary>
        NonRepudiation = (1 << 6),

        /// <summary>
        /// Key encipherment
        /// Binary: 100000
        /// Hex: 0x20
        /// Dec: 32
        /// </summary>
        KeyEncipherment = (1 << 5),

        /// <summary>
        /// Data encipherment
        /// Binary: 10000
        /// Hex: 0x10
        /// Dec: 16
        /// </summary>
        DataEncipherment = (1 << 4),

        /// <summary>
        /// Key agreement
        /// Binary: 100
        /// Hex: 0x08
        /// Dec: 8
        /// </summary>
        KeyAgreement = (1 << 3),

        /// <summary>
        /// Certificate signing
        /// Binary: 100
        /// Hex: 0x04
        /// Dec: 4
        /// </summary>
        KeyCertSign = (1 << 2),

        /// <summary>
        /// CRL signing
        /// Binary: 10
        /// Hex: 0x02
        /// Dec: 2
        /// </summary>
        CrlSign = (1 << 1),

        /// <summary>
        /// Encipherment only
        /// Binary: 1
        /// Hex: 0x01
        /// Dec: 1
        /// </summary>
        EncipherOnly = (1 << 0),

        /// <summary>
        /// Dicipherment only
        /// Binary: 1000000000000000
        /// Hex: 0x8000
        /// Dec: 32768
        /// </summary>
        DecipherOnly = (1 << 15),



    }


}
