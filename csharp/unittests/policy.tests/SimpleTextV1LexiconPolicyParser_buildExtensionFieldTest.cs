
using Health.Direct.Common.Policies;
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

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.KeyUsage");
            Assert.NotNull(field);
            Assert.Equal(typeof(KeyUsageExtensionField), field.GetType());
            Assert.False(field.IsRequired());

            field = parser.BuildExtensionField("X509.TBS.EXTENSION.KeyUsage+");
            Assert.NotNull(field);
            Assert.Equal(typeof(KeyUsageExtensionField), field.GetType());
            Assert.True(field.IsRequired());
        }

        [Fact]
        public void TestBuildExtensionField_SubjectAltName_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectAltName");
            Assert.NotNull(field);
            Assert.Equal(typeof(SubjectAltNameExtensionField), field.GetType());
            Assert.False(field.IsRequired());

            field = parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectAltName+");
            Assert.NotNull(field);
            Assert.Equal(typeof(SubjectAltNameExtensionField), field.GetType());
            Assert.True(field.IsRequired());
        }

        [Fact]
        public void TestBuildExtensionField_SubjectDirectoryAttributes_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectDirectoryAttributes"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectDirectoryAttributes+"));
        }

        [Fact]
        public void testBuildExtensionField_subjectKeyIdentifier_assertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectKeyIdentifier");
            Assert.NotNull(field);
            Assert.Equal(typeof(SubjectKeyIdentifierExtensionField), field.GetType());
            Assert.False(field.IsRequired());


            field = parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectKeyIdentifier+");
            Assert.NotNull(field);
            Assert.Equal(typeof(SubjectKeyIdentifierExtensionField), field.GetType());
            Assert.True(field.IsRequired());
        }

        [Fact]
        public void TestBuildExtensionField_IssuierAltName_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.IssuerAltName"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.IssuerAltName+"));
        }

        [Fact]
        public void TestBuildExtensionField_AutorityKeyId_AssertFields()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.KeyId");
            Assert.NotNull(field);
            Assert.Equal(typeof(AuthorityKeyIdentifierKeyIdExtensionField), field.GetType());
            Assert.False(field.IsRequired());


            field = parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.KeyId+");
            Assert.NotNull(field);
            Assert.Equal(typeof(AuthorityKeyIdentifierKeyIdExtensionField), field.GetType());
            Assert.True(field.IsRequired());

            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.CertIssuers"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.CertIssuers+"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.SerialNumber"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityKeyIdentifier.SerialNumber+"));
        }

        [Fact]
        public void TestBuildExtensionField_CertificatePolicies_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs");
            Assert.NotNull(field);
            Assert.Equal(typeof(CertificatePolicyIndentifierExtensionField), field.GetType());
            Assert.False(field.IsRequired());

            field = parser.BuildExtensionField("X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs+");
            Assert.NotNull(field);
            Assert.Equal(typeof(CertificatePolicyIndentifierExtensionField), field.GetType());
            Assert.True(field.IsRequired());

            field = parser.BuildExtensionField("X509.TBS.EXTENSION.CertificatePolicies.CPSUrls");
            Assert.NotNull(field);
            Assert.Equal(typeof(CertificatePolicyCpsUriExtensionField), field.GetType());
            Assert.False(field.IsRequired());

            field = parser.BuildExtensionField("X509.TBS.EXTENSION.CertificatePolicies.CPSUrls+");
            Assert.NotNull(field);
            Assert.Equal(typeof(CertificatePolicyCpsUriExtensionField), field.GetType());
            Assert.True(field.IsRequired());
        }

        [Fact]
        public void TestBuildExtensionField_PolicyMappings_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.PolicyMappings"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.PolicyMappings+"));
        }

        [Fact]
        public void TestBuildExtensionField_BasicContraints_AssertFields()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.BasicConstraints.CA");
            Assert.NotNull(field);
            Assert.Equal(typeof(BasicContraintsExtensionField), field.GetType());
            Assert.False(field.IsRequired());


            field = parser.BuildExtensionField("X509.TBS.EXTENSION.BasicConstraints.CA+");
            Assert.NotNull(field);
            Assert.Equal(typeof(BasicContraintsExtensionField), field.GetType());
            Assert.True(field.IsRequired());

            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.BasicConstraints.MaxPathLength"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.BasicConstraints.MaxPathLength+"));
        }

        [Fact]
        public void TestBuildExtensionField_NameConstraints_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.NameConstraints"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.NameConstraints+"));
        }

        [Fact]
        public void TestBuildExtensionField_PolicyConstraints_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.PolicyConstraints"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.PolicyConstraints+"));
        }

        [Fact]
        public void TestBuildExtensionField_ExtendedKeyUsage_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.ExtKeyUsageSyntax");
            Assert.NotNull(field);
            Assert.Equal(typeof(ExtendedKeyUsageExtensionField), field.GetType());
            Assert.False(field.IsRequired());


            field = parser.BuildExtensionField("X509.TBS.EXTENSION.ExtKeyUsageSyntax+");
            Assert.NotNull(field);
            Assert.Equal(typeof(ExtendedKeyUsageExtensionField), field.GetType());
            Assert.True(field.IsRequired());
        }


        [Fact]
        public void TestBuildExtensionField_CRLDispPoints_AssertFields()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.FullName");
            Assert.NotNull(field);
            Assert.Equal(typeof(CRLDistributionPointNameExtentionField), field.GetType());
            Assert.False(field.IsRequired());


            field = parser.BuildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.FullName+");
            Assert.NotNull(field);
            Assert.Equal(typeof(CRLDistributionPointNameExtentionField), field.GetType());
            Assert.True(field.IsRequired());

            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.RelativeToIssuer"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.RelativeToIssuer+"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.Reasons"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.Reasons+"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.CRLIssuer"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.CRLDistributionPoints.CRLIssuer+"));
        }

        [Fact]
        public void TestBuildExtensionField_InhibitAnyPolicy_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.InhibitAnyPolicy"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.InhibitAnyPolicy+"));
        }

        [Fact]
        public void TestBuildExtensionField_FreshestCRL_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.FreshestCRL.FullName"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.FreshestCRL.FullName+"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.FreshestCRL.RelativeToIssuer"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.FreshestCRL.RelativeToIssuer+"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.FreshestCRL.Reasons"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.FreshestCRL.Reasons+"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.FreshestCRL.CRLIssuer"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.FreshestCRL.CRLIssuer+"));
        }

        [Fact]
        public void TestBuildExtensionField_AuhtorityInformationAccess_AssertFields()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();

            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url");
            Assert.NotNull(field);
            Assert.Equal(typeof(AuthorityInfoAccessExtentionField), field.GetType());
            Assert.False(field.IsRequired());


            field = parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url+");
            Assert.NotNull(field);
            Assert.Equal(typeof(AuthorityInfoAccessExtentionField), field.GetType());
            Assert.True(field.IsRequired());

            field = parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation");
            Assert.NotNull(field);
            Assert.Equal(typeof(AuthorityInfoAccessOCSPLocExtentionField), field.GetType());
            Assert.False(field.IsRequired());


            field = parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation+");
            Assert.NotNull(field);
            Assert.Equal(typeof(AuthorityInfoAccessOCSPLocExtentionField), field.GetType());
            Assert.True(field.IsRequired());
            
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.AccessMethod"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.AccessMethod+"));
        }

        [Fact]
        public void TestBuildExtensionField_AuhtorityInformationAccess_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.Url"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.Url+"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.AccessMethod"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.AccessMethod+"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.OCSPLocation"));
            Assert.Throws<PolicyParseException>(() => parser.BuildExtensionField("X509.TBS.EXTENSION.SubjectInfoAccessSyntax.OCSPLocation+"));
        }

        [Fact]
        public void TestBuildExtensionField_UnknownField_AssertNull() 
	    {
		    var parser = new SimpleTextV1LexiconPolicyParser();
            dynamic field = parser.BuildExtensionField("X509.TBS.EXTENSION.Bogus");
            Assert.Null(field);
	    }
    }
}
