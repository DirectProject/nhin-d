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
using System.IO;
using Health.Direct.Common;
using Health.Direct.Common.Mime;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mime
{
    public class QuotedPrintableFacts
    {
        public static IEnumerable<object[]> GoodStrings
        {
            get
            {
                yield return new[] { 
                        "There is no code in this",
                        "There is no code in this"
                };
                yield return new[] { 
                        "There is only one=\r\n",
                        "There is only one"
                };
                yield return new[] { 
                        "There is only one =\r\n line in this text", 
                        "There is only one  line in this text"
                };
                yield return new[] { 
                        "This line has trailing encoded whitespace.=20=20=20",
                        "This line has trailing encoded whitespace.   ",
                };
                yield return new[] { 
                        "This line has trailing non-encoded whitespace.=20=20=20  \t\t\t  ",
                        "This line has trailing non-encoded whitespace.   ",
                };
                yield return new[] { 
                        "This line has trailing non-encoded whitespace.=\r\n  \t\t\r\n\t  ",
                        "This line has trailing non-encoded whitespace.\r\n",
                };
                yield return new[] { 
                        "This line has trailing non-encoded whitespace.=\r\n  A\t\t\t  ",
                        "This line has trailing non-encoded whitespace.  A",
                };
                yield return new[] { 
                        "This is line one=\r\n, and this is line two =\r\n and line 3.",
                        "This is line one, and this is line two  and line 3."
                };
                yield return new[] { 
                        "This example does some =\r\nof everything,\r\n=20and then=20=\r\nsome more of=0D=0Aeverything.",
                        "This example does some of everything,\r\n and then some more of\r\neverything.",
                };
            }
        }

        [Theory]
        [PropertyData("GoodStrings")]
        public void TestStandard(string text, string expectedText)
        {
            QuotedPrintableDecoder decoder = new QuotedPrintableDecoder(text);
            char[] chars = null;
            Assert.DoesNotThrow(() => chars = decoder.GetChars().ToArray()); 
            Assert.True(chars.Length == expectedText.Length);
            
            string decodedString = null;    
            Assert.DoesNotThrow(() => decodedString = new string(chars));
            Assert.True(string.Equals(decodedString , expectedText));
        }
                
        public static IEnumerable<object[]> FileNames
        {
            get
            {
                yield return new[] {"QuotedHtml_1.txt"};
                yield return new[] { "QuotedHtml_2.txt"};
                yield return new[] { "QuotedXml_1.txt" };
            }
        }
        
        [Theory]
        [PropertyData("FileNames")]
        public void TestFiles(string fileName)
        {
            string text = LoadFile(fileName);
            QuotedPrintableDecoder decoder = new QuotedPrintableDecoder(text);

            char[] chars = null;
            Assert.DoesNotThrow(() => chars = decoder.GetChars().ToArray());
            string decodedString = null;
            Assert.DoesNotThrow(() => decodedString = new string(chars));            
        }
        
        public static string LoadFile(string name)
        {
            string path = Path.Combine("Mime\\TestFiles", name);
            return File.ReadAllText(path);
        }

        public static IEnumerable<object[]> TestAllCharsParams
        {
            get
            {
                yield return new object[] { true};  
                yield return new object[] { false};  
            }
        }
        
        [Theory]
        [PropertyData("TestAllCharsParams")]
        public void TestAllChars(bool lowerCase)
        {
            // Quoted printable all possible chars
            TestString testString = this.MakeAllEncodeChars(lowerCase);
            string decoded = null;
            Assert.DoesNotThrow(() => decoded = new QuotedPrintableDecoder(testString.Encoded).GetString());
            
            Assert.True(testString.Expected.Length == decoded.Length);
            for (byte i = 0; i < decoded.Length; ++i)
            {
                Assert.True(testString.Expected[i] == decoded[i]);
            } 
        }
        
        public static IEnumerable<object[]> RandomTestParams
        {
            get
            {
                yield return new object[] {8192, 0.4, 0.5};  // String with 8K chars, 0.4 chance of encoding a char, 0.5 chance of lower case
                yield return new object[] {65000, 0.3, 0.5};  // String with 65K chars, 20% chance of encoding a char
                yield return new object[] {1024 * 1024, 0.2, 0.5 };  
            }
        }
                    
        [Theory]
        [PropertyData("RandomTestParams")]
        public void TestRandom(int textLength, double encodeProbability, double lowerCaseProbability)
        {
            TestString testString = this.MakeRandom(textLength, encodeProbability,lowerCaseProbability);            
            string decodedString = null;
            Assert.DoesNotThrow(() => decodedString = new QuotedPrintableDecoder(testString.Encoded).GetString());
            Assert.True(testString.Expected.Length == decodedString.Length);
            Assert.True(testString.Expected.Equals(decodedString));            
        }

        public static IEnumerable<object[]> BadStrings
        {
            get
            {
                yield return new[] {"Standalone = not allowed"};
                yield return new[] {"Standalone =not allowed"};
                yield return new[] { "Standalone ==not allowed" };
                yield return new[] { "Bad soft linebreak=\r in line" };
                yield return new[] { "Bad soft linebreak=\rin line" };
                yield return new[] { "Bad encoding =T in line" };
                yield return new[] { "Bad encoding =pp in line" };
                yield return new[] { "Bad encoding =0t in line" };
                yield return new[] { "Bad encoding =00 in line" };
                yield return new[] { "Bad encoding =A in line" };
                yield return new[] { "Bad encoding=aP in line" };
                yield return new[] { "Bad encoding=a P in line" };
                yield return new[] { "Bad encoding= 0D in line" };
                yield return new[] { "=foo encoding line" };
                yield return new[] { "=ag encoding line" };
                yield return new[] { "=\nBad encoding line" };
                yield return new[] { "==Bad encoding line" };
                yield return new[] { "Bad encoding line=" };
                yield return new[] { "Bad encoding line=\r" };
                yield return new[] { "Bad encoding line=0g" };
            }
        }
        
        [Theory]
        [PropertyData("BadStrings")]
        public void TestFailures(string badString)
        {
            string decoded = null;
            Assert.Throws<MimeException>(() => decoded = new QuotedPrintableDecoder(badString).GetString());
        }
        
        TestString MakeAllEncodeChars(bool lowerCase)
        {
            StringBuilder allEncoded = new StringBuilder();
            StringBuilder expected = new StringBuilder();
            for (int ch = 1; ch <= byte.MaxValue; ++ch)
            {
                allEncoded.Append(this.Encode((char)ch, lowerCase));
                expected.Append((char) ch);
            }
            
            return new TestString
            {
                Encoded = allEncoded.ToString(),
                Expected = expected.ToString()
            };
        }
        
        TestString MakeRandom(int textLength, double encodeProbability, double lowerCaseProbability)
        {
            Random rand = new Random();
            StringBuilder encodedBuilder = new StringBuilder();
            StringBuilder expectedBuilder = new StringBuilder();
            int lineLength = 0;
            for (int i = 0; i < textLength; ++i)
            {
                char ch = (char) rand.Next(1, byte.MaxValue);
                
                if (ch == MimeStandard.CR || ch == MimeStandard.LF)
                {
                    encodedBuilder.Append(MimeStandard.CRLF);
                    expectedBuilder.Append(MimeStandard.CRLF);
                    lineLength += 2;
                    continue;
                }
                
                if (ch == QuotedPrintableDecoder.EncodingChar || 
                    MimeStandard.IsWhitespace(ch) ||            // Always encode whitespace
                    rand.NextDouble() <= encodeProbability)
                {
                    if (lineLength > 72)
                    {
                        encodedBuilder.Append("=\r\n"); // Soft line break
                        lineLength = 0;
                    }
                    encodedBuilder.Append(this.Encode(ch, rand.NextDouble() <= lowerCaseProbability));
                    lineLength += 3;
                }
                else
                {
                    if (lineLength >= 75)
                    {
                        encodedBuilder.Append("=\r\n"); // Soft line break
                        lineLength = 0;
                    }
                    encodedBuilder.Append(ch);
                    ++lineLength;
                }
                expectedBuilder.Append(ch);
            }
            
            return new TestString
            {
                Encoded = encodedBuilder.ToString(),
                Expected = expectedBuilder.ToString()
            };
        }
                                
        string Encode(char ch, bool lowerCase)
        {
            if (lowerCase)
            {
                return string.Format("={0:x2}", (byte)ch);
            }
            return string.Format("={0:X2}", (byte)ch);
        }
        
        internal struct TestString
        {
            internal string Encoded;
            internal string Expected;
        }
    }
}
