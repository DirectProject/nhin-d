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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;

using Xunit;

using Health.Direct.Common.Metadata;
using System.IO;

namespace Health.Direct.Common.Tests.Metadata
{
    // Many other tests in XD Consume/Generate tests
    public class DocumentMetadataFacts
    {

        [Fact]
        public void SetDocumentSetsHash()
        {
            DocumentMetadata m = new DocumentMetadata();
            m.SetDocument("abc");
            SHA1 sha = new SHA1CryptoServiceProvider();
            byte[] bytes = (new UTF8Encoding()).GetBytes("abc");
            string hash = BitConverter.ToString(sha.ComputeHash(bytes)).Replace("-", "");
            Assert.Equal(hash, m.Hash);
        }

        [Fact]
        public void SetDocumentSetsSize()
        {
            DocumentMetadata m = new DocumentMetadata();
            m.SetDocument(new byte[] { 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100 });
            Assert.Equal(11, m.Size);
        }

        [Fact]
        public void SetDocumentWithStream()
        {
            DocumentMetadata m = new DocumentMetadata();
            using (MemoryStream stream = new MemoryStream(new byte[] { 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100 }))
            {
                m.SetDocument(stream);
            }
            Assert.Equal(11, m.Size);                
        }

        [Fact]
        public void SetDocumentWithExistingWrongHashThrowsError()
        {
            DocumentMetadata m = new DocumentMetadata();
            m.Hash = "abc123";
            Assert.Throws<FormatException>(() => m.SetDocument("abc"));
        }

        [Fact]
        public void SetDocumentWithExistingRightHashWorks()
        {
            DocumentMetadata m = new DocumentMetadata();
            SHA1 sha = new SHA1CryptoServiceProvider();
            byte[] bytes = (new UTF8Encoding()).GetBytes("abc");
            string hash = BitConverter.ToString(sha.ComputeHash(bytes)).Replace("-", "");
            m.Hash = hash;
            m.SetDocument("abc");
            Assert.Equal(hash, m.Hash);
        }

        [Fact]
        public void SetDocumentWithExistingWrongSizeThrowsError()
        {
            DocumentMetadata m = new DocumentMetadata();
            m.Size = 1000000000;
            Assert.Throws<FormatException>(() => m.SetDocument("abc"));
        }

        [Fact]
        public void SetDocumentWithExistingRightSizeWorks()
        {
            DocumentMetadata m = new DocumentMetadata();
            byte[] bytes = (new UTF8Encoding()).GetBytes("abc");
            m.Size = bytes.Length;
            m.SetDocument("abc");
            Assert.Equal(bytes.Length, m.Size);
        }

        [Fact]
        public void DocumentStringHasStringRepresentation()
        {
            DocumentMetadata m = new DocumentMetadata();
            m.SetDocument("abc123");
            Assert.Equal("abc123", m.DocumentString);
        }
    }
}
