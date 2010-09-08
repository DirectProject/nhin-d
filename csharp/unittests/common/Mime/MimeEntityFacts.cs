using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using NHINDirect.Mime;
using Xunit;

namespace NHINDirect.Tests.Mime
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

        [Fact] public void MimeEntityShouldHaveHeaders()
        {
            MimeEntity e = new MimeEntity("Hellow, world", "text/silly");
            e.ContentDisposition = "inline";
            e.ContentTransferEncoding = "base64";
            e.Headers.Add(new Header("foo", "bar"));
            
            Assert.True(e.HasHeader("FOO"));
            Assert.Equal(4, e.Headers.Count);
        }
    }
}
