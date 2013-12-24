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
using System.Collections.Generic;
using System.Net;
using System.IO;

using Health.Direct.Common.DnsResolver;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.DnsResolver
{
    public class TestSerialization
    {
        public static IEnumerable<object[]> AddressData
        {
            get
            {
                yield return new[] { "foo", "65.55.12.249" };
                yield return new[] { "www.microsoft.com", "65.55.12.249" };
                yield return new[] {"www.bing.com", "67.148.71.19"};
            }
        }

        public static IEnumerable<object[]> MxData
        {
            get
            {
                yield return new[] { "www.microsoft.com", "www.microsoft.com" };
                yield return new[] { "www.hotmail.com", "www.hotmail.com" };
            }
        }

        public static IEnumerable<object[]> CertData
        {
            get
            {
                yield return new[] { "redmond.cer" };
                yield return new[] { "umesh.cer" };
            }
        }
            
        [Theory]
        [PropertyData("AddressData")]
        public void TestA(string name, string ip)
        {
            IPAddress original = IPAddress.Parse(ip);            
            AddressRecord record = new AddressRecord(name, original.ToIPV4());
            Assert.True(record.IPAddress.Equals(original));
            
            AddressRecord parsedRecord = this.Roundtrip<AddressRecord>(record);            
            Assert.True(parsedRecord.Equals(record));
        }

        [Theory]
        [PropertyData("MxData")]
        public void TestMX(string name, string exchange)
        {
            MXRecord record = new MXRecord(name, exchange);
            MXRecord parsedRecord = this.Roundtrip<MXRecord>(record);
            Assert.True(parsedRecord.Equals(record));
        }

        [Theory]
        [PropertyData("CertData")]
        public void TestCert(string name)
        {
            CertRecord record = new CertRecord(this.LoadCert(name));
            CertRecord parsedRecord = this.Roundtrip<CertRecord>(record);
            Assert.True(parsedRecord.Equals(record));
        }
        
        [Fact]
        public void TestValidation()
        {
            DnsRequest request = DnsRequest.CreateA("foo.com");
            Assert.DoesNotThrow(() => request.Validate());
            request.Header.IsRequest = false;
            Assert.Throws<DnsProtocolException>(() => request.Validate());
        }

        [Fact]
        public void TestProtocolException()
        {
            DnsProtocolException ex = new DnsProtocolException(DnsProtocolError.InvalidMXRecord);
            string error;
            Assert.DoesNotThrow(() => ex.ToString());
            Assert.DoesNotThrow(() => error = ex.Message);
            Assert.True(ex.ToString().Contains("MXRecord"));
            Assert.True(ex.Message.Contains("MXRecord"));

            ex = new DnsProtocolException(DnsProtocolError.InvalidMXRecord, IPAddress.Parse("1.2.3.4"));
            Assert.DoesNotThrow(() => ex.ToString());
            Assert.DoesNotThrow(() => error = ex.Message);
            Assert.True(ex.ToString().Contains("MXRecord"));
            Assert.True(ex.Message.Contains("MXRecord"));
            Assert.True(ex.ToString().Contains("1.2.3.4"));
            Assert.True(ex.Message.Contains("1.2.3.4"));
        }

        T Roundtrip<T>(T record)
            where T : DnsResourceRecord
        {
            DnsBuffer buffer = new DnsBuffer(1024);
            record.Serialize(buffer);

            DnsBufferReader reader = buffer.CreateReader();
            return (T) DnsResourceRecord.Deserialize(ref reader);        
        }
                        
        DnsX509Cert LoadCert(string name)
        {
            string path = Path.Combine(Directory.GetCurrentDirectory(), Path.Combine("DnsResolver", Path.Combine("DnsTestCerts", name)));
            return new DnsX509Cert(File.ReadAllBytes(path));
        }
    }
}