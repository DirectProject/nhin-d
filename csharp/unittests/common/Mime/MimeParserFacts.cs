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
using System.Linq;
using System.Net.Mail;
using System.Collections.Generic;
using System.IO;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Mail;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mime
{
    public class MimeParserFacts
    {
        [Theory]
        [InlineData("This is an invalid line \r")]
        [InlineData("This is an invalid line \r\n foo \r")]
        [InlineData("This is an invalid line \r \n foo \r\n")]
        [InlineData("This is an invalid line \r     \n foo \r\n")]
        public void TestInvalidCRLN(string text)
        {
            MimeException error = null;
            
            error = AssertEx.Throws<MimeException>(() => {
                MimeParser.ReadLines(text).ToArray();
            });
            
            Assert.True(error.Error == MimeError.InvalidCRLF);
        }
        
        public static IEnumerable<object[]> ValidLines
        {
            get 
            {   
                yield return new object[]
                {
                    "This is a valid line .\r\n",
                    new string[] {"This is a valid line ."}
                };
                yield return new object[]
                {
                    "Two valid lines \r\n foo \r\n",
                    new string[] {
                        "Two valid lines ",
                        " foo "
                    }
                };
                yield return new object[]
                {
                    "  Four valid =3D \r\n =0D =\r\n lines \r\n foo \r\n",
                    new string[] {
                        "  Four valid =3D ",
                        " =0D =",
                        " lines ",
                        " foo "
                    }
                };
            }
        }
        
        [Theory]
        [PropertyData("ValidLines")]
        public void TestValidCRLN(string text, string[] expectedLines)
        {
            StringSegment[] parsedLines = null;
            Assert.DoesNotThrow(() => parsedLines = MimeParser.ReadLines(text).ToArray());
            
            Assert.True(parsedLines[parsedLines.Length - 1].IsEmpty);
            Assert.True((parsedLines.Length - 1) == expectedLines.Length);
            for (int i = 0; i < expectedLines.Length; ++i)
            {
                Assert.Equal(parsedLines[i].ToString(), expectedLines[i]);
            }
        }
                    
        public static IEnumerable<object[]> HeaderFiles
        {
            get
            {
                yield return new object[] {"Mime\\TestFiles\\Multipart.txt", 13}; // Expect 13 headers
            }
        }
            
        [Theory]
        [PropertyData("HeaderFiles")]
        public void TestHeaders(string filePath, int expectedHeaderCount)
        {
            string entity = File.ReadAllText(filePath);
            Header[] headers = null;
            Assert.DoesNotThrow(() => headers = MimeParser.ReadHeaders(entity).ToArray());
            Assert.True(headers.Length == expectedHeaderCount);
            foreach(Header header in headers)
            {
                Assert.True(header != null);
                
                string name = null;
                Assert.DoesNotThrow(() => name = header.Name);
                Assert.True(!string.IsNullOrEmpty(name));

                string value = null;
                Assert.DoesNotThrow(() => value = header.ValueRaw);
            }
        }

        public static IEnumerable<object[]> GoodHeaders
        {
            get
            {
                yield return new object[] { "To:", string.Empty};  
                yield return new object[] { "To:toby@foo.bar", "toby@foo.bar"};
                yield return new object[] { "To: ", string.Empty}; 
                yield return new object[] { "To:toby@foo\r\n .bar \r\n .goo", "toby@foo.bar .goo"};
            }
        }
        
        [Theory]
        [PropertyData("GoodHeaders")]
        public void TestValidHeaders(string entity, string expectedValue)
        {
            string headerValue = null;
            Assert.DoesNotThrow(() =>
            {
                Header[] headers = MimeParser.ReadHeaders(entity).ToArray();
                headerValue = headers[0].ValueRaw;                
            });

            Assert.Equal(headerValue, expectedValue);
        }

        public static IEnumerable<object[]> BadHeaders
        {
            get
            {
                yield return new object[] { "To", MimeError.MissingNameValueSeparator};  // No : character
                yield return new object[] { "To:\r\n \r\n \r\n", MimeError.MissingHeaderValue }; 
                yield return new object[] { " To: foo", MimeError.InvalidHeader }; // Can't start with a space
                yield return new object[] { "To foo@goo", MimeError.MissingNameValueSeparator};
                yield return new object[] { "To: ", MimeError.MissingHeaderValue };
            }
        }
        
        [Theory]
        [PropertyData("BadHeaders")]
        public void TestInvalidHeaders(string entity, MimeError expectedError)
        {
            MimeException error;
            error = AssertEx.Throws<MimeException>(() => {
                Header[] headers = MimeParser.ReadHeaders(entity).ToArray();
                string value = headers[0].Value;
            });
            
            Assert.True(error.Error == expectedError);
        }
        
        [Theory]
        [InlineData("--foo", "foo", true)]
        [InlineData("--1233423233432--", "1233423233432", true)]
        [InlineData(" --1233423233432--", "1233423233432", false)]
        [InlineData("-1233423233432--", "1233423233432", false)]
        [InlineData("1233423233432--", "1233423233432", false)]
        [InlineData("", "1233423233432", false)]
        [InlineData("-", "1233423233432", false)]
        [InlineData("--", "1233423233432", false)]
        [InlineData("--123", "1233423233432", false)]
        public void TestBoundaryStart(string text, string boundary, bool isBoundary)
        {
            bool result = false;            
            Assert.DoesNotThrow(() => result = MimeParser.IsBoundary(new StringSegment(text), boundary));
            Assert.True(result == isBoundary);
        }
        
        [Theory]
        [InlineData("", false)]
        [InlineData("--", false)]
        [InlineData("-", false)]
        [InlineData("--1233423233432--", true)]
        [InlineData("--2--", true)]
        public void TestBoundaryEnd(string text, bool isEnd)
        {
            bool result = false;
            Assert.DoesNotThrow(() => result = MimeParser.IsBoundaryEnd(new StringSegment(text)));
            Assert.True(result == isEnd);        
        }

        public static IEnumerable<object[]> MultipartFiles
        {
            get
            {
                yield return new object[] { "Mime\\TestFiles\\Multipart.txt", 9 }; // Expect 9 parts
            }
        }

        [Theory]
        [PropertyData("MultipartFiles")]
        public void TestMultipart(string filePath, int expectedPartCount)
        {
            string entityText = File.ReadAllText(filePath);
            
            MimeEntity entity = null;
            Assert.DoesNotThrow(() => entity = MimeParser.Read<MimeEntity>(entityText));
            Assert.True(entity.IsMultiPart);
            
            MimePart[] parts = null;
            Assert.DoesNotThrow(() => parts = entity.GetAllParts().ToArray());
            if (expectedPartCount > 0)
            {
                Assert.True(parts != null && parts.Length == expectedPartCount);
            }
        }
    }
}
