/* 
 Copyright (c) 2013, Direct Project
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
using System.Linq;
using System.Text;
using Xunit;
using Xunit.Extensions;
using System.Net.Mime;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Cryptography;

namespace Health.Direct.Agent.Tests
{
    /// <summary>
    /// The SMIMECryptographer actually gets a good workout through BasicAgentTests and elswhere
    /// Here, we add SMIME specific boudary case validiation
    /// </summary>
    public class CryptographerTests
    {
        readonly AgentTester m_tester;
        X509Certificate2 m_cert;
        SMIMECryptographer m_cryptographer;

        static CryptographerTests()
        {
            AgentTester.EnsureStandardMachineStores();
        }

        public CryptographerTests()
        {
            m_tester = AgentTester.CreateTest();
            m_cryptographer = m_tester.AgentA.Cryptographer;
            MemoryX509Store certs = AgentTester.LoadPrivateCerts("redmond");
            m_cert = certs.First();
        }

        public static IEnumerable<object[]> DigestAlgorithms
        {
            get
            {
                foreach (DigestAlgorithm algo in Enum.GetValues(typeof(DigestAlgorithm)))
                {
                    yield return new object[] { algo};
                }
            }
        }

        [Theory]
        [PropertyData("DigestAlgorithms")]
        public void TestDigestMicalgParameter(DigestAlgorithm algo)
        {
            ContentType type = SignedEntity.CreateContentType(algo);
            Assert.True(type.Parameters["micalg"] == SMIMEStandard.ToString(algo));
        }

        [Theory]
        [PropertyData("DigestAlgorithms")]
        public void TestSignatureOIDs(DigestAlgorithm algo)
        {
            string messageText = m_tester.ReadMessageText("simple.eml");
            m_cryptographer.DigestAlgorithm = algo;
            SignedCms signedData = null;
            
            Assert.DoesNotThrow(() => signedData = m_cryptographer.CreateSignature(Encoding.ASCII.GetBytes(messageText), m_cert)); 
            
            Assert.True(signedData.SignerInfos.Count == 1);
            Assert.True(signedData.SignerInfos[0].DigestAlgorithm.Value == SMIMECryptographer.ToDigestAlgorithmOid(algo).Value);
        }

        [Fact]
        public void TestDispositionHeaders()
        {
            string messageText = m_tester.ReadMessageText("simple.eml");
            Message message = MimeSerializer.Default.Deserialize<Message>(messageText);

            SignedEntity signedEntity = m_cryptographer.Sign(message, m_cert);
            string disposition = signedEntity.Signature.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.SignatureDisposition == disposition);

            MimeEntity encryptedEntity = m_cryptographer.Encrypt(message, m_cert);
            disposition = encryptedEntity.ContentDisposition;
            Assert.True(!string.IsNullOrEmpty(disposition));
            Assert.True(SMIMEStandard.EncryptedEnvelopeDisposition == disposition);
        }
    }
}
