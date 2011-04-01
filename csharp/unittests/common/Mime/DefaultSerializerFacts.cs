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
using System.Collections.Generic;
using System.IO;

using Health.Direct.Common.Mime;

using Xunit;

namespace Health.Direct.Common.Tests.Mime
{
    public class DefaultSerializerFacts
    {
        private readonly DefaultSerializer m_serializer;

        public DefaultSerializerFacts()
        {
            m_serializer = new DefaultSerializer();
        }

        [Fact]
        public void JoinHeaderWithNullKeyThrowsArgumentException()
        {
            var nvp = new KeyValuePair<string, string>(null, "value");
            Assert.Throws<ArgumentException>(() => m_serializer.JoinHeader(nvp));
        }

        [Fact]
        public void JoinHeaderWithNullValueThrowsArgumentException()
        {
            var nvp = new KeyValuePair<string, string>("name", null);
            Assert.Throws<ArgumentException>(() => m_serializer.JoinHeader(nvp));
        }

        [Fact]
        public void SerializeWithNullEntitiesThrowsArgumentNullException()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => m_serializer.Serialize(null, "123", StreamWriter.Null));
            Assert.Equal("entities", ex.ParamName);
        }

        [Fact]
        public void SerializeWithNullEntityThrowsArgumentNullException()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => m_serializer.Serialize(null, StreamWriter.Null));
            Assert.Equal("entity", ex.ParamName);
        }

        [Fact]
        public void SerializeWithNullEntityThrowsArgumentNullException2()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => m_serializer.Serialize(null, new MimeWriter(StreamWriter.Null)));
            Assert.Equal("entity", ex.ParamName);
        }

        [Fact]
        public void SerializeWithNullMimeWriterThrowsArgumentNullException()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => m_serializer.Serialize(new MimeEntity(), null));
            Assert.Equal("entityWriter", ex.ParamName);
        }

        [Fact]
        public void SerializeWithNonMessageMimeEntityPassedToMimeSerializer()
        {
            Assert.Equal("", m_serializer.Serialize(new MimeEntity()));
        }

        [Fact]
        public void SerializeToFilePath()
        {
            var path = Path.GetTempFileName();
            try
            {
                m_serializer.Serialize(new MimeEntity(), path);
                Assert.Equal("", File.ReadAllText(path));
            }
            finally
            {
                File.Delete(path);
            }
        }

        [Fact]
        public void DeserializeThrowsArgumentNullException()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => m_serializer.Deserialize<MimeEntity>((byte[]) null));
            Assert.Equal("messageBytes", ex.ParamName);
        }

        [Fact]
        public void DeserializeThrowsArgumentException()
        {
            var ex = Assert.Throws<ArgumentException>(() => m_serializer.Deserialize<MimeEntity>(new byte[0]));
            Assert.Equal("messageBytes", ex.ParamName);
        }

        [Fact]
        public void DeserializeThrowsArgumentNullException2()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => m_serializer.Deserialize<MimeEntity>((Stream) null));
            Assert.Equal("stream", ex.ParamName);
        }

        [Fact]
        public void DeserializeThrowsArgumentNullException3()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => m_serializer.Deserialize<MimeEntity>((string) null));
            Assert.Equal("messageText", ex.ParamName);
        }

        [Fact]
        public void DeserializeThrowsArgumentNullException4()
        {
            var ex = Assert.Throws<ArgumentNullException>(() => m_serializer.Deserialize<MimeEntity>((TextReader) null));
            Assert.Equal("reader", ex.ParamName);
        }
    }
}