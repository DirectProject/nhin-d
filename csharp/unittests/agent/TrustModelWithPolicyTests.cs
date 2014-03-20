/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using FluentAssertions;
using Health.Direct.Common.Policies;
using Moq;
using Xunit;

namespace Health.Direct.Agent.Tests
{
    public class TrustModelWithPolicyTests
    {
        readonly Mock<TrustChainValidator> mockTrustChainValidator = new Mock<TrustChainValidator>();

        //DefaultPolicyFilter(ICompiler compiler, IExecutionEngine engine, IPolicyLexiconParser parser)
        
        readonly Mock<IPolicyFilter> mockPolicyFilter = new Mock<IPolicyFilter>();
        readonly Mock<IPolicyResolver> mockPolicyResolver = new Mock<IPolicyResolver>();


        [Fact]
        public void TestIsCertPolicyCompiant_NoResolver_NoFilter_AssertTrue()
        {
            TrustModel trustModel = new TrustModel(mockTrustChainValidator.Object);
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();
            trustModel.IsCertPolicyCompliant(new MailAddress("me@test.com"), mockCert.Object).Should().BeTrue();
        }

        [Fact]
        public void TestIsCertPolicyCompiant_NoPolicyExpression_AssertTrue()
        {
            TrustModel trustModel = new TrustModel(mockTrustChainValidator.Object, mockPolicyResolver.Object, mockPolicyFilter.Object);
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();

            mockPolicyResolver.Setup(resolver => resolver.GetIncomingPolicy(new MailAddress("me@test.com")))
                .Returns(new List<IPolicyExpression>());

            trustModel.IsCertPolicyCompliant(new MailAddress("me@test.com"), mockCert.Object).Should().BeTrue();
        }


        [Fact]
        public void TestIsCertPolicyCompiant_PolicyCompliant_AssertTrue()
        {
            TrustModel trustModel = new TrustModel(mockTrustChainValidator.Object, mockPolicyResolver.Object, mockPolicyFilter.Object);
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();

            mockPolicyFilter.Setup(
                filter => filter.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Returns(true);
            
            Mock<IPolicyExpression> mockExpression = new Mock<IPolicyExpression>();

            mockPolicyResolver.Setup(
                resolver => resolver.GetIncomingPolicy(new MailAddress("me@test.com")))
                .Returns(new List<IPolicyExpression>{mockExpression.Object});

            trustModel.IsCertPolicyCompliant(new MailAddress("me@test.com"), mockCert.Object).Should().BeTrue();

            mockPolicyFilter.VerifyAll();
        }


        [Fact]
        public void TestIsCertPolicyCompiant_PolicyNotCompliant_AssertFalse()
        {
            TrustModel trustModel = new TrustModel(mockTrustChainValidator.Object, mockPolicyResolver.Object, mockPolicyFilter.Object);
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();

            mockPolicyFilter.Setup(
                filter => filter.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Returns(false);

            Mock<IPolicyExpression> mockExpression = new Mock<IPolicyExpression>();

            mockPolicyResolver.Setup(
                resolver => resolver.GetIncomingPolicy(new MailAddress("me@test.com")))
                .Returns(new List<IPolicyExpression> { mockExpression.Object });

            trustModel.IsCertPolicyCompliant(new MailAddress("me@test.com"), mockCert.Object).Should().BeFalse();

            mockPolicyFilter.VerifyAll();
        }


        [Fact]
        public void TestIsCertPolicyCompiant_MissingRequiredField_AssertFalse()
        {
            TrustModel trustModel = new TrustModel(mockTrustChainValidator.Object, mockPolicyResolver.Object, mockPolicyFilter.Object);
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();

            mockPolicyFilter.Setup(
                filter => filter.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Throws<PolicyRequiredException>();

            Mock<IPolicyExpression> mockExpression = new Mock<IPolicyExpression>();

            mockPolicyResolver.Setup(
                resolver => resolver.GetIncomingPolicy(new MailAddress("me@test.com")))
                .Returns(new List<IPolicyExpression> { mockExpression.Object });

            trustModel.IsCertPolicyCompliant(new MailAddress("me@test.com"), mockCert.Object).Should().BeFalse();

            mockPolicyFilter.VerifyAll();
        }


        [Fact]
        public void TestIsCertPolicyCompiant_PolicyExpressionError_AssertException()
        {
            TrustModel trustModel = new TrustModel(mockTrustChainValidator.Object, mockPolicyResolver.Object, mockPolicyFilter.Object);
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();

            mockPolicyFilter.Setup(
                filter => filter.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Throws<PolicyProcessException>();

            Mock<IPolicyExpression> mockExpression = new Mock<IPolicyExpression>();

            mockPolicyResolver.Setup(
                resolver => resolver.GetIncomingPolicy(new MailAddress("me@test.com")))
                .Returns(new List<IPolicyExpression> { mockExpression.Object });

            Action action = () => trustModel.IsCertPolicyCompliant(new MailAddress("me@test.com"), mockCert.Object);
            action.ShouldThrow<AgentException>().WithInnerException<PolicyProcessException>();


            mockPolicyFilter.VerifyAll();
        }

    }
}
