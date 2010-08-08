using System;
using System.Collections.Generic;
using System.IO;

using NHINDirect.Agent;
using NHINDirect.Cryptography;
using NHINDirect.Mail;
using NHINDirect.Mime;

using Xunit;
using Xunit.Extensions;

namespace AgentTests
{
    public class BasicAgentTests
    {
        private static IEnumerable<string> EndToEndFiles
        {
			get
			{
				yield return "simple.eml";
				yield return "multipart_1to.eml";
				yield return "multipart_2to.eml";
			}
        }
        
        public static IEnumerable<object[]> IncomingFiles
        {
			get
			{
				yield return new[] {"envelopeSignature.eml"};
			}
        }

        public static IEnumerable<object[]> OutgoingUntrustedFiles
        {
			get
			{
				yield return new[] {Path.Combine("Outgoing", "untrusted_1.eml")};
			}
        }

        public static IEnumerable<object[]> OutgoingUntrustedFullyFiles
        {
			get
			{
				yield return new[] {Path.Combine("Outgoing", "fully_untrusted_1.eml")};
			}
        }

    	readonly AgentTester m_tester;

		public BasicAgentTests()
        {
            m_tester = AgentTester.CreateTest();
        }
        
        [Theory]
		[PropertyData("IncomingFiles")]
        public void TestIncoming(string fileName)
        {
        	m_tester.AgentA.Cryptographer.EncryptionAlgorithm = EncryptionAlgorithm.AES128;
        	m_tester.AgentA.Cryptographer.DigestAlgorithm = DigestAlgorithm.SHA1;
        	m_tester.ProcessIncomingFile(fileName);
        }

    	/// <summary>
        /// Outgoing messages with Untrusted Recipients
        /// Test if the agent catches them
        /// </summary>
        [Theory]
		[PropertyData("OutgoingUntrustedFullyFiles")]
        public void TestOutgoingUntrustedFully(string fileName)
    	{
    		m_tester.AgentA.Cryptographer.EncryptionAlgorithm = EncryptionAlgorithm.AES128;
    		m_tester.AgentA.Cryptographer.DigestAlgorithm = DigestAlgorithm.SHA1;

    		//
    		// All recipients are untrusted. The agent should reject the message completely
    		//
    		Assert.Throws<AgentException>(() => m_tester.ProcessOutgoingFileToString(fileName));
    	}


    	//
   		// Some recipients are untrusted. The agent should return > 1 rejected recipients
   		//
		[Theory]
		[PropertyData("OutgoingUntrustedFiles")]
		public void OutgoingUntrusted(string fileName)
		{
			OutgoingMessage outgoing = m_tester.ProcessOutgoingFile(fileName);
			Assert.True(outgoing.RejectedRecipients.Count > 0);
		}

    	/// <summary>
        /// The Agent has methods that allow for you to construct OutgoingMessage/IncomingMessage directly
        /// </summary>
        [Fact]
        public void TestWithMessageObjects()
        {
            var message = MimeSerializer.Default.Deserialize<Message>(m_tester.ReadMessageText("simple.eml"));
            
            var outgoing = new OutgoingMessage(message);                        
            outgoing = m_tester.AgentA.ProcessOutgoing(outgoing);
            
            Assert.True(SMIMEStandard.IsEncrypted(outgoing.Message));
            VerifyTrusted(outgoing.Recipients, m_tester.AgentA.MinTrustRequirement);
            Assert.True(outgoing.RejectedRecipients.Count == 0);
            
            var incoming = new IncomingMessage(outgoing.Message);
            incoming = m_tester.AgentB.ProcessIncoming(incoming);
            
            Assert.False(SMIMEStandard.IsEncrypted(incoming.Message));
            Assert.False(WrappedMessage.IsWrapped(incoming.Message));

            VerifyTrusted(incoming.Recipients, m_tester.AgentB.MinTrustRequirement);
            Assert.True(outgoing.RejectedRecipients.Count == 0);
        }


		// this allows us to easily iterate over the cross product between
		// EncryptionAlgorithm x DigestAlgorithm x EndToEndFiles
    	public static IEnumerable<object[]> EndToEndParameters
    	{
    		get
    		{
				foreach (string fileName in EndToEndFiles)
				{
					foreach (EncryptionAlgorithm encAlgo in Enum.GetValues(typeof(EncryptionAlgorithm)))
					{
						foreach (DigestAlgorithm digAlgo in Enum.GetValues(typeof(DigestAlgorithm)))
						{
							yield return new object[] {fileName, encAlgo, digAlgo};
						}
					}
				}
    		}
    	}

		[Theory]
		[PropertyData("EndToEndParameters")]
        public void TestEndToEnd(string fileName, EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
		{
			m_tester.AgentA.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
			m_tester.AgentA.Cryptographer.DigestAlgorithm = digestAlgorithm;
			m_tester.AgentB.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
			m_tester.AgentB.Cryptographer.DigestAlgorithm = digestAlgorithm;
			m_tester.TestEndToEndFile(fileName);
		}

		//void TestEndToEndFail(EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
		//{
		//    m_tester.AgentA.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
		//    m_tester.AgentA.Cryptographer.DigestAlgorithm = digestAlgorithm;
		//    m_tester.AgentB.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
		//    m_tester.AgentB.Cryptographer.DigestAlgorithm = digestAlgorithm;

		//    foreach (string fileName in EndToEndFiles)
		//    {
		//        m_tester.TestEndToEndFile(fileName);
		//    }
		//}

    	static void VerifyTrusted(NHINDAddress address, TrustEnforcementStatus minStatus)
        {
            Assert.True(address.IsTrusted(minStatus));
        }

    	static void VerifyTrusted(IEnumerable<NHINDAddress> addresses, TrustEnforcementStatus minStatus)
        {
            foreach (NHINDAddress address in addresses)
            {
                VerifyTrusted(address, minStatus);
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
