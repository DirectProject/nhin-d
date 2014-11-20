/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Domains;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Agent.Tests
{
    public class BasicAgentTests
    {
        readonly AgentTester m_tester;
        
        static BasicAgentTests()
        {
            AgentTester.EnsureStandardMachineStores();
        }
        
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

        [Theory]
        [PropertyData("OutgoingFiles")]
        public void TestOutgoing(string fileName)
        {
            m_tester.AgentA.Cryptographer.EncryptionAlgorithm = EncryptionAlgorithm.AES128;
            m_tester.AgentA.Cryptographer.DigestAlgorithm = DigestAlgorithm.SHA1;            
            
            OutgoingMessage message = null;            
            Assert.DoesNotThrow(() => message = m_tester.ProcessOutgoingFile(fileName));
            Assert.NotNull(message);
            Assert.NotNull(message.Message);
            
            byte[] encryptedBytes = null;
            Assert.DoesNotThrow(() => encryptedBytes = m_tester.AgentA.Cryptographer.GetEncryptedBytes(message.Message));
            Assert.NotNull(encryptedBytes);
            Assert.True(encryptedBytes.Length > 0);
            
            encryptedBytes = null;
            message.Message.ContentType = SMIMEStandard.SignatureContentMediaType;    
            Assert.Throws<EncryptionException>(() => m_tester.AgentA.Cryptographer.GetEncryptedBytes(message.Message));
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
            Assert.Throws<OutgoingAgentException>(() => m_tester.ProcessOutgoingFileToString(fileName));
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
                       
            Assert.True(outgoing.Message.HasHeader(MailStandard.Headers.Date));

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
        
        /// <summary>
        /// This is the real test - does a FULL END TO END TEST using a cross product of variations
        /// </summary>
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
        
        /// <summary>
        /// Similar to TestEndToEnd, but we let the receiving agent run with Default settings
        /// </summary>
        [Theory]
        [PropertyData("EndToEndParameters")]
        public void TestEndToEndCompat(string fileName, EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
        {
            m_tester.AgentA.Cryptographer.EncryptionAlgorithm = encryptionAlgorithm;
            m_tester.AgentA.Cryptographer.DigestAlgorithm = digestAlgorithm;
            m_tester.TestEndToEndFile(fileName);
        }
        
        [Theory]
        [PropertyData("DecryptionFailureFiles")]
        public void TestDecryptionFailure(string filename)
        {
            //
            // Return the wrong certificate, forcing decryption to fail
            //
            DirectAgent baseAgent = m_tester.AgentB;
            DirectAgent badAgent = new DirectAgent(new StaticDomainResolver(baseAgent.Domains.Domains.ToArray()), 
                                                 new BadCertResolver(m_tester.AgentA.PrivateCertResolver, baseAgent.PrivateCertResolver, false),  // returns the wrong private certs
                                                 baseAgent.PublicCertResolver, 
                                                 baseAgent.TrustAnchors);
            m_tester.AgentB = badAgent;
            Assert.Throws<AgentException>(() => m_tester.TestEndToEndFile(filename));
            //
            // Now, it returns BOTH wrong and right certs, so decryption should eventually succeed
            //
            badAgent = new DirectAgent(new StaticDomainResolver(baseAgent.Domains.Domains.ToArray()),
                                      new BadCertResolver(m_tester.AgentA.PrivateCertResolver, baseAgent.PrivateCertResolver, true),  // returns both wrong and right certs
                                      baseAgent.PublicCertResolver,
                                      baseAgent.TrustAnchors);
            m_tester.AgentB = badAgent;
            Assert.DoesNotThrow(() => m_tester.TestEndToEndFile(filename));
        }
        
        [Theory]
        [PropertyData("ManInMiddleParams")]
        public void TestManInMiddle(string fileName, string injectedWithPersonalKey, string injectedWithOrgKey)
        {
            //
            // Take an encrypted message, add additional recipients
            //
            OutgoingMessage outgoing = m_tester.ProcessOutgoingFile(fileName);
            string message = outgoing.SerializeMessage();
            IncomingMessage incoming = new IncomingMessage(message, outgoing.Recipients, outgoing.Sender);
            Assert.DoesNotThrow(() => m_tester.AgentB.ProcessIncoming(incoming));
            //
            // Add additional recipient not in original message
            //
            DirectAddressCollection recipients = new DirectAddressCollection(outgoing.Recipients);
            recipients.Add(new DirectAddress(injectedWithPersonalKey));            
            incoming = new IncomingMessage(message, recipients, outgoing.Sender);
            Assert.Throws<AgentException>(() => m_tester.AgentB.ProcessIncoming(incoming));
            //
            // Replace recipient entirely
            //
            recipients.Clear();
            recipients.Add(new DirectAddress(injectedWithPersonalKey));
            incoming = new IncomingMessage(message, recipients, outgoing.Sender);
            Assert.Throws<AgentException>(() => m_tester.AgentB.ProcessIncoming(incoming));            
            //
            // Inject recipient with org certs
            //
            recipients.Clear();
            recipients.Add(outgoing.Recipients);
            recipients.Add(new DirectAddress(injectedWithOrgKey));
            incoming = new IncomingMessage(message, recipients, outgoing.Sender);
            Assert.Throws<AgentException>(() => m_tester.AgentB.ProcessIncoming(incoming));
        }
        
        [Fact]
        public void TestPrivateKeyLookupFailure()
        {
            string fileName = "simple.eml";            
            string messageText = m_tester.ReadMessageText(fileName);
            
            Message message = MailParser.ParseMessage(messageText);
            message.ToValue = "foo@bar.com";  // this should force a key lookup failure            
            AgentTester.CheckErrorCode<AgentException, AgentError>(
                () => m_tester.AgentA.ProcessOutgoing(new OutgoingMessage(message)),
                AgentError.NoTrustedRecipients
            );

            DirectAgent badAgent = new DirectAgent(new StaticDomainResolver(m_tester.AgentA.Domains.Domains.ToArray()),
                                                 new NullResolver(),  // returns null private certs
                                                 m_tester.AgentA.PublicCertResolver,
                                                 m_tester.AgentA.TrustAnchors);

            message = MailParser.ParseMessage(messageText);
            AgentTester.CheckErrorCode<AgentException, AgentError>(
                () => badAgent.ProcessOutgoing(new OutgoingMessage(message)),
                AgentError.CouldNotResolvePrivateKey
            );
            
            OutgoingMessage outgoing = m_tester.AgentA.ProcessOutgoing(messageText);

            badAgent = new DirectAgent(new StaticDomainResolver(m_tester.AgentB.Domains.Domains.ToArray()),
                                                 new NullResolver(),  // returns null private certs
                                                 m_tester.AgentB.PublicCertResolver,
                                                 m_tester.AgentB.TrustAnchors);

            AgentTester.CheckErrorCode<AgentException, AgentError>(
                () => badAgent.ProcessIncoming(outgoing.SerializeMessage()),
                AgentError.CouldNotResolvePrivateKey
            );

            badAgent = new DirectAgent(new StaticDomainResolver(m_tester.AgentB.Domains.Domains.ToArray()),
                                                 new NullResolver(true),  // returns empty private cert collections
                                                 m_tester.AgentB.PublicCertResolver,
                                                 m_tester.AgentB.TrustAnchors);

            AgentTester.CheckErrorCode<AgentException, AgentError>(
                () => badAgent.ProcessIncoming(outgoing.SerializeMessage()),
                AgentError.CouldNotResolvePrivateKey
            );
        }
                               
        static void VerifyTrusted(DirectAddress address, TrustEnforcementStatus minStatus)
        {
            Assert.True(address.IsTrusted(minStatus));
        }

        static void VerifyTrusted(IEnumerable<DirectAddress> addresses, TrustEnforcementStatus minStatus)
        {
            foreach (DirectAddress address in addresses)
            {
                VerifyTrusted(address, minStatus);
            }
        }

        private static IEnumerable<string> EndToEndFiles
        {
            get
            {
                yield return "simple.eml";
                yield return "multipart_1to.eml";
                yield return "multipart_2to.eml";
                yield return "Complicated.eml";
                yield return "Attachments.eml";
            }
        }

        public static IEnumerable<object[]> IncomingFiles
        {
            get
            {
                yield return new[] { "envelopeSignature.eml" };
                yield return new[] { "envelopeSignatureContentType1.eml" };
                yield return new[] { "envelopeSignatureContentType2.eml" };

            }
        }

        public static IEnumerable<object[]> OutgoingFiles
        {
            get
            {
                yield return new[] { "simple.eml" };
                yield return new[] { "multipart_2to.eml" };
            }
        }

        public static IEnumerable<object[]> DecryptionFailureFiles
        {
            get
            {
                yield return new[] { "multipart_2to.eml" };
                yield return new[] { "simple.eml" };
                yield return new[] { "multipart_1to.eml" };
            }
        }

        public static IEnumerable<object[]> OutgoingUntrustedFiles
        {
            get
            {
                yield return new[] { Path.Combine("Outgoing", "untrusted_1.eml") };
            }
        }

        public static IEnumerable<object[]> OutgoingUntrustedFullyFiles
        {
            get
            {
                yield return new[] { Path.Combine("Outgoing", "fully_untrusted_1.eml") };
            }
        }

        public static IEnumerable<object[]> ManInMiddleParams
        {
            get
            {
                yield return new object[] { "simple_ToOrg.eml", "bob@nhind.hsgincubator.com", "gussie@nhind.hsgincubator.com" };
                yield return new object[] { "simple.eml", "bob@nhind.hsgincubator.com", "bertie@nhind.hsgincubator.com" };
                yield return new object[] { "multipart_1to.eml", "bob@nhind.hsgincubator.com", "bertie@nhind.hsgincubator.com" };
            }
        }
    }
}