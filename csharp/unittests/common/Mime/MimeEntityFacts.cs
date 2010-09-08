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
    }
}
