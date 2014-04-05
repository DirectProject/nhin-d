using System;
using System.Security.Cryptography.X509Certificates;
using FluentAssertions;
using Health.Direct.Policy.X509;
using Xunit;

namespace Health.Direct.Policy.Tests.x509
{
    public class SubjectPublicKeySizeField_InjectReferenceValueTest
    {
        [Fact]
        public void SubjectPublicKeySizeField_injectReferenceValueTest()
        {
            var cert = new X509Certificate2(@"resources/certs/altNameOnly.der");
            var field = new SubjectPublicKeySizeField();
            field.InjectReferenceValue(cert);
            field.GetPolicyValue().GetPolicyValue().Should().Be(1024);
        }

        [Fact]
        public void testInjectRefereneValue_rsa2024_assertValue()
        {
            var cert = new X509Certificate2(@"resources/certs/umesh.der");
            var field = new SubjectPublicKeySizeField();
            field.InjectReferenceValue(cert);
            field.GetPolicyValue().GetPolicyValue().Should().Be(2024);
        }

        [Fact]
        public void testInjectRefereneValue_dsa1024_assertValue()
        {
            var cert = new X509Certificate2(@"resources/certs/dsa1024.der");
            var field = new SubjectPublicKeySizeField();
            field.InjectReferenceValue(cert);
            field.GetPolicyValue().GetPolicyValue().Should().Be(1024);
        }

        [Fact]
        public void testInjectRefereneValue_ecc_assertValue()
        {
            var cert = new X509Certificate2(@"resources/certs/ecc.der");
            var field = new SubjectPublicKeySizeField();
            field.InjectReferenceValue(cert);
            field.GetPolicyValue().GetPolicyValue().Should().Be(0);
        }

        [Fact]
        public void TestInjectReferenceValue_NoInjection_GetPolicyValue_AssertException()
        {
            var field = new SubjectPublicKeyAlgorithmField();
            Action action = () => field.GetPolicyValue();
            action.ShouldThrow<InvalidOperationException>();
        }
    }
}