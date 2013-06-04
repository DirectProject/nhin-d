/* 
 Copyright (c) 2010, Direct Project
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
using System.Net.Mail;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mail
{
    public class MailSerializationFacts
    {
        [Theory]
        [InlineData(1, "\"McDuff, Toby\" <toby@redmond.hsgincubator.com>")]
        [InlineData(1, "\"McDuff, Toby\"<toby@redmond.hsgincubator.com>")]
        [InlineData(1, "\"Toby McDuff\" <toby@redmond.hsgincubator.com>")]
        [InlineData(1, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>")]
        [InlineData(2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>, <biff@direct.org>")]
        [InlineData(2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>, biff@direct.org")]
        [InlineData(2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>, \"Biff Hooper\" <biff@direct.org>")]
        [InlineData(2, "\"Toby,McDuff\"<toby@redmond.hsgincubator.com>, \"Biff,Hooper\" <biff@direct.org>")]
        [InlineData(2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>, \"Biff,Hooper\" <biff@direct.org>")]
        [InlineData(2, "<toby@redmond.hsgincubator.com>, \"Biff,Hooper\" <biff@direct.org>")]
        [InlineData(3, "<toby@redmond.hsgincubator.com>, \"Biff,Hooper\" <biff@direct.org>, <nimbu@xyz.com>")]
        [InlineData(3, "<toby@redmond.hsgincubator.com>, \"Biff Hooper\" <biff@direct.org>, <nimbu@xyz.com>")]
        [InlineData(4, "<toby@redmond.hsgincubator.com>, <biff@direct.org>, <nimbu@xyz.com>, <violet@xyz.com>")]
        public void TestAddressCollectionFolding(int addressCount, string source)
        {
            MailAddressCollection addresses = null;
            Assert.DoesNotThrow(() => addresses = MailParser.ParseAddressCollection(source));

            string foldedText = null;
            Assert.DoesNotThrow(() => foldedText = addresses.ToStringWithFolding());
            Assert.True(!string.IsNullOrEmpty(foldedText));

            string[] foldedParts = null;
            Assert.DoesNotThrow(() => foldedParts = foldedText.Split(new string[] { MailStandard.CRLF}, StringSplitOptions.None));
            this.CheckParts(foldedParts, addressCount);
        }
        
        void CheckParts(string[] foldedParts, int addressCount)
        {
            Assert.True(foldedParts.Length == addressCount);

            for (int i = 0; i < foldedParts.Length; ++i)
            {
                string line = foldedParts[i];
                Assert.True(line.Length > 0);
                Assert.True(line.Length < MailStandard.MaxCharsInLine);
                if (i > 0)
                {   
                    // Verify starts with whitespace
                    Assert.True(MailStandard.IsWhitespace(line[0]));
                }
                if (i < foldedParts.Length - 1)
                {
                    Assert.True(line[line.Length - 1] == ',');
                }
            }
        }        
    }
}
