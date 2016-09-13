using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Mail;
using System.Net.Mime;
using System.Security.Cryptography;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Domains;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Policies;
using Health.Direct.Hsm;
using Moq;
using Health.Direct.Common.Mail;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.Impl;
using Net.Pkcs11Interop.Common;
using Net.Pkcs11Interop.HighLevelAPI;
using Net.Pkcs11Interop.HighLevelAPI.MechanismParams;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.Cms;
using Org.BouncyCastle.Cms;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.X509;
using Xunit;

namespace hsmCryptographer.tests
{
    public class HsmCryptographerTests : HsmCryptographerTestsBase, IDisposable
    {
        readonly AgentTester m_tester;
        readonly X509Certificate2 m_softSenderCertWithKeyRedomond;
        readonly X509Certificate2 m_softSenderCertWithKeyNHind;
        readonly X509Certificate2 m_singleUseEnciphermentPublicCert;
        readonly X509Certificate2 m_singleUseSigningPublicCert;
        readonly X509Certificate2 m_singleUseSigningPublicCertCrossover;
        readonly X509Certificate2 m_singleUseEnciphermentPublicCertCrossover;
        readonly ISmimeCryptographer m_cryptographerA;
        readonly X509Certificate2 m_dualUseCertWithPrivateKey;
        readonly X509Certificate2 m_extraDualUseCertWithPrivateKey;

        static HsmCryptographerTests()
        {
            AgentTester.EnsureStandardMachineStores();
        }

        public HsmCryptographerTests()
        {
            var resolverSettings = MockTokenResolverSettings(TokenSettings);

            // Software Cryptographer Agent
            var agentA = AgentTester.CreateAgent(
                AgentTester.DefaultDomainA,
                AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond"),
                //new TestSmimeCryptographer(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA256));
                SMIMECryptographer.Default);

            var hsmCryptographer = new HsmCryptographerProxy();
            hsmCryptographer.Init(resolverSettings);

            // Hardware Cryptographer Agent
            var agentB = AgentTester.CreateAgent(
                "hsm.DirectInt.lab",
                AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                hsmCryptographer);

            m_tester = new AgentTester(agentA, agentB);
            m_cryptographerA = m_tester.AgentA.Cryptographer;
            var privateCerts = AgentTester.LoadPrivateCerts("redmond");
            m_softSenderCertWithKeyRedomond = privateCerts.Single(c => c.Subject.Contains("redmond.hsgincubator.com"));
            var nhinCerts = AgentTester.LoadPrivateCerts("nhind");
            m_softSenderCertWithKeyNHind = nhinCerts.First(c => c.Subject.Contains("nhind.hsgincubator.com"));
            var pubCerts = AgentTester.LoadCertificates(@"Certificates\redmond\Public");

            m_singleUseEnciphermentPublicCert = pubCerts.Single(c =>
                c.Subject.Contains("hsm.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages == X509KeyUsageFlags.KeyEncipherment);

            //
            // Private is in token.  Public side is in config store.
            // We sign the hash with the private token based key.  We include the public cert in the Signed Entity (S/MIME)
            //
            var pubSignCerts = AgentTester.LoadCertificates(@"Certificates\redmond\Private");
            m_singleUseSigningPublicCert = pubSignCerts.Single(c =>
                c.Subject.Contains("hsm.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages == X509KeyUsageFlags.DigitalSignature);

            m_singleUseSigningPublicCertCrossover = pubSignCerts.Single(c =>
                c.Subject.Contains("fha-crossover.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages == X509KeyUsageFlags.DigitalSignature);

            //
            // This is another HSM test certificate
            //
            m_singleUseEnciphermentPublicCertCrossover = pubCerts.Single(c =>
                c.Subject.Contains("fha-crossover.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages == X509KeyUsageFlags.KeyEncipherment);

            //
            // Get a dual-use certificate for the hsm.DirectInt.Lab domain.
            // This is used for cut over tests.  Meaning the transition from soft to hardware stored keys.
            //
            m_dualUseCertWithPrivateKey = privateCerts.Single(c =>
                c.Subject.Contains("hsm.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages == 
                (X509KeyUsageFlags.KeyEncipherment | X509KeyUsageFlags.DigitalSignature));

            //
            // Get an extra dual-use certificate for the hsm.DirectInt.Lab domain.
            // This is used for cut over tests.  Meaning the transition from soft to hardware stored keys.
            //
            m_extraDualUseCertWithPrivateKey = nhinCerts.Single(c =>
                c.Subject.Contains("fha-crossover.DirectInt.lab") &&
                c.FindKeyUsageExtension()?.KeyUsages ==
                (X509KeyUsageFlags.KeyEncipherment | X509KeyUsageFlags.DigitalSignature));
        }

        public static TokenSettings TokenSettings
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

                //
                // Examples
                //

                //return new TokenSettings
                //{
                //    DefaultEncryption = EncryptionAlgorithm.AES128,
                //    DefaultDigest = DigestAlgorithm.SHA256,
                //    NormalUserPin = "password",
                //    Pkcs11LibraryPath = @"C:\Program Files\SafeNet\LunaClient\cryptoki.dll",
                //    TokenLabel = "partition_name",
                //    TokenSerial = "Serial #"
                //};

                //<TokenSettings>
                //  <Library>C:\Program Files\SafeNet\LunaClient\cryptoki.dll</Library>
                //  <TokenSerial>Serial #</TokenSerial>
                //  <TokenLabel>partition_name</TokenLabel>
                //  <UserPin>password</UserPin>
                //  <DefaultEncryption>AES256</DefaultEncryption>
                //  <DefaultDigest>SHA256</DefaultDigest>
                //</TokenSettings>
            }
        }

        public static IEnumerable<object[]> DigestAlgorithms
        {
            get
            {
                foreach (DigestAlgorithm algo in Enum.GetValues(typeof(DigestAlgorithm)))
                {
                    yield return new object[] { algo };
                }
            }
        }

        [Theory]
        [MemberData(nameof(DigestAlgorithms))]
        public void TestDigestMicalgParameter(DigestAlgorithm algo)
        {
            ContentType type = SignedEntity.CreateContentType(algo);
            Assert.True(type.Parameters["micalg"] == SMIMEStandard.ToString(algo));
        }

        /// <summary>
        /// Sign with software (<see cref="SMIMECryptographer"/>).  Decrypt with hardware (<see cref="HsmCryptographer"/>)
        /// </summary>
        [Fact]
        public void TestDispositionHeaders_Soft_To_HSM()
        {
            string messageText = m_tester.ReadMessageText("simpleSoftToHsm.eml");
            var message = MimeSerializer.Default.Deserialize<Message>(messageText);

            var signedEntity = m_cryptographerA.Sign(message, m_softSenderCertWithKeyRedomond);
            string disposition = signedEntity.Signature.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.SignatureDisposition == disposition);

            var encryptedEntity = m_cryptographerA.Encrypt(signedEntity.ToEntity(), m_singleUseEnciphermentPublicCert);
            disposition = encryptedEntity.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.EncryptedEnvelopeDisposition == disposition);

            var cryptographerB = m_tester.AgentB.Cryptographer;

            var decryptedEntity = cryptographerB
                .DecryptEntity(
                    cryptographerB.GetEncryptedBytes(encryptedEntity),
                    m_singleUseEnciphermentPublicCert);

            Console.WriteLine("Example of a signed entity: \r\n" + decryptedEntity.Body.Text.Trim());

            var signedEntityReceived = SignedEntity.Load(decryptedEntity);
            Assert.Equal(message.Body.Text.Trim(), signedEntityReceived.Content.Body.Text.Trim());

        }

        /// <summary>
        /// Sign with software (<see cref="SMIMECryptographer"/>).  Decrypt with hardware (<see cref="HsmCryptographer"/>)
        /// </summary>
        [Fact]
        public void Test_Soft_To_HSM_MultiRecipient()
        {
            string messageText = m_tester.ReadMessageText("simpleSoftToHsmMultiRecipient.eml");
            var message = MimeSerializer.Default.Deserialize<Message>(messageText);

            var signedEntity = m_cryptographerA.Sign(message, m_softSenderCertWithKeyRedomond);
            string disposition = signedEntity.Signature.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.SignatureDisposition == disposition);

            var encryptedEntity = m_cryptographerA.Encrypt(
                signedEntity.ToEntity(),
                new X509Certificate2Collection(new[]
                    {   m_singleUseEnciphermentPublicCert,
                        m_singleUseEnciphermentPublicCertCrossover
                    }));

            //Console.WriteLine("Example of an encrypted entity: \r\n" + encryptedEntity);

            disposition = encryptedEntity.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.EncryptedEnvelopeDisposition == disposition);

            var cryptographerB = m_tester.AgentB.Cryptographer;

            var decryptedEntity = cryptographerB
                .DecryptEntity(
                    cryptographerB.GetEncryptedBytes(encryptedEntity),
                    m_singleUseEnciphermentPublicCert);

            Console.WriteLine("Example of a multipart/signed entity (detached signature): \r\n" + decryptedEntity);

            var signedEntityReceived = SignedEntity.Load(decryptedEntity);
            Assert.Equal(message.Body.Text.Trim(), signedEntityReceived.Content.Body.Text.Trim());


            decryptedEntity = cryptographerB
                .DecryptEntity(
                    cryptographerB.GetEncryptedBytes(encryptedEntity),
                    m_singleUseEnciphermentPublicCertCrossover);

            var signedEntityReceived2 = SignedEntity.Load(decryptedEntity);
            Assert.Equal(message.Body.Text.Trim(), signedEntityReceived2.Content.Body.Text.Trim());

        }

        /// <summary>
        /// Sign with software (<see cref="SMIMECryptographer"/>).  Decrypt with hardware (<see cref="HsmCryptographer"/>) and software.
        /// Then also validate both signatures.
        /// </summary>
        [Fact]
        public void Test_SoftMultiSign_To_HSM_MultiRecipient()
        {
            string messageText = m_tester.ReadMessageText("simpleSoftToHsmMultiRecipient.eml");
            var message = MimeSerializer.Default.Deserialize<Message>(messageText);

            var signingCerts = new X509Certificate2Collection(new[]
            {
                m_softSenderCertWithKeyRedomond,
                m_softSenderCertWithKeyNHind  //extra unneeded signature
            });

            var signedEntity = m_cryptographerA.Sign(message, signingCerts);
            string disposition = signedEntity.Signature.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.SignatureDisposition == disposition);

            var encryptedEntity = m_cryptographerA.Encrypt(
                signedEntity.ToEntity(),
                new X509Certificate2Collection(new[]
                    {   m_singleUseEnciphermentPublicCert,
                        m_singleUseEnciphermentPublicCertCrossover
                    }));

            //Console.WriteLine("Example of an encrypted entity: \r\n" + encryptedEntity);

            disposition = encryptedEntity.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.EncryptedEnvelopeDisposition == disposition);

            //
            // Recipient #1
            //

            var cryptographerB = m_tester.AgentB.Cryptographer;

            var decryptedEntity = cryptographerB
                .DecryptEntity(
                    cryptographerB.GetEncryptedBytes(encryptedEntity),
                    m_singleUseEnciphermentPublicCert);

            Console.WriteLine("Example of a multipart/signed entity (detached signature): \r\n" + decryptedEntity);

            var signedEntityReceived = SignedEntity.Load(decryptedEntity);
            Assert.Equal(message.Body.Text.Trim(), signedEntityReceived.Content.Body.Text.Trim());

            var signatures = cryptographerB.DeserializeDetachedSignature(signedEntityReceived);
            var allSigners = signatures.SignerInfos;
            
            Assert.Equal(2, allSigners.Count);
            Assert.NotNull(allSigners.FindByName("redmond.hsgincubator.com"));
            //
            // Recipient #2
            //

            decryptedEntity = cryptographerB
                .DecryptEntity(
                    cryptographerB.GetEncryptedBytes(encryptedEntity),
                    m_singleUseEnciphermentPublicCertCrossover);

            var signedEntityReceived2 = SignedEntity.Load(decryptedEntity);
            Assert.Equal(message.Body.Text.Trim(), signedEntityReceived2.Content.Body.Text.Trim());

            signatures = cryptographerB.DeserializeDetachedSignature(signedEntityReceived2);
            allSigners = signatures.SignerInfos;

            
            Assert.Equal(2, allSigners.Count);
            Assert.NotNull(allSigners.FindByName("redmond.hsgincubator.com"));
        }

        /// <summary>
        /// Sign hardware (<see cref="HsmCryptographer"/>).  Decrypte with software (<see cref="SMIMECryptographer"/>)
        /// </summary>
        [Fact]
        public void TestDispositionHeaders_HSM_To_Soft()
        {
            var cryptographerB = m_tester.AgentB.Cryptographer;
            string messageText = m_tester.ReadMessageText("simpleHsmToSoft.eml");
            var message = MimeSerializer.Default.Deserialize<Message>(messageText);

            var signedEntity = cryptographerB.Sign(message, new X509Certificate2Collection(m_singleUseSigningPublicCert));
            string disposition = signedEntity.Signature.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.SignatureDisposition == disposition);

            var encryptedEntity = cryptographerB.Encrypt(message, m_softSenderCertWithKeyRedomond);
            disposition = encryptedEntity.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.EncryptedEnvelopeDisposition == disposition);

            var decryptedEntity = m_cryptographerA.DecryptEntity(cryptographerB.GetEncryptedBytes(encryptedEntity),
                m_softSenderCertWithKeyRedomond);

            Assert.Equal(message.Body.Text.Trim(), decryptedEntity.Body.Text.Trim());

            Console.WriteLine(message.ToString());
            Console.WriteLine(decryptedEntity.ToString());
        }


        /// <summary>
        /// Multi sign hardware (<see cref="HsmCryptographer"/>).  Decrypte with software (<see cref="SMIMECryptographer"/>)
        /// </summary>
        [Fact]
        public void TestMultiSign_HSM_to_Soft()
        {
            var cryptographerB = m_tester.AgentB.Cryptographer;
            string messageText = m_tester.ReadMessageText("simpleHsmToSoft.eml");
            var message = MimeSerializer.Default.Deserialize<Message>(messageText);

            var signingCerts = new X509Certificate2Collection(new[]
            {
                m_singleUseSigningPublicCertCrossover,  //extra unneeded signature
                m_singleUseSigningPublicCert,
            });

            var signedEntity = cryptographerB.Sign(message, signingCerts);
            string disposition = signedEntity.Signature.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.SignatureDisposition == disposition);

            var encryptedEntity = cryptographerB.Encrypt(signedEntity.ToEntity(), m_softSenderCertWithKeyRedomond);
            disposition = encryptedEntity.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.EncryptedEnvelopeDisposition == disposition);

            var decryptedEntity = m_cryptographerA.DecryptEntity(cryptographerB.GetEncryptedBytes(encryptedEntity),
                m_softSenderCertWithKeyRedomond);

            /*
            Console.WriteLine(message.ToString());
            Console.WriteLine(decryptedEntity.ToString());
            */

            var signedEntityReceived = SignedEntity.Load(decryptedEntity);
            Assert.Equal(message.Body.Text.Trim(), signedEntityReceived.Content.Body.Text.Trim());

            var signatures = cryptographerB.DeserializeDetachedSignature(signedEntityReceived);
            var allSigners = signatures.SignerInfos;

            Assert.Equal(2, allSigners.Count);
            Assert.NotNull(allSigners.FindByName("hsm.DirectInt.lab"));
            Assert.NotNull(allSigners.FindByName("fha-crossover.DirectInt.lab"));

        }

        /// <summary>
        /// Multi sign hardware (<see cref="HsmCryptographer"/>).  Decrypte with software (<see cref="SMIMECryptographer"/>)
        /// </summary>
        [Fact]
        public void TestMultiSign_HSMSoftMix_to_Soft()
        {
            var cryptographerB = m_tester.AgentB.Cryptographer;
            string messageText = m_tester.ReadMessageText("simpleHsmToSoft.eml");
            var message = MimeSerializer.Default.Deserialize<Message>(messageText);

            var signingCerts = new X509Certificate2Collection(new[]
            {
                m_singleUseSigningPublicCert,
                m_extraDualUseCertWithPrivateKey,  //extra unneeded signature
            });

            var signedEntity = cryptographerB.Sign(message, signingCerts);
            string disposition = signedEntity.Signature.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.SignatureDisposition == disposition);

            var encryptedEntity = cryptographerB.Encrypt(signedEntity.ToEntity(), m_softSenderCertWithKeyRedomond);
            disposition = encryptedEntity.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.EncryptedEnvelopeDisposition == disposition);

            var decryptedEntity = m_cryptographerA.DecryptEntity(cryptographerB.GetEncryptedBytes(encryptedEntity),
                m_softSenderCertWithKeyRedomond);

            /*
            Console.WriteLine(message.ToString());
            Console.WriteLine(decryptedEntity.ToString());
            */

            var signedEntityReceived = SignedEntity.Load(decryptedEntity);
            Assert.Equal(message.Body.Text.Trim(), signedEntityReceived.Content.Body.Text.Trim());

            var signatures = cryptographerB.DeserializeDetachedSignature(signedEntityReceived);
            var allSigners = signatures.SignerInfos;

            Assert.Equal(2, allSigners.Count);
            Assert.NotNull(allSigners.FindByName("hsm.DirectInt.lab"));
            Assert.NotNull(allSigners.FindByName("fha-crossover.DirectInt.lab"));

            foreach (var signerInfo in allSigners)
            {
                signerInfo.CheckSignature(true);
            }
        }

        [Fact]
        public void TestDispositionHeaders_HSM_To_Soft_OverrideSettingsFromProperties()
        {
            var cryptographerB = m_tester.AgentB.Cryptographer;
            cryptographerB.DefaultCryptographer = SMIMECryptographer.Default;
            Assert.Same(SMIMECryptographer.Default, cryptographerB.DefaultCryptographer);

            cryptographerB.IncludeMultipartEpilogueInSignature = true;
            Assert.Equal(true, cryptographerB.IncludeMultipartEpilogueInSignature);

            cryptographerB.IncludeCertChainInSignature = X509IncludeOption.WholeChain;
            Assert.Equal(X509IncludeOption.WholeChain, cryptographerB.IncludeCertChainInSignature);

            cryptographerB.EncryptionAlgorithm = EncryptionAlgorithm.RSA_3DES;
            Assert.Equal(EncryptionAlgorithm.RSA_3DES, cryptographerB.EncryptionAlgorithm);

            cryptographerB.DigestAlgorithm = DigestAlgorithm.SHA512;
            Assert.Equal(DigestAlgorithm.SHA512, cryptographerB.DigestAlgorithm);
        }


        /// <summary>
        /// This is the real test - does a FULL END TO END TEST using a cross product of variations
        /// Ensure test certs are installed.
        /// Invoke-psake .\default.ps1 ConfigureHSMTestdata
        /// Invoke-psake .\default.ps1 ConfigureTestdata
        /// </summary>
        [Theory]
        [MemberData(nameof(EndToEndSoftToHsmParameters))]
        public void TestEndToEnd_SoftToHsm(string fileName, EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
        {
            var mockPolicyFilterMock = new Mock<IPolicyFilter>();
            var mockPolicyResolverMock = new Mock<IPolicyResolver>();
            var parser = new SimpleTextV1LexiconPolicyParser();
            IPolicyExpression expression;

            using (var stream = ("(X509.TBS.EXTENSION.KeyUsage & 32) > 0").ToStream()) //keyEncipherment
            {
                expression = parser.Parse(stream);
            }

            mockPolicyFilterMock.Setup(
                filter => filter.IsCompliant(It.Is<X509Certificate2>(c =>
                c.FindKeyUsageExtension() != null &&
                c.FindKeyUsageExtension().KeyUsages == X509KeyUsageFlags.KeyEncipherment),
                It.IsAny<IPolicyExpression>()))
                .Returns(true);

            mockPolicyFilterMock.Setup(
                filter => filter.IsCompliant(It.Is<X509Certificate2>(c =>
                c.FindKeyUsageExtension() == null ||
                c.FindKeyUsageExtension().KeyUsages != X509KeyUsageFlags.KeyEncipherment),
                It.IsAny<IPolicyExpression>()))
                .Returns(false);

            mockPolicyResolverMock.Setup(
                resolver => resolver.GetOutgoingPolicy(new MailAddress("hobojoe@hsm.DirectInt.lab")))
                .Returns(new List<IPolicyExpression> { expression });

            var policyResolvers = new List<KeyValuePair<string, IPolicyResolver>>();
            policyResolvers.Add(new KeyValuePair<string, IPolicyResolver>(CertPolicyResolvers.PublicPolicyName, mockPolicyResolverMock.Object));
            var certPolicyResolvers = new CertPolicyResolvers(policyResolvers);

            var staticDomainResolverMock = new Mock<IDomainResolver>();

            staticDomainResolverMock.Setup(
                domainResolver => domainResolver.Domains)
                .Returns("hsm.DirectInt.lab".Split(','));

            staticDomainResolverMock.Setup(
                domainResolver => domainResolver
                .IsManaged(It.Is<string>(address =>
                    address.Contains("hsm.DirectInt.lab"))))
                .Returns(true);

            staticDomainResolverMock.Setup(
                domainResolver => domainResolver.HsmEnabled(It.IsAny<string>()))
                .Returns(true);

            var tokenSettings = TokenSettings;
            tokenSettings.DefaultDigest = digestAlgorithm;
            tokenSettings.DefaultEncryption = encryptionAlgorithm;
            var resolverSettings = MockTokenResolverSettings(tokenSettings);

            m_tester.AgentA = AgentTester.CreateAgent(
                new StaticDomainResolver(
                    AgentTester.DefaultDomainA.Split(',')),
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond"),
                    SMIMECryptographer.Default,
                    certPolicyResolvers);

            m_tester.AgentA.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
            m_tester.AgentA.Cryptographer.DigestAlgorithm = digestAlgorithm;

            var hsmCryptographer = new HsmCryptographerProxy();
            hsmCryptographer.Init(resolverSettings);

            m_tester.AgentB = AgentTester.CreateAgent(
                staticDomainResolverMock.Object,
                AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                hsmCryptographer,
                CertPolicyResolvers.Default);

            Console.WriteLine("AgentA: EncryptionAlg: {0} \t DigestAlg: {1}", m_tester.AgentA.Cryptographer.EncryptionAlgorithm, m_tester.AgentA.Cryptographer.DigestAlgorithm);
            Console.WriteLine("AgentB: EncryptionAlg: {0} \t DigestAlg: {1}", m_tester.AgentB.Cryptographer.EncryptionAlgorithm, m_tester.AgentB.Cryptographer.DigestAlgorithm);

            m_tester.TestEndToEndFile(fileName);
        }

        /// <summary>
        /// This is the real test - does a FULL END TO END TEST using a cross product of variations
        /// Note the sender has two signing certs.  One dual-use software based cert and one single-use hardware based cert.
        /// Ensure test certs are installed.
        /// Invoke-psake .\default.ps1 ConfigureHSMTestdata
        /// Invoke-psake .\default.ps1 ConfigureTestdata
        /// </summary>
        [Theory]
        [MemberData(nameof(EndToEndHsmToSoftParameters))]
        public void TestEndToEnd_HsmToSoft(string fileName, EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
        {
            var staticDomainResolverMock = new Mock<IDomainResolver>();

            staticDomainResolverMock.Setup(
                domainResolver => domainResolver.Domains)
                .Returns("fha-crossover.DirectInt.lab".Split(','));

            staticDomainResolverMock.Setup(
                domainResolver => domainResolver
                .IsManaged(It.Is<string>(address =>
                    address.Contains("fha-crossover.DirectInt.lab"))))
                .Returns(true);

            staticDomainResolverMock.Setup(
                domainResolver => domainResolver.HsmEnabled(It.IsAny<string>()))
                .Returns(true);

            var tokenSettings = TokenSettings;
            tokenSettings.DefaultDigest = digestAlgorithm;
            tokenSettings.DefaultEncryption = encryptionAlgorithm;
            var resolverSettings = MockTokenResolverSettings(tokenSettings);

            var hsmCryptographer = new HsmCryptographerProxy();
            hsmCryptographer.Init(resolverSettings);

            m_tester.AgentA = AgentTester.CreateAgent(
                staticDomainResolverMock.Object,
                AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                hsmCryptographer,
                CertPolicyResolvers.Default);

            m_tester.AgentB = AgentTester.CreateAgent(
                new StaticDomainResolver(
                    "redmond.hsgincubator.com".Split(',')),
                    AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "redmond"),
                    SMIMECryptographer.Default,
                    CertPolicyResolvers.Default);

            m_tester.AgentB.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
            m_tester.AgentB.Cryptographer.DigestAlgorithm = digestAlgorithm;

            Console.WriteLine("AgentA: EncryptionAlg: {0} \t DigestAlg: {1}", m_tester.AgentA.Cryptographer.EncryptionAlgorithm, m_tester.AgentA.Cryptographer.DigestAlgorithm);
            Console.WriteLine("AgentB: EncryptionAlg: {0} \t DigestAlg: {1}", m_tester.AgentB.Cryptographer.EncryptionAlgorithm, m_tester.AgentB.Cryptographer.DigestAlgorithm);

            m_tester.TestEndToEndFile(fileName);
        }

        [Fact]
        public void TestHardDeserializeDetatchedSignature()
        {
            var resolverSettings = MockTokenResolverSettings(TokenSettings);
            // Hardware Cryptographer Agent

            using (var hsmCryptographer = new HsmCryptographerProxy())
            {
                hsmCryptographer.Init(resolverSettings);
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

                var signedEntity = tester.AgentB.Cryptographer
                    .Sign(
                        message,
                        new X509Certificate2Collection(m_singleUseSigningPublicCert));

                //
                // SignedEntity has Content and Signature in MimeEntity formats. 
                //
                Console.WriteLine("*** Content Entity:\r\n" + signedEntity.Content.ToString());
                Console.WriteLine("*** Signature Entity:\r\n" + signedEntity.Signature.ToString());

                // Test DeserializeDetachedSignature
                var signatures = tester.AgentB.Cryptographer.DeserializeDetachedSignature(signedEntity);

                Assert.True(signatures.Detached);
                Assert.Equal(1, signatures.Certificates.Count);
                Assert.NotNull(signatures.ContentInfo.Content);
            }
        }

        /// <summary>
        /// Covering methods that are not already exercised.  
        /// Because HSMCryptographer is a plugin encapsulated by HSMCryptographerProxy some methods are not 
        /// exercised.
        /// </summary>
        [Fact]
        public void TestHsmCrypto_Explore_methods()
        {
            var resolverSettings = MockTokenResolverSettings(TokenSettings);

            var hsmCryptographer = new HsmCryptographerProxy();
            hsmCryptographer.Init(resolverSettings);
            var agentB = AgentTester.CreateAgent(
                "hsm.DirectInt.lab",
                AgentTester.MakeCertificatesPath(Directory.GetCurrentDirectory(), "nhind"),
                hsmCryptographer);

            var tester = new AgentTester(new DirectAgent(AgentTester.DefaultDomainA), agentB);
            string messageText = tester.ReadMessageText("simpleHsmToSoft.eml");
            var message = MimeSerializer.Default.Deserialize<Message>(messageText);

            var encryptedEntity = tester.AgentB.Cryptographer
                .Encrypt(
                    message,
                    new X509Certificate2Collection(m_singleUseEnciphermentPublicCert));

            string disposition = encryptedEntity.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.EncryptedEnvelopeDisposition == disposition);
        }

        //
        // Technology discovery tests.
        //

        [Fact]
        public void TestEncryptDecrypt_PureCMS_HsmPublicPrivateKeys_With_OAEP()
        {
            var pkcs11 = new Pkcs11(TokenSettings.Pkcs11LibraryPath, TokenSettings.UseOsLocking);
            var slot = Pkcs11Util.FindSlot(pkcs11, TokenSettings);

            if (slot == null)
                throw new ArgumentNullException(nameof(slot));

            using (var session = slot.OpenSession(true))
            {
                session.Login(CKU.CKU_USER, TokenSettings.NormalUserPin);

                //Get cert
                var x509CertificateParser = new X509CertificateParser();
                var x509Certificate = x509CertificateParser.ReadCertificate(m_singleUseEnciphermentPublicCert.RawData);

                var pubKeyParams = x509Certificate.GetPublicKey(); //AsymmetricKeyParameter
                if (!(pubKeyParams is RsaKeyParameters))
                    throw new NotSupportedException("Unsupported keys.  Currently supporting RSA keys only.");

                var rsaPubKeyParams = (RsaKeyParameters)pubKeyParams;

                //Correlate with HSM
                var publicKeySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PUBLIC_KEY),
                    new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                    new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                    new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
                };

                var publicKey = session.FindAllObjects(publicKeySearchTemplate);

                CkRsaPkcsOaepParams mechanismParams = new CkRsaPkcsOaepParams((ulong)CKM.CKM_SHA_1, (ulong)CKG.CKG_MGF1_SHA1, (ulong)CKZ.CKZ_DATA_SPECIFIED, null);

                // Specify encryption mechanism with parameters
                Mechanism mechanism = new Mechanism(CKM.CKM_RSA_PKCS_OAEP, mechanismParams);

                byte[] sourceData = ConvertUtils.Utf8StringToBytes("Hello world");

                // Encrypt data
                byte[] encryptedData = session.Encrypt(mechanism, publicKey.First(), sourceData);


                var privKeySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                    new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                    new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                    new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
                };

                var privateKey = session.FindAllObjects(privKeySearchTemplate);


                // Decrypt data
                byte[] decryptedData = session.Decrypt(mechanism, privateKey.First(), encryptedData);

                Console.WriteLine(Encoding.UTF8.GetString(decryptedData));

                session.Logout();
            }
        }

        [Fact]
        public void TestEncryptDecrypt_HsmPublicPrivateKeys_With_RSASimple()
        {
            var pkcs11 = new Pkcs11(TokenSettings.Pkcs11LibraryPath, TokenSettings.UseOsLocking);
            var slot = Pkcs11Util.FindSlot(pkcs11, TokenSettings);

            if (slot == null)
                throw new ArgumentNullException(nameof(slot));

            using (var session = slot.OpenSession(true))
            {
                session.Login(CKU.CKU_USER, TokenSettings.NormalUserPin);

                //Get cert
                var x509CertificateParser = new X509CertificateParser();
                var x509Certificate = x509CertificateParser.ReadCertificate(m_singleUseEnciphermentPublicCert.RawData);

                var pubKeyParams = x509Certificate.GetPublicKey(); //AsymmetricKeyParameter
                if (!(pubKeyParams is RsaKeyParameters))
                    throw new NotSupportedException("Unsupported keys.  Currently supporting RSA keys only.");

                var rsaPubKeyParams = (RsaKeyParameters)pubKeyParams;

                //Correlate with HSM
                var publicKeySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PUBLIC_KEY),
                    new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                    new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                    new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
                };

                var publicKey = session.FindAllObjects(publicKeySearchTemplate);

                // Specify encryption mechanism with parameters
                Mechanism mechanism = new Mechanism(CKM.CKM_RSA_PKCS);

                byte[] sourceData = ConvertUtils.Utf8StringToBytes("Hello world");

                // Encrypt data
                byte[] encryptedData = session.Encrypt(mechanism, publicKey.First(), sourceData);


                var privKeySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                    new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                    new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                    new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
                };

                var privateKey = session.FindAllObjects(privKeySearchTemplate);


                // Decrypt data
                byte[] decryptedData = session.Decrypt(mechanism, privateKey.First(), encryptedData);

                Console.WriteLine(Encoding.UTF8.GetString(decryptedData));

                session.Logout();
            }
        }

        [Fact]
        public void TestEncryptDecrypt_SignedPublicCertandHsmPrivateKeys()
        {
            var pkcs11 = new Pkcs11(TokenSettings.Pkcs11LibraryPath, TokenSettings.UseOsLocking);
            var slot = Pkcs11Util.FindSlot(pkcs11, TokenSettings);

            if (slot == null)
                throw new ArgumentNullException(nameof(slot));

            using (var session = slot.OpenSession(true))
            {
                session.Login(CKU.CKU_USER, TokenSettings.NormalUserPin);

                //Get cert
                var x509CertificateParser = new X509CertificateParser();
                var x509Certificate = x509CertificateParser.ReadCertificate(m_singleUseEnciphermentPublicCert.RawData);

                var pubKeyParams = x509Certificate.GetPublicKey(); //AsymmetricKeyParameter

                if (!(pubKeyParams is RsaKeyParameters))
                    throw new NotSupportedException("Unsupported keys.  Currently supporting RSA keys only.");

                var rsaPubKeyParams = (RsaKeyParameters)pubKeyParams;

                byte[] sourceData = ConvertUtils.Utf8StringToBytes("Hello world");
                var rsaProvider = (RSACryptoServiceProvider)m_singleUseEnciphermentPublicCert.PublicKey.Key;

                // Encrypt data
                var encryptedData = rsaProvider.Encrypt(sourceData, false);

                var privKeySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                    new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                    new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                    new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
                };

                var privateKey = session.FindAllObjects(privKeySearchTemplate);

                Assert.NotNull(privateKey);
                Assert.NotNull(privateKey.First());


                // Specify encryption mechanism with parameters
                var mechanism = new Mechanism(CKM.CKM_RSA_PKCS);

                // Decrypt data
                byte[] decryptedData = session.Decrypt(mechanism, privateKey.First(), encryptedData);

                Console.WriteLine(Encoding.UTF8.GetString(decryptedData));

                session.Logout();
            }
        }

        [Fact]
        public void TestEncryptDecrypt_SignedPublicX509CertandHsmPrivateKeys_With_OAEP()
        {
            var pkcs11 = new Pkcs11(TokenSettings.Pkcs11LibraryPath, TokenSettings.UseOsLocking);
            var slot = Pkcs11Util.FindSlot(pkcs11, TokenSettings);

            if (slot == null)
                throw new ArgumentNullException(nameof(slot));

            using (var session = slot.OpenSession(true))
            {
                session.Login(CKU.CKU_USER, TokenSettings.NormalUserPin);

                //Get cert
                var x509CertificateParser = new X509CertificateParser();
                var x509Certificate = x509CertificateParser.ReadCertificate(m_singleUseEnciphermentPublicCert.RawData);

                var pubKeyParams = x509Certificate.GetPublicKey(); //AsymmetricKeyParameter

                if (!(pubKeyParams is RsaKeyParameters))
                    throw new NotSupportedException("Unsupported keys.  Currently supporting RSA keys only.");

                var rsaPubKeyParams = (RsaKeyParameters)pubKeyParams;

                byte[] sourceData = ConvertUtils.Utf8StringToBytes("Hello world");
                var rsaProvider = (RSACryptoServiceProvider)m_singleUseEnciphermentPublicCert.PublicKey.Key;

                // Encrypt data
                var encryptedData = rsaProvider.Encrypt(sourceData, true);

                var privKeySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                    new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                    new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                    new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
                };

                var privateKey = session.FindAllObjects(privKeySearchTemplate);

                Assert.NotNull(privateKey);
                Assert.NotNull(privateKey.First());

                var mechanismParams = new CkRsaPkcsOaepParams(
                    (ulong)CKM.CKM_SHA_1,
                    (ulong)CKG.CKG_MGF1_SHA1,
                    (ulong)CKZ.CKZ_DATA_SPECIFIED, null);

                // Specify encryption mechanism with parameters
                var mechanism = new Mechanism(CKM.CKM_RSA_PKCS_OAEP, mechanismParams);

                // Decrypt data
                byte[] decryptedData = session.Decrypt(mechanism, privateKey.First(), encryptedData);

                Console.WriteLine(Encoding.UTF8.GetString(decryptedData));

                session.Logout();
            }
        }

        [Fact]
        public void TestEncryptDecrypt_SignedPublicX509CertandHsmPrivateKeys_With_OAEP_As_CMS()
        {
            var pkcs11 = new Pkcs11(TokenSettings.Pkcs11LibraryPath, TokenSettings.UseOsLocking);
            var slot = Pkcs11Util.FindSlot(pkcs11, TokenSettings);

            if (slot == null)
                throw new ArgumentNullException(nameof(slot));

            using (var session = slot.OpenSession(true))
            {
                session.Login(CKU.CKU_USER, TokenSettings.NormalUserPin);

                //Get BouncyCastle cert from System.Security.Cryptography.X509Certificates.X509Certificate2
                var x509CertificateParser = new X509CertificateParser();
                var x509Certificate = x509CertificateParser.ReadCertificate(m_singleUseEnciphermentPublicCert.RawData);
                Console.WriteLine(x509Certificate.SerialNumber);
                var pubKeyParams = x509Certificate.GetPublicKey(); //AsymmetricKeyParameter

                if (!(pubKeyParams is RsaKeyParameters))
                    throw new NotSupportedException("Unsupported keys.  Currently supporting RSA keys only.");

                var rsaPubKeyParams = (RsaKeyParameters)pubKeyParams;

                byte[] sourceData = ConvertUtils.Utf8StringToBytes("Hello world");


                var certs = new X509Certificate2Collection(m_singleUseEnciphermentPublicCert);
                var recipients = new CmsRecipientCollection(SubjectIdentifierType.IssuerAndSerialNumber, certs);

                //
                // SMIMECryptographerBase.CryptoOids.ContentType_Data = 1.2.840.113549.1.7.1
                //      1.2.840.113549.1.7 = iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) 7
                //      .1                 = Data

                // EncryptionAlgorithm.AES128 = 2.16.840.1.101.3.4.1.2
                //
                var content = new System.Security.Cryptography.Pkcs.ContentInfo(SMIMECryptographerBase.CryptoOids.ContentType_Data, sourceData);
                var dataEnvelope = new EnvelopedCms(content, SMIMECryptographerBase.ToAlgorithmID(EncryptionAlgorithm.AES128));
                // Encrypt data
                dataEnvelope.Encrypt(recipients);
                var encryptedData = dataEnvelope.Encode();


                var privKeySearchTemplate = new List<ObjectAttribute>
                {
                    new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                    new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                    new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                    new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
                };

                var privateKey = session.FindAllObjects(privKeySearchTemplate);

                Assert.NotNull(privateKey);
                Assert.NotNull(privateKey.First());

                var mechanismParams = new CkRsaPkcsOaepParams(
                    (ulong)CKM.CKM_SHA_1,
                    (ulong)CKG.CKG_MGF1_SHA1,
                    (ulong)CKZ.CKZ_DATA_SPECIFIED, null);

                // Specify encryption mechanism with parameters
                var mechanism = new Mechanism(CKM.CKM_RSA_PKCS_OAEP, mechanismParams);



                //
                // Troubleshooting data
                //
                EnvelopedCms envelopedCms = new EnvelopedCms();
                //  Decode the message.
                envelopedCms.Decode(encryptedData);
                var rsaEncryptKeyFromCMS = envelopedCms.RecipientInfos[0].EncryptedKey;
                //  Display the number of recipients the message is
                //  enveloped for; it should be 1 for this example.
                DisplayEnvelopedCms(envelopedCms, true);


                //
                // BouncyCastle CmsEnvelopedData
                //
                var envelopedData = new CmsEnvelopedData(encryptedData);
                EnvelopedData envData = EnvelopedData.GetInstance(envelopedData.ContentInfo.Content);
                var recip = Org.BouncyCastle.Asn1.Cms.RecipientInfo.GetInstance((Asn1Sequence)envData.RecipientInfos[0]);
                var keyTransRecipientInfo = Org.BouncyCastle.Asn1.Cms.KeyTransRecipientInfo.GetInstance(recip.Info);
                var rsaEncryptKey = keyTransRecipientInfo.EncryptedKey.GetOctets();

                //
                // Show EncryptedKey is the equivalent from both .NET and BouncyCastle parsers.
                //
                Assert.Equal(
                    Convert.ToBase64String(rsaEncryptKeyFromCMS),
                    Convert.ToBase64String(rsaEncryptKey));



                // Decrypt data
                byte[] decryptedData = session.Decrypt(mechanism, privateKey.First(), rsaEncryptKey);

                Console.WriteLine(Encoding.UTF8.GetString(decryptedData));

                session.Logout();
            }
        }

        //  Display some properties of an EnvelopedCms object.
        private static void DisplayEnvelopedCms(EnvelopedCms e,
            Boolean displayContent)
        {
            Console.WriteLine("\nEnveloped CMS/PKCS #7 Message " +
                "Information:");
            Console.WriteLine(
                "\tThe number of recipients for the Enveloped CMS/PKCS " +
                "#7 is: {0}", e.RecipientInfos.Count);
            for (int i = 0; i < e.RecipientInfos.Count; i++)
            {
                Console.WriteLine(
                    "\tRecipient #{0} has type {1}.",
                    i + 1,
                    e.RecipientInfos[i].RecipientIdentifier.Type);

                //Console.WriteLine(
                //    "\tRecipient #{0} has EncryptedKey(base64) {1}.",
                //    i + 1,
                //    Convert.ToBase64String(e.RecipientInfos[i].EncryptedKey));

                Console.WriteLine(
                    "\tRecipient #{0} has OID {1}.",
                    i + 1,
                    e.RecipientInfos[i].KeyEncryptionAlgorithm.Oid.Value);

                Console.WriteLine(
                    "\tRecipient #{0} has Paramters {1}.",
                    i + 1,
                    Convert.ToBase64String(e.RecipientInfos[i].KeyEncryptionAlgorithm.Parameters));
            }
            if (displayContent)
            {
                DisplayEnvelopedCmsContent("Enveloped CMS/PKCS " +
                    "#7 Content", e);
            }
            Console.WriteLine();
        }

        private static void DisplayEnvelopedCmsContent(String desc,
           EnvelopedCms envelopedCms)
        {
            Console.WriteLine(desc + " (length {0}):  ",
                envelopedCms.ContentInfo.Content.Length);
            foreach (byte b in envelopedCms.ContentInfo.Content)
            {
                Console.Write(b.ToString() + " ");
            }
            Console.WriteLine();
        }

        // this allows us to easily iterate over the cross product between
        // EncryptionAlgorithm x DigestAlgorithm x EndToEndSoftToHsmFiles
        public static IEnumerable<object[]> EndToEndSoftToHsmParameters
        {
            get
            {
                foreach (string fileName in EndToEndSoftToHsmFiles)
                {
                    foreach (EncryptionAlgorithm encAlgo in Enum.GetValues(typeof(EncryptionAlgorithm)))
                    {
                        foreach (DigestAlgorithm digAlgo in Enum.GetValues(typeof(DigestAlgorithm)))
                        {
                            //yield return new object[] { fileName, EncryptionAlgorithm.AES128, DigestAlgorithm.SHA1 };
                            yield return new object[] { fileName, encAlgo, digAlgo };
                        }
                    }
                }
            }
        }

        private static IEnumerable<string> EndToEndSoftToHsmFiles
        {
            get
            {
                yield return "simpleSoftToHsm.eml";
            }
        }

        // this allows us to easily iterate over the cross product between
        // EncryptionAlgorithm x DigestAlgorithm x EndToEndSoftToHsmFiles
        public static IEnumerable<object[]> EndToEndHsmToSoftParameters
        {
            get
            {
                foreach (string fileName in EndToEndHsmToSoftFiles)
                {
                    foreach (EncryptionAlgorithm encAlgo in Enum.GetValues(typeof(EncryptionAlgorithm)))
                    {
                        foreach (DigestAlgorithm digAlgo in Enum.GetValues(typeof(DigestAlgorithm)))
                        {
                            //yield return new object[] { fileName, EncryptionAlgorithm.AES128, DigestAlgorithm.SHA1 };
                            yield return new object[] { fileName, encAlgo, digAlgo };
                        }
                    }
                }
            }
        }

        private static IEnumerable<string> EndToEndHsmToSoftFiles
        {
            get
            {
                yield return "simpleHsmToSoft.eml";
            }
        }

        public void Dispose()
        {
            if (m_tester.AgentB.Cryptographer is HsmCryptographerProxy)
            {
                ((HsmCryptographerProxy)m_tester.AgentB.Cryptographer).Dispose();
            }

            if (m_tester.AgentA.Cryptographer is HsmCryptographerProxy)
            {
                ((HsmCryptographerProxy)m_tester.AgentA.Cryptographer).Dispose();
            }
        }
    }
}
