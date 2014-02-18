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


using System.IO;
using System.Security.Cryptography.X509Certificates;
using FluentAssertions;
using Health.Direct.Policy.Impl;
using Health.Direct.Policy.Machine;
using Health.Direct.Policy.Tests.Extensions;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class PolicyFilter_SimpleTextLexiconTest
    {
        [Fact]
        public void TestX509SignatureAlgorithm_Equals_AssertTrue()
        {
            using (Stream stream = "X509.Algorithm = 1.2.840.113549.1.1.5".ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestTBSSerialNumber_AssertTrue()
        {
            using (Stream stream = "X509.TBS.SerialNumber = 00F74F1C4FE4E1762E".ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }


            using (Stream stream = "X509.TBS.SerialNumber = 00f74f1c4fe4e1762e".ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }

            using (Stream stream = "X509.TBS.SerialNumber = f74f1c4fe4e1762e".ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact (Skip = "Work on a ST to S substitution")]
        public void TestTBSIssuer_AssertTrue_WithState_ST() 
	    {
            using (Stream stream = ("(X509.TBS.Issuer.CN {?} SimpleInterop) && (X509.TBS.Issuer.C {?} US) && (X509.TBS.Issuer.ST {?} Missouri) " +
				    " && (X509.TBS.Issuer.E {?} cmii@cerner.com) && (X509.TBS.Issuer.OU {?} Medical Informatics)").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
	    }

        [Fact]
        public void TestTBSIssuer_AssertTrue_WithState_S()
        {
            using (Stream stream = ("(X509.TBS.Issuer.CN {?} SimpleInterop) && (X509.TBS.Issuer.C {?} US) && (X509.TBS.Issuer.S {?} Missouri) " +
                    " && (X509.TBS.Issuer.E {?} cmii@cerner.com) && (X509.TBS.Issuer.OU {?} Medical Informatics)").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }



        [Fact(Skip = "Work on a ST to S substitution")]
        public void TestTBSSubject_AssertTrue_WithState_ST()
        {
            using (Stream stream = ("(X509.TBS.Subject.CN {?} umesh) && (X509.TBS.Subject.C {?} US) && (X509.TBS.Subject.ST {?} Missouri) " +
                " && (X509.TBS.Subject.E {?} umesh@securehealthemail.com) && (X509.TBS.Subject.OU {?} Medical Informatics)").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }


        [Fact]
        public void TestTBSSubject_AssertTrue_WithState_S()
        {
            using (Stream stream = ("(X509.TBS.Subject.CN {?} umesh) && (X509.TBS.Subject.C {?} US) && (X509.TBS.Subject.S {?} Missouri) " +
                " && (X509.TBS.Subject.E {?} umesh@securehealthemail.com) && (X509.TBS.Subject.OU {?} Medical Informatics)").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }


        [Fact]
        public void TestTBSPublicKeyAlgorithm_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.SubjectPublicKeyInfo.Algorithm = 1.2.840.113549.1.1.1").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }


        [Fact]
        public void TestTBSPublicKeySize_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.SubjectPublicKeyInfo.Size = 2024").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionKeyUsage_Equals_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.KeyUsage = 224").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionKeyUsage_SingleUsageCheck_AssertTrue()
        {
            using (Stream stream = ("(X509.TBS.EXTENSION.KeyUsage & 32) > 0").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionSubjectAltName_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.SubjectAltName {?} rfc822:AlAnderson@hospitalA.direct.visionshareinc.com").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionSubjectAltName_NotContains_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.SubjectAltName {?}! me@you.com").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionSubjectAltName_RegexContains_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.SubjectAltName {}$ AlAnderson@hospitalA.direct.visionshareinc.com").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionSubjectAltName_Empty_AssertTrue()
        {
            using (Stream stream = ("{}X509.TBS.EXTENSION.SubjectAltName").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionSubjectAltName_NotEmpty_AssertTrue()
        {
            using (Stream stream = ("{}!X509.TBS.EXTENSION.SubjectAltName").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }


        [Fact]
        public void TestExtensionSubjectKeyId_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.SubjectKeyIdentifier = e0f63ccfeb5ce3eef5c04efe8084c92bc628682c").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionAuthorityKeyIdent_KeyId_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.AuthorityKeyIdentifier.KeyId = 3aa0074b77b2493efb447de5ce6cd055085de3f0").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionPolicy_OIDS_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs {?} 1.3.6.1.4.1.41179.0.1.2").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/policyMixedQualifier.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionPolicy_Intersection_SingleQueryOID_AssertTrue()
        {
            using (Stream stream = ("^(X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs {}& 1.3.6.1.4.1.41179.0.1.2) = 1").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/policyMixedQualifier.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionPolicy_Intersection_MultipleQueryOID_SingleIntersection_AssertTrue()
        {
            using (Stream stream = ("^(X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs {}& 1.3.6.1.4.1.41179.0.1.2,12345) = 1").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/policyMixedQualifier.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionPolicy_Intersection_MultipleQueryOID_MultipleIntersection_AssertTrue()
        {
            using (Stream stream = ("^(X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs {}& 1.3.6.1.4.1.41179.0.1.2,1.3.6.1.4.1.41179.1.3) = 2").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/policyMixedQualifier.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionPolicy_OIDS_Size_AssertTrue()
        {
            using (Stream stream = ("^X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs = 4").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/policyMixedQualifier.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionPolicy_OIDS_Empty_AssertTrue()
        {
            using (Stream stream = ("{}X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionPolicy_PolicyURL_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.CertificatePolicies.CPSUrls {?} https://www.phicert.com/cps").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/policyMixedQualifier.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionBasicContraint_CA_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.BasicConstraints.CA = true").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/CernerDirect DevCert Provider CA.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }


        [Fact]
        public void TestExtensionExtendedKeyUsage_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.ExtKeyUsageSyntax {?} 1.3.6.1.5.5.7.3.4").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionAIA_URL_Caissues_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url " +
                "{?} caIssuers:http://ca.cerner.com/public/root.der").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/CernerDirectProviderCA.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionAIA_URL_OCSP_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url " +
                "{?} OCSP:http://ca.cerner.com/OCSP").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/CernerDirectProviderCA.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }


        [Fact]
        public void TestExtensionAIA_Size_AssertTrue()
        {
            using (Stream stream = ("^X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url = 2").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/CernerDirectProviderCA.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void TestExtensionAIA_OCSPLocation_AssertTrue()
        {
            using (Stream stream = ("X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation " +
                "{?} http://ca.cerner.com/OCSP").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/CernerDirectProviderCA.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }


        [Fact]
        public void TestExtensionAIA_OCSPLocation_Size_AssertTrue()
        {
            using (Stream stream = ("^X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation  = 1").ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/CernerDirectProviderCA.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(),
                    new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }



    }
}
