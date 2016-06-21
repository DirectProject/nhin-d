using System;
using System.Collections.Generic;
using Net.Pkcs11Interop.HighLevelAPI;
using Net.Pkcs11Interop.Common;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Math;

namespace Health.Direct.Config.Console
{
    public static class Pkcs11Explorer 
    {
        public static void GetTokenObjects(
            this Pkcs11 pkcs11, 
            Slot slot, 
            string pin, 
            out List<Pkcs11PrivateKey> privateKeys,
            out List<Pkcs11PublicKey> publicKeys
            )
        {
            if (slot == null)
                throw new ArgumentNullException(nameof(slot));

            // Define search template for private keys
            var keySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                    new ObjectAttribute(CKA.CKA_TOKEN, true)
                };


            SearchToken(slot, keySearchTemplate, out privateKeys, out publicKeys);
        }

        private static void SearchToken(Slot slot, List<ObjectAttribute> keySearchTemplate, out List<Pkcs11PrivateKey> privateKeys, out List<Pkcs11PublicKey> publicKeys)
        {
            privateKeys = new List<Pkcs11PrivateKey>();
            publicKeys = new List<Pkcs11PublicKey>();

            // Notice we are opening a ReadOnly session
            using (var session = slot.OpenSession(true))
            {
                //session.Login(CKU.CKU_USER, pin);

                // Define key attributes that should be selected
                var keyAttributes = new List<CKA>
                {
                    CKA.CKA_ID,
                    CKA.CKA_LABEL,
                    CKA.CKA_KEY_TYPE
                };

                // Define RSA private key attributes that should be selected
                var rsaAttributes = new List<CKA>
                {
                    CKA.CKA_MODULUS,
                    CKA.CKA_PUBLIC_EXPONENT
                };

                // Find private keys
                var foundKeyObjects = session.FindAllObjects(keySearchTemplate);

                foreach (var objectHandle in foundKeyObjects)
                {
                    var keyObjectAttributes = session.GetAttributeValue(objectHandle, keyAttributes);

                    string ckaId = ConvertUtils.BytesToHexString(keyObjectAttributes[0].GetValueAsByteArray());
                    string ckaLabel = keyObjectAttributes[1].GetValueAsString();
                    RsaKeyParameters rsaKeyInfo = null;

                    if (keyObjectAttributes[2].GetValueAsUlong() == Convert.ToUInt64(CKK.CKK_RSA))
                    {
                        var rsaObjectAttributes = session.GetAttributeValue(objectHandle, rsaAttributes);
                        var modulus = new BigInteger(1, rsaObjectAttributes[0].GetValueAsByteArray());
                        var exponent = new BigInteger(1, rsaObjectAttributes[1].GetValueAsByteArray());
                        rsaKeyInfo = new RsaKeyParameters(false, modulus, exponent);
                    }

                    privateKeys.Add(new Pkcs11PrivateKey(ckaId, ckaLabel, rsaKeyInfo));
                }

                // Define search template for X.509 certificates
                var certSearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PUBLIC_KEY),
                    new ObjectAttribute(CKA.CKA_TOKEN, true)
                };


                // Find X.509 certificates
                var foundCertObjects = session.FindAllObjects(certSearchTemplate);

                foreach (var objectHandle in foundCertObjects)
                {
                    var keyObjectAttributes = session.GetAttributeValue(objectHandle, keyAttributes);

                    string ckaId = ConvertUtils.BytesToHexString(keyObjectAttributes[0].GetValueAsByteArray());
                    string ckaLabel = keyObjectAttributes[1].GetValueAsString();
                    RsaKeyParameters rsaKeyInfo = null;

                    if (keyObjectAttributes[2].GetValueAsUlong() == Convert.ToUInt64(CKK.CKK_RSA))
                    {
                        var rsaObjectAttributes = session.GetAttributeValue(objectHandle, rsaAttributes);
                        var modulus = new BigInteger(1, rsaObjectAttributes[0].GetValueAsByteArray());
                        var exponent = new BigInteger(1, rsaObjectAttributes[1].GetValueAsByteArray());
                        rsaKeyInfo = new RsaKeyParameters(false, modulus, exponent);
                    }

                    publicKeys.Add(new Pkcs11PublicKey(ckaId, ckaLabel, rsaKeyInfo));
                }

                //session.Logout();
            }
        }

        public static void GetTokenObjectsByName(
            this Pkcs11 pkcs11, 
            Slot slot, 
            string name,
            out List<Pkcs11PrivateKey> privateKeys,
            out List<Pkcs11PublicKey> publicKeys)
        {
            if (slot == null)
                throw new ArgumentNullException(nameof(slot));

            // Define search template for private keys by name
            var keySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                    new ObjectAttribute(CKA.CKA_TOKEN, true),
                    new ObjectAttribute(CKA.CKA_LABEL, name)
                };

            SearchToken(slot, keySearchTemplate, out privateKeys, out publicKeys);
        }
    }
}
