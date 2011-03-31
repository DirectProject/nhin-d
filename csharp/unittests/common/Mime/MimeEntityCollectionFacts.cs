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
using Health.Direct.Common.Mime;

using Xunit;

namespace Health.Direct.Common.Tests.Mime
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