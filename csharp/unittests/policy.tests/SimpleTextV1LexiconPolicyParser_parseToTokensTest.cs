using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentAssertions;
using Health.Direct.Policy.Impl;
using Health.Direct.Policy.Tests.Extensions;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class SimpleTextV1LexiconPolicyParser_parseToTokensTest
    {
        [Fact]
        public void TestParse_SimpleExpression_ValidateTokens()
        {
            SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = File.OpenRead("./resources/policies/simpleLexiconSamp1.txt"))
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(11);
            }
        }

        [Fact]
        public void TestParse_LogicalAndOperator_ValidateSingleTokens()
        {
            SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

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
            SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = File.OpenRead("./resources/policies/lexiconWithCertificateStruct.txt"))
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
                tokens.First().GetType().Should().Be(TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
            }
        }

        [Fact]
        public void TestParse_LiteralWithSpaces_ValidateTokens()
        {
            SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = File.OpenRead("./resources/policies/literalWithSpaces.txt"))
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
                tokens.First().GetType().Should().Be(TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
            }
        }

        [Fact]
        public void testParse_requiredCertField_validateTokens()
        {
            SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();

            using (Stream stream = "X509.TBS.EXTENSION.SubjectKeyIdentifier+ = 1.3.2.3".ToStream())
            {
                IList<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.ParseToTokens(stream);
                tokens.Count.Should().Be(3);
                
            }
        }
    }
}
