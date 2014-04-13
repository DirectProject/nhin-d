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
using Health.Direct.Policy.Operators;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class UnaryBooleanPolicyOperatorExecutor_executeTest
    {
        [Fact]
        public void testExecute_logicalNot_assertResults()
        {
            Assert.False(PolicyOperator<bool>.LOGICAL_NOT.Execute(true));
            Assert.True(PolicyOperator<bool>.LOGICAL_NOT.Execute(false));
        }

        [Fact]
        public void testExecute_empty_assertResults()
        {
            var op1 = new List<string> {"A"};
            Assert.False(PolicyOperator<IList<string>, bool>.EMPTY.Execute(op1));
            op1 = new List<string>();
            Assert.True(PolicyOperator<IList<string>, bool>.EMPTY.Execute(op1));
        }

        [Fact]
        public void testExecute_notEmpty_assertResults()
        {
            var op1 = new List<string> {"A"};
            Assert.True(PolicyOperator<IList<string>, bool>.NOT_EMPTY.Execute(op1));
            op1 = new List<string>();
            Assert.False(PolicyOperator<IList<string>, bool>.NOT_EMPTY.Execute(op1));
        }

        [Fact]
        public void testExecute_uriValidate_assertResults()
        {
            var fakeDiagnostics = new FakeDiagnostics(typeof(UriValid<>));
            PolicyOperator<string>.URI_VALIDATE.Warning += fakeDiagnostics.OnWarn;

            // valid
            Assert.True(PolicyOperator<string>.URI_VALIDATE.Execute("http://www.cerner.com/CPS"));// not found
            Assert.False(PolicyOperator<string>.URI_VALIDATE.Execute("http://www.google.com/333333"));
            Assert.Equal("http://www.google.com/333333:The remote server returned an error: (404) Not Found.", fakeDiagnostics.ActualErrorMessages[0]);

            // host not found
            Assert.False(PolicyOperator<string>.URI_VALIDATE.Execute("http://bogus.unit.test.ccc"));
            Assert.Equal("http://bogus.unit.test.ccc:The remote name could not be resolved: 'bogus.unit.test.ccc'", fakeDiagnostics.ActualErrorMessages[1]);

            // invalid
            Assert.False(PolicyOperator<string>.URI_VALIDATE.Execute("htt://invalid.lab"));
            Assert.Equal("htt://invalid.lab:The URI prefix is not recognized.", fakeDiagnostics.ActualErrorMessages[2]);

            // invalid
            Assert.False(PolicyOperator<string>.URI_VALIDATE.Execute("http//invalid.lab"));
            Assert.Equal("http//invalid.lab:Invalid URI: The format of the URI could not be determined.", fakeDiagnostics.ActualErrorMessages[3]);
        }
        
    }

    public class FakeDiagnostics
    {
        public bool Called;
        readonly Type m_operatorType;

        public FakeDiagnostics(Type operatorType)
        {
            m_operatorType = operatorType;
        }

        private readonly List<string> _actualMessages = new List<string>();
        public List<string> ActualErrorMessages
        {
            get { return _actualMessages; }
        }

        public void OnWarn(OperatorBase op, string message)
        {
            _actualMessages.Add(message);
        }
    }
}
