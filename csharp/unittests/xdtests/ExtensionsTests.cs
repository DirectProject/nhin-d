/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;

using Health.Direct.Common.Metadata;

using Xunit;

namespace Health.Direct.Xd.Tests
{
    public class ExtensionTests
    {
        [Fact]
        public void HL7SimpleDate()
        {
            DateTime? dt = new DateTime(2000, 09, 16, 0, 0, 0, DateTimeKind.Utc);
            Assert.Equal("20000916000000", dt.ToHL7Date());
        }

        [Fact]
        public void HL7DateHMS()
        {
            DateTime? dt = new DateTime(2000, 09, 16, 12, 9, 2, DateTimeKind.Utc);
            Assert.Equal("20000916120902", dt.ToHL7Date());
        }

        [Fact]
        public void StringBreakWithRemainder()
        {
            string testString = "abcabcabcabca";
            string[] expected = new string[] { "abc", "abc", "abc", "abc", "a" };
            Assert.Equal(expected, testString.Break(3).ToArray());
        }

        [Fact]
        public void StringBreakExact()
        {
            string testString = "abcabcabc";
            string[] expected = new string[] { "abc", "abc", "abc" };
            Assert.Equal(expected, testString.Break(3).ToArray());
        }

        [Fact]
        public void StringBreakShorterThanN()
        {
            string testString = "abc";
            string[] expected = new string[] { "abc" };
            Assert.Equal(expected, testString.Break(100).ToArray());
        }
    }
}