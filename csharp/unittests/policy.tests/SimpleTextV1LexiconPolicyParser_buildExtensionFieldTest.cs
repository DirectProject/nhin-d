using FluentAssertions;
using Health.Direct.Policy.Impl;
using Health.Direct.Policy.X509;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class SimpleTextV1LexiconPolicyParser_buildExtensionFieldTest
    {
        [Fact]
        public void TestBuildExtensionField_KeyUsage_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            IExtensionField<int> field = parser.BuildExtensionField("X509.TBS.EXTENSION.KeyUsage");
            field.Should().NotBeNull();
            field.Should().BeAssignableTo<KeyUsageExtensionField>();
            field.IsRequired().Should().BeFalse();

            field = parser.BuildExtensionField("X509.TBS.EXTENSION.KeyUsage+");
            field.Should().NotBeNull();
            field.Should().BeAssignableTo<KeyUsageExtensionField>();
            field.IsRequired().Should().BeTrue();
        }

        [Fact]
        public void TestBuildExtensionField_SubjectAltName_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            IExtensionField<int> field = parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectAltName");
            field.Should().NotBeNull();
            field.Should().BeAssignableTo<SubjectAltNameExtensionField>();
            field.IsRequired().Should().BeFalse();

            field = parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectAltName+");
            field.Should().NotBeNull();
            field.Should().BeAssignableTo<SubjectAltNameExtensionField>();
            field.IsRequired().Should().BeTrue();
        }
    }
}
