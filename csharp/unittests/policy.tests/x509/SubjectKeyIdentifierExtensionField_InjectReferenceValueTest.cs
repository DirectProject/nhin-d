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
using System.Security.Cryptography.X509Certificates;
using FluentAssertions;
using Health.Direct.Policy.X509;
using Xunit;

namespace Health.Direct.Policy.Tests.x509
{
    public class SubjectKeyIdentifierExtensionField_InjectReferenceValueTest
    {

        [Fact]
        public void testInjectRefereneValue_rdnAttributeDoesNotExist_notRequired_assertValueCollection()
        {
            var cert = new X509Certificate2(@"resources/certs/cernerDemosCaCert.der");
            var field = new SubjectKeyIdentifierExtensionField(false);
            field.InjectReferenceValue(cert);
            field.GetPolicyValue().GetPolicyValue().Should().Be(string.Empty);
        }

        [Fact]
        public void testInjectRefereneValue_subjectAltNameDoesNotExist_required_assertException()
        {
            var cert = new X509Certificate2(@"resources/certs/cernerDemosCaCert.der");
            var field = new SubjectKeyIdentifierExtensionField(true);

            Action action = () => field.InjectReferenceValue(cert);
            action.ShouldThrow<PolicyRequiredException>();
        }

        [Fact]
        public void testInjectRefereneValue_keyIdUsageExists_assertValue()
        {
            var cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
            var field = new SubjectKeyIdentifierExtensionField(false);
            field.InjectReferenceValue(cert);
            field.GetPolicyValue().GetPolicyValue().Should().BeEquivalentTo("e0f63ccfeb5ce3eef5c04efe8084c92bc628682c");
        }

        [Fact]
        public void testInjectRefereneValue_noInjection_getPolicyValue_assertException()
        {
            var field = new SubjectKeyIdentifierExtensionField(true);
            Action action = () => field.GetPolicyValue();
            action.ShouldThrow<InvalidOperationException>();
        }
    }
}