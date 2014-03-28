/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec   arien.malec@nhindirect.org

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Linq;

using Health.Direct.Common.Mime;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mime
{
    public class MimeEntityFacts
    {
        [Fact]
        public void DefaultConstructorShouldCreateEmptyEntity()
        {
            MimeEntity e = new MimeEntity();
            Assert.Empty(e.Headers);
            Assert.False(e.HasBody);
            Assert.False(e.HasHeaders);
            Assert.False(e.IsMultiPart);
        }

        [Fact]
        public void TextConstructiorShouldCreateTextPlain()
        {
            MimeEntity e = new MimeEntity("Hello, world");
            Assert.True(e.HasMediaType("text/plain"));
        }

        [Fact]
        public void TextPlainShouldNotBeMultipart()
        {
            MimeEntity e = new MimeEntity("Hello, world");
            Assert.False(e.IsMultiPart);
        }


        [Fact]
        public void BodyTextShouldBeAccessible()
        {
            MimeEntity e = new MimeEntity("Hello, world");
            Assert.Equal("Hello, world", e.Body.Text);
        }

        [Fact]
        public void BodySourceTextShouldBeAccessible()
        {
            MimeEntity e = new MimeEntity("Hello, world");
            Assert.Equal("Hello, world", e.Body.SourceText.ToString());
        }

        [Fact]
        public void ConstructorWithContentTypeShouldSetContentType()
        {
            MimeEntity e = new MimeEntity("Hello, world", "text/silly; charset=uk-monty");
            Assert.True(e.HasHeaders);
            Assert.True(e.HasHeader("content-type"));
            Assert.True(e.HasMediaType("text/silly"));
            Assert.Equal("text/silly; charset=uk-monty", e.ContentType);
        }

        [Fact] 
        public void MimeEntityShouldHaveHeaders()
        {
            MimeEntity e = new MimeEntity("Hello, world", "text/silly");
            e.ContentDisposition = "inline";
            e.ContentTransferEncoding = "base64";
            e.Headers.Add(new Header("foo", "bar"));
            
            Assert.True(e.HasHeader("FOO"));
            Assert.Equal(4, e.Headers.Count);
        }

        [Theory]
        [InlineData("text/plain")]
        [InlineData("application/xml")]
        [InlineData("message/rfc822")]
        [InlineData("multipart/mixed")]
        public void EntityShouldHaveParsedContentType(string mediaType)
        {
            MimeEntity e = new MimeEntity("Hello, world", mediaType);
            Assert.Equal(mediaType, e.ParsedContentType.MediaType);
        }

        MimeEntity m_basicMultipart;
        MimeEntity BasicMultipartEntity
        {
            get
            {
                if (m_basicMultipart == null)
                {
                    MimeEntity e = new MimeEntity();
                    MimeEntityCollection c = new MimeEntityCollection("multipart/mixed");
                    c.Entities.Add(new MimeEntity("Text part", "text/plain"));
                    c.Entities.Add(new MimeEntity("<html><body><p>Hello, World!</p></body></html>", "text/html"));
                    e.UpdateBody(c);
                    m_basicMultipart = e;
                }
                return m_basicMultipart;
            }
        }


        [Fact]
        public void MultipartEntityShouldBeMultipart()
        {
            Assert.True(BasicMultipartEntity.IsMultiPart);
        }

        [Fact]
        public void BasicMultipartEntityShoudlHave2Parts()
        {
            Assert.Equal(2, BasicMultipartEntity.GetParts().Count());
        }

        [Theory]
        [InlineData(0, "Text part", true)]
        [InlineData(0, "Foo bar", false)]
        [InlineData(1, "<html><body><p>Hello, World!</p></body></html>", true)]
        [InlineData(1, "la la la", false)]
        public void MultipartEntityShouldHavePartContent(int index, string content, bool expected)
        {
            Assert.True(BasicMultipartEntity.GetParts()
                            .ElementAt(index).Body.Text.Equals(content) == expected);
        }

        [Theory]
        [InlineData(0, "text/plain", true)]
        [InlineData(0, "text/silly", false)]
        [InlineData(1, "text/html", true)]
        [InlineData(1, "", false)]
        public void MultipartEntityShouldHavePartContentTypes(int index, string content, bool expected)
        {
            Assert.True(BasicMultipartEntity.GetParts()
                            .ElementAt(index).ContentType.Equals(content) == expected);
        }

    }
}