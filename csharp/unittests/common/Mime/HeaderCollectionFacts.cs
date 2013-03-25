/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen    jtheisen@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;

using Health.Direct.Common.Mime;

using Xunit;

namespace Health.Direct.Common.Tests.Mime
{
    public class HeaderCollectionFacts
    {
        private readonly HeaderCollection m_headers;

        public HeaderCollectionFacts()
        {
            m_headers = new HeaderCollection(new[] {new Header("key", "value")});
        }

        [Fact]
        public void IndexOfThrowsArgumentException()
        {
            var headers = new HeaderCollection();
            var ex = Assert.Throws<ArgumentException>(() => headers.IndexOf(null));
            Assert.Equal("name", ex.ParamName);
        }

        [Fact]
        public void AddThrowsArgumentNullException()
        {
            var headers = new HeaderCollection();
            var ex = Assert.Throws<ArgumentNullException>(() => headers.Add(null));
            Assert.Equal("headers", ex.ParamName);
        }

        [Fact]
        public void AddUpdateThrowsArgumentNullException()
        {
            var headers = new HeaderCollection();
            var ex = Assert.Throws<ArgumentNullException>(() => headers.AddUpdate((IEnumerable<Header>)null));
            Assert.Equal("headers", ex.ParamName);
        }

        [Fact]
        public void AddUpdateThrowsArgumentNullException2()
        {
            var headers = new HeaderCollection();
            var ex = Assert.Throws<ArgumentNullException>(() => headers.AddUpdate((IEnumerable<KeyValuePair<string,string>>)null));
            Assert.Equal("headers", ex.ParamName);
        }

        [Fact]
        public void AddFilteredByHeaderStringsThrowsArgumentNullException()
        {
            var headers = new HeaderCollection();
            var ex = Assert.Throws<ArgumentNullException>(() => headers.Add(null, header => true));
            Assert.Equal("source", ex.ParamName);
        }

        [Fact]
        public void AddThrowsArgumentNullException2()
        {
            var headers = new HeaderCollection();
            var ex = Assert.Throws<ArgumentNullException>(() => headers.Add(null, new string[0]));
            Assert.Equal("source", ex.ParamName);
        }

        [Fact]
        public void SetThrowsArgumentNullException()
        {
            var headers = new HeaderCollection();
            var ex = Assert.Throws<ArgumentNullException>(() => headers.Set(null));
            Assert.Equal("header", ex.ParamName);
        }

        [Fact]
        public void ItemSetWithNullRemovesHeader()
        {
            Assert.NotNull(m_headers["key"]);
            Assert.Equal("value", m_headers["key"].Value);

            m_headers["key"] = null;
            Assert.Null(m_headers["key"]);
        }

        [Fact]
        public void GetValueReturnsNullWhenHeaderNotFound()
        {
            Assert.Null(m_headers.GetValue("unknown"));
        }

        [Fact]
        public void FilteredVersionOfAdd()
        {
            var a = new Header("a", "a");
            var b = new Header("b", "b");
            var aa = new Header("aa", "c");
            Header[] headers =  new Header[] { a, b, aa };
            HeaderCollection coll = new HeaderCollection();

            coll.Add(headers, h => h.Name.StartsWith("a"));

            Assert.True(coll.Contains(a));
            Assert.True(coll.Contains(aa));
            Assert.False(coll.Contains(b));
        }

        [Fact]
        public void StringArrayFilteredVersionOfAdd()
        {
            var a = new Header("a", "a");
            var b = new Header("b", "b");
            var aa = new Header("aa", "c");
            var headers = new Header[] { a, b, aa };
            HeaderCollection coll = new HeaderCollection();

            coll.Add(headers, new string[] { "a", "aa" });
            Assert.True(coll.Contains(a));
            Assert.True(coll.Contains(aa));
            Assert.False(coll.Contains(b));
        }

    }
}