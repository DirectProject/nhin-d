/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen    jtheisen@kryptiq.com
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
    public class BodyFacts
    {
        [Fact]
        public void DefaultConstructor()
        {
            var body = new Body();
            Assert.Equal(MimePartType.Body, body.Type);
            Assert.Equal(0, body.SourceText.Length);
            Assert.Equal("", body.Text);
        }

        [Fact]
        public void BodyFromStringShouldHaveMatchingText()
        {
            string bText = "Hello, world";
            Body b = new Body(bText);
            Assert.Equal(bText, b.Text);
            Assert.Equal(bText, b.SourceText.ToString());
            Assert.Equal(bText, b.ToString());
        }

        [Fact]
        public void BodyFromStringSegmentShouldHaveMatchingText()
        {
            string s = "abcHello, worlddef";
            string bText = "Hello, world";
            StringSegment ss = new StringSegment(s, 3, 14);
            Body b = new Body(ss);
            Assert.Equal(bText, b.Text);
            Assert.Equal(bText, b.SourceText.ToString());
            Assert.Equal(bText, b.ToString());
        }

    }
}