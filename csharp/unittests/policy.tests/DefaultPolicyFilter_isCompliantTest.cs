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
using FluentAssertions;
using Moq;
using System.Collections.Generic;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class DefaultPolicyFilter_IsCompliantTest
    {
        [Fact(Skip = "Xml parser not implemented...  Maybe never.")]
        public void testIsCompliant_parse_engineReturnsCompliant_assertTrue() 
	    {
            using (Stream stream = File.OpenRead("./resources/policies/dataEnciphermentOnlyRequired.xml"))
            {
                Mock<ICompiler> mockCompiler = new Mock<ICompiler>();
                Mock<IExecutionEngine> mockEngine = new Mock<IExecutionEngine>();
                Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();
                mockEngine.Setup(e => e.Evaluate(It.IsAny<IList<IOpCode>>())).Returns(true);

                DefaultPolicyFilter filter = new DefaultPolicyFilter(mockCompiler.Object, mockEngine.Object, new XMLLexiconPolicyParser());
                filter.IsCompliant(mockCert.Object, stream);

            }
	    }

        [Fact]
        public void testIsCompliant_engineReturnsCompliant_assertTrue() 
        {
            Mock<ICompiler> mockCompiler = new Mock<ICompiler>();
            Mock<IExecutionEngine> mockEngine = new Mock<IExecutionEngine>();
            Mock<IPolicyExpression> mockExpression = new Mock<IPolicyExpression>();
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();

            mockEngine.Setup(e => e.Evaluate(It.IsAny<IList<IOpCode>>())).Returns(true);
            DefaultPolicyFilter filter = new DefaultPolicyFilter(mockCompiler.Object, mockEngine.Object);
            filter.IsCompliant(mockCert.Object, mockExpression.Object);
        }

        [Fact]
        public void testIsCompliant_missingComplier_assertException()
        {
            Mock<IPolicyExpression> mockExpression = new Mock<IPolicyExpression>();
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();

            DefaultPolicyFilter filter = new DefaultPolicyFilter(null, null);
            Action action = () => filter.IsCompliant(mockCert.Object, mockExpression.Object);
            action.ShouldThrow<InvalidOperationException>().WithMessage("Compiler cannot be null");

        }

        [Fact]
        public void testIsCompliant_missingEngine_assertException()
        {
            Mock<ICompiler> mockCompiler = new Mock<ICompiler>();
            Mock<IPolicyExpression> mockExpression = new Mock<IPolicyExpression>();
            Mock<X509Certificate2> mockCert = new Mock<X509Certificate2>();

            DefaultPolicyFilter filter = new DefaultPolicyFilter(mockCompiler.Object, null);
            Action action = () => filter.IsCompliant(mockCert.Object, mockExpression.Object);
            action.ShouldThrow<InvalidOperationException>().WithMessage("Execution engine cannot be null");
        }

    }
}
