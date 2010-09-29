using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using NHINDirect.Mime;

using Xunit;

namespace NHINDirect.Tests.Mime
{
    public class MimeEntityCollectionFacts
    {
        MimeEntityCollection m_basicCollection;

        /// <summary>
        /// Treat as value object -- do not modify.
        /// </summary>
        MimeEntityCollection BasicCollection
        {
            get
            {
                if (m_basicCollection == null)
                {
                    m_basicCollection = new MimeEntityCollection("multipart/mixed");
                    m_basicCollection.Entities.Add(new MimeEntity("Text part", "text/plain"));
                    m_basicCollection.Entities.Add(new MimeEntity("<html><body><p>Hello, World!</p></body></html>", "text/html"));
                }
                return m_basicCollection;
            }
        }


        [Fact]
        public void DefaultConstructorShouldCreateEmptyEntities()
        {
            MimeEntityCollection c = new MimeEntityCollection();
            Assert.NotNull(c.Entities);
            Assert.Empty(c.Entities);
        }

        [Fact]
        public void BasicCollectionShouldHave2Parts()
        {
            MimeEntityCollection c = BasicCollection;
            Assert.Equal(2, c.Entities.Count);
        }

        [Fact]
        public void BasicCollectionFirstPartShouldMatchContentType()
        {
            MimeEntityCollection c = BasicCollection;
            Assert.Equal("text/plain", c.Entities[0].ContentType);
        }

        [Fact]
        public void BasicCollectionSecondPartBodyTextShouldMatch()
        {
            Assert.Equal("<html><body><p>Hello, World!</p></body></html>",
                BasicCollection.Entities[1].Body.Text);
        }

        [Fact]
        public void BasicCollectionSecondPartBodySourceTextShouldMatch()
        {
            Assert.Equal("<html><body><p>Hello, World!</p></body></html>",
                BasicCollection.Entities[1].Body.SourceText.ToString());
        }



    }
}
