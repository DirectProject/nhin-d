/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using FluentAssertions;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.Impl;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class SimpleTextV1LexiconPolicyParser_parseToTokensTest
    {
        [Fact]
        public void TestParse_SimpleExpression_ValidateTokens()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = File.OpenRead("./resources/policies/simpleLexiconSamp1.txt"))
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(11);
            }
        }

        [Fact]
        public void TestParse_SimpleExpression_PolicyRef_ValidateTokens()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = "X509.Algorithm = 1.2.840.113549.1.1.5".ToStream())
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
            }
        }

        [Fact]
        public void TestParse_LogicalAndOperator_ValidateSingleTokens()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = File.OpenRead("./resources/policies/logicalAndOperator.txt"))
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(1);
                tokens.First().GetToken().Should().Be("&&");
            }
        }

        [Fact]
        public void TestParse_CertificateStruct_ValidateTokens()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = File.OpenRead("./resources/policies/lexiconWithCertificateStruct.txt"))
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
                tokens.First().GetTokenType().Should().Be(TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
            }
        }

        [Fact]
        public void TestParse_LiteralWithSpaces_ValidateTokens()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = File.OpenRead("./resources/policies/literalWithSpaces.txt"))
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
                tokens.First().GetTokenType().Should().Be(TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
            }
        }

        [Fact]
        public void testParse_requiredCertField_validateTokens()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = "X509.TBS.EXTENSION.SubjectKeyIdentifier+ = 1.3.2.3".ToStream())
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
            }
        }

        [Fact]
        public void testParse_tbs_serialnumber_validateTokens()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = "X509.TBS.SerialNumber = f74f1c4fe4e1762e".ToStream())
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
            }
        }


        [Fact]
        public void TestExtensionBasicContraint_CA_AssertTrue()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            using (Stream stream = ("X509.TBS.EXTENSION.BasicConstraints.CA = true").ToStream())
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
            }

            using (Stream stream = ("X509.TBS.EXTENSION.BasicConstraints.CA = true").ToStream())
            {
                IPolicyExpression expression = parser.Parse(stream);
                expression.Should().BeAssignableTo<OperationPolicyExpression>();

                var operationPolicyExpression = expression as OperationPolicyExpression;
                operationPolicyExpression.GetOperands().Count.Should().Be(2);
               
            }
        }

    }
}
