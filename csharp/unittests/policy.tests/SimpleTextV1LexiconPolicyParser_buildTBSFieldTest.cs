
using System;
using FluentAssertions;
using Health.Direct.Policy.Impl;
using Health.Direct.Policy.X509;
using Health.Direct.Policy.X509.Standard;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class SimpleTextV1LexiconPolicyParser_buildTBSFieldTest
    {
        [Fact]
        public void TestBuildTBSField_Version_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() => parser.BuildTBSField("X509.TBS.Version"));
        }

        [Fact]
        public void TestBuildTBSField_SerialNumber_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            dynamic tbsField = parser.BuildTBSField("X509.TBS.SerialNumber");
            Assert.NotNull(tbsField);
            Assert.Equal(tbsField.GetType(), typeof(SerialNumberAttributeField));
            Assert.True(tbsField.IsRequired());
        }


        [Fact]
        public void TestBuildTBSField_Signature_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Assert.Throws<PolicyParseException>(() =>parser.BuildTBSField("X509.TBS.Signature"));
            Assert.Throws<PolicyParseException>(() => parser.BuildTBSField("X509.TBS.Signature+"));
        }

        [Fact]
        public void TestBuildTBSField_Issuer_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            var tbsField = parser.BuildTBSField("X509.TBS.Issuer.CN") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof (IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.COMMON_NAME);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.CN+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.COMMON_NAME);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.C") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.COUNTRY);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.C+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.COUNTRY);
            tbsField.IsRequired().Should().BeTrue();


            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.O") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.ORGANIZATION);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.O+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.ORGANIZATION);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.OU") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.ORGANIZATIONAL_UNIT);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.OU+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.ORGANIZATIONAL_UNIT);
            tbsField.IsRequired().Should().BeTrue();


            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.ST") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.STATE_ST);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.ST+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.STATE_ST);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.L") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.LOCALITY);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.L+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.LOCALITY);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.E") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.EMAIL);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.E+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.EMAIL);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.DC") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DOMAIN_COMPONENT);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.DC+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DOMAIN_COMPONENT);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.DNQUALIFIER") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DISTINGUISHED_NAME_QUALIFIER);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.DNQUALIFIER+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DISTINGUISHED_NAME_QUALIFIER);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.SERIALNUMBER") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.SERIAL_NUMBER);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.SERIALNUMBER+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.SERIAL_NUMBER);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.SN") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.SURNAME);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.SN+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.SURNAME);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.TITLE") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.TITLE);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.TITLE+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.TITLE);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.GIVENNAME") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.GIVEN_NAME);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.GIVENNAME+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.GIVEN_NAME);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.INITIALS") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.INITIALS);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.INITIALS+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.INITIALS);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.PSEUDONYM") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.PSEUDONYM);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.PSEUDONYM+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.PSEUDONYM);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.GERNERAL_QUALIFIER") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.GERNERAL_QUALIFIER);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.GERNERAL_QUALIFIER+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.GERNERAL_QUALIFIER);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.DN") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DISTINGUISHED_NAME);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Issuer.DN+") as IssuerAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(IssuerAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DISTINGUISHED_NAME);
            tbsField.IsRequired().Should().BeTrue();
            
        }

        [Fact]
        public void TestBuildTBSField_Validity_AssertFieldNotImplemented()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            Action action = () => parser.BuildTBSField("X509.TBS.Validity.ValidFrom");
            action.ShouldThrow<PolicyParseException>();

            action = () => parser.BuildTBSField("X509.TBS.Validity.ValidFrom+");
            action.ShouldThrow<PolicyParseException>();

            action = () => parser.BuildTBSField("X509.TBS.Validity.ValidTo");
            action.ShouldThrow<PolicyParseException>();

            action = () => parser.BuildTBSField("X509.TBS.Validity.ValidTo+");
            action.ShouldThrow<PolicyParseException>();
        }

        [Fact]
        public void TestBuildTBSField_Subject_AssertBuilt()
        {
            var parser = new SimpleTextV1LexiconPolicyParser();
            var tbsField = parser.BuildTBSField("X509.TBS.Subject.CN") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof (SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.COMMON_NAME);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.CN+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.COMMON_NAME);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.C") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.COUNTRY);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.C+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.COUNTRY);
            tbsField.IsRequired().Should().BeTrue();


            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.O") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.ORGANIZATION);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.O+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.ORGANIZATION);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.OU") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.ORGANIZATIONAL_UNIT);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.OU+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.ORGANIZATIONAL_UNIT);
            tbsField.IsRequired().Should().BeTrue();


            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.ST") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.STATE_ST);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.ST+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.STATE_ST);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.L") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.LOCALITY);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.L+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.LOCALITY);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.E") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.EMAIL);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.E+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.EMAIL);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.DC") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DOMAIN_COMPONENT);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.DC+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DOMAIN_COMPONENT);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.DNQUALIFIER") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DISTINGUISHED_NAME_QUALIFIER);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.DNQUALIFIER+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DISTINGUISHED_NAME_QUALIFIER);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.SERIALNUMBER") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.SERIAL_NUMBER);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.SERIALNUMBER+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.SERIAL_NUMBER);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.SN") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.SURNAME);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.SN+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.SURNAME);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.TITLE") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.TITLE);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.TITLE+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.TITLE);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.GIVENNAME") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.GIVEN_NAME);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.GIVENNAME+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.GIVEN_NAME);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.INITIALS") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.INITIALS);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.INITIALS+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.INITIALS);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.PSEUDONYM") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.PSEUDONYM);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.PSEUDONYM+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.PSEUDONYM);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.GERNERAL_QUALIFIER") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.GERNERAL_QUALIFIER);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.GERNERAL_QUALIFIER+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.GERNERAL_QUALIFIER);
            tbsField.IsRequired().Should().BeTrue();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.DN") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DISTINGUISHED_NAME);
            tbsField.IsRequired().Should().BeFalse();

            parser = new SimpleTextV1LexiconPolicyParser();
            tbsField = parser.BuildTBSField("X509.TBS.Subject.DN+") as SubjectAttributeField;
            tbsField.Should().NotBeNull();
            tbsField.GetType().Should().Be(typeof(SubjectAttributeField));
            tbsField.GetRDNAttributeFieldId().Should().Be(RDNAttributeIdentifier.DISTINGUISHED_NAME);
            tbsField.IsRequired().Should().BeTrue();
            
        
        }
    }
}
