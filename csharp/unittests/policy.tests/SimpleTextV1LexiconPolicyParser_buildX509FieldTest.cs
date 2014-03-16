using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentAssertions;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.Impl;
using Health.Direct.Policy.X509;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class SimpleTextV1LexiconPolicyParser_buildX509FieldTest
    {
        [Fact]
        public void TestBuildX509Field_SignatureAlgorithm_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            IPolicyExpression field = parser.BuildX509Field("X509.Algorithm");
            Assert.NotNull(field);
            field.GetType().Should().Be(typeof(SignatureAlgorithmField));
        }

        [Fact]
        public void TestBuildX509Field_Signature_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildX509Field("X509.Signature"));
        }

        [Fact]
        public void TestBuildX509Field_UnknownField_AssertNull()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            IPolicyExpression field = parser.BuildX509Field("X509.Bogus");
            Assert.Null(field);
        }

    }
}
