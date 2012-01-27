/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:    
    Umesh Madan     umeshma@microsoft.com

 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Caching;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Certificates
{
    public class CertificateResolverBasicTests
    {
        public CertificateResolverBasicTests()
        {
        }

        [Fact]
        public void Test()
        {
            DummyX509Index index = new DummyX509Index();
            CertificateResolver resolver = new CertificateResolver(index, null);

            resolver.GetCertificates(new MailAddress("toby@redmond.hsgincubator.com"));
            Assert.True(index.Log.Count == 2);
            Assert.True(index.Log[0] == "toby@redmond.hsgincubator.com");
            Assert.True(index.Log[1] == "redmond.hsgincubator.com");
        }

        [Fact]
        public void TestDomain()
        {
            DummyX509Index index = new DummyX509Index();
            CertificateResolver resolver = new CertificateResolver(index, null);

            resolver.GetCertificatesForDomain("redmond.hsgincubator.com");
            Assert.True(index.Log.Count == 1);
            Assert.True(index.Log[0] == "redmond.hsgincubator.com");
        }

        [Fact]
        public void TestOrgOnly()
        {
            DummyX509Index index = new DummyX509Index();
            CertificateResolver resolver = new CertificateResolver(index, null);
            resolver.OrgCertificatesOnly = true;

            resolver.GetCertificates(new MailAddress("toby@redmond.hsgincubator.com"));
            Assert.True(index.Log.Count == 1);
            Assert.True(index.Log[0] == "redmond.hsgincubator.com");
        }

        public class DummyX509Index : IX509CertificateIndex
        {
            List<string> m_callLog;

            public DummyX509Index()
            {
                m_callLog = new List<string>();
            }

            public X509Certificate2Collection this[string subjectName]
            {
                get
                {
                    m_callLog.Add(subjectName);
                    return null;
                }
            }

            public List<string> Log
            {
                get { return m_callLog; }
            }
        }
    }
}
