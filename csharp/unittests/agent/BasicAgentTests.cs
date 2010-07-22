using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using NHINDirect.Agent;
using NHINDirect.Cryptography;

namespace AgentTests
{
    [TestFixture]
    public class BasicAgentTests
    {
        static string[] EndToEndFiles = new string[]
        {
            "simple.eml",
            "multipart_1to.eml",
            "multipart_2to.eml",
        };
        
        static string[] IncomingFiles = new string[]
        {
            "envelopeSignature.eml"
        };

        static string[] OutgoingUntrusted = new string[]
        {
            "Outgoing\\untrusted_1.eml",
        };

        static string[] OutgoingUntrustedFully = new string[]
        {
            "Outgoing\\fully_untrusted_1.eml",
        };
        
        AgentTester m_tester;
        
        public BasicAgentTests()
        {
        }
        
        [SetUp]
        public void Init()
        {
            m_tester = AgentTester.CreateTest();
        }
        
        [Test]
        public void TestEndToTend()
        {
            TestEndToEnd(EncryptionAlgorithm.RSA_3DES, DigestAlgorithm.SHA1);
            TestEndToEnd(EncryptionAlgorithm.RSA_3DES, DigestAlgorithm.SHA256);
            TestEndToEnd(EncryptionAlgorithm.RSA_3DES, DigestAlgorithm.SHA512);

            TestEndToEnd(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA1);
            TestEndToEnd(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA256);
            TestEndToEnd(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA512);

            TestEndToEnd(EncryptionAlgorithm.AES192, DigestAlgorithm.SHA1);
            TestEndToEnd(EncryptionAlgorithm.AES192, DigestAlgorithm.SHA256);
            TestEndToEnd(EncryptionAlgorithm.AES192, DigestAlgorithm.SHA512);

            TestEndToEnd(EncryptionAlgorithm.AES256, DigestAlgorithm.SHA1);
            TestEndToEnd(EncryptionAlgorithm.AES256, DigestAlgorithm.SHA256);
            TestEndToEnd(EncryptionAlgorithm.AES256, DigestAlgorithm.SHA512);
        }

        [Test]
        public void TestEndToEndDefault()
        {
            TestEndToEnd(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA1);
        }
        
        [Test]
        public void TestIncoming()
        {
            m_tester.AgentA.Cryptographer.EncryptionAlgorithm = EncryptionAlgorithm.AES128;
            m_tester.AgentA.Cryptographer.DigestAlgorithm = DigestAlgorithm.SHA1;

            foreach (string fileName in IncomingFiles)
            {
                Assert.DoesNotThrow(() => m_tester.TestIncomingFile(fileName), fileName);
            }
        }
        
        [Test]
        public void TestOutgoingUntrusted()
        {
            m_tester.AgentA.Cryptographer.EncryptionAlgorithm = EncryptionAlgorithm.AES128;
            m_tester.AgentA.Cryptographer.DigestAlgorithm = DigestAlgorithm.SHA1;

            foreach (string fileName in OutgoingUntrustedFully)
            {
                Assert.Throws<AgentException>(() => m_tester.TestOutgoingFile(fileName), fileName);
            }

            OutgoingMessage outgoing;
            foreach (string fileName in OutgoingUntrusted)
            {
                outgoing = null;
                try
                {
                    outgoing = m_tester.CreateOutgoingFile(fileName);
                    Assert.True(outgoing.RejectedRecipients.Count > 0);
                }
                catch
                {
                    Assert.Fail(fileName);
                }
            }            
        }
        
        void TestEndToEnd(EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
        {
            m_tester.AgentA.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
            m_tester.AgentA.Cryptographer.DigestAlgorithm = digestAlgorithm;
            m_tester.AgentB.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
            m_tester.AgentB.Cryptographer.DigestAlgorithm = digestAlgorithm;
            foreach (string fileName in EndToEndFiles)
            {
                Assert.DoesNotThrow(() => m_tester.TestEndToEndFile(fileName), fileName);
            }
        }

        void TestEndToEndFail(EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
        {
            m_tester.AgentA.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
            m_tester.AgentA.Cryptographer.DigestAlgorithm = digestAlgorithm;
            m_tester.AgentB.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
            m_tester.AgentB.Cryptographer.DigestAlgorithm = digestAlgorithm;
            foreach (string fileName in EndToEndFiles)
            {
                Assert.DoesNotThrow(() => m_tester.TestEndToEndFile(fileName), fileName);
            }
        }
    }
    /*
    [TestFixture]
    public class DebugTest
    {
        static string[] FileList = new string[]
        {
            "multipart_1to.eml",
        };

        AgentTester m_tester;

        public DebugTest()
        {
        }

        [SetUp]
        public void Init()
        {
            m_tester = AgentTester.CreateDefault();
        }

        [Test]
        public void TestEndToTend()
        {
            foreach (string fileName in FileList)
            {
                Assert.DoesNotThrow(() => m_tester.TestEndToEndFile(fileName), fileName);
            }
        }
    }
     */
}
