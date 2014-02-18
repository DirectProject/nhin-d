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
using FluentAssertions;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.Operators;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class BinaryBooleanPolicyOperatorExecutor_ExecuteTest
    {
        [Fact]
        public void TestExecute_LogicalAnd_AssertResults()
        {
            Assert.True(PolicyOperator<bool>.LOGICAL_AND.Execute(true, true));
            Assert.False(PolicyOperator<bool>.LOGICAL_AND.Execute(true, false));
            Assert.False(PolicyOperator<bool>.LOGICAL_AND.Execute(false, true));
            Assert.False(PolicyOperator<bool>.LOGICAL_AND.Execute(false, false)); 
        }

        [Fact]
        public void TestExecute_LogicalAnd_StringArguments_AssertResults()
        {
            //don't care... maybe...
        }

        [Fact]
        public void TestExecute_LogicalOr_AssertResults()
        {
            OperatorBase equalsOperator = PolicyOperator.FromToken(("=_" + "Boolean_Boolean").GetHashCode());
            equalsOperator.Should().NotBeNull();

            Assert.True(PolicyOperator<bool>.LOGICAL_OR.Execute(true, true));
            Assert.True(PolicyOperator<bool>.LOGICAL_OR.Execute(true, false));
            Assert.True(PolicyOperator<bool>.LOGICAL_OR.Execute(false, true));
            Assert.False(PolicyOperator<bool>.LOGICAL_OR.Execute(false, false)); 
        }

        [Fact]
        public void TestExecute_Equals_AssertResults()
        {
            Assert.True(PolicyOperator<bool, bool, bool>.EQUALS.Execute(true, true));
            Assert.False(PolicyOperator<bool, bool, bool>.EQUALS.Execute(true, false));
            Assert.True(PolicyOperator<int, int, bool>.EQUALS.Execute(123, 123));
            Assert.False(PolicyOperator<int, int, bool>.EQUALS.Execute(123, 456)); 
        }

        [Fact]
        public void TestExecute_Equals_Convertparamspecial_AssertResults()
        {
            Assert.True(PolicyOperator<Int64, Int64, bool>.EQUALS.Execute("00F74F1C4FE4E1762E".HexAsLong(), "f74f1c4fe4e1762e".HexAsLong()));
            Assert.True(PolicyOperator<Int64, String, bool>.EQUALS.Execute("00F74F1C4FE4E1762E".HexAsLong(), "f74f1c4fe4e1762e"));

            Delegate del = PolicyOperator<Int64, String, bool>.EQUALS.ExecuteRef;
            Assert.True((bool)del.DynamicInvoke(new object[] { "00F74F1C4FE4E1762E".HexAsLong(), "f74f1c4fe4e1762e" }));

        }

        [Fact]
        public void TestExecute_Equals_Convertparam_AssertResults()
        {
            Assert.True(PolicyOperator<int, int, bool>.EQUALS.Execute(123, 123));
            Assert.True(PolicyOperator<int, String, bool>.EQUALS.Execute(123, "123"));

            Delegate del = PolicyOperator<int, String, bool>.EQUALS.ExecuteRef;
            Assert.True((bool)del.DynamicInvoke(new object[] { 123, "123"}));

        }


        [Fact]
        public void TestExecute_Equals_Dynamic_AssertResults()
        {
            Delegate del = PolicyOperator<bool, bool, bool>.EQUALS.ExecuteRef;

            Assert.True((bool)del.DynamicInvoke(new object[] { true, true }));
            Assert.False((bool)del.DynamicInvoke(new object[] { true, false }));

            del = PolicyOperator<int, int, bool>.EQUALS.ExecuteRef;
            Assert.True((bool)del.DynamicInvoke(new object[] { 123, 123 }));
            Assert.False((bool)del.DynamicInvoke(new object[] { 123, 456 }));
        }

        [Fact]
        public void TestExecute_NotEquals_AssertResults()
        {
            Assert.False(PolicyOperator<bool, bool, bool>.NOT_EQUALS.Execute(true, true));
            Assert.True(PolicyOperator<bool, bool, bool>.NOT_EQUALS.Execute(true, false));
            Assert.False(PolicyOperator<int, int, bool>.NOT_EQUALS.Execute(123, 123));
            Assert.True(PolicyOperator<int, int, bool>.NOT_EQUALS.Execute(123, 456)); 
        }

        [Fact]
        public void TestExecute_NotEquals_Convertparamspecial_AssertResults()
        {
            Assert.False(PolicyOperator<Int64, Int64, bool>.NOT_EQUALS.Execute("00F74F1C4FE4E1762E".HexAsLong(), "f74f1c4fe4e1762e".HexAsLong()));
            Assert.False(PolicyOperator<Int64, String, bool>.NOT_EQUALS.Execute("00F74F1C4FE4E1762E".HexAsLong(), "f74f1c4fe4e1762e"));
            Assert.True(PolicyOperator<Int64, String, bool>.NOT_EQUALS.Execute("00F74F1C4FE4E17600".HexAsLong(), "f74f1c4fe4e1762e"));

            Delegate del = PolicyOperator<Int64, String, bool>.NOT_EQUALS.ExecuteRef;
            Assert.False((bool) del.DynamicInvoke(new object[] {"00F74F1C4FE4E1762E".HexAsLong(), "f74f1c4fe4e1762e"}));
            Assert.True((bool)del.DynamicInvoke(new object[] { "00F74F1C4FE4E17600".HexAsLong(), "f74f1c4fe4e1762e" }));

        }

        [Fact]
        public void TestExecute_NotEquals_Convertparam_AssertResults()
        {
            Assert.False(PolicyOperator<int, int, bool>.NOT_EQUALS.Execute(123, 123));
            Assert.False(PolicyOperator<int, String, bool>.NOT_EQUALS.Execute(123, "123"));

            Delegate del = PolicyOperator<int, String, bool>.NOT_EQUALS.ExecuteRef;
            Assert.False((bool)del.DynamicInvoke(new object[] { 123, "123" }));

        }


        [Fact]
        public void TestExecute_NotEquals_Dynamic_AssertResults()
        {
            Delegate del = PolicyOperator<bool, bool, bool>.NOT_EQUALS.ExecuteRef;

            Assert.False((bool)del.DynamicInvoke(new object[] { true, true }));
            Assert.True((bool)del.DynamicInvoke(new object[] { true, false }));

            del = PolicyOperator<int, int, bool>.NOT_EQUALS.ExecuteRef;
            Assert.False((bool)del.DynamicInvoke(new object[] { 123, 123 }));
            Assert.True((bool)del.DynamicInvoke(new object[] { 123, 456 }));
        }


        [Fact]
        public void TestExecute_Greater_AssertResults()
        {
            Assert.True(PolicyOperator<int, int>.GREATER.Execute(5, 4));
            Assert.False(PolicyOperator<int, int>.GREATER.Execute(4, 5));
        }

        [Fact]
        public void TestExecute_Less_AssertResults()
        {
            Assert.True(PolicyOperator<int, int>.LESS.Execute(4, 5));
            Assert.False(PolicyOperator<int, int>.LESS.Execute(5, 4));
        }

        [Fact]
        public void TestExecute_Contains_AssertResults()
        {
            var strings = new List<string> {"123", "456", "689"};
            strings.Any(m => m == "123").Should().BeTrue();
            Assert.True(PolicyOperator<IList<string>, String, bool>.CONTAINS.Execute(strings, "689"));
            Assert.True(PolicyOperator<IList<string>, String, bool>.CONTAINS.Execute(strings, "456"));
            Assert.False(PolicyOperator<IList<string>, String, bool>.CONTAINS.Execute(strings, "777"));

            var integers = new List<int> { 123, 456, 689 };
            Assert.True(PolicyOperator<IList<int>, int, bool>.CONTAINS.Execute(integers, 689));
            Assert.True(PolicyOperator<IList<int>, int, bool>.CONTAINS.Execute(integers, 456));
            Assert.False(PolicyOperator<IList<int>, int, bool>.CONTAINS.Execute(integers, 777));
        }

        

        [Fact]
        public void TestExecute_NotContains_AssertResults()
        {
            var strings = new List<string> { "123", "456", "689" };
            Assert.False(PolicyOperator<IList<string>, String, bool>.NOT_CONTAINS.Execute(strings, "689"));
            Assert.False(PolicyOperator<IList<string>, String, bool>.NOT_CONTAINS.Execute(strings, "456"));
            Assert.True(PolicyOperator<IList<string>, String, bool>.NOT_CONTAINS.Execute(strings, "777"));

            var integers  = new List<int> { 123, 456, 689 };
            Assert.False(PolicyOperator<IList<int>, int, bool>.NOT_CONTAINS.Execute(integers, 689));
            Assert.False(PolicyOperator<IList<int>, int, bool>.NOT_CONTAINS.Execute(integers, 456));
            Assert.True(PolicyOperator<IList<int>, int, bool>.NOT_CONTAINS.Execute(integers, 777));
        }


        [Fact]
        public void TestExecute_ContainsRegEx_AssertResults()
        {
            var urls = new List<string> {"http://thisis.aurl.com"};
            Assert.True(PolicyOperator<IList<String>, String, bool>.CONTAINS_REG_EX.Execute(urls, "http"));

            Assert.False(PolicyOperator<IList<String>, String, bool>.CONTAINS_REG_EX.Execute(urls, "777"));

        }
        [Fact]
        public void TestExecute_RegEx_AssertResults()
        {
            Assert.True(PolicyOperator<string, bool>
                .REG_EX.Execute("http", "http://thisis.aurl.com"));

            Assert.False(PolicyOperator<string, bool>
                .REG_EX.Execute("777", "http://thisis.aurl.com"));
        }



    }
    
}
