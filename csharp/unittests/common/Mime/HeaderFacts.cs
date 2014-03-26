/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen    jtheisen@kryptiq.com
    Umesh Madan     umeshma@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Text;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Mail;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mime
{
    public class HeaderFacts
    {
        [Fact]
        public void HeaderConstrcutor()
        {
            var pair = new KeyValuePair<string, string>("key", "value");
            var header = new Header(pair);
            Assert.Equal("key", header.Name);
            Assert.Equal("value", header.Value);
        }

        [Theory]
        [InlineData("key", true)]
        [InlineData("KEY", true)]
        [InlineData("NAME", false)]
        [InlineData(null, false)]
        [InlineData("", false)]
        public void IsHeaderName(string name, bool expected)
        {
            var pair = new KeyValuePair<string, string>("key", "value");
            var header = new Header(pair);
            Assert.Equal(expected, header.IsNamed(name));
        }

        [Theory]
        [InlineData("to", true)]
        [InlineData("TO", true)]
        [InlineData("from", true)]
        [InlineData("cc", true)]
        [InlineData("subject", true)]
        [InlineData("date", false)]
        public void IsHeaderNameAnyOf(string name, bool expected)
        {
            var pair = new KeyValuePair<string, string>(name, "value");
            var header = new Header(pair);
            Assert.Equal(expected, header.IsHeaderNameOneOf(new[] {"to", "from", "cc", "subject"}));
        }

        [Fact]
        public void IsHeaderNameAnyOfWIthNullArray()
        {
            var pair = new KeyValuePair<string, string>("key", "value");
            var header = new Header(pair);
            Assert.False(header.IsHeaderNameOneOf(null));
        }

        [Fact]
        public void CloneHeader()
        {
            var pair = new KeyValuePair<string, string>("key", "value");
            var header = new Header(pair);

            var clone = header.Clone();

            Assert.Equal(header.Name, clone.Name);
            Assert.Equal(header.Value, clone.Value);
        }

        [Theory]
        [InlineData(10, 1)]
        [InlineData(100, 1)]
        [InlineData(255, 1)]
        [InlineData(1000, 2)]
        [InlineData(2000, 3)]
        [InlineData(2300, 3)]
        [InlineData(3000, 4)]
        public void LineFolding(int characterCount, int expectedLines)
        {
            string source = new string('a', characterCount);
            
            string foldedText = null;
            Assert.DoesNotThrow(() => foldedText = Header.LineFoldText(source, MailStandard.MaxCharsInLine));
            Assert.True(!string.IsNullOrEmpty(foldedText));
            
            string[] lines = foldedText.Split(new string[] {MailStandard.CRLF}, StringSplitOptions.None);
            Assert.True(lines.Length == expectedLines);
            for (int i = 0; i < lines.Length; ++i)
            {
                string line = lines[i];
                Assert.True(line.Length > 0);
                Assert.True(line.Length <= MailStandard.MaxCharsInLine);
                if (i > 0)
                {
                    Assert.True(MimeStandard.IsWhitespace(line[0]));
                }
                if ((i == lines.Length - 1) && line.Length > 0)
                {
                    Assert.True(line[line.Length - 1] != MimeStandard.LF);
                }
            }
        }        
    }
}