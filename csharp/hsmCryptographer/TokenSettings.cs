/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Xml.Serialization;
using Health.Direct.Common.Cryptography;

namespace Health.Direct.Hsm
{
    /// <summary>
    /// Configuration for pkcs#11 hardware token settings
    /// </summary>
    public class TokenSettings 
    {
        private string m_pkcs11LibraryPath;
        private string m_tokenLabel;
        private EncryptionAlgorithm m_defaultEncryption = EncryptionAlgorithm.AES128;
        private DigestAlgorithm m_defaultDigest = DigestAlgorithm.SHA256;

        static TokenSettings()
        {
        }

        public event Action<ISmimeCryptographer, Exception> Error;

        /// <summary>
        /// The default encryption algorithm to use.
        /// </summary>
        [XmlElement]
        public EncryptionAlgorithm DefaultEncryption
        {
            get
            {
                return m_defaultEncryption;
            }
            set
            {
                m_defaultEncryption = value;
            }
        }

        /// <summary>
        /// The default digest algorithm to use.
        /// </summary>
        [XmlElement]
        public DigestAlgorithm DefaultDigest
        {
            get
            {
                return m_defaultDigest;
            }
            set
            {
                m_defaultDigest = value;
            }
        }

        [XmlElement(ElementName = "Library")]
        public string Pkcs11LibraryPath
        {
            get
            {
                return m_pkcs11LibraryPath;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                    throw new ArgumentException("value was null or empty", nameof(value));
                m_pkcs11LibraryPath = value;
            }
        }

        /// <summary>
        /// Should always be true
        /// </summary>
        public bool UseOsLocking { get { return true; } }

        /// <summary>
        /// Value of CKA_LABEL
        /// </summary>
        /// <exception cref="ArgumentException"></exception>
        [XmlElement]
        public string TokenLabel
        {
            get
            {
                return m_tokenLabel;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                    throw new ArgumentException("value was null or empty", nameof(value));
                m_tokenLabel = value;
            }
        }

        
        /// <summary>
        /// Password or pin to authorize access to a partition or slot.
        /// This is in addition to prerequisite certificate exchange when setting up a computer to authenticate with a pkcs#11 token.
        /// </summary>
        [XmlElement(ElementName = "UserPin")]
        public string NormalUserPin { get; set; }

        /// <summary>
        /// Arguments passed to the C_Initialize function in LowLevelAPI41 tests.
        /// </summary>
        public static Net.Pkcs11Interop.LowLevelAPI41.CK_C_INITIALIZE_ARGS InitArgs41 = null;

        /// <summary>
        /// PIN of the normal user.
        /// </summary>
        public byte[] NormalUserPinArray = null;

        /// <summary>
        /// PKCS#11 URI that identifies private key usable in signature creation tests.
        /// </summary>
        public string PrivateKeyUri = null;

        public HsmCryptographer Create()
        {
            var cryptographer = new HsmCryptographer();
            cryptographer.Error += Error;
            cryptographer.Init(this);

            return cryptographer;
        }
    }
}
