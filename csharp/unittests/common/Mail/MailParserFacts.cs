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
using System.Linq;
using System.Net.Mail;
using System.IO;
using System.Collections.ObjectModel;
using Health.Direct.Common.Mail;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mail
{
    /// <summary>
    /// MOST of Mail Parsing tests are done through Agent & SmtpAgent Unit Tests - end to end testing
    /// We are adding additional checks here
    /// </summary>
    public class MailParserFacts
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
        public void ParseAddressCollection(int expectedCount, string source)
        {
            Assert.Equal(expectedCount, MailParser.ParseAddressCollection(source).Count);
        }

        [Theory]
        [InlineData(1, "SMTP:toby@redmond.hsgincubator.com")]
        [InlineData(2, "SMTP:toby@redmond.hsgincubator.com; SMTP:biff@direct.org")]
        [InlineData(2, "SMTP:toby@redmond.hsgincubator.com;SMTP:biff@direct.org")]
        [InlineData(3, "SMTP:toby@redmond.hsgincubator.com; SMTP:biff@direct.org; SMTP:nimbu@xyz.com")]
        [InlineData(4, "SMTP:toby@redmond.hsgincubator.com; SMTP:biff@direct.org; SMTP:nimbu@xyz.com; SMTP:VIOLET@xyz.com")]
        [InlineData(2, "\"Toby,McDuff\"<toby@redmond.hsgincubator.com>; \"Biff,Hooper\" <biff@direct.org>")]
        [InlineData(2, "\"Toby McDuff\"<toby@redmond.hsgincubator.com>; \"Biff,Hooper\" <biff@direct.org>")]
        [InlineData(2, "SMTP:\"Toby,McDuff\"<toby@redmond.hsgincubator.com>; SMTP:\"Biff,Hooper\" <biff@direct.org>")]
        [InlineData(2, "SMTP:\"Toby McDuff\"<toby@redmond.hsgincubator.com>; SMTP:\"Biff,Hooper\" <biff@direct.org>")]
        public void ParseSMTPAddressCollection(int expectedCount, string source)
        {
            Assert.Equal(expectedCount, MailParser.ParseSMTPServerEnvelopeAddresses<MailAddress, Collection<MailAddress>>(source, a => new MailAddress(a)).Count);
        }

        [Theory]
        [InlineData("\"McDuff, Toby\" toby@redmond.hsgincubator.com>")]
        [InlineData("<toby@redmond.hsgincubator.com")]
        public void ParseAddressCollectionFail(string source)
        {
            Assert.Throws<FormatException>(() => MailParser.ParseAddressCollection(source));
        }        
    }
}
