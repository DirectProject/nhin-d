/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
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
using Health.Direct.Agent;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Agent.Tests
{
    public class DirectAddressCollectionFacts
    {
        public static IEnumerable<object[]> Addresses
        {
            get
            {
                yield return new object[] {1, "\"McDuff, Toby\" <toby@redmond.hsgincubator.com>", MailStandard.Headers.To};
                yield return new object[] {1, "\"McDuff, Toby\"<toby@redmond.hsgincubator.com>", MailStandard.Headers.Cc};
                yield return new object[] {1, "\"Toby McDuff\" <toby@redmond.hsgincubator.com>", MailStandard.Headers.Bcc};
                yield return new object[] {1, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>", MailStandard.Headers.To};
                yield return new object[] {2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>, <biff@direct.org>", MailStandard.Headers.To};
                yield return new object[] {2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>, biff@direct.org", MailStandard.Headers.To};
                yield return new object[] {2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>, \"Biff Hooper\" <biff@direct.org>", MailStandard.Headers.To};
                yield return new object[] {2, "\"Toby,McDuff\"<toby@redmond.hsgincubator.com>, \"Biff,Hooper\" <biff@direct.org>", MailStandard.Headers.Bcc};
                yield return new object[] {2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>, \"Biff,Hooper\" <biff@direct.org>", MailStandard.Headers.Cc};
                yield return new object[] {2, "<toby@redmond.hsgincubator.com>, \"Biff,Hooper\" <biff@direct.org>", MailStandard.Headers.To};
                yield return new object[] {3, "<toby@redmond.hsgincubator.com>, \"Biff,Hooper\" <biff@direct.org>, <nimbu@xyz.com>", MailStandard.Headers.To};
                yield return new object[] {3, "<toby@redmond.hsgincubator.com>, \"Biff Hooper\" <biff@direct.org>, <nimbu@xyz.com>", MailStandard.Headers.To};
                yield return new object[] { 4, "<toby@redmond.hsgincubator.com>, <biff@direct.org>, <nimbu@xyz.com>, <violet@xyz.com>", MailStandard.Headers.Cc};
            }
        }

        [Theory]
        [PropertyData("Addresses")]
        public void Parse(int addressCount, string source, string headerName)
        {
            DirectAddressCollection addresses = null;
            Assert.DoesNotThrow(() => addresses = DirectAddressCollection.Parse(source));
            Assert.True(addresses.Count == addressCount);
        }

        [Theory]
        [PropertyData("Addresses")]
        public void ToHeaderUnfolded(int addressCount, string source, string headerName)
        {
            DirectAddressCollection addresses = null;
            Assert.DoesNotThrow(() => addresses = DirectAddressCollection.Parse(source));
            
            Header header = null;   
            Assert.DoesNotThrow(() => header = addresses.ToHeader(headerName));
            Assert.True(string.Equals(header.Name, headerName));

            DirectAddressCollection reparsedAddresses = null;
            Assert.DoesNotThrow(() => reparsedAddresses = DirectAddressCollection.Parse(header.Value));
            Assert.True(reparsedAddresses.Count == addressCount);
        }
                
        [Theory]
        [PropertyData("Addresses")]
        public void ToHeader(int addressCount, string source, string headerName)
        {
            DirectAddressCollection addresses = null;
            Assert.DoesNotThrow(() => addresses = DirectAddressCollection.Parse(source));
            Assert.True(addresses.Count == addressCount);
            
            Header header = null;   
            Assert.DoesNotThrow(() => header = addresses.ToHeader(headerName));
            Assert.True(string.Equals(header.Name, headerName));
            
            string foldedText = null;
            Assert.DoesNotThrow(() => foldedText = header.SourceText.ToString());
            Assert.True(!string.IsNullOrEmpty(foldedText));

            string[] foldedParts = foldedText.Split(new string[] { MailStandard.CRLF }, StringSplitOptions.None);
            Assert.True(foldedParts.Length == addressCount);
            
            string entity = foldedText + MailStandard.CRLF;
            Header[] reparsedHeaders = null;
            Assert.DoesNotThrow(() => reparsedHeaders = MimeParser.ReadHeaders(entity).ToArray());
            Assert.True(reparsedHeaders.Length == 1);
            Assert.True(reparsedHeaders[0].Name == headerName);

            DirectAddressCollection reparsedAddresses = null;
            Assert.DoesNotThrow(() => reparsedAddresses = DirectAddressCollection.Parse(reparsedHeaders[0].Value));
            Assert.True(reparsedAddresses.Count == addressCount);
        }        
        
        [Fact]
        public void ToHeaderNull()
        {
            DirectAddressCollection addresses = new DirectAddressCollection();
            Header header = null;
            Assert.DoesNotThrow(() => header = addresses.ToHeader(MailStandard.Headers.To));
            Assert.True(header == null);
        }
    }
}
