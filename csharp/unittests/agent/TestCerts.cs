/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using Health.Direct.Common.Certificates;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Agent.Tests
{
    public class TestCertNameExtraction
    {
        static TestCertNameExtraction()
        {
            AgentTester.EnsureStandardMachineStores();
        }
        
        public static IEnumerable<object[]> PrivateCerts
        {
            get
            {
                using(SystemX509Store store = SystemX509Store.OpenPrivate())
                {
                    foreach (X509Certificate2 cert in store)
                    {
                        yield return new[] { cert };
                    }
                }
            }
        }

        public static IEnumerable<object[]> PublicCerts
        {
            get
            {
                using (SystemX509Store store = SystemX509Store.OpenExternal())
                {
                    foreach (X509Certificate2 cert in store)
                    {
                        yield return new[] { cert };
                    }
                }
            }
        }

        public static IEnumerable<object[]> Anchors
        {
            get
            {
                using (SystemX509Store store = SystemX509Store.OpenAnchor())
                {
                    foreach (X509Certificate2 cert in store)
                    {
                        yield return new[] { cert };
                    }
                }
            }
        }
        
        public static IEnumerable<object[]> AllCerts
        {
            get
            {
                foreach (object[] o in PublicCerts)
                {
                    yield return o;
                }

                foreach (object[] o in Anchors)
                {
                    yield return o;
                }
            }
        }        
                
        [Theory]
        [PropertyData("AllCerts")]
        public void TestNameExtraction(X509Certificate2 cert)
        {
            string name = cert.ExtractEmailNameOrName();
            Assert.False(string.IsNullOrEmpty(name));
            Assert.True(cert.MatchEmailNameOrName(name));
        }



        /// <summary>
        /// Certificate extracted from the NIST INVALID_CERT test.
        /// Sender Address: InvalidCert@ttt.transport-testing.org
        /// dNSName in subjectAltName = foo.transport-testing.org
        /// 
        /// Applicability Statement 4.1.2
        /// </summary>
        [Fact]
        public void TestVerifySignature_dNSName_in_subjectAltName_doesNotMatch_Health_Internet_Domain()
        {

            string signingCert = @"MIAGCSqGSIb3DQEHAqCAMIACAQExCzAJBgUrDgMCGgUAMIAGCSqGSIb3DQEHAQAAoIAwggQhMIID
iqADAgECAghs4QKo+HXxyzANBgkqhkiG9w0BAQUFADCBpDEkMCIGCSqGSIb3DQEJARYVdHJhbnNw
b3J0LXRlc3Rpbmcub3JnMR4wHAYDVQQDDBV0cmFuc3BvcnQtdGVzdGluZy5vcmcxCzAJBgNVBAYT
AlVTMQswCQYDVQQIDAJNRDEVMBMGA1UEBwwMR2FpdGhlcnNidXJnMSswKQYDVQQKDCJ0cmFuc3Bv
cnQtdGVzdGluZy5vcmcgVHJ1c3QgQW5jaG9yMB4XDTEzMDMwMTIwMDQxOFoXDTIzMDIyMzIwMDQx
OFowga4xKDAmBgkqhkiG9w0BCQEWGWZvby50cmFuc3BvcnQtdGVzdGluZy5vcmcxIjAgBgNVBAMM
GXR0dC50cmFuc3BvcnQtdGVzdGluZy5vcmcxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJNRDEVMBMG
A1UEBwwMR2FpdGhlcnNidXJnMS0wKwYDVQQKDCRUVFQgb24gdHJhbnNwb3J0LXRlc3Rpbmcub3Jn
IElOVkFMSUQwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAKEpqL3f+SPBEbrY+NCRCVcykXHD
0sQybh1/M0Hw2mcif6j6Lb5kU6asWc3OeceM0IoUZTUVKeJHFIvofDXZ0DOEDm0GbQkmc+obKABy
yJ8w7Rj4/Fnm7MZjU9jho6i/YEKO2ugPbZ7z+ySRqNsfCbWtgx/iukodnAAXJKNViRynAgMBAAGj
ggFOMIIBSjCB0gYDVR0jBIHKMIHHgBTrrsEnwuMJMY/2SPMsnJRWLJsXwKGBpKSBoTCBnjEkMCIG
CSqGSIb3DQEJARYVc2luZ2luZy52aWRlbnRpdHkuY29tMR4wHAYDVQQDDBVzaW5naW5nLnZpZGVu
dGl0eS5jb20xCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJNRDESMBAGA1UEBwwJQmFsdGltb3JlMSgw
JgYDVQQKDB9WaWRlbnRpdHkgSW5jIFNpZ25pbmcgQXV0aG9yaXR5gggL3YP9ycOMUTAdBgNVHQ4E
FgQUVAgSrXlommFjDPdcsh6RC+upobEwDAYDVR0TAQH/BAIwADAgBgNVHRIEGTAXghV0cmFuc3Bv
cnQtdGVzdGluZy5vcmcwJAYDVR0RBB0wG4IZZm9vLnRyYW5zcG9ydC10ZXN0aW5nLm9yZzANBgkq
hkiG9w0BAQUFAAOBgQBxTmu9qwK/aXB1c6CJQzqdWh3UsbLFdG/DlhhDvAIOxt8fCtkUNvTO0DZQ
nzNRiHpNIXlHrixzppg2utKDBe050OuSKZhdLqr+BI/XmR/386T2t9Da8i8DUoEfOfT7HnH0I80Y
bJkn0NbdA7LzIVt/tq15kvm1ijEdfJ7/h6G3ugAAMYIC0TCCAs0CAQEwgbEwgaQxJDAiBgkqhkiG
9w0BCQEWFXRyYW5zcG9ydC10ZXN0aW5nLm9yZzEeMBwGA1UEAwwVdHJhbnNwb3J0LXRlc3Rpbmcu
b3JnMQswCQYDVQQGEwJVUzELMAkGA1UECAwCTUQxFTATBgNVBAcMDEdhaXRoZXJzYnVyZzErMCkG
A1UECgwidHJhbnNwb3J0LXRlc3Rpbmcub3JnIFRydXN0IEFuY2hvcgIIbOECqPh18cswCQYFKw4D
AhoFAKCCAXUwGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMTMwNTIz
MTkwODI0WjAjBgkqhkiG9w0BCQQxFgQUsKPMV4E62N7WgadDDEyKEiBKHDswTwYJKoZIhvcNAQkP
MUIwQDAKBggqhkiG9w0DBzAOBggqhkiG9w0DAgICAIAwBwYFKw4DAgcwCwYJKoZIhvcNAQcBMAwG
CiqGSIb3DQEJFgEwgcQGCyqGSIb3DQEJEAILMYG0oIGxMIGkMSQwIgYJKoZIhvcNAQkBFhV0cmFu
c3BvcnQtdGVzdGluZy5vcmcxHjAcBgNVBAMMFXRyYW5zcG9ydC10ZXN0aW5nLm9yZzELMAkGA1UE
BhMCVVMxCzAJBgNVBAgMAk1EMRUwEwYDVQQHDAxHYWl0aGVyc2J1cmcxKzApBgNVBAoMInRyYW5z
cG9ydC10ZXN0aW5nLm9yZyBUcnVzdCBBbmNob3ICCGzhAqj4dfHLMA0GCSqGSIb3DQEBAQUABIGA
g9DY8Y/ubZz99nUCA0ZEMUYEaLG+gWjzD0TXU+IBKmzX55p2DktPBSGz+rO3TdDzCa3oRJsHBIFp
4jyAfk0shWgFxuio9fpIBvJNOvhSmjE6e3T+is5TE3ZGnhkMCic1ukOxZd9kTZnot3JMHhOlpgnt
8RhS81VCdshLgSTuLLwAAAAAAAA=";

            X509Certificate2 cert = new X509Certificate2();
            cert.Import(Encoding.UTF8.GetBytes(signingCert));
            
            Assert.Equal("foo.transport-testing.org", cert.GetNameInfo(X509NameType.DnsFromAlternativeName, false));

            Assert.False(cert.MatchEmailNameOrName("InvalidCert@ttt.transport-testing.org"));
            Assert.False(cert.MatchDnsOrEmailOrName("ttt.transport-testing.org"));
        }

    }

    public class TestCertFind
    {
        X509Certificate2Collection m_certs;

        public TestCertFind()
        {
            m_certs = TestCertificates.AllPublicCerts;
        }

        public static IEnumerable<object[]> MixedNames
        {
            get
            {
                yield return new object[] { "redmond.hsgincubator.com", true };
                yield return new object[] { "nhind.hsgincubator.com", true };
                yield return new object[] { "bob@nhind.hsgincubator.com", true };
                yield return new object[] { "jupiter@nhind.hsgincubator.com", false };
                yield return new object[] { "pete@nhind.hsgincubator.com", false };
            }
        }

        public static IEnumerable<object[]> Names
        {
            get
            {
                yield return new object[] { "redmond.hsgincubator.com", true };
                yield return new object[] { "nhind.hsgincubator.com", true };
                yield return new object[] { "www.healthvault.com", false };
            }
        }

        [Theory]
        [PropertyData("MixedNames")]
        public void TestFindMixed(string name, bool shouldMatch)
        {
            bool found = (m_certs.Find(x => x.MatchEmailNameOrName(name)) != null);
            Assert.True(found == shouldMatch);
        }

        [Theory]
        [PropertyData("Names")]
        public void TestFindNames(string name, bool shouldMatch)
        {
            bool found = (m_certs.FindByName(name) != null);
            Assert.True(found == shouldMatch);
        }
    }
}