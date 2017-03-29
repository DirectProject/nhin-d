using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Moq;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Container;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Hsm;
using Xunit;

namespace hsmCryptographer.tests
{
    public class HsmCryptographerFailureTests : HsmCryptographerTestsBase
    {
        X509Certificate2 m_softSenderCertWithoutKey;
        X509Certificate2 m_dualUseCertWithPrivateKey;
        X509Certificate2 m_singleUseEnciphermentPublicCert;
        X509Certificate2 m_singleUseSigningPublicCert;

        public HsmCryptographerFailureTests()
        {
            var privateRedmondCerts = AgentTester.LoadPrivateCerts("redmond");
            var publicRedmondCerts = AgentTester.LoadPublicCerts(
                Path.Combine(AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond")));
            var privateKryptiqCerts = AgentTester.LoadPrivateCerts("nhind");

            m_softSenderCertWithoutKey = publicRedmondCerts.Single(c =>
                c.Subject.Contains("redmond.hsgincubator.com"));

            //
            // Get a dual-use certificate for the hsm.DirectInt.Lab domain.
            // This is used for cut over tests.  Meaning the transition from soft to hardware stored keys.
            //
            m_dualUseCertWithPrivateKey = privateKryptiqCerts.Single(c =>
                c.Subject.Contains("hsm.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages == (X509KeyUsageFlags.KeyEncipherment | X509KeyUsageFlags.DigitalSignature));

            var pubCerts = AgentTester.LoadCertificates(@"Certificates\redmond\Public");
            m_singleUseEnciphermentPublicCert = pubCerts.Single(c =>
                c.Subject.Contains("hsm.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages == X509KeyUsageFlags.KeyEncipherment);

            //
            // Private is in token.  Public side is in config store.
            // We sign the hash with the private token based key.  We include the public cert in the Signed Entity (S/MIME)
            //
            m_singleUseSigningPublicCert = privateRedmondCerts.Single(c =>
                c.Subject.Contains("hsm.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages == X509KeyUsageFlags.DigitalSignature);
        }


        /// <summary>
        /// We do not want to fail Direct when HSM is not availble during startup.
        /// The internal soft cryptographer should still service requests.  
        /// A missing HSM or credential problem should result in DSNs.
        /// </summary>
        [Fact]
        public void TestHSM_To_Soft_InitFails_C_Login()
        {
            var tokensettings = TokenSettings;
            tokensettings.NormalUserPin = "badpin";
            var resolverSettings = MockTokenResolverSettings(tokensettings);

            // Hardware Cryptographer Agent
            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(resolverSettings);

                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                Assert.NotNull(agentB);
                Assert.Equal(1, diagnostics.ActualErrorMessages.Count);
                Assert.Equal("Method C_Login returned CKR_PIN_INCORRECT", diagnostics.ActualErrorMessages[0]);

            }
        }

        [Fact]
        public void TestHSM_To_Soft_InitFails_ModuleNotFound()
        {
            var tokensettings = TokenSettings;
            tokensettings.Pkcs11LibraryPath = "badpath";
            var resolverSettings = MockTokenResolverSettings(tokensettings);

            // Hardware Cryptographer Agent

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(resolverSettings);
                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                Assert.NotNull(agentB);
                Assert.Equal(1, diagnostics.ActualErrorMessages.Count);
                Assert.Equal("Unable to load library. Error code: 0x0000007E",
                    diagnostics.ActualErrorMessages[0]);
            }
        }

        [Fact]
        public void TestHSM_To_Soft_InitFails_BadLabel()
        {
            var tokensettings = TokenSettings;
            tokensettings.TokenLabel = "badlabel";
            var resolverSettings = MockTokenResolverSettings(tokensettings);

            // Hardware Cryptographer Agent

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(resolverSettings);
                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                Assert.NotNull(agentB);
                Assert.Equal(1, diagnostics.ActualErrorMessages.Count);

                Assert.Equal(
                    "Did not find an available slot with TokenLable:badlabel",
                    diagnostics.ActualErrorMessages[0]);
            }
        }

        [Fact]
        public void TestHSM_To_Soft_HsmObjectNotFoundException()
        {
            var resolverSettings = MockTokenResolverSettings(TokenSettings);

            // Hardware Cryptographer Agent
            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(resolverSettings);

                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                var tester = new AgentTester(new DirectAgent(AgentTester.DefaultDomainA), agentB);

                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);

                tester.AgentB.Cryptographer
                    .Sign(message, m_softSenderCertWithoutKey); //wrong cert, not in HSM

                Assert.Equal(1, diagnostics.ActualErrorMessages.Count);

                Assert.True(
                    diagnostics.ActualErrorMessages[0].Contains("Private key correlation failed for signing cert"));
            }
        }

        [Fact]
        public void TestHsmCrypto_Unsuported_methods()
        {
            using (var hsmCryptographer = new HsmCryptographer())
            {
                hsmCryptographer.Init(TokenSettings);
                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                var tester = new AgentTester(new DirectAgent(AgentTester.DefaultDomainA), agentB);
                string messageText = tester.ReadMessageText("simpleHsmToSoft.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);

                Assert.Throws<NotImplementedException>(() => tester.AgentB.Cryptographer
                    .Encrypt(
                        message,
                        m_singleUseEnciphermentPublicCert));

                Assert.Throws<NotImplementedException>(() => tester.AgentB.Cryptographer
                    .Encrypt(
                        message,
                        new X509Certificate2Collection(m_singleUseEnciphermentPublicCert)));
            }
        }

        [Fact]
        public void TestHsmCrypto_Unsupported_methods2()
        {
            // Hardware Cryptographer Agent

            using (var hsmCryptographer = new HsmCryptographer())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.Error += diagnostics.OnResolverError;

                hsmCryptographer.Init(TokenSettings);
                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                // Software Cryptographer Agent

                var agentA = AgentTester.CreateAgent(
                    AgentTester.DefaultDomainA,
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond"),
                    //new TestSmimeCryptographer(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA256));
                    SMIMECryptographer.Default);

                var tester = new AgentTester(agentA, agentB);

                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);

                var signedEntity = tester.AgentB.Cryptographer.Sign(message, m_singleUseSigningPublicCert);

                //
                // SignedEntity has Content and Signature in MimeEntity formats. 
                //
                Console.WriteLine("*** Content Entity:\r\n" + signedEntity.Content);
                Console.WriteLine("*** Signature Entity:\r\n" + signedEntity.Signature);

                Assert.Throws<NotImplementedException>(() =>
                    tester.AgentB.Cryptographer.DeserializeDetachedSignature(signedEntity));

                Assert.Throws<NotImplementedException>(() =>
                    tester.AgentB.Cryptographer.DeserializeEnvelopedSignature(signedEntity.ToEntity()));
            }
        }


        /// <summary>
        /// 
        /// Hsm fails to decrypt because sender is still using soft cert during cut over period.  
        /// Failover to soft cryptographer triggers a Warning.
        /// 
        /// This is refered to as the Cut-Over period.  For some amount of time existing software 
        /// based certs will be cached in DNS servers throughout the internet.  So for some 
        /// amount of time based on internal policy HSMCryptographer will failover to the software
        /// key to decrypt messages.
        /// </summary>
        [Fact]
        public void TestSoft_To_HSM_Failover()
        {
            var resolverSettings = MockTokenResolverSettings(TokenSettings);

            // Hardware Cryptographer Agent

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.Init(resolverSettings);
                hsmCryptographer.Error += diagnostics.OnResolverError;
                hsmCryptographer.Warning += diagnostics.OnResolverWarning;

                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                // Software Cryptographer Agent

                var agentA = AgentTester.CreateAgent(
                    AgentTester.DefaultDomainA,
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond"),
                    //new TestSmimeCryptographer(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA256));
                    SMIMECryptographer.Default);

                var tester = new AgentTester(agentA, agentB);

                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);

                var encryptedEntity = tester.AgentA.Cryptographer.Encrypt(message, m_dualUseCertWithPrivateKey);

                // Decrypt with first resolved local cert (hardware cert) but is wrong

                var decryptedEntity = tester.AgentB.Cryptographer
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedEntity),
                        m_singleUseEnciphermentPublicCert);

                Assert.Null(decryptedEntity);
                Assert.Equal(0, diagnostics.ActualWarningMessages.Count);
                Assert.Equal(0, diagnostics.ActualErrorMessages.Count);

                // Decrypt with second resolved cert.  Message encrypted with DNS cashed cert.

                decryptedEntity = tester.AgentB.Cryptographer
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedEntity),
                        m_dualUseCertWithPrivateKey);

                Assert.Equal(2, diagnostics.ActualWarningMessages.Count);
                Assert.Equal(0, diagnostics.ActualErrorMessages.Count);

                Assert.Equal(
                    "Cutover to Soft SMIMECryptographer started...",
                    diagnostics.ActualWarningMessages[0]);

                Assert.Equal(
                    "Cutover succeeded.",
                    diagnostics.ActualWarningMessages[1]);

                Assert.Equal(message.Body.Text.Trim(), decryptedEntity.Body.Text.Trim());
            }
        }

        /// <summary>
        /// 
        /// Hsm recovers from bad connection
        /// 
        /// </summary>
        [Fact]
        public void TestSoft_To_HSM_Recovery()
        {
            // setup token to fail
            var tokensettings = TokenSettings;
            var pin = tokensettings.NormalUserPin;
            tokensettings.NormalUserPin = "badpin";
            var resolverSettings = MockTokenResolverSettings(tokensettings);

            // Hardware Cryptographer Agent

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(resolverSettings);
                hsmCryptographer.Error += diagnostics.OnResolverError;
                hsmCryptographer.Warning += diagnostics.OnResolverWarning;
                Assert.Equal(1, diagnostics.ActualErrorMessages.Count);
                Assert.Equal("Method C_Login returned CKR_PIN_INCORRECT", diagnostics.ActualErrorMessages[0]);

                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                // Software Cryptographer Agent

                var agentA = AgentTester.CreateAgent(
                    AgentTester.DefaultDomainA,
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond"),
                    //new TestSmimeCryptographer(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA256));
                    SMIMECryptographer.Default);

                var tester = new AgentTester(agentA, agentB);

                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);

                var encryptedEntity = tester.AgentA.Cryptographer.Encrypt(message, m_singleUseEnciphermentPublicCert);

                // Decrypt with first resolved local cert (hardware cert) but is wrong

                var decryptedEntity = tester.AgentB.Cryptographer
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedEntity),
                        m_singleUseEnciphermentPublicCert);

                Assert.Null(decryptedEntity);
                Assert.Equal(1, diagnostics.ActualWarningMessages.Count);
                Assert.Equal(3, diagnostics.ActualErrorMessages.Count);

                Assert.Equal("Method C_Login returned CKR_PIN_INCORRECT", diagnostics.ActualErrorMessages[1]);
                Assert.Equal("Method C_Login returned CKR_PIN_INCORRECT", diagnostics.ActualErrorMessages[2]);
                Assert.Equal("Attempting to connect to Token", diagnostics.ActualWarningMessages[0]);

                // Fix tokensettings
                hsmCryptographer.TokenSettings.NormalUserPin = pin;

                decryptedEntity = tester.AgentB.Cryptographer
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedEntity),
                        m_singleUseEnciphermentPublicCert);

                //still the same one error messages in list
                Assert.Equal(3, diagnostics.ActualErrorMessages.Count);
                Assert.Equal(2, diagnostics.ActualWarningMessages.Count);
                Assert.Equal("Attempting to connect to Token", diagnostics.ActualWarningMessages[1]);

                Assert.Equal(message.Body.Text.Trim(), decryptedEntity.Body.Text.Trim());
            }
        }

        /// <summary>
        /// Encrypt with single use encipherment cert
        /// </summary>
        [Fact]
        public void TestSoft_To_HSM_DecryptException_NoCerts()
        {
            var resolverSettings = MockTokenResolverSettings(TokenSettings);

            // Hardware Cryptographer Agent

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                hsmCryptographer.Init(resolverSettings);

                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.Error += diagnostics.OnResolverError;
                hsmCryptographer.Warning += diagnostics.OnResolverWarning;

                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                // Software Cryptographer Agent

                var agentA = AgentTester.CreateAgent(
                    AgentTester.DefaultDomainA,
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond"),
                    //new TestSmimeCryptographer(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA256));
                    SMIMECryptographer.Default);

                var tester = new AgentTester(agentA, agentB);

                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);

                var encryptedEntity = tester.AgentA.Cryptographer.Encrypt(message, m_singleUseSigningPublicCert);

                // Decrypt with first resolved local cert (hardware cert) but is wrong

                var decryptedEntity = tester.AgentB.Cryptographer
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedEntity),
                        null);

                Assert.Null(decryptedEntity);

                Assert.Equal(0, diagnostics.ActualWarningMessages.Count);
                Assert.Equal(1, diagnostics.ActualErrorMessages.Count);

                Assert.Equal(
                    "Error occurred during a cryptographic operation.",
                    diagnostics.ActualErrorMessages[0]);
            }
        }


        /// <summary>
        /// Sign with software (<see cref="SMIMECryptographer"/>).  Decrypt with hardware (<see cref="HsmCryptographer"/>)
        /// This is a manual test.  You must block the outgoing firewall or break connection to the HSM as indicated below
        /// </summary>
        [Fact(Skip = "Manually break the network")]
        public void TestDispositionHeaders_Soft_To_HSM()
        {
            var resolverSettings = MockTokenResolverSettings(TokenSettings);

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                hsmCryptographer.Init(resolverSettings);

                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.Error += diagnostics.OnResolverError;
                hsmCryptographer.Warning += diagnostics.OnResolverWarning;

                var agentB = AgentTester.CreateAgent(
                    "hsm.DirectInt.lab",
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                    hsmCryptographer);

                // Software Cryptographer Agent

                var agentA = AgentTester.CreateAgent(
                    AgentTester.DefaultDomainA,
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond"),
                    //new TestSmimeCryptographer(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA256));
                    SMIMECryptographer.Default);

                var tester = new AgentTester(agentA, agentB);
                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);
                var encryptedEntity = tester.AgentA.Cryptographer.Encrypt(message, m_singleUseEnciphermentPublicCert);

                // [BreakPoint] Break network connection here

                var decryptedEntity = tester.AgentB.Cryptographer
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedEntity),
                        m_singleUseEnciphermentPublicCert);

                Assert.Null(decryptedEntity);
                Assert.Equal(0, diagnostics.ActualWarningMessages.Count);
                Assert.Equal(1, diagnostics.ActualErrorMessages.Count);

                Assert.Equal(
                    "Method C_OpenSession returned CKR_DEVICE_ERROR",
                    diagnostics.ActualErrorMessages[0]);

                // [BreakPoint] Restore network connection here

                decryptedEntity = tester.AgentB.Cryptographer
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedEntity),
                        m_singleUseEnciphermentPublicCert);

                Assert.NotNull(decryptedEntity);

                Assert.Equal(1, diagnostics.ActualWarningMessages.Count);
                Assert.Equal(2, diagnostics.ActualErrorMessages.Count);


            }
        }


        /// <summary>
        /// Example Settings XmlNode
        /// 
        /// string xmlContent = 
        ///    @"<Settings>
        ///      <Library>C:\Program Files\SafeNet\LunaClient\cryptoki.dll</Library>
        ///      <TokenSerial>Serial #</TokenSerial>
        ///      <TokenLabel>partition_name</TokenLabel>
        ///      <UserPin>password</UserPin>
        ///      <DefaultEncryption>AES256</DefaultEncryption>
        ///      <DefaultDigest>SHA256</DefaultDigest>
        ///    </Settings>";
        /// </summary>
        [Fact]
        public void TestPluginInitFailures()
        {
            var pluginDef = new Mock<PluginDefinition>();
            pluginDef.Object.TypeName = "Surescripts.Health.Direct.Hsm.HsmCryptographerProxy, Surescripts.Health.Direct.Hsm";

            var tokenSettings =
                @"<TokenSettings>
                  
                  <TokenSerial>Serial #</TokenSerial>
                  <TokenLabel>partition_name</TokenLabel>
                  <UserPin>password</UserPin>
                  <DefaultEncryption>AES256</DefaultEncryption>
                  <DefaultDigest>SHA256</DefaultDigest>
                </TokenSettings>";

            var resolverSettings = MockTokenResolverSettings(tokenSettings.FromXml<TokenSettings>());

            pluginDef.Setup(p => p.DeserializeSettings<TokenResolverSettings>())
                .Returns(resolverSettings);

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(pluginDef.Object);
                Assert.Equal(1, diagnostics.ActualErrorMessages.Count);

                Assert.Equal(
                    "Unable to load DLL '__Internal': The specified module could not be found. (Exception from HRESULT: 0x8007007E)",
                    diagnostics.ActualErrorMessages[0]);

                hsmCryptographer.Error += diagnostics.OnResolverError;
                hsmCryptographer.Warning += diagnostics.OnResolverWarning;

                var agentB = AgentTester.CreateAgent(
                   "hsm.DirectInt.lab",
                   AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                   hsmCryptographer);

                var tester = new AgentTester(new DirectAgent(AgentTester.DefaultDomainA), agentB);
                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);
                var signed = tester.AgentB.Cryptographer
                    .Sign(
                        message,
                        new X509Certificate2Collection(m_singleUseSigningPublicCert));

                Assert.Null(signed);
                Assert.Equal(3, diagnostics.ActualErrorMessages.Count);
                Assert.Equal(1, diagnostics.ActualWarningMessages.Count);
                Assert.Equal("Attempting to connect to Token", diagnostics.ActualWarningMessages[0]);
                // while signing the we tried to initialize the token again.               

                Assert.Equal(
                    "Unable to load DLL '__Internal': The specified module could not be found. (Exception from HRESULT: 0x8007007E)",
                    diagnostics.ActualErrorMessages[1]);

                Assert.Equal(
                    "Unable to load DLL '__Internal': The specified module could not be found. (Exception from HRESULT: 0x8007007E)",
                    diagnostics.ActualErrorMessages[2]);

                var encryptedMessage = tester.AgentA.Cryptographer.Encrypt(message, m_singleUseEnciphermentPublicCert);

                tester.AgentB.Cryptographer  // Decrypt Test
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedMessage),
                        m_singleUseEnciphermentPublicCert);

                Assert.Equal(5, diagnostics.ActualErrorMessages.Count);

                Assert.Equal(
                    "Unable to load DLL '__Internal': The specified module could not be found. (Exception from HRESULT: 0x8007007E)",
                    diagnostics.ActualErrorMessages[3]);

                Assert.Equal(
                    "Unable to load DLL '__Internal': The specified module could not be found. (Exception from HRESULT: 0x8007007E)",
                    diagnostics.ActualErrorMessages[4]);
            }

            TokenSettings ts = TokenSettings;
            ts.NormalUserPin = null;
            tokenSettings = ts.ToXml();

            resolverSettings = MockTokenResolverSettings(tokenSettings.FromXml<TokenSettings>());

            pluginDef.Setup(p => p.DeserializeSettings<TokenResolverSettings>())
                .Returns(resolverSettings);

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(pluginDef.Object);

                var agentB = AgentTester.CreateAgent(
                   "hsm.DirectInt.lab",
                   AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                   hsmCryptographer);

                var tester = new AgentTester(new DirectAgent(AgentTester.DefaultDomainA), agentB);
                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);
                var signed = tester.AgentB.Cryptographer.Sign(message, m_singleUseSigningPublicCert);  //Sign Test

                Assert.Null(signed);
                Assert.Equal(2, diagnostics.ActualErrorMessages.Count);

                Assert.Equal(
                    "Method C_Login returned CKR_PIN_INCORRECT",
                    diagnostics.ActualErrorMessages[0]);

                Assert.Equal(
                    "Method C_Login returned CKR_PIN_INCORRECT",
                    diagnostics.ActualErrorMessages[1]);

                var encryptedMessage = tester.AgentA.Cryptographer.Encrypt(message, m_singleUseEnciphermentPublicCert);

                tester.AgentB.Cryptographer  // Decrypt Test
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedMessage),
                        m_singleUseEnciphermentPublicCert);

                Assert.Equal(3, diagnostics.ActualErrorMessages.Count);
                Assert.Equal(
                    "Method C_Login returned CKR_PIN_INCORRECT",
                    diagnostics.ActualErrorMessages[2]);
            }

            tokenSettings =
                @"<TokenSettings>
                  <Library>C:\Program Files\SafeNet\LunaClient\cryptoki.dll</Library>
                  <TokenSerial>Serial #</TokenSerial>
                  
                  <UserPin>password</UserPin>
                  <DefaultEncryption>AES256</DefaultEncryption>
                  <DefaultDigest>SHA256</DefaultDigest>
                </TokenSettings>";

            resolverSettings = MockTokenResolverSettings(tokenSettings.FromXml<TokenSettings>());

            pluginDef.Setup(p => p.DeserializeSettings<TokenResolverSettings>())
                .Returns(resolverSettings);

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(resolverSettings);

                var agentB = AgentTester.CreateAgent(
                   "hsm.DirectInt.lab",
                   AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                   hsmCryptographer);

                var tester = new AgentTester(new DirectAgent(AgentTester.DefaultDomainA), agentB);
                string messageText = tester.ReadMessageText("simpleSoftToHsm.eml");
                var message = MimeSerializer.Default.Deserialize<Message>(messageText);
                var signed = tester.AgentB.Cryptographer.Sign(message, m_singleUseSigningPublicCert);  //Sign Test

                //
                // Yes you can sign without a TokenLabel.  You need the TokenSerial and TokenLabel for searching for objects, not signing.
                //
                Assert.Null(signed);
                Assert.Equal(2, diagnostics.ActualErrorMessages.Count);

                Assert.Equal(
                    "Did not find an available slot with TokenLable:",
                    diagnostics.ActualErrorMessages[0]);

                Assert.Equal(
                    "Did not find an available slot with TokenLable:",
                    diagnostics.ActualErrorMessages[1]);

                var encryptedMessage = tester.AgentA.Cryptographer.Encrypt(message, m_singleUseEnciphermentPublicCert);

                tester.AgentB.Cryptographer  // Decrypt Test
                    .DecryptEntity(
                        hsmCryptographer.GetEncryptedBytes(encryptedMessage),
                        m_singleUseEnciphermentPublicCert);

                Assert.Equal(3, diagnostics.ActualErrorMessages.Count);
                Assert.Equal(
                    "Did not find an available slot with TokenLable:",
                    diagnostics.ActualErrorMessages[2]);
            }
        }

        [Fact]
        public void TestPluginInitNullTokenSettings()
        {
            var pluginDef = new Mock<PluginDefinition>();
            pluginDef.Object.TypeName = "Surescripts.Health.Direct.Hsm.HsmCryptographerProxy, Surescripts.Health.Direct.Hsm";

            pluginDef.Setup(p => p.DeserializeSettings<TokenResolverSettings>())
                .Returns(null as TokenResolverSettings);

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                var diagnostics = new FakeDiagnostics(typeof(HsmCryptographerProxy));
                hsmCryptographer.ProxyError += diagnostics.OnResolverError;
                hsmCryptographer.Init(pluginDef.Object);
            }
        }


        [Fact]
        public void TestTokenSettingsExceptions()
        {
            // Not very interesting other than code coverage...
            var tokenSettings = TokenSettings;
            Assert.Throws<ArgumentException>(() => tokenSettings.Pkcs11LibraryPath = "");
            Assert.Throws<ArgumentException>(() => tokenSettings.Pkcs11LibraryPath = null);

            Assert.Throws<ArgumentException>(() => tokenSettings.TokenLabel = "");
            Assert.Throws<ArgumentException>(() => tokenSettings.TokenLabel = null);
        }
        private static TokenSettings TokenSettings
        {
            get
            {
                //
                // Testing with a real HSM  
                // You will have to supply the credentials and create the test data
                //

                string dataPath = Path.Combine(
                    Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData), "DirectProject");

                string tokenSettingsXml = File.ReadAllText(Path.Combine(dataPath, "TokenSettings.xml"));
                TokenSettings tokenSettings = tokenSettingsXml.FromXml<TokenSettings>();

                return tokenSettings;
            }
        }

        public class FakeDiagnostics
        {
            public bool Called;
            readonly Type m_cryptographerType;

            public FakeDiagnostics(Type resolverType)
            {
                m_cryptographerType = resolverType;
            }

            private readonly List<string> _actualErrorMessages = new List<string>();
            private readonly List<string> _actualWarningMessages = new List<string>();

            public List<string> ActualErrorMessages
            {
                get { return _actualErrorMessages; }
            }

            public List<string> ActualWarningMessages
            {
                get { return _actualWarningMessages; }
            }

            public void OnResolverError(ISmimeCryptographer cryptographer, Exception error)
            {
                _actualErrorMessages.Add(error.Message);
                //Logger.Error("CRYPTOGRPHER ERROR {0}, {1}", resolver.GetType().Name, error.Message);
            }

            public void OnResolverWarning(ISmimeCryptographer cryptographer, string message)
            {
                _actualWarningMessages.Add(message);
                //Logger.Warn("CRYPTOGRPHER Warning {0}, {1}", resolver.GetType().Name, message);
            }
        }
    }
}
