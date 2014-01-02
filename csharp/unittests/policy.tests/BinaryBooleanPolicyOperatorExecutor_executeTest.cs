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
using FluentAssertions;
using Health.Direct.Policy.Operators;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class BinaryBooleanPolicyOperatorExecutor_ExecuteTest
    {
        [Fact]
        public void testExecute_logicalAnd_assertResults()
        {
            Assert.True(PolicyOperator<bool>.LOGICAL_AND.Execute(true, true));
            Assert.False(PolicyOperator<bool>.LOGICAL_AND.Execute(true, false));
            Assert.False(PolicyOperator<bool>.LOGICAL_AND.Execute(false, true));
            Assert.False(PolicyOperator<bool>.LOGICAL_AND.Execute(false, false)); 
        }

        [Fact]
        public void testExecute_logicalAnd_stringArguments_assertResults()
        {
            //don't care... maybe...
        }

        [Fact]
        public void testExecute_logicalOr_assertResults()
        {
            OperatorBase equalsOperator = PolicyOperator.FromToken(("=_" + "Boolean_Boolean").GetHashCode());
            equalsOperator.Should().NotBeNull();

            Assert.True(PolicyOperator<bool>.LOGICAL_OR.Execute(true, true));
            Assert.True(PolicyOperator<bool>.LOGICAL_OR.Execute(true, false));
            Assert.True(PolicyOperator<bool>.LOGICAL_OR.Execute(false, true));
            Assert.False(PolicyOperator<bool>.LOGICAL_OR.Execute(false, false)); 
        }

        [Fact]
        public void testExecute_equals_assertResults()
        {
            Assert.True(PolicyOperator<bool, bool, bool>.EQUALS.Execute(true, true));
            Assert.False(PolicyOperator<bool, bool, bool>.EQUALS.Execute(true, false));
            Assert.True(PolicyOperator<int, int, bool>.EQUALS.Execute(123, 123));
            Assert.False(PolicyOperator<int, int, bool>.EQUALS.Execute(123, 456)); 
        }

        [Fact]
        public void testExecute_equals_dynamic_assertResults()
        {
            Delegate del = PolicyOperator<bool, bool, bool>.EQUALS.ExecuteRef;

            Assert.True((bool)del.DynamicInvoke(new object[] { true, true }));
            Assert.False((bool)del.DynamicInvoke(new object[] { true, false }));

            del = PolicyOperator<int, int, bool>.EQUALS.ExecuteRef;
            Assert.True((bool)del.DynamicInvoke(new object[] { 123, 123 }));
            Assert.False((bool)del.DynamicInvoke(new object[] { 123, 456 }));
        }

        [Fact]
        public void testExecute_notEquals_assertResults()
        {
            Assert.False(PolicyOperator<bool, bool>.NOT_EQUALS.Execute(true, true));
            Assert.True(PolicyOperator<bool, bool>.NOT_EQUALS.Execute(true, false));
            Assert.False(PolicyOperator<int, bool>.NOT_EQUALS.Execute(123, 123));
            Assert.True(PolicyOperator<int, bool>.NOT_EQUALS.Execute(123, 456)); 
        }

        [Fact]
        public void testExecute_greater_assertResults()
        {
            Assert.True(PolicyOperator<int, bool>.GREATER.Execute(5, 4));
            Assert.False(PolicyOperator<int, bool>.GREATER.Execute(4, 5));
        }

        [Fact]
        public void testExecute_less_assertResults()
        {
            Assert.True(PolicyOperator<int, bool>.LESS.Execute(4, 5));
            Assert.False(PolicyOperator<int, bool>.LESS.Execute(5, 4));
        }

        [Fact]
        public void testExecute_contains_assertResults()
        {
            var strings = new List<string> {"123", "456", "689"};
            Assert.True(PolicyOperator<string, IList<string>, bool>.CONTAINS.Execute("689", strings));
            Assert.True(PolicyOperator<string, IList<string>, bool>.CONTAINS.Execute("456", strings));
            Assert.False(PolicyOperator<string, IList<string>, bool>.CONTAINS.Execute("777", strings));

            var integers = new List<int> { 123, 456, 689 };
            Assert.True(PolicyOperator<int, IList<int>, bool>.CONTAINS.Execute(689, integers));
            Assert.True(PolicyOperator<int, IList<int>, bool>.CONTAINS.Execute(456, integers));
            Assert.False(PolicyOperator<int, IList<int>, bool>.CONTAINS.Execute(777, integers));
        }


        [Fact]
        public void testExecute_notContains_assertResults()
        {
            var strings = new List<string> { "123", "456", "689" };
            Assert.False(PolicyOperator<string, IList<string>, bool>.NOT_CONTAINS.Execute("689", strings));
            Assert.False(PolicyOperator<string, IList<string>, bool>.NOT_CONTAINS.Execute("456", strings));
            Assert.True(PolicyOperator<string, IList<string>, bool>.NOT_CONTAINS.Execute("777", strings));

            var integers = new List<int> { 123, 456, 689 };
            Assert.False(PolicyOperator<int, IList<int>, bool>.NOT_CONTAINS.Execute(689, integers));
            Assert.False(PolicyOperator<int, IList<int>, bool>.NOT_CONTAINS.Execute(456, integers));
            Assert.True(PolicyOperator<int, IList<int>, bool>.NOT_CONTAINS.Execute(777, integers));
        }


        [Fact]
        public void testExecute_containsRegEx_assertResults()
        {
            var urls = new List<string> {"http://thisis.aurl.com"};
            Assert.True(PolicyOperator<string, IList<string>, bool>.CONTAINS_REG_EX.Execute("http", urls));

            Assert.False(PolicyOperator<string, IList<string>, bool>.CONTAINS_REG_EX.Execute("777", urls));

        }
        [Fact]
        public void testExecute_regEx_assertResults()
        {
            Assert.True(PolicyOperator<string, bool>
                .REG_EX.Execute("http", "http://thisis.aurl.com"));

            Assert.False(PolicyOperator<string, bool>
                .REG_EX.Execute("777", "http://thisis.aurl.com"));
        }



    }
    
}
