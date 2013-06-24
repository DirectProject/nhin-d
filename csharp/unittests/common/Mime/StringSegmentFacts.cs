/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     john.theisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;

using Health.Direct.Common.Mime;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mime
{
    public class StringSegmentFacts
    {
        [Fact]
        public void DefaultConstructor()
        {
            var segment = new StringSegment();
            Assert.Equal(0, segment.Length);

            Assert.True(segment.IsEmpty);
            Assert.True(segment.IsNull);
        }

        [Fact]
        public void Null()
        {
            var segment = new StringSegment(null);
            Assert.Equal(0, segment.Length);
            Assert.True(segment.IsEmpty);
            Assert.True(segment.IsNull);
        }

        [Fact]
        public void NullEqNull()
        {
            var nullSegment = new StringSegment(null);
            Assert.Equal(StringSegment.Null, nullSegment);
        }

        [Fact]
        public void NullNeNull()
        {
            var nullSegment = new StringSegment(TestContent);
            Assert.NotEqual(StringSegment.Null, nullSegment);
        }

        [Fact]
        public void Empty()
        {
            var segment = new StringSegment("");
            Assert.Equal(0, segment.Length);
            Assert.True(segment.IsEmpty);
            Assert.False(segment.IsNull);
        }

        [Theory]
        [InlineData("", 0, -1, "")]
        [InlineData("abc", 0, 2, "abc")]
        [InlineData("abc", 0, 1, "ab")]
        public void TestToString(string content, int startIndex, int endIndex, string expected)
        {
            var segment = new StringSegment(content, startIndex, endIndex);
            Assert.Equal(expected, segment.ToString());
        }

        private const string TestContent = "The quick brown fox jumped over the lazy dog.";

        [Theory]
        [InlineData(TestContent, 4, 8, "quick")]
        public void Segmented(string content, int start, int end, string expectedSegment)
        {
            var segment = new StringSegment(content, start, end);
            Assert.Equal(end, segment.EndIndex);
            Assert.False(segment.IsEmpty);
            Assert.False(segment.IsNull);
            Assert.Equal(expectedSegment.Length, segment.Length);
            Assert.Equal(TestContent, segment.Source);
            Assert.Equal(start, segment.StartIndex);
            Assert.Equal(expectedSegment, segment.ToString());
        }

        [Fact]
        public void ToStringOnNullSegment()
        {
            StringSegment nullSegment = StringSegment.Null;
            Assert.Equal("", nullSegment.ToString());
        }

        [Fact]
        public void NullSource()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => new StringSegment(null, 0, 0));
            Assert.Equal("source", ex.ParamName);
        }

        [Theory]
        [InlineData("lorem ipsum", int.MinValue)]
        [InlineData("lorem ipsum", int.MaxValue)]
        [InlineData("lorem ipsum", -1)]
        [InlineData("lorem ipsum", 12)]
        public void InvalidStartIndex(string source, int startIndex)
        {
            var ex = Assert.Throws<ArgumentException>(() => new StringSegment(source, startIndex, 5));
            Assert.Equal("startIndex", ex.ParamName);
        }

        [Theory]
        [InlineData("lorem ipsum", int.MinValue)]
        [InlineData("lorem ipsum", int.MaxValue)]
        [InlineData("lorem ipsum", -2)]
        [InlineData("lorem ipsum", 11)]
        public void InvalidEndIndex(string source, int endIndex)
        {
            var ex = Assert.Throws<ArgumentException>(() => new StringSegment(source, 0, endIndex));
            Assert.Equal("endIndex", ex.ParamName);
        }

        [Theory]
        [InlineData("lorem ipsum")]
        public void Item(string content)
        {
            var segment = new StringSegment(content);
            for (int i=0; i<content.Length; i++)
            {
                Assert.Equal(content[i], segment[i]);
            }
        }

        [Theory]
        [InlineData(null)]
        [InlineData("")]
        [InlineData("abcd")]
        [InlineData("ABCD")]
        public void TestEquals(string content)
        {
            var segment = new StringSegment(content);
            Assert.True(segment.Equals(segment));
        }

        [Theory]
        [InlineData("abcd")]
        [InlineData("ABCD")]
        public void TestSubstring(string content)
        {
            int length = content.Length;
            var segment = new StringSegment(content);
            for (int i = 0; i < content.Length; i++)
            {
                Assert.Equal(content.Substring(0, i), segment.Substring(0, i));
                Assert.Equal(content.Substring(i, length - i), segment.Substring(i, length - i));
            }
        }

        [Fact]
        public void TestSubstringToEnd()
        {
            var segment = new StringSegment(TestContent);
            for (int i = 0; i < TestContent.Length; i++)
            {
                Assert.Equal(TestContent.Substring(i), segment.Substring(i));
            }
        }

        [Theory]
        [InlineData("", "", true)]
        [InlineData("abcde", "abcde", true)]
        [InlineData("ABCDE", "abcde", true)]
        [InlineData("ABCDE", "abcde", true)]
        [InlineData("ABCDE", "abcde", true)]
        [InlineData("ABCDE", "", false)]
        [InlineData("ABCDE", "abcd", false)]
        [InlineData("", "abcde", false)]
        [InlineData("abcd", "abcde", false)]
        public void EqualsString(string content, string content2, bool expected)
        {
            var segment = new StringSegment(content);
            Assert.Equal(expected, segment.Equals(content2));
        }

        [Theory]
        [InlineData("", 1, 0)]
        [InlineData("", 0, 1)]
        [InlineData("a", 1, 1)]
        [InlineData("a", 0, 2)]
        public void SubstringIndexOutOfRange(string content, int startAt, int length)
        {
            Assert.Throws<IndexOutOfRangeException>(() => new StringSegment(content).Substring(startAt, length));
        }

        [Theory]
        [InlineData("", 1)]
        [InlineData("a", 2)]
        public void SubstringToEndIndexOutOfRange(string content, int startAt)
        {
            Assert.Throws<IndexOutOfRangeException>(() => new StringSegment(content).Substring(startAt));
        }

        [Theory]
        [InlineData("abcde", "", 0)]
        [InlineData("abcde", "a", 0)]
        [InlineData("abcde", "ab", 0)]
        [InlineData("abcde", "bcd", 1)]
        [InlineData("abcde", "xyz", -1)]
        [InlineData("abcde", "E", 4)]
        [InlineData("abcde", "DE", 3)]
        [InlineData("abc", "abcde", -1)]
        public void IndexOf(string content, string search, int expected)
        {
            var segment = new StringSegment(content);
            Assert.Equal(expected, segment.IndexOf(search));
        }

        [Fact]
        public void StartsWith()
        {
            var segment = new StringSegment(TestContent);
            for (int i=0; i<TestContent.Length; i++)
            {
                string prefix = TestContent.Substring(0, i);
                Assert.True(segment.StartsWith(prefix));
                Assert.True(segment.StartsWith(prefix.ToLower()));
            }
        }

        [Fact]
        public void StartsWithPrefixTooLong()
        {
            var segment = new StringSegment("abc");
            Assert.False(segment.StartsWith("abcde"));
        }

        [Fact]
        public void UnionInvalidSegment()
        {
            Assert.Throws<ArgumentNullException>(() => new StringSegment(TestContent).Union(StringSegment.Null));
        }

        [Fact]
        public void UnionSegmentNullAccretion()
        {
            var segment = StringSegment.Null;
            var testSegment = new StringSegment(TestContent);
            segment.Union(testSegment);
            Assert.Equal(testSegment.Source, segment.Source);
            Assert.Equal(testSegment.StartIndex, segment.StartIndex);
            Assert.Equal(testSegment.EndIndex, segment.EndIndex);
        }

        [Fact]
        public void UnionNotReferencingSameSource()
        {
            var segment = new StringSegment("abcde");
            var segment2 = new StringSegment("fghij");
            Assert.Throws<InvalidOperationException>(() => segment.Union(segment2));
        }

        [Theory]
        [InlineData(0, 5, 5, 10, 0, 10)]
        [InlineData(1, 5, 0, 3, 0, 5)]
        [InlineData(0, 2, 4, 10, 0, 10)] //Disjoint segments
        [InlineData(4, 10, 0, 2, 0, 10)] //Same but reversed
        public void UnionCases(int firstStart, int firstEnd, int secondStart, int secondEnd, int expectedStart, int expectedEnd)
        {
            var segment = new StringSegment(TestContent, firstStart, firstEnd);
            var segment2 = new StringSegment(TestContent, secondStart, secondEnd);
            segment.Union(segment2);
            Assert.Equal(expectedStart, segment.StartIndex);
            Assert.Equal(expectedEnd, segment.EndIndex);
        }

        [Theory]
        [InlineData(0, null)]
        [InlineData(0, "")]
        [InlineData(1, "0")]
        [InlineData(2, "0,1")]
        [InlineData(3, "0,1,2")]
        public void Split(int expectedCount, string value)
        {
            Assert.Equal(expectedCount, StringSegment.Split(value, ',').Count());
        }

        [Theory]
        [InlineData(2, "pqr\\:xyz:123")]
        [InlineData(2, "abc\"quoted:\"pqr:123")]
        [InlineData(2, "abc\"quoted:\"\\:pqr:123")]
        [InlineData(2, "abc\"quo\\:ted:\"\\:pqr:123")]
        [InlineData(2, "\"quoted:\"pqr:123")]
        [InlineData(2, "pqr:123")]
        [InlineData(2, "\"quoted:\"pqr\"quoted:foo:bar\":123")]
        [InlineData(2, ":123")]
        [InlineData(4, "abc\"quoted:\"pqr:123\"quoted:\":456:32")]
        [InlineData(2, "abcde\\:fgh:123\\:536")]
        public void SplitWithQuotes(int expectedCount, string value)
        {
            Assert.Equal(expectedCount, StringSegment.Split(value, ':', '"').Count());
        }
    }
}
