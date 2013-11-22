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
using System;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using System.Net.Mail;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client;
using Health.Direct.Common.Certificates;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.SmtpAgent.Tests
{
    /// <summary>
    /// This is really an integration test and will eventually be moved out of here. Incrementally placed here for now. 
    /// These tests are currently disabled in 'normal' runs - since they require the Config Service to be installed
    /// However, they are here for debugging etc
    /// 
    /// </summary>
    public class TestConfigService
    {                
        
        [Theory]
        [InlineData(false)]
        [InlineData(true)]
        public void TestResolverInSecure(bool validateAddress)
        {
            this.RunTest(validateAddress, false);
        }

        
        
        //[Theory]
        [InlineData(false)]
        [InlineData(true)]
        public void TestResolverSecure(bool validateAddress)
        {
            this.RunTest(validateAddress, true);
        }
        
        void RunTest(bool validateAddress, bool secure)
        {
            ConfigCertificateResolver resolver = this.CreateResolver(validateAddress, secure);
              
            X509Certificate2Collection matches = resolver.GetCertificates(new MailAddress("toby@redmond.hsgincubator.com"));
            Assert.True(!matches.IsNullOrEmpty());

            matches = resolver.GetCertificates(new MailAddress("biff@nhind.hsgincubator.com"));
            Assert.True(!matches.IsNullOrEmpty());            
            //
            // This address does not exist, but should find org cert
            //
            matches = resolver.GetCertificates(new MailAddress("toto@nhind.hsgincubator.com"));
            Assert.True(!matches.IsNullOrEmpty());
            this.VerifyIsOrgCert(matches, "nhind.hsgincubator.com");
            //
            // This should match NOTHING, since no org or address
            //
            matches = resolver.GetCertificates(new MailAddress("yossarian@xyz.com"));
            Assert.True(matches.IsNullOrEmpty());            
            //
            // Should return ORG cert only
            //
            resolver.OrgCertificatesOnly = true;
            matches = resolver.GetCertificates(new MailAddress("toby@redmond.hsgincubator.com"));
            Assert.True(!matches.IsNullOrEmpty());
            this.VerifyIsOrgCert(matches, "redmond.hsgincubator.com");
        }
        
        void VerifyIsOrgCert(X509Certificate2Collection matches, string org)
        {
            foreach (X509Certificate2 cert in matches)
            {
                Assert.True(cert.MatchEmailNameOrName(org));
            }
        }
                        
        ConfigCertificateResolver CreateResolver(bool verifyAddress, bool secure)
        {
            string scheme = secure ? "https://" : "http://";
            
            return new ConfigCertificateResolver(
                new ClientSettings()
                {
                    Url = scheme + "localhost/ConfigService/CertificateService.svc/Certificates",
                    Secure = secure
                },
                !verifyAddress ? null :
                new ClientSettings()
                {
                    Url = scheme + "localhost/ConfigService/DomainManagerService.svc/Addresses",
                    Secure = secure
                }
              );
        }
    }
}