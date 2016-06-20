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
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using Net.Pkcs11Interop.Common;
using Net.Pkcs11Interop.HighLevelAPI;
using Net.Pkcs11Interop.HighLevelAPI.MechanismParams;
using Org.BouncyCastle.Asn1.Cms;
using Org.BouncyCastle.Asn1.Pkcs;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.X509;

namespace Health.Direct.Hsm
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
                {
                    if (0 != String.Compare(
                        settings.TokenLabel,
                        tokenInfo.Label,
                        StringComparison.Ordinal))
                        continue;
                }

                return slot;
            }

            return null;
        }

        public static byte[] Decrypt(Session session, KeyTransRecipientInfo keyTransRecipientInfo, X509Certificate2 cert)
        {

            var x509CertificateParser = new X509CertificateParser();
            var x509Certificate = x509CertificateParser.ReadCertificate(cert.RawData);

            // Get public key from certificate
            var pubKeyParams = x509Certificate.GetPublicKey(); //AsymmetricKeyParameter
            if (!(pubKeyParams is RsaKeyParameters))
                throw new NotSupportedException("Unsupported keys.  Currently supporting RSA keys only.");

            var rsaPubKeyParams = (RsaKeyParameters)pubKeyParams;

            //Correlate with HSM
            var privKeySearchTemplate = new List<ObjectAttribute>
            {
                new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
            };


            // Get handle to private key.
            // TODO: potential for multiple keys. (old/new)
            var hsmObjects = session.FindAllObjects(privKeySearchTemplate);
            var rsaEncryptedKey = keyTransRecipientInfo.EncryptedKey.GetOctets();

            var id = keyTransRecipientInfo.KeyEncryptionAlgorithm.Algorithm.Id;
            var mechanism = SelectMechanism(id);

            foreach (var objectHandle in hsmObjects)
            {
                try
                {
                    byte[] decryptedData = session.Decrypt(mechanism, objectHandle, rsaEncryptedKey);

                    // Return first found.  
                    // todo: need to test multi certs where some are expired or possible bad.
                    // The idea is to eventually find the good cer and not just find the first cert that can decrypt

                    return decryptedData;
                }
                catch (Exception ex)
                {
                    //keep trying
                    //log ex
                }
            }

            return null;
        }

        private static Mechanism SelectMechanism(string id)
        {
            //dataEnvelope EncryptionAlgOid
            //2.16.840.1.101.3.4.1.2
            //DerObjectIdentifier("2.16.840.1.101.3.4") + HashAlgs.Branch("1" = IdSha256) + DerObjectIdentifier(Aes + ".2" = IdAes128Cbc)

            Mechanism mechanism;

            if (id == PkcsObjectIdentifiers.IdRsaesOaep.Id)
            {
                // RecipientInfos
                //1.2.840.113549.1.1.7
                //pkcs1 + .7 (.7 = IdRsaesOaep)

                var mechanismParams = new CkRsaPkcsOaepParams(
                    (ulong)CKM.CKM_SHA_1,
                    (ulong)CKG.CKG_MGF1_SHA1,
                    (ulong)CKZ.CKZ_DATA_SPECIFIED, null);

                mechanism = new Mechanism(CKM.CKM_RSA_PKCS_OAEP, mechanismParams);

                return mechanism;
            }

            if (id == PkcsObjectIdentifiers.RsaEncryption.Id)
            {
                mechanism = new Mechanism(CKM.CKM_RSA_PKCS);

                return mechanism;
            }

            throw new NotSupportedException(string.Format("No supported HSM mechanisms for pkcs-1 OBJECT IDENTIFIER ::={{iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) 1 }}{0}", id));
        }
    }
}
