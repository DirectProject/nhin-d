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


using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using FluentAssertions;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.X509;
using Health.Direct.Policy.X509.Standard;
using Xunit;

namespace Health.Direct.Policy.Tests.x509
{
    public class IssuerAttributeField_injectReferenceValueTest
    {
        [Fact]
        public void TestInjectRefereneValue_RDNAttributeDoesNotExist_NotRequired_AssertValueCollection()
        {
            var cert = new X509Certificate2(@"resources/certs/altNameOnly.der");
            var field = new IssuerAttributeField(false, RDNAttributeIdentifier.INITIALS);

            field.InjectReferenceValue(cert);
            IList<String> values = field.GetPolicyValue().GetPolicyValue();
            values.Count.Should().Be(0);
        }

        [Fact]
        public void TestInjectRefereneValue_RDNAttributeDoesNotExist_Required_ThrowException()
        {
            var cert = new X509Certificate2(@"resources/certs/altNameOnly.der");
            var field = new IssuerAttributeField(true, RDNAttributeIdentifier.INITIALS);

            Action action = () => field.InjectReferenceValue(cert);
            action.ShouldThrow<PolicyRequiredException>();
        }


        [Fact]
        public void TestInjectRefereneValue_RDNSingleAttributeExists_AssertValue()
        {
            var cert = new X509Certificate2(@"resources/certs/altNameOnly.der");
            var field = new IssuerAttributeField(true, RDNAttributeIdentifier.COMMON_NAME);
            field.InjectReferenceValue(cert);

            IList<String> values = field.GetPolicyValue().GetPolicyValue();
            values.Count.Should().Be(1);
            values.First().Should().Be("test.email.com");
        }

        [Fact]
        public void TestInjectRefereneValue_DistinguishedName_AssertValue()
        {
            var cert = new X509Certificate2(@"resources/certs/altNameOnly.der");
            var field = new IssuerAttributeField(true, RDNAttributeIdentifier.DISTINGUISHED_NAME);
            field.InjectReferenceValue(cert);

            IList<String> values = field.GetPolicyValue().GetPolicyValue();
            values.Count.Should().Be(1);
            values.First().Should().Be("O=Cerner,L=Kansas City,S=MO,C=US,CN=test.email.com");
        }

        [Fact]
        public void TestInjectRefereneValue_NoInjection_GetPolicyValue_AssertException()
        {
            var field = new IssuerAttributeField(true, RDNAttributeIdentifier.COMMON_NAME);

            Action action = () => field.GetPolicyValue();
            action.ShouldThrow<InvalidOperationException>();
        }
    }
}
