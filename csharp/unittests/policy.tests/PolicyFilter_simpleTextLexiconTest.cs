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
        public void testX509SignatureAlgorithm_equals_assertTrue()
        {
            using (Stream stream = "X509.Algorithm = 1.2.840.113549.1.1.5".ToStream())
            {
                X509Certificate2 cert = new X509Certificate2(@"resources/certs/umesh.der");
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                filter.IsCompliant(cert, stream).Should().BeTrue();
            }
        }

        [Fact]
        public void testTBSSerialNumber_assertTrue()
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
    }
}
