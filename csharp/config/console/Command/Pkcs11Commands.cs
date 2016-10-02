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
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.ServiceModel;
using System.Text;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Cryptography;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;
using Net.Pkcs11Interop.Common;
using Net.Pkcs11Interop.HighLevelAPI;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.Pkcs;
using Org.BouncyCastle.Asn1.X509;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.OpenSsl;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.Utilities.IO.Pem;
using Org.BouncyCastle.X509;
using PemWriter = Org.BouncyCastle.OpenSsl.PemWriter;
using X509Extension = Org.BouncyCastle.Asn1.X509.X509Extension;

namespace Health.Direct.Config.Console.Command
{
    public class Pkcs11Commands : CommandsBase<CertificateStoreClient>
    {
        private readonly TokenSettings m_tokenSettings;
        private Session m_sessionApplication;
        private Pkcs11 m_pkcs11;
        private Slot m_slot;

        internal Pkcs11Commands(ConfigConsole console, Func<CertificateStoreClient> client, TokenSettings settings)
            : base(console, client)
        {
            m_tokenSettings = settings;

            try
            {
                InitializePkcs11(settings);
                EnsureLoggedInSession(settings);
            }
            catch (Exception ex)
            {
                WriteLine(ex.Message);
                WriteLine(ex.StackTrace);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="settings"></param>
        private void InitializePkcs11(TokenSettings settings)
        {
            m_pkcs11 = new Pkcs11(settings.Pkcs11LibraryPath, settings.UseOsLocking);
            m_slot = Pkcs11Util.FindSlot(m_pkcs11, settings);

            if (m_slot == null)
            {
                throw new ArgumentNullException(nameof(m_slot));
            }
        }

        /// <summary>
        /// From PKCS#11 V2.20: CRYPTOGRAPHIC TOKEN INTERFACE STANDARD, section 6.7.4 Session events.
        /// 
        /// In Cryptoki, all sessions that an application has with a token must have the same
        /// login/logout status(i.e., for a given application and token, one of the following holds: all
        /// sessions are public sessions; all sessions are SO sessions; or all sessions are user
        /// sessions). When an application’s session logs into a token, all of that application’s
        /// sessions with that token become logged in, and when an application’s session logs out of
        ///  a token, all of that application’s sessions with that token become logged out. Similarly,
        /// for example, if an application already has a R/O user session open with a token, and then
        /// opens a R/W session with that token, the R/W session is automatically logged in. 
        /// </summary>
        /// <param name="settings"></param>
        private void EnsureLoggedInSession(TokenSettings settings)
        {
            m_sessionApplication = m_slot.OpenSession(true);

            var sessionInfo = m_sessionApplication.GetSessionInfo();

            //
            // If another session has logged in then we should also be logged in.
            //
            //
            if (sessionInfo.State != CKS.CKS_RO_USER_FUNCTIONS)
            {
                m_sessionApplication.Login(CKU.CKU_USER, settings.NormalUserPin);
            }
        }


        /// <summary>
        /// Import a p12 (.pfx) certificate from a file.
        /// Extract the private key to pkcs#8 format.
        /// </summary>
        [Command(Name = "Pkcs11_ExportPkcs8Key_Pfx", Usage = CertificateExportUsage)]
        public void CertificateExportKey(string[] args)
        {
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(0, args);
            MemoryX509Store certStore = certFileInfo.LoadCerts();
            ShowKey(certStore);
        }

        private const string CertificateExportUsage
            = "Import a p12 (.pfx) certificate from a file.\r\n"
              + "Extract the private key to pkcs#8 format.\r\n"
              + Constants.CRLF + CertificateFileInfo.Usage;

        /// <summary>
        /// List all keys and x509 certificates
        /// </summary>
        [Command(Name = "Pkcs11_ListKey_All", Usage = CertificateListUsage)]
        public void KeyListsAll(string[] args)
        {
            List<Pkcs11PrivateKey> privateKeys;
            List<Pkcs11PublicKey> publicKeys;

            m_pkcs11.GetTokenObjects(m_slot, m_tokenSettings.NormalUserPin, out privateKeys, out publicKeys);
            PrintKeyInfo(privateKeys, publicKeys);
        }

        private
            const string CertificateListUsage
            = "List all keys"
              + Constants.CRLF + "  [chunkSize]"
              + Constants.CRLF + "\t chunkSize: (Optional) Enumeration size. Default is 25";

        /// <summary>
        /// Find all keys and x509 certificates by email or domain.
        /// </summary>
        [Command(Name = "Pkcs11_Search_ByOwner", Usage = SearchByOwnerUsage)]
        public void KeysSearchByOwner(string[] args)
        {
            string name = args.GetRequiredValue(0);
            List<Pkcs11PrivateKey> privateKeys;
            List<Pkcs11PublicKey> publicKeys;
            m_pkcs11.GetTokenObjectsByName(m_slot, name, out privateKeys, out publicKeys);
            PrintKeyInfo(privateKeys, publicKeys);
        }

        private
            const string SearchByOwnerUsage
            = "Search for a certificate by email or domain owner"
              + Constants.CRLF + "Case sensitive search"
              + Constants.CRLF + "    owner"
              + Constants.CRLF + "\t name: Should be the same as the value of the certificate subjecAlt name.";

        /// <summary>
        /// Remove key and x509 certificate by Id (CKA_ID)
        /// <remarks>
        /// Key and certificate should be stored with the same CKA_ID.
        /// </remarks>
        /// </summary>
        [Command(Name = "Pkcs11_Delete_ById", Usage = CertificateRemoveUsage)]
        public void CertificateRemove(string[] args)
        {
            string id = args.GetRequiredValue(0);

            using (var session = m_slot.OpenSession(true))
            {
                // Define search template for private keys
                var keySearchTemplate = new List<ObjectAttribute>
                {
                    //new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                    new ObjectAttribute(CKA.CKA_TOKEN, true),
                    new ObjectAttribute(CKA.CKA_ID, ConvertUtils.HexStringToBytes(id))
                };

                var keyAttributes = new List<CKA>
                {
                    CKA.CKA_ID,
                    CKA.CKA_LABEL,
                    CKA.CKA_CLASS
                };

                // Get search results
                var hsmObjects = session.FindAllObjects(keySearchTemplate);

                WriteLine("Found {0} private keys.", hsmObjects.Count);

                foreach (var objectHandle in hsmObjects)
                {
                    var keyObjectAttributes = session.GetAttributeValue(objectHandle, keyAttributes);
                    string ckaId = ConvertUtils.BytesToHexString(keyObjectAttributes[0].GetValueAsByteArray());
                    string ckaLabel = keyObjectAttributes[1].GetValueAsString();
                    ulong ckaClass = keyObjectAttributes[2].GetValueAsUlong();

                    session.DestroyObject(objectHandle);

                    WriteLine("Removed");
                    WriteLine("\t CKA_ID: {0}", ckaId);
                    WriteLine("\t CKA_LABEL: {0}", ckaLabel);
                    WriteLine("\t CKA_CLASS: {0}", ckaClass.ToString());
                }
            }
        }

        private const string CertificateRemoveUsage
            = "Remove certificate by Id (CKA_ID)"
              + Constants.CRLF + "    certificateID";

        /// <summary>
        /// Create certificate signing request
        /// <remarks>
        /// Public and private key are stored with the same CKA_ID.  Later when deleting expired certificates the CKA_ID passed to Pkcs11_Delete_ById will clean both records.
        /// </remarks>
        /// <example>
        /// PKCS11_CREATE_CSR hobojoe.lab CN=hobojoe.lab 1024
        /// PKCS11_CREATE_CSR hobojoe.lab CN=hobojoe.lab
        /// </example>
        /// </summary>
        [Command(Name = "Pkcs11_Create_Domain_CSR", Usage = CertificateSigningRequestForDomainUsage)]
        public void CertificateDomainCsr(string[] args)
        {
            string directDomain = args.GetRequiredValue(0);
            string distinguishedName = args.GetRequiredValue(1);
            int defaultBits = args.GetOptionalValue(2, 2048);
            string outPath = args.GetOptionalValue(3, ".");

            using (var session = m_slot.OpenSession(true))
            {
                string ckaLabel = directDomain;
                byte[] ckaId = session.GenerateRandom(20);

                var csrSign = CreateCertificateSigningRequest(session, ckaLabel, ckaId, defaultBits, directDomain, distinguishedName, KeyUsage.DigitalSignature);
                WriteLine("Cert request created for KeyUsage.DigitalSignature");
                WriteLine(csrSign);
                File.WriteAllText(Path.Combine(outPath, directDomain + "_digitalsignature.csr"), csrSign);

                var csrEncipherment = CreateCertificateSigningRequest(session, ckaLabel, ckaId, defaultBits, directDomain, distinguishedName, KeyUsage.KeyEncipherment);
                WriteLine("Cert request created for KeyUsage.KeyEncipherment");
                WriteLine(csrEncipherment);
                File.WriteAllText(Path.Combine(outPath, directDomain + "_encipherment.csr"), csrEncipherment);
            }
        }

        private const string CertificateSigningRequestForDomainUsage
            = "Create certificate signing request.  CN of distinguishedName will typically be the same as directDomain"
                + Constants.CRLF + "    directDomain distinguishedName [defaultBits] [out]"
                + Constants.CRLF + "\t directDomain: New direct domain name.  Will be the SubjectAlt name in format DNS:{directDomain}"
                + Constants.CRLF + "\t distinguishedName: X500DistinguishedName"
                + Constants.CRLF + "\t\t Example distinguisedName: \"CN=hsm.DirectInt.lab, OU=DirectInt.Lab, O=Surescripts, C=US\""
                + Constants.CRLF + "\t defaultBits: Default key length is 2048 bits"
                + Constants.CRLF + "\t out: folder path.";


        /// <summary>
        /// Create certificate signing request
        /// <remarks>
        /// Public and private key are stored with the same CKA_ID.  Later when deleting expired certificates the CKA_ID passed to Pkcs11_Delete_ById will clean both records.
        /// </remarks>
        /// <example>
        /// PKCS11_CREATE_CSR hobojoe.lab CN=hobojoe.lab 1024
        /// PKCS11_CREATE_CSR hobojoe.lab CN=hobojoe.lab
        /// </example>
        /// </summary>
        [Command(Name = "Pkcs11_Create_Address_CSR", Usage = CertificateSigningRequestForAddressUsage)]
        public void CertificateAddressCsr(string[] args)
        {
            string emailAddress = args.GetRequiredValue(0);
            string distinguishedName = args.GetRequiredValue(1);
            int defaultBits = args.GetOptionalValue(2, 2048);
            string outPath = args.GetOptionalValue(4, ".");


            using (var session = m_slot.OpenSession(true))
            {
                string ckaLabel = emailAddress;
                byte[] ckaId = session.GenerateRandom(20);

                var csrSign = CreateCertificateSigningRequest(session, ckaLabel, ckaId, defaultBits, emailAddress, distinguishedName, KeyUsage.DigitalSignature);
                WriteLine("Cert request created for KeyUsage.DigitalSignature");
                WriteLine(csrSign);
                File.WriteAllText(Path.Combine(outPath, emailAddress + "_digitalsignature.csr"), csrSign);

                var csrEncipherment = CreateCertificateSigningRequest(session, ckaLabel, ckaId, defaultBits, emailAddress, distinguishedName, KeyUsage.KeyEncipherment);
                WriteLine("Cert request created for KeyUsage.KeyEncipherment");
                WriteLine(csrEncipherment);
                File.WriteAllText(Path.Combine(outPath, emailAddress + "_encipherment.csr"), csrEncipherment);
            }
        }

        private const string CertificateSigningRequestForAddressUsage
            = "Create certificate signing request.  CN of distinguishedName will typically be the same as directDomain"
                + Constants.CRLF + "    email distinguishedName [defaultBits] [NPI] [out]"
                + Constants.CRLF + "\t email: Will be the SubjectAlt name in format rfc822:{directDomain}"
                + Constants.CRLF + "\t distinguishedName: X500DistinguishedName"
                + Constants.CRLF + "\t\t Example distinguisedName: \"CN=hsm.DirectInt.lab, OU=DirectInt.Lab, O=Surescripts, C=US\""
                + Constants.CRLF + "\t defaultBits: Default key length is 2048 bits"
                + Constants.CRLF + "\t out: folder path.";

        /// <summary>
        /// Install signed x509 certificate
        /// <remarks>
        /// Validation will be performed for matching HSM record.
        /// </remarks>
        /// <example>
        /// PKCS11_ADD_CERT hobojoe.lab c:\cert\hobojoe.cer
        /// </example>
        /// </summary>
        [Command(Name = "Pkcs11_Add_Cert", Usage = CertificateAddUsage)]
        public void CertificateAdd(string[] args)
        {
            var certFileInfo = CertificateFileInfo.Create(0, args);
            var certStore = certFileInfo.LoadCerts();

            using (var session = m_slot.OpenSession(true))
            {
                foreach (var x509Cert in certStore.GetAllCertificates())
                {
                    // Parse certificate
                    var x509CertificateParser = new X509CertificateParser();
                    var x509Certificate = x509CertificateParser.ReadCertificate(x509Cert.RawData);

                    // Get public key from certificate
                    var pubKeyParams = x509Certificate.GetPublicKey(); //AsymmetricKeyParameter

                    if (!(pubKeyParams is RsaKeyParameters))
                        throw new NotSupportedException("Unsupported keys.  Currently supporting RSA keys only.");

                    var rsaPubKeyParams = (RsaKeyParameters)pubKeyParams;
                    ValidatePrivateKeyCorrelatioin(rsaPubKeyParams, session);
                    PushPublicCert(x509Cert, false, certFileInfo.Status);
                }
            }
        }

        private static void ValidatePrivateKeyCorrelatioin(RsaKeyParameters rsaPubKeyParams, Session session)
        {
            //Correlate with HSM
            var privKeySearchTemplate = new List<ObjectAttribute>
            {
                new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
            };

            var hsmObjects = session.FindAllObjects(privKeySearchTemplate);

            if (hsmObjects.Count != 1)
                throw new HsmObjectNotFoundException("Correlating RSA private key not found");
        }

        private const string CertificateAddUsage
            = "Import a certificate from a file and push it into the store."
              + Constants.CRLF + CertificateFileInfo.Usage;

        private static string CreateCertificateSigningRequest(Session session, string ckaLabel, byte[] ckaId, int defaultBits,
            string directDomain, string distinguishedName, int keyUsage)
        {
            // Generate key pair - Signing
            ObjectHandle publicKeyHandle;
            ObjectHandle privateKeyHandle;
            Pkcs11Util.GenerateKeyPair(session, ckaLabel, ckaId, out publicKeyHandle, out privateKeyHandle, defaultBits);

            // Generate x509 attributes for csr 
            IList oids = new ArrayList();
            IList values = new ArrayList();

            oids.Add(X509Extensions.BasicConstraints);
            values.Add(new X509Extension(
                true,
                new DerOctetString(new BasicConstraints(true))));

            oids.Add(X509Extensions.KeyUsage);
            values.Add(new X509Extension(
                true,
                new DerOctetString(new KeyUsage(keyUsage))));

            if (directDomain.Contains("@"))
            {
                AddSubjectAltNameForRfc822Name(directDomain, oids, values);
            }
            else
            {
                AddSubjectAltNameForDnsName(directDomain, oids, values);
            }

            var attribute = new AttributePkcs(
                PkcsObjectIdentifiers.Pkcs9AtExtensionRequest,
                new DerSet(new X509Extensions(oids, values)));

            var asn1Attributes = new DerSet(attribute);

            // Generate certificate request in PKCS#10 format
            byte[] pkcs10 = Pkcs11Util.GeneratePkcs10(
                session,
                publicKeyHandle,
                privateKeyHandle,
                distinguishedName,
                DigestAlgorithm.SHA256,
                asn1Attributes);

            //Export to Pem format.
            var sb = new StringBuilder();
            var pemObject = new PemObject("CERTIFICATE REQUEST", pkcs10);

            using (var str = new StringWriter(sb))
            {
                var pemWriter = new PemWriter(str);
                pemWriter.WriteObject(pemObject);
            }

            return sb.ToString();
        }

        private static void AddSubjectAltNameForDnsName(string directDomain, IList oids, IList values)
        {
            oids.Add(X509Extensions.SubjectAlternativeName);
            values.Add(new X509Extension(
                false,
                new DerOctetString(
                    new GeneralNames(
                        new GeneralName(GeneralName.DnsName, directDomain)))));
        }

        private static void AddSubjectAltNameForRfc822Name(string email, IList oids, IList values)
        {
            oids.Add(X509Extensions.SubjectAlternativeName);
            
            values.Add(new X509Extension(
                false,
                new DerOctetString(
                    new GeneralNames(
                        new GeneralName(GeneralName.Rfc822Name, email)))));
        }
        
        internal void PushPublicCert(X509Certificate2 signedCert, bool checkForDupes, EntityStatus? status)
        {
            var owner = signedCert.ExtractEmailNameOrDnsName();

            try
            {
                if (!checkForDupes || !Client.Contains(signedCert))
                {
                    var certEntry = new Certificate(owner, signedCert);
                    if (status != null)
                    {
                        certEntry.Status = status.Value;
                    }
                    Client.AddPkcs11Certificate(certEntry);
                    WriteLine("Added {0}", signedCert.Subject);
                }
                else
                {
                    WriteLine("Exists {0}", signedCert.Subject);
                }
            }
            catch (FaultException<ConfigStoreFault> ex)
            {
                if (ex.Detail.Error == ConfigStoreError.UniqueConstraint)
                {
                    WriteLine("Exists {0}", signedCert.Subject);
                }
            }
        }

        internal void ShowKey(IEnumerable<X509Certificate2> certs)
        {
            foreach (var cert in certs)
            {
                var csp = (RSACryptoServiceProvider)cert.PrivateKey;
                var pkcs8 = GetPkcs8Format(csp);

                WriteLine("BouncyCastle::");
                WriteLine(pkcs8);

                WriteLine(".Net Code only::");
                var pkcs8Ms = GetPkcs8FormatMs(csp);
                WriteLine(pkcs8Ms);
            }
        }

        //
        // Bouncy Castle PKCS#8 Formater
        // Output is the same as this OpenSSL command:
        // openssl pkcs12 -in certname.pfx -nocerts -out key.pem -nodes
        //
        private string GetPkcs8Format(RSACryptoServiceProvider csp)
        {
            var rsaParams = csp.ExportParameters(true);
            var keyPair = DotNetUtilities.GetRsaKeyPair(rsaParams);
            var pkcs8Gen = new Pkcs8Generator(keyPair.Private);
            var pemObj = pkcs8Gen.Generate();
            var sb = new StringBuilder();

            using (var pkcs8Out = new StringWriter(sb))
            {
                var pemWriter = new PemWriter(pkcs8Out);
                pemWriter.WriteObject(pemObj);
            }

            return sb.ToString();
        }

        //
        // Microsoft PKCS#8 Formater
        // If you run the following two OpenSSL commands then you will get this output.  
        // Something to look into... Maybe.
        // openssl pkcs12 -in certname.pfx -nocerts -out key.pem -nodes
        // openssl rsa -in key.pem -out private.key 
        // 
        private string GetPkcs8FormatMs(RSACryptoServiceProvider csp)
        {
            if (csp.PublicOnly) throw new ArgumentException("CSP does not contain a private key", nameof(csp));
            var parameters = csp.ExportParameters(true);
            var sb = new StringBuilder();

            using (var stream = new MemoryStream())
            using (var pcks8Out = new StringWriter(sb))
            {
                var writer = new BinaryWriter(stream);
                writer.Write((byte)0x30); // SEQUENCE
                using (var innerStream = new MemoryStream())
                {
                    var innerWriter = new BinaryWriter(innerStream);
                    EncodeIntegerBigEndian(innerWriter, new byte[] { 0x00 }); // Version
                    EncodeIntegerBigEndian(innerWriter, parameters.Modulus);
                    EncodeIntegerBigEndian(innerWriter, parameters.Exponent);
                    EncodeIntegerBigEndian(innerWriter, parameters.D);
                    EncodeIntegerBigEndian(innerWriter, parameters.P);
                    EncodeIntegerBigEndian(innerWriter, parameters.Q);
                    EncodeIntegerBigEndian(innerWriter, parameters.DP);
                    EncodeIntegerBigEndian(innerWriter, parameters.DQ);
                    EncodeIntegerBigEndian(innerWriter, parameters.InverseQ);
                    var length = (int)innerStream.Length;
                    EncodeLength(writer, length);
                    writer.Write(innerStream.GetBuffer(), 0, length);
                }

                var base64 = Convert.ToBase64String(stream.GetBuffer(), 0, (int)stream.Length).ToCharArray();
                pcks8Out.WriteLine("-----BEGIN RSA PRIVATE KEY-----");
                // Output as Base64 with lines chopped at 64 characters
                for (var i = 0; i < base64.Length; i += 64)
                {
                    pcks8Out.WriteLine(base64, i, Math.Min(64, base64.Length - i));
                }
                pcks8Out.WriteLine("-----END RSA PRIVATE KEY-----");
            }

            return sb.ToString();
        }

        private static void EncodeLength(BinaryWriter stream, int length)
        {
            if (length < 0) throw new ArgumentOutOfRangeException(nameof(length), "Length must be non-negative");
            if (length < 0x80)
            {
                // Short form
                stream.Write((byte)length);
            }
            else
            {
                // Long form
                var temp = length;
                var bytesRequired = 0;
                while (temp > 0)
                {
                    temp >>= 8;
                    bytesRequired++;
                }
                stream.Write((byte)(bytesRequired | 0x80));
                for (var i = bytesRequired - 1; i >= 0; i--)
                {
                    stream.Write((byte)(length >> (8 * i) & 0xff));
                }
            }
        }

        private static void EncodeIntegerBigEndian(BinaryWriter stream, byte[] value, bool forceUnsigned = true)
        {
            stream.Write((byte)0x02); // INTEGER
            var prefixZeros = 0;
            for (var i = 0; i < value.Length; i++)
            {
                if (value[i] != 0) break;
                prefixZeros++;
            }
            if (value.Length - prefixZeros == 0)
            {
                EncodeLength(stream, 1);
                stream.Write((byte)0);
            }
            else
            {
                if (forceUnsigned && value[prefixZeros] > 0x7f)
                {
                    // Add a prefix zero to force unsigned if the MSB is 1
                    EncodeLength(stream, value.Length - prefixZeros + 1);
                    stream.Write((byte)0);
                }
                else
                {
                    EncodeLength(stream, value.Length - prefixZeros);
                }
                for (var i = prefixZeros; i < value.Length; i++)
                {
                    stream.Write(value[i]);
                }
            }
        }

        private void PrintKeyInfo(List<Pkcs11PrivateKey> privateKeys, List<Pkcs11PublicKey> publicKeys)
        {
            int i = 1;
            foreach (var privateKey in privateKeys)
            {
                WriteLine("");
                WriteLine("Private key no." + i);
                WriteLine("\t ID (CKA_ID):        " + privateKey.Id);
                WriteLine("\t Label (CKA_LABEL):  " + privateKey.Label);

                // Print public part of RSA key
                if (privateKey.PublicKey.Exponent != null)
                {
                    var rsa = privateKey.PublicKey;
                    WriteLine("\t RSA exponent:       " + (rsa.Exponent));
                    WriteLine("\t RSA public modulus: " + (rsa.Modulus));
                }

                var publicKey = publicKeys.Where(k =>
                    privateKey.PublicKey.Modulus.CompareTo(k.PublicKey.Modulus) == 0 &&
                    privateKey.PublicKey.Exponent.CompareTo(k.PublicKey.Exponent) == 0 &&
                    k.Label == privateKey.Label
                    ).ToList();

                if (publicKey.Any())
                {
                    WriteLine("Matching Public key");
                }
                else
                {
                    WriteLine("!!! MISSING PUBLIC KEY !!!");
                }

                i++;
            }
        }
    }

    internal class HsmObjectNotFoundException : Exception
    {
        public HsmObjectNotFoundException(string correlatingRsaPrivateKeyNotFound) : base(correlatingRsaPrivateKeyNotFound)
        {
        }
    }
}
