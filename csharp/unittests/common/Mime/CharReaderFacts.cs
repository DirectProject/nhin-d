/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     john.theisen@kryptiq.com
    Arien Malec      arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Health.Direct.Common.Mime;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mime
{
    public class CharReaderFacts
    {
        private const string TestContent = "the quick brown fox";

        [Fact]
        public void CharReaderFromSegment()
        {
            var segment = new StringSegment(TestContent);
            var reader = new CharReader(segment);
            Assert.False(reader.IsDone);
            Assert.Equal(-1, reader.Position);
        }

        [Theory]
        [InlineData(null)]
        [InlineData("")]
        public void CharReaderNullOrEmptySource(string source)
        {
            Assert.Throws<ArgumentException>(() => new CharReader(source));
        }

        [Fact]
        public void CharReaderShouldReadCharacters()
        {
            string source = "abc";
            CharReader reader = new CharReader(source);
            Assert.False(reader.IsDone);
            Assert.Equal('a', reader.Read());
            Assert.Equal('b', reader.Read());
            Assert.Equal('c', reader.Read());
            Assert.Equal(CharReader.EOF, reader.Read());
            Assert.True(reader.IsDone);
        }

        [Fact]
        public void CharReaderShouldReadToCharacter()
        {
            StringSegment source = new StringSegment("abc:123");
            CharReader reader = new CharReader(source);
            reader.ReadTo(':', false);
            Assert.Equal(3, reader.Position);
        }

        [Fact]
        public void CharReaderShouldSkipEscape()
        {
            string source = "a\\:c:123";
            CharReader reader = new CharReader(source);

            reader.ReadTo(':', true);
            Assert.Equal(4, reader.Position);
        }

        [Fact]
        public void CharReaderNotAtEndIfFindsCharacter()
        {
            string source = "abc:123";
            CharReader reader = new CharReader(source);

            bool found = reader.ReadTo(':', false);
            Assert.True(found);
            Assert.False(reader.IsDone);
        }

        [Fact]
        public void CharReaderShouldBeAtEndIfItDoesNotFindChar()
        {
            string source = "abc:123";
            CharReader reader = new CharReader(source);

            bool found = reader.ReadTo('?', false);
            Assert.False(found);
            Assert.True(reader.IsDone);
        }

        [Fact]
        public void CharReaderKeepsReadingAfterFoundPosition()
        {
            string source = "abc:123";
            CharReader reader = new CharReader(source);
            reader.ReadTo(':', false);
            Assert.Equal('1', reader.Read());
        }

        [Fact]
        public void GetSegmentShouldReturnSegment()
        {
            string source = "abc:123";
            CharReader reader = new CharReader(source);
            Assert.Equal("abc", reader.GetSegment(0, 2).ToString());
            Assert.Equal("123", reader.GetSegment(4, 6).ToString());
        }
        
        [Theory]
        [InlineData("pqr\\:xyz:123")]
        [InlineData("abc\"quoted:\"pqr:123")]
        [InlineData("abc\"quoted:\"\\:pqr:123")]
        [InlineData("abc\"quo\\:ted:\"\\:pqr:123")]
        [InlineData("\"quoted:\"pqr:123")]
        [InlineData("pqr:123")]
        [InlineData("\"quoted:\"pqr\"quoted:foo:bar\":123")]
        [InlineData(":123")]
        public void ParseQuotedSuccess(string source)
        {
            CharReader reader = new CharReader(source);
            Assert.True(reader.ReadTo(':', true, '"'));
            Assert.Equal("123", reader.GetRemainder().ToString());
        }

        [Theory]
        [InlineData("abc\"quoted")]
        [InlineData("abc\"quoted:\"pqr123")]
        [InlineData("pqr123")]
        [InlineData("\"quoted:\"pqr\"quoted:foo:bar\"123")]
        [InlineData("pqr\\:123")]
        public void ParseQuotedFail(string source)
        {
            CharReader reader = new CharReader(source);
            Assert.False(reader.ReadTo(':', true, '"'));
        }
    }
}