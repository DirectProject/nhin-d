using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using NHINDirect.Agent;
using NHINDirect.Cryptography;
using NHINDirect.Mail;
using NHINDirect.Mime;

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
        
        /// <summary>
        /// Basic End to End Test
        ///  ProcessIncoming(ProcessOutgoing(...))
        /// </summary>
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
                Assert.DoesNotThrow(() => m_tester.ProcessIncomingFile(fileName), fileName);
            }
        }
        
        /// <summary>
        /// Outgoing messages with Untrusted Recipients
        /// Test if the agent catches them
        /// </summary>
        [Test]
        public void TestOutgoingUntrusted()
        {
            m_tester.AgentA.Cryptographer.EncryptionAlgorithm = EncryptionAlgorithm.AES128;
            m_tester.AgentA.Cryptographer.DigestAlgorithm = DigestAlgorithm.SHA1;
            //
            // All recipients are untrusted. The agent should reject the message completely
            //
            foreach (string fileName in OutgoingUntrustedFully)
            {
                Assert.Throws<AgentException>(() => m_tester.ProcessOutgoingFileToString(fileName), fileName);
            }
            //
            // Some recipients are untrusted. The agent should return > 1 rejected recipients
            //
            OutgoingMessage outgoing;
            foreach (string fileName in OutgoingUntrusted)
            {
                outgoing = null;
                try
                {
                    outgoing = m_tester.ProcessOutgoingFile(fileName);
                    Assert.True(outgoing.RejectedRecipients.Count > 0);
                }
                catch
                {
                    Assert.Fail(fileName);
                }
            }            
        }

        /// <summary>
        /// The Agent has methods that allow for you to construct OutgoingMessage/IncomingMessage directly
        /// </summary>
        [Test]
        public void TestWithMessageObjects()
        {
            Message message = MimeSerializer.Default.Deserialize<Message>(m_tester.ReadMessageText("simple.eml"));
            
            OutgoingMessage outgoing = new OutgoingMessage(message);                        
            outgoing = m_tester.AgentA.ProcessOutgoing(outgoing);
            
            Assert.IsTrue(SMIMEStandard.IsEncrypted(outgoing.Message));
            this.VerifyTrusted(outgoing.Recipients, m_tester.AgentA.MinTrustRequirement);
            Assert.IsTrue(outgoing.RejectedRecipients.Count == 0);
            
            IncomingMessage incoming = new IncomingMessage(outgoing.Message);
            incoming = m_tester.AgentB.ProcessIncoming(incoming);
            
            Assert.IsTrue(!SMIMEStandard.IsEncrypted(incoming.Message));
            Assert.IsTrue(!WrappedMessage.IsWrapped(incoming.Message));
            this.VerifyTrusted(incoming.Recipients, m_tester.AgentB.MinTrustRequirement);
            Assert.IsTrue(outgoing.RejectedRecipients.Count == 0);
        }
        
        [Test]
        public void TestIntegrations()
        {
            foreach(string fileName in EndToEndFiles)
            {
                bool isIncoming = false;
                
                MessageEnvelope envelope = m_tester.AgentA.Process(m_tester.ReadMessageText(fileName), ref isIncoming);
                Assert.IsFalse(isIncoming);
                this.VerifyTrusted(envelope.Recipients, m_tester.AgentA.MinTrustRequirement);
                
                string outgoingText = envelope.SerializeMessage();
                envelope = m_tester.AgentB.Process(outgoingText, ref isIncoming);
                Assert.IsTrue(isIncoming);
                Assert.IsTrue(!SMIMEStandard.IsEncrypted(envelope.Message));
                
                this.VerifyTrusted(envelope.Recipients, m_tester.AgentB.MinTrustRequirement);
                
                string incomingText = envelope.SerializeMessage();
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

        void VerifyTrusted(NHINDAddress address, TrustEnforcementStatus minStatus)
        {
            Assert.IsTrue(address.IsTrusted(minStatus));
        }

        void VerifyTrusted(NHINDAddressCollection addresses, TrustEnforcementStatus minStatus)
        {
            foreach(NHINDAddress address in addresses)
            {
                this.VerifyTrusted(address, minStatus);
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
