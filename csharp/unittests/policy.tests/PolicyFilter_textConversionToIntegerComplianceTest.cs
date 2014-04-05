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
using Health.Direct.Common.Policies;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.Impl;
using Health.Direct.Policy.Machine;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class PolicyFilter_TextConversionToIntegerComplianceTest
    {
        [Fact]
        public void testCompliance_simpleTextLexicon_equalsIntegerCertValue_assertTrue()
        {
            using (Stream stream = "X509.TBS.EXTENSION.KeyUsage = 224".ToStream())
            {
                var cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new PolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void testCompliance_simpleTextLexicon_notEqualsIntegerCertValue_assertTrue()
        {
            using (Stream stream = "X509.TBS.EXTENSION.KeyUsage != 223".ToStream())
            {
                var cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new PolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void testCompliance_simpleTextLexicon_lessThanAndGreaterThanIntegerCertValue_assertTrue()
        {
            using (Stream stream = "(X509.TBS.EXTENSION.KeyUsage > 0) && (X509.TBS.EXTENSION.KeyUsage < 225)".ToStream())
            {
                var cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new PolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void testCompliance_simpleTextLexicon_bitWiseAndIntegerCertValue_assertEquals()
        {
            using (Stream stream = "(X509.TBS.EXTENSION.KeyUsage & 224) = 224".ToStream())
            {
                var cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new PolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void testCompliance_simpleTextLexicon_bitWiseAndIntegerCertValue_assertNotEquals()
        {
            using (Stream stream = "(X509.TBS.EXTENSION.KeyUsage & 200) != 224".ToStream())
            {
                var cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new PolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void testCompliance_simpleTextLexicon_bitWiseOrIntegerCertValue_assertEquals()
        {
            using (Stream stream = "(X509.TBS.EXTENSION.KeyUsage | 0) = 224".ToStream())
            {
                var cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new PolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void testCompliance_simpleTextLexicon_bitWiseOrIntegerCertValue_assertNotEquals()
        {
            using (Stream stream = "(X509.TBS.EXTENSION.KeyUsage | 255) != 224".ToStream())
            {
                var cert = new X509Certificate2(@"resources/certs/AlAnderson@hospitalA.direct.visionshareinc.com.der");
                IPolicyFilter filter = new PolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

    }
}
