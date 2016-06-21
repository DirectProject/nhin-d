/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using Net.Pkcs11Interop.Common;
using Net.Pkcs11Interop.HighLevelAPI;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.Pkcs;
using Org.BouncyCastle.Asn1.X509;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Pkcs;
using Health.Direct.Common.Cryptography;
using Health.Direct.Config.Console;

namespace Health.Direct.Config.Console
{
    /// <summary>
    /// Pkcs#11 Utilities
    /// </summary>
    public class Pkcs11Util
    {
        /// <summary>
        /// Finds slot containing the token that matches criteria specified in <see cref="TokenSettings"/> class
        /// </summary>
        /// <param name='pkcs11'>Initialized PKCS11 wrapper</param>
        /// <param name="settings"></param>
        /// <returns>Slot containing the token that matches criteria in <see cref="TokenSettings"/></returns>
        public static Slot FindSlot(Pkcs11 pkcs11, TokenSettings settings)
        {
            // Get list of available slots with token present
            var slots = pkcs11.GetSlotList(true);

            // No criteria, not go.
            if (settings.TokenLabel == null)
                return null;

            foreach (var slot in slots)
            {
                TokenInfo tokenInfo = null;

                try
                {
                    tokenInfo = slot.GetTokenInfo();
                }
                catch (Pkcs11Exception ex)
                {
                    if (ex.RV != CKR.CKR_TOKEN_NOT_RECOGNIZED && 
                        ex.RV != CKR.CKR_TOKEN_NOT_PRESENT)
                        throw;
                }

                if (tokenInfo == null)
                    continue;

                if (!String.IsNullOrEmpty(settings.TokenLabel))
                    if (0 != String.Compare(
                        settings.TokenLabel, 
                        tokenInfo.Label, 
                        StringComparison.Ordinal))
                        continue;

                System.Console.WriteLine("tokenInfo.Label {0}", tokenInfo.Label);
                System.Console.WriteLine("tokenInfo.SerialNumber {0}", tokenInfo.SerialNumber);
                System.Console.WriteLine("tokenInfo.SlotId {0}", tokenInfo.SlotId);
                System.Console.WriteLine("tokenInfo.SessionCount {0}", tokenInfo.SessionCount);

                return slot;
            }

            System.Console.WriteLine("Did not find an available slot with TokenLable:{0}", settings.TokenLabel);
            return null;
        }

        /// <summary>
        /// Generates asymmetric key pair.
        /// </summary>
        /// <param name='session'>Read-write session with user logged in</param>
        /// <param name="ckaLabel">Value of CKA_LABEL.  Should Match SubjectAlt name which is the same as what we call DirectDomain or Email formated as DNS name (replace "@" with ".".</param>
        /// <param name="ckaId">Value of CKA_ID attribute.  Unique within the 4 objects created for single use certificates.  One pair for signing and one pair for encipherment.</param>
        /// <param name='publicKeyHandle'>Output parameter for public key object handle</param>
        /// <param name='privateKeyHandle'>Output parameter for private key object handle</param>
        /// <param name="modulusBits">Length in bits of modulus n</param>
        public static void GenerateKeyPair(Session session,
            string ckaLabel, 
            byte[] ckaId,
            out ObjectHandle publicKeyHandle,
            out ObjectHandle privateKeyHandle,
            int modulusBits)
        {

            // Prepare attribute template of new public key
            var publicKeyAttributes = new List<ObjectAttribute>
            {
                new ObjectAttribute(CKA.CKA_TOKEN, true),
                new ObjectAttribute(CKA.CKA_PRIVATE, false),
                new ObjectAttribute(CKA.CKA_LABEL, ckaLabel),
                new ObjectAttribute(CKA.CKA_ID, ckaId),
                new ObjectAttribute(CKA.CKA_ENCRYPT, true),
                new ObjectAttribute(CKA.CKA_VERIFY, true),
                new ObjectAttribute(CKA.CKA_VERIFY_RECOVER, true),
                new ObjectAttribute(CKA.CKA_WRAP, true),
                new ObjectAttribute(CKA.CKA_MODULUS_BITS, Convert.ToUInt64(modulusBits)), //Length in bits of modulus n
                new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, new byte[] {0x01, 0x00, 0x01}) //Public exponent e
            };

            // Prepare attribute template of new private key
            var privateKeyAttributes = new List<ObjectAttribute>
            {
                new ObjectAttribute(CKA.CKA_TOKEN, true),
                new ObjectAttribute(CKA.CKA_PRIVATE, true),
                new ObjectAttribute(CKA.CKA_LABEL, ckaLabel),
                new ObjectAttribute(CKA.CKA_ID, ckaId),
                new ObjectAttribute(CKA.CKA_SENSITIVE, true),
                new ObjectAttribute(CKA.CKA_DECRYPT, true),
                new ObjectAttribute(CKA.CKA_SIGN, true),
                new ObjectAttribute(CKA.CKA_SIGN_RECOVER, true),
                new ObjectAttribute(CKA.CKA_UNWRAP, true)
            };

            // Specify key generation mechanism
            var mechanism = new Mechanism(CKM.CKM_RSA_PKCS_KEY_PAIR_GEN);

            // Generate key pair
            session.GenerateKeyPair(mechanism, publicKeyAttributes, privateKeyAttributes, out publicKeyHandle, out privateKeyHandle);
        }


        /// <summary>
        /// Generates certificate request in PKCS#10 format defined by RFC 2986
        /// </summary>
        /// <param name="session">Read-write session with user logged in</param>
        /// <param name="publicKeyHandle">Handle of existing public key</param>
        /// <param name="privateKeyHandle">Handle of existing private key</param>
        /// <param name="subjectDistinguishedName">Subject entity's distinguished name</param>
        /// <param name="digestAlgorithm">Standard digest (hash) algorithm used for the creation of request signature</param>
        /// <param name="asn1Attributes"><exception cref="Asn1Set"> representing attributes to be added to the certificate request</exception></param>
        /// <returns>Certificate request in PKCS#10 format</returns>
        public static byte[] GeneratePkcs10(Session session, ObjectHandle publicKeyHandle, ObjectHandle privateKeyHandle, string subjectDistinguishedName, DigestAlgorithm digestAlgorithm, Asn1Set asn1Attributes)
        {
            var pubKeyAttrsToRead = new List<CKA>();
            pubKeyAttrsToRead.Add(CKA.CKA_KEY_TYPE);
            pubKeyAttrsToRead.Add(CKA.CKA_MODULUS);
            pubKeyAttrsToRead.Add(CKA.CKA_PUBLIC_EXPONENT);

            // Read public key attributes
            var publicKeyAttributes = session.GetAttributeValue(publicKeyHandle, pubKeyAttrsToRead);
            if (CKK.CKK_RSA != (CKK)publicKeyAttributes[0].GetValueAsUlong())
                throw new NotSupportedException("Currently only RSA keys are supported");

            // Create instance of RsaKeyParameters class usable for BouncyCastle
            var modulus = new BigInteger(1, publicKeyAttributes[1].GetValueAsByteArray());
            var publicExponent = new BigInteger(1, publicKeyAttributes[2].GetValueAsByteArray());
            var publicKeyParameters = new RsaKeyParameters(false, modulus, publicExponent);

            // Determine algorithms
            Mechanism mechanism = null;
            string signatureAlgorithm;

            switch (digestAlgorithm)
            {
                case DigestAlgorithm.SHA1:
                    mechanism = new Mechanism(CKM.CKM_SHA1_RSA_PKCS);
                    signatureAlgorithm = PkcsObjectIdentifiers.Sha1WithRsaEncryption.Id;
                    break;
                case DigestAlgorithm.SHA256:
                    mechanism = new Mechanism(CKM.CKM_SHA256_RSA_PKCS);
                    signatureAlgorithm = PkcsObjectIdentifiers.Sha256WithRsaEncryption.Id;
                    break;
                case DigestAlgorithm.SHA384:
                    mechanism = new Mechanism(CKM.CKM_SHA384_RSA_PKCS);
                    signatureAlgorithm = PkcsObjectIdentifiers.Sha384WithRsaEncryption.Id;
                    break;
                case DigestAlgorithm.SHA512:
                    mechanism = new Mechanism(CKM.CKM_SHA512_RSA_PKCS);
                    signatureAlgorithm = PkcsObjectIdentifiers.Sha512WithRsaEncryption.Id;
                    break;
                default:
                    throw new ArgumentOutOfRangeException(nameof(digestAlgorithm), digestAlgorithm, null);
            }

            // Generate and sign PKCS#10 request. See RFC 2986 4.2 CertificationRequest
            var pkcs10 = new Pkcs10CertificationRequestDelaySigned(signatureAlgorithm, new X509Name(subjectDistinguishedName), publicKeyParameters, asn1Attributes);
            // Use HSM to generate signature for the csr
            byte[] signature = session.Sign(mechanism, privateKeyHandle, pkcs10.GetDataToSign());
            // set the signature BIT STRING of the pkcs#10 request. See RFC 2986 4.2 CertificationRequest
            pkcs10.SignRequest(new DerBitString(signature));

            return pkcs10.GetDerEncoded();
        }
    }
}
