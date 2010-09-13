/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography.X509Certificates;
using NHINDirect;
using NHINDirect.Certificates;
using Xunit;
using Xunit.Extensions;

namespace AgentTests
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
            string manualName = this.ExtractEmailNameOrName(cert);
            Assert.False(string.IsNullOrEmpty(name));
            Assert.Equal(name, manualName);
            Assert.True(cert.MatchEmailNameOrName(name));
        }
        
        const string SubjectNamePrefix = "CN=";
        const string EmailNamePrefix = "E=";

        public string ExtractEmailNameOrName(X509Certificate2 cert)
        {
            string[] parts = cert.Subject.Split(',');
            if (parts != null)
            {
                for (int i = 0; i < parts.Length; ++i)
                {
                    string prefix = EmailNamePrefix;
                    int index = parts[i].IndexOf(prefix);
                    if (index < 0)
                    {
                        prefix = SubjectNamePrefix;
                        index = parts[i].IndexOf(prefix);
                    }
                    if (index >= 0)
                    {
                        return parts[i].Substring(index + prefix.Length).Trim();
                    }
                }
            }
            return null;
        }
    }
    
    public class TestCertFind
    {
        X509Certificate2Collection m_certs;
        
        static TestCertFind()
        {
            AgentTester.EnsureStandardMachineStores();            
        }
        
        public TestCertFind()
        {
            m_certs = this.GetCerts();
        }
        
        public static IEnumerable<object[]> MixedNames
        {
            get
            {
                yield return new object[] {"redmond.hsgincubator.com", true};
                yield return new object[] { "nhind.hsgincubator.com", true};
                yield return new object[] { "bob@nhind.hsgincubator.com", true };
                yield return new object[] { "jupiter@nhind.hsgincubator.com", false};
                yield return new object[] { "pete@nhind.hsgincubator.com", false };
            }
        }

        public static IEnumerable<object[]> Names
        {
            get
            {
                yield return new object[] { "redmond.hsgincubator.com", true };
                yield return new object[] { "nhind.hsgincubator.com", true };
                yield return new object[] { "www.healthvault.com", false};
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

        X509Certificate2Collection GetCerts()
        {
            using (SystemX509Store store = SystemX509Store.OpenExternal())
            {
                return store.GetAllCertificates();
            }
        }
    }
}
