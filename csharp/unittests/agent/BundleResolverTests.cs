﻿/* 
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

using System;
using System.IO;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Xunit;

namespace Health.Direct.Agent.Tests
{
    public class BundleResolverTests
    {
        [Fact]
        public void TestNoMetadata()
        {
            byte[] p7bData = File.ReadAllBytes(@"Certificates\Bundles\bundleNoMetadata.p7b");
            AnchorBundle bundle = null;
            Assert.Null(Record.Exception(() => bundle = new AnchorBundle(p7bData)));
            Assert.True(!bundle.Certificates.IsNullOrEmpty());
            Assert.True(bundle.Certificates.Count == 2);
            Assert.True(string.IsNullOrEmpty(bundle.Metadata));
        }

        [Fact]
        public void TestWithMetadata()
        {
            byte[] p7bData = File.ReadAllBytes(@"Certificates\Bundles\bundleWithMetadata.p7b");
            AnchorBundle bundle = null;
            Assert.Null(Record.Exception(() => bundle = new AnchorBundle(p7bData)));
            Assert.True(!bundle.Certificates.IsNullOrEmpty());
            Assert.True(bundle.Certificates.Count == 2);
            Assert.True(!string.IsNullOrEmpty(bundle.Metadata));
        }

        [Fact]
        public void TestPEM()
        {
            byte[] p7bData = File.ReadAllBytes(@"Certificates\Bundles\bundlePEM.p7b");
            AnchorBundle bundle = null;
            Assert.Null(Record.Exception(() => bundle = new AnchorBundle(p7bData)));
            Assert.True(!bundle.Certificates.IsNullOrEmpty());
        }

        [Fact]
        public void TestSigned()
        {
            X509Certificate2 signingCert = AgentTester.LoadPrivateCerts("redmond").First();
            X509Certificate2Collection certs = AgentTester.LoadPrivateCerts("nhind").GetAllCertificates();

            byte[] p7sData = null;
            Assert.Null(Record.Exception(() => p7sData = AnchorBundle.CreateSigned(certs, signingCert)));
            Assert.True(!p7sData.IsNullOrEmpty());

            AnchorBundle bundle = null;
            Assert.Null(Record.Exception(() => bundle = new AnchorBundle(p7sData, true)));
            Assert.True(!bundle.Certificates.IsNullOrEmpty());
            Assert.True(certs.Count == bundle.Certificates.Count);
        }

        const string PatientTestBundleUrl = "https://bundles.nate-trust.org/NateService/admin/download?type=.p7b&bundleName=NATE%20Blue%20Button%20For%20Consumers%20(NBB4C)";

        //TODO: Need to investigate this more.It may be related to why the trustbundle resolvers sometimes is empty.
        [Fact(Skip = "Fails for FileIOPermission.HasIllegalCharacters on build server.  Works when ran from desktop.  Might be a Windows security issue where a download is not trusted.")]
        public void TestDownloadBundle()
        {
            AnchorBundleDownloader downloader = new AnchorBundleDownloader();
            downloader.MaxRetries = 1;

            AnchorBundle bundle = null;
            Assert.Null(Record.Exception(() => bundle = downloader.Download(new Uri(PatientTestBundleUrl))));
            Assert.True(!bundle.Certificates.IsNullOrEmpty());
        }

        //TODO: Need to investigate this more.  It may be related to why the trustbundle resolvers sometimes is empty.
        [Fact(Skip = "Fails for FileIOPermission.HasIllegalCharacters on build server.  Works when ran from desktop.  Might be a Windows security issue where a download is not trusted.")]
        public void TestDownloadCerts()
        {
            AnchorBundleDownloader downloader = new AnchorBundleDownloader();
            downloader.MaxRetries = 1;

            X509Certificate2Collection bundle = null;
            Assert.Null(Record.Exception(() => bundle = downloader.DownloadCertificates(new Uri(PatientTestBundleUrl))));
            Assert.True(!bundle.IsNullOrEmpty());
        }
    }
}
